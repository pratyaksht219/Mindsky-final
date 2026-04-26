"""
FastAPI entry point for the Google Maps Scraping Service.

Endpoints:
  - GET  /health  → Health check
  - POST /scrape  → Scrape Google Maps and return CSV
"""

from __future__ import annotations

import asyncio
import logging
import sys
from datetime import datetime, timezone
from typing import Optional

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, model_validator

from app.scraper.maps_scraper import scrape_google_maps
from app.services.json_service import format_results, save_json
from app.services.geo_service import geocode_location, generate_grid_points
from app.utils.deduplicator import deduplicate

# --- Logging setup ---
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)-8s | %(name)s | %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
    handlers=[logging.StreamHandler(sys.stdout)],
)
logger = logging.getLogger(__name__)

# --- App ---
app = FastAPI(
    title="Google Maps Scraper API",
    description=(
        "Scrape psychologist and psychiatrist listings from Google Maps "
        "for any geographic region. Returns results as a downloadable CSV."
    ),
    version="1.0.0",
)

# --- Default search keywords ---
DEFAULT_KEYWORDS = [
    "psychologist", 
    # "psychiatrist"
]


# --- Request / Response Models ---
class ScrapeRequest(BaseModel):
    """
    Request body for the /scrape endpoint.

    Provide either (lat, lng) or (district, state). If both are given,
    lat/lng takes precedence.
    """

    lat: Optional[float] = None
    lng: Optional[float] = None
    district: Optional[str] = None
    state: Optional[str] = None
    country: Optional[str] = "India"
    radius_km: int = 10
    step_km: float = 5.0
    keywords: Optional[list[str]] = None
    save_to_disk: bool = False
    max_results: Optional[int] = 10

    @model_validator(mode="after")
    def validate_location(self):
        has_coords = self.lat is not None and self.lng is not None
        has_district = self.district is not None and self.state is not None
        if not has_coords and not has_district:
            raise ValueError(
                "Provide either (lat, lng) or (district, state)."
            )
        return self


# --- Endpoints ---
@app.get("/health")
async def health_check():
    """Health check endpoint."""
    return {
        "status": "healthy",
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }


@app.post("/scrape")
async def scrape_endpoint(request: ScrapeRequest):
    """
    Scrape Google Maps for psychologists/psychiatrists in the given area.

    Accepts lat/lng or district+state. Returns a downloadable CSV file.

    Steps:
      1. Resolve coordinates (geocode if needed)
      2. Generate grid of search points
      3. Scrape Google Maps for each keyword × grid point
      4. Deduplicate results
      5. Return CSV as streaming download
    """
    try:
        # Step 1: Resolve coordinates
        lat, lng = await _resolve_coordinates(request)
        logger.info("Search center: lat=%.6f, lng=%.6f", lat, lng)

        # Step 2: Generate grid points
        grid_points = generate_grid_points(
            center_lat=lat,
            center_lng=lng,
            radius_km=request.radius_km,
            step_km=request.step_km,
        )
        logger.info("Grid points generated: %d", len(grid_points))

        # Step 3: Determine keywords
        keywords = request.keywords or DEFAULT_KEYWORDS

        # Step 4: Scrape for each keyword × grid point
        all_results: list[dict] = []
        final_unique_results: list[dict] = []
        total_tasks = len(grid_points) * len(keywords)
        task_num = 0

        for keyword in keywords:
            keyword_results: list[dict] = []
            keyword_unique: list[dict] = []

            for point_lat, point_lng in grid_points:
                # Stop early if we have reached the requested limit for THIS keyword
                if request.max_results and len(keyword_unique) >= request.max_results:
                    logger.info("Reached max_results (%d) for keyword '%s'. Moving to next.", request.max_results, keyword)
                    break

                task_num += 1
                logger.info(
                    "=== Task %d/%d: '%s' near (%.4f, %.4f) ===",
                    task_num, total_tasks, keyword, point_lat, point_lng,
                )
                try:
                    current_limit = (request.max_results - len(keyword_unique)) if request.max_results else None
                    results = await scrape_google_maps(
                        keyword=keyword,
                        lat=point_lat,
                        lng=point_lng,
                        limit=current_limit,
                    )
                    keyword_results.extend(results)
                    keyword_unique = deduplicate(keyword_results)
                    
                    logger.info(
                        "Task %d/%d complete: %d results collected (Unique '%s' so far: %d).",
                        task_num, total_tasks, len(results), keyword, len(keyword_unique),
                    )
                except Exception as e:
                    logger.error(
                        "Task %d/%d failed: %s. Continuing...",
                        task_num, total_tasks, e,
                    )
                    continue
            
            # Truncate this keyword's results exactly to max_results and add to final pool
            if request.max_results:
                keyword_unique = keyword_unique[:request.max_results]
                
            final_unique_results.extend(keyword_unique)

        # Final global deduplication (in case a person is listed as both psychologist & psychiatrist!)
        unique_results = deduplicate(final_unique_results)

        logger.info(
            "Final results: %d total unique entities exported.",
            len(unique_results),
        )

        if not unique_results:
            raise HTTPException(
                status_code=404,
                detail="No results found for the given location and keywords.",
            )

        # Step 6: Format keys cleanly (Name, Address, etc)
        formatted_results = format_results(unique_results)

        # Step 7: Optionally save to disk
        if request.save_to_disk:
            json_path = save_json(formatted_results)
            logger.info("Results saved to: %s", json_path)

        # Step 8: Return RAW JSON response
        return {
            "status": "success",
            "metadata": {
                "total_results": len(formatted_results),
                "center": {"lat": lat, "lng": lng},
                "radius_km": request.radius_km
            },
            "data": formatted_results
        }

    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except HTTPException:
        raise
    except Exception as e:
        logger.exception("Unexpected error in /scrape endpoint.")
        raise HTTPException(status_code=500, detail=f"Internal error: {e}")


async def _resolve_coordinates(request: ScrapeRequest) -> tuple[float, float]:
    """
    Resolve the search center coordinates.
    Uses lat/lng if provided, otherwise geocodes district+state.
    """
    if request.lat is not None and request.lng is not None:
        return (request.lat, request.lng)

    if request.district and request.state:
        return geocode_location(
            district=request.district,
            state=request.state,
            country=request.country or "India",
        )

    raise ValueError("No valid location provided.")
