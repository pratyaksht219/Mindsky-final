"""
Core scraping engine for Google Maps.

Handles browser automation, stealth measures, and scrolling.
Extracts raw unstructured data and passes it to the parser.
"""

from __future__ import annotations

import asyncio
import logging
import random
from typing import Any

from playwright.async_api import async_playwright, Browser, BrowserContext, Page, Playwright, TimeoutError as PlaywrightTimeout
from playwright_stealth import Stealth

from app.scraper.parser import extract_place_details

logger = logging.getLogger(__name__)

# --- Configuration & Anti-Detection ---
USER_AGENTS = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
]

VIEWPORTS = [
    {"width": 1920, "height": 1080},
    {"width": 1366, "height": 768},
    {"width": 1536, "height": 864},
    {"width": 1440, "height": 900},
]

# Delays to mimic human behavior
SCROLL_DELAY_MIN_S = 1.0
SCROLL_DELAY_MAX_S = 2.5
CLICK_DELAY_MIN_S = 1.0
CLICK_DELAY_MAX_S = 3.0

# Timeouts
NAVIGATION_TIMEOUT_MS = 60000
MAX_RETRIES = 3


async def scrape_google_maps(
    keyword: str,
    lat: float,
    lng: float,
    limit: int | None = None,
) -> list[dict[str, Any]]:
    """
    Core scraping engine for Google Maps.

    Launches a stealth headless browser, performs the search, scrolls through
    all results, clicks each listing to extract details, and returns the data.

    Args:
        keyword: Search term (e.g. "psychologist", "psychiatrist").
        lat: Latitude of the search center.
        lng: Longitude of the search center.
        limit: Optional maximum number of listings to return.

    Returns:
        List of dicts, each with keys: name, address, phone, website, maps_link.
    """
    results: list[dict[str, Any]] = []
    # Updated search URL format that explicitly centers the viewport on the coordinates
    # Added ?hl=en to enforce English language regardless of Docker host geolocation
    search_url = (
        f"https://www.google.com/maps/search/{keyword}/"
        f"@{lat},{lng},14z/data=!3m1!4b1?hl=en"
    )
    logger.info("Scraping: %s", search_url)

    async with Stealth().use_async(async_playwright()) as p:
        browser = await _launch_browser(p)
        try:
            # Pass coordinates to mock Geolocation API
            context = await _create_context(browser, lat, lng)
            page = await context.new_page()

            # Navigate to Google Maps search
            await page.goto(search_url, timeout=NAVIGATION_TIMEOUT_MS)
            await _handle_consent(page)

            # Wait for the results feed to load
            feed = await _wait_for_feed(page)
            if feed is None:
                logger.warning(
                    "No results feed found for '%s' near (%.4f, %.4f). "
                    "Possibly no results or page structure changed.",
                    keyword, lat, lng,
                )
                await browser.close()
                return results

            # Scroll to load all results
            await _scroll_results_feed(page, feed)

            # Collect all listing data (href and name)
            listing_links = await _collect_listing_links(page, feed)
            if limit and limit > 0:
                listing_links = listing_links[:limit]

            logger.info(
                "Found %d listings (limited to %s) for '%s' near (%.4f, %.4f).",
                len(listing_links), limit, keyword, lat, lng,
            )

            # We will use a separate page for details to avoid detached DOM elements 
            # and SPA back-navigation issues.
            detail_page = await context.new_page()

            # Process each listing by its direct URL
            for idx, link_data in enumerate(listing_links):
                place_data = await _process_listing_direct(
                    detail_page, link_data, idx, len(listing_links)
                )
                if place_data and place_data.get("name"):
                    results.append(place_data)

                # Random delay between listings
                await asyncio.sleep(
                    random.uniform(CLICK_DELAY_MIN_S, CLICK_DELAY_MAX_S)
                )
                
            await detail_page.close()

        except Exception as e:
            logger.error("Scraping failed for '%s': %s", search_url, e)
        finally:
            await browser.close()

    logger.info(
        "Scraping complete: %d results for '%s' near (%.4f, %.4f).",
        len(results), keyword, lat, lng,
    )
    return results


async def _launch_browser(p: Playwright) -> Browser:
    """Launch a headless Chromium browser."""
    # Playwright typing isn't super clean, but this works
    browser = await p.chromium.launch(
        headless=True,
        args=[
            "--disable-blink-features=AutomationControlled",
            "--no-sandbox",
            "--disable-dev-shm-usage",
        ],
    )
    return browser


async def _create_context(browser: Browser, lat: float, lng: float) -> BrowserContext:
    """Create a browser context with randomized fingerprint and specific geolocation."""
    ua = random.choice(USER_AGENTS)
    viewport = random.choice(VIEWPORTS)

    context = await browser.new_context(
        user_agent=ua,
        viewport=viewport,
        locale="en-US",
        timezone_id="Asia/Kolkata",
        geolocation={"latitude": lat, "longitude": lng},
        permissions=["geolocation"],
    )
    return context


async def _handle_consent(page: Page) -> None:
    """Accept Google's cookie consent dialog if it appears."""
    try:
        # Looking for the "Accept all" button or consent form buttons
        # The URL hl=en param ensures it stays in English
        consent_button = page.locator('button:has-text("Accept all"), form[action*="consent"] button').first
        if await consent_button.is_visible(timeout=5000):
            await consent_button.click()
            logger.debug("Accepted Google consent dialog.")
    except PlaywrightTimeout:
        pass  # No consent dialog appeared
    except Exception as e:
        logger.debug("Error handling consent: %s", e)


async def _wait_for_feed(page: Page):
    """Wait for the main search results feed container."""
    try:
        # Google Maps results feed usually has role="feed"
        feed = page.locator('div[role="feed"]').first
        await feed.wait_for(state="visible", timeout=NAVIGATION_TIMEOUT_MS)
        return feed
    except PlaywrightTimeout:
        return None


async def _scroll_results_feed(page: Page, feed) -> None:
    """Scroll down the results feed until the end of list is reached."""
    logger.info("Scrolling through search results...")
    
    # We detect the end of the list when an element with text text like
    # "You've reached the end of the list." inside the feed is found.
    # Alternatively, if scrolling stops adding new items, we also break.
    
    previous_count = 0
    same_count_retries = 0
    max_same_count_retries = 3

    while True:
        # Count current items
        current_count = await feed.locator('a[href*="/maps/place/"]').count()
        
        # Check for end of list marker
        end_marker = feed.locator('span:has-text("You\'ve reached the end of the list")').first
        if await end_marker.is_visible():
            logger.debug("Reached end of list marker.")
            break
            
        if current_count == previous_count:
            same_count_retries += 1
            if same_count_retries >= max_same_count_retries:
                logger.debug("No new items loaded after multiple scrolls. Assuming end.")
                break
        else:
            same_count_retries = 0
            
        previous_count = current_count
        
        # Scroll down by hovering and using mouse wheel, or pressing PageDown
        await feed.hover()
        await page.mouse.wheel(0, 5000)
        
        # Random delay to simulate reading / wait for items to load
        await asyncio.sleep(random.uniform(SCROLL_DELAY_MIN_S, SCROLL_DELAY_MAX_S))


async def _collect_listing_links(page: Page, feed) -> list[dict[str, str]]:
    """Collect all listing hrefs and names from the feed."""
    links_data = []
    seen_hrefs: set[str] = set()

    # Google Maps place links contain /maps/place/ in the href
    locator = feed.locator('a[href*="/maps/place/"]')
    count = await locator.count()

    for i in range(count):
        try:
            link = locator.nth(i)
            href = await link.get_attribute("href")
            name = await link.get_attribute("aria-label")
            if href and href not in seen_hrefs:
                seen_hrefs.add(href)
                links_data.append({
                    "href": href,
                    "name": name.strip() if name else None
                })
        except Exception as e:
            logger.debug("Failed to extract link data: %s", e)
            continue

    return links_data


async def _process_listing_direct(
    page: Page,
    link_data: dict[str, str],
    index: int,
    total: int,
) -> dict[str, Any] | None:
    """
    Navigate directly to a listing's URL and extract place details.
    Includes retry logic for transient failures.
    """
    href = link_data["href"]
    listing_name = link_data["name"]

    for retry in range(MAX_RETRIES):
        try:
            logger.info(
                "Processing listing %d/%d (attempt %d)...",
                index + 1, total, retry + 1,
            )

            # Navigate directly to the place URL
            await page.goto(href, timeout=NAVIGATION_TIMEOUT_MS)
            
            # Handle possible consent dialogs on the new page
            await _handle_consent(page)

            # Wait for some detail panel indicators
            try:
                await page.wait_for_selector(
                    'h1, button[data-item-id="address"], button[data-item-id*="phone"]',
                    timeout=NAVIGATION_TIMEOUT_MS // 3,
                )
            except PlaywrightTimeout:
                logger.debug("Detail panel slow to load, proceeding anyway...")

            # Small delay to let everything render
            await asyncio.sleep(random.uniform(0.5, 1.0))

            # Extract data from the detail panel
            place_data = await extract_place_details(
                page, listing_name=listing_name
            )

            if place_data.get("name"):
                logger.info(
                    "  → Extracted: %s", place_data["name"]
                )
                return place_data
            else:
                logger.warning(
                    "  → No name found for listing %d, retrying...",
                    index + 1,
                )

        except Exception as e:
            logger.warning(
                "  → Error on listing %d (attempt %d): %s",
                index + 1, retry + 1, e,
            )
            await asyncio.sleep(1.0 * (retry + 1))  # Exponential-ish backoff

    logger.error("  → Failed to extract listing %d after %d retries.", index + 1, MAX_RETRIES)
    return None
