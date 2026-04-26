"""
JSON data formatting and storage service.

Responsible for formatting raw scraper dicts into clean JSON records,
and supporting writing those records to disk.
"""

from __future__ import annotations

import json
import logging
import os
from datetime import datetime, timezone
from typing import Any

logger = logging.getLogger(__name__)

# Internal dict keys → Clean JSON keys mapping
_KEY_TO_COLUMN = {
    "name": "Name",
    "address": "Address",
    "phone": "Phone",
    "website": "Website",
    "maps_link": "Maps Link",
}


def format_results(data: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """
    Format internal dictionary keys to human-readable strings.
    """
    return [
        {_KEY_TO_COLUMN.get(k, k): (v if v is not None else "") for k, v in entry.items()}
        for entry in data
    ]


def save_json(
    data: list[dict[str, Any]],
    output_dir: str = "output",
    filename: str | None = None,
) -> str:
    """
    Save scraped results to a JSON file on disk.

    Args:
        data: List of formatted place dicts.
        output_dir: Directory to save the file in (created if missing).
        filename: Optional custom filename. Defaults to results_<timestamp>.json.

    Returns:
        Absolute path to the saved JSON file.
    """
    os.makedirs(output_dir, exist_ok=True)

    if filename is None:
        ts = datetime.now(timezone.utc).strftime("%Y%m%d_%H%M%S")
        filename = f"results_{ts}.json"

    filepath = os.path.join(output_dir, filename)

    with open(filepath, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    logger.info("Saved JSON to %s (%d entries).", filepath, len(data))
    return os.path.abspath(filepath)
