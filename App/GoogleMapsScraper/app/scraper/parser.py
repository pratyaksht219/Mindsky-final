"""
Parser module for extracting structured data from Google Maps place detail panels.

Uses aria-label and data-item-id based selectors for stability against
Google's frequent DOM changes. Each field extraction is fault-tolerant —
missing fields return None instead of crashing.
"""

from __future__ import annotations

import logging
import re
from typing import Any, Optional

from playwright.async_api import Page, Locator, TimeoutError as PlaywrightTimeout

logger = logging.getLogger(__name__)

# --- Timeout for waiting on detail panel elements (ms) ---
DETAIL_WAIT_TIMEOUT_MS = 5000
FIELD_WAIT_TIMEOUT_MS = 2000


async def extract_place_details(
    page: Page,
    listing_name: str | None = None,
) -> dict[str, Any]:
    """
    Extract structured data from the currently open Google Maps place detail panel.

    Assumes the detail panel is already loaded (a listing has been clicked).

    Args:
        page: Playwright Page with the detail panel visible.
        listing_name: Optional name pre-extracted from the listing's aria-label.
                      Used as a reliable fallback since the detail panel h1
                      can be ambiguous.

    Returns:
        Dict with keys: name, address, phone, website, maps_link.
        Missing fields are set to None.
    """
    result: dict[str, Any] = {
        "name": None,
        "address": None,
        "phone": None,
        "website": None,
        "maps_link": None,
    }

    # --- Name ---
    result["name"] = await _extract_name(page, listing_name)

    # --- Address ---
    result["address"] = await _extract_field(
        page,
        selectors=[
            'button[data-item-id="address"]',
            '[aria-label*="Address"]',
            'button[data-item-id="oloc"]',
        ],
        field_name="address",
    )

    # --- Phone ---
    result["phone"] = await _extract_field(
        page,
        selectors=[
            'button[data-item-id*="phone"]',
            '[aria-label*="Phone"]',
            '[data-tooltip="Copy phone number"]',
        ],
        field_name="phone",
    )

    # --- Website ---
    result["website"] = await _extract_website(page)

    # --- Maps Link (current URL) ---
    result["maps_link"] = page.url

    logger.debug("Extracted place: %s", result.get("name", "Unknown"))
    return result


async def _extract_name(
    page: Page,
    listing_name: str | None = None,
) -> Optional[str]:
    """
    Extract the place name from the detail panel.

    Strategy (in priority order):
      1. The place-specific h1 inside the detail/info panel (not the generic "Results" h1)
      2. The aria-label pre-extracted from the listing link (most reliable)
      3. The place name from the Maps URL
    """
    # Strategy 1: h1 scoped to the detail panel container
    # Google Maps detail panel typically lives inside a container with
    # role="main" that has an aria-label matching the place name,
    # or within a specific detail pane. We look for h1 elements that
    # do NOT contain generic text like "Results".
    try:
        # Try all h1 elements on the page; find one that isn't "Results"
        all_h1s = await page.locator('h1').all()
        for h1 in all_h1s:
            try:
                if await h1.is_visible(timeout=FIELD_WAIT_TIMEOUT_MS):
                    text = (await h1.text_content() or "").strip()
                    if text and text.lower() not in ("results", "result", "search"):
                        return text
            except (PlaywrightTimeout, Exception):
                continue
    except (PlaywrightTimeout, Exception) as e:
        logger.debug("h1 name extraction failed: %s", e)

    # Strategy 2: Use the pre-extracted listing aria-label
    if listing_name:
        return listing_name

    # Strategy 3: Try parsing the name from the current URL
    try:
        url = page.url
        # URLs look like: /maps/place/Dr.+Rajesh+Kumar+.../@lat,lng,...
        match = re.search(r"/maps/place/([^/@]+)", url)
        if match:
            name_from_url = match.group(1).replace("+", " ")
            # URL-decode common patterns
            name_from_url = re.sub(r"%[0-9A-Fa-f]{2}", " ", name_from_url).strip()
            if name_from_url and len(name_from_url) > 2:
                logger.debug("Extracted name from URL: %s", name_from_url)
                return name_from_url
    except Exception as e:
        logger.debug("URL name extraction failed: %s", e)

    return None


async def _extract_field(
    page: Page,
    selectors: list[str],
    field_name: str,
) -> Optional[str]:
    """
    Try multiple CSS selectors to extract a text field.
    Returns the aria-label text or inner text of the first match found.
    """
    for selector in selectors:
        try:
            element = page.locator(selector).first
            if await element.is_visible(timeout=FIELD_WAIT_TIMEOUT_MS):
                # Prefer aria-label (usually contains the full value)
                aria = await element.get_attribute("aria-label")
                if aria:
                    # aria-label often looks like "Address: 123 Main St"
                    # Strip the prefix if present
                    cleaned = _strip_label_prefix(aria, field_name)
                    if cleaned:
                        return cleaned

                # Fallback to text content
                text = (await element.text_content() or "").strip()
                if text:
                    return text
        except (PlaywrightTimeout, Exception):
            continue

    logger.debug("Field '%s' not found for current listing.", field_name)
    return None


async def _extract_website(page: Page) -> Optional[str]:
    """
    Extract the website URL from the detail panel.
    Looks for the authority link or website-labeled elements.
    """
    selectors = [
        'a[data-item-id="authority"]',
        '[aria-label*="Website"]',
        'a[data-item-id*="website"]',
    ]

    for selector in selectors:
        try:
            element = page.locator(selector).first
            if await element.is_visible(timeout=FIELD_WAIT_TIMEOUT_MS):
                # Try href first (actual link)
                href = await element.get_attribute("href")
                if href and href.startswith("http"):
                    return href

                # Fallback to aria-label or text
                aria = await element.get_attribute("aria-label")
                if aria:
                    cleaned = _strip_label_prefix(aria, "website")
                    if cleaned:
                        return cleaned

                text = (await element.text_content() or "").strip()
                if text:
                    return text
        except (PlaywrightTimeout, Exception):
            continue

    logger.debug("Website not found for current listing.")
    return None


def _strip_label_prefix(label: str, field_name: str) -> str:
    """
    Strip common prefixes from aria-label values.
    e.g. "Address: 123 Main St" → "123 Main St"
         "Phone: +1-555-1234" → "+1-555-1234"
    """
    # Try stripping "FieldName: " prefix (case-insensitive)
    pattern = re.compile(rf"^{re.escape(field_name)}\s*:\s*", re.IGNORECASE)
    result = pattern.sub("", label).strip()
    return result if result else label.strip()
