"""
Deduplication utility for scraped Google Maps results.

Uses Maps URL as primary key and name+address as secondary check
to eliminate duplicate listings across multiple grid-point searches.
"""

from __future__ import annotations

import logging
from typing import Any

logger = logging.getLogger(__name__)


def deduplicate(results: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """
    Remove duplicate place entries from a list of scraped results.

    Deduplication strategy:
      1. Primary: Unique Google Maps place URL (maps_link)
      2. Secondary: Normalized (name + address) composite key

    Args:
        results: List of place dicts with keys:
                 name, address, phone, website, maps_link

    Returns:
        Deduplicated list preserving original insertion order.
    """
    seen_urls: set[str] = set()
    seen_name_addr: set[str] = set()
    unique_results: list[dict[str, Any]] = []

    for entry in results:
        maps_link = (entry.get("maps_link") or "").strip()
        name = (entry.get("name") or "").strip().lower()
        address = (entry.get("address") or "").strip().lower()

        # Skip entries with no name at all
        if not name:
            logger.debug("Skipping entry with no name: %s", entry)
            continue

        # Primary dedup: by Maps URL
        if maps_link and maps_link in seen_urls:
            logger.debug("Duplicate URL skipped: %s", maps_link)
            continue

        # Secondary dedup: by name + address composite
        composite_key = f"{name}||{address}"
        if composite_key in seen_name_addr:
            logger.debug("Duplicate name+address skipped: %s", composite_key)
            continue

        # Mark as seen
        if maps_link:
            seen_urls.add(maps_link)
        seen_name_addr.add(composite_key)
        unique_results.append(entry)

    removed_count = len(results) - len(unique_results)
    if removed_count > 0:
        logger.info(
            "Deduplication: %d entries removed, %d unique results retained.",
            removed_count,
            len(unique_results),
        )

    return unique_results
