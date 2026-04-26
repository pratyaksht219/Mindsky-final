"""
Geo service for geocoding and grid-based coordinate generation.

Handles:
  - Converting district+state text to lat/lng via Nominatim (OpenStreetMap)
  - Generating a grid of search points to cover a given radius
"""

from __future__ import annotations

import logging
import math
from typing import Optional

from geopy.geocoders import Nominatim

logger = logging.getLogger(__name__)

# --- Constants ---
EARTH_KM_PER_DEGREE_LAT = 111.0  # ~111 km per degree of latitude


def geocode_location(
    district: str,
    state: str,
    country: str = "India",
) -> tuple[float, float]:
    """
    Convert a district + state string into (latitude, longitude).

    Uses Nominatim (OpenStreetMap) geocoder. Free, no API key required,
    but rate-limited to 1 request/second.

    Args:
        district: District / city name (e.g. "Patna")
        state: State / province name (e.g. "Bihar")
        country: Country name for disambiguation (default "India")

    Returns:
        Tuple of (latitude, longitude).

    Raises:
        ValueError: If the location could not be resolved.
    """
    geolocator = Nominatim(user_agent="google_maps_scraper_v1")
    query = f"{district}, {state}, {country}"
    logger.info("Geocoding query: %s", query)

    location = geolocator.geocode(query)
    if location is None:
        raise ValueError(
            f"Could not geocode location: '{query}'. "
            "Please verify the district and state names."
        )

    logger.info(
        "Geocoded '%s' → lat=%.6f, lng=%.6f",
        query,
        location.latitude,
        location.longitude,
    )
    return (location.latitude, location.longitude)


def generate_grid_points(
    center_lat: float,
    center_lng: float,
    radius_km: float = 10.0,
    step_km: float = 5.0,
) -> list[tuple[float, float]]:
    """
    Generate a grid of (lat, lng) points covering a circular area.

    The grid is a square lattice inscribed within the specified radius,
    ensuring overlapping coverage for thorough scraping.

    Args:
        center_lat: Center latitude of the search area.
        center_lng: Center longitude of the search area.
        radius_km: Radius to cover in kilometers.
        step_km: Distance between grid points in km. Smaller values give
                 denser coverage but more scraping requests.

    Returns:
        List of (latitude, longitude) tuples forming the search grid.
        Always includes the center point.
    """
    if radius_km <= 0:
        return [(center_lat, center_lng)]

    if step_km <= 0:
        step_km = radius_km  # At minimum, just do the center + edges

    # Convert km offsets to degree offsets
    lat_offset_per_step = step_km / EARTH_KM_PER_DEGREE_LAT
    lng_offset_per_step = step_km / (
        EARTH_KM_PER_DEGREE_LAT * math.cos(math.radians(center_lat))
    )

    # Number of steps in each direction from center
    steps = int(math.ceil(radius_km / step_km))

    grid_points: list[tuple[float, float]] = []

    for lat_step in range(-steps, steps + 1):
        for lng_step in range(-steps, steps + 1):
            point_lat = center_lat + (lat_step * lat_offset_per_step)
            point_lng = center_lng + (lng_step * lng_offset_per_step)

            # Only include points within the circular radius
            distance_km = _haversine_km(
                center_lat, center_lng, point_lat, point_lng
            )
            if distance_km <= radius_km:
                grid_points.append((round(point_lat, 6), round(point_lng, 6)))

    logger.info(
        "Generated %d grid points for center=(%.4f, %.4f), "
        "radius=%dkm, step=%dkm",
        len(grid_points),
        center_lat,
        center_lng,
        radius_km,
        step_km,
    )
    return grid_points


def _haversine_km(
    lat1: float, lng1: float, lat2: float, lng2: float
) -> float:
    """
    Calculate the great-circle distance between two points
    on Earth using the Haversine formula.

    Returns:
        Distance in kilometers.
    """
    R = 6371.0  # Earth radius in km

    d_lat = math.radians(lat2 - lat1)
    d_lng = math.radians(lng2 - lng1)

    a = (
        math.sin(d_lat / 2.0) ** 2
        + math.cos(math.radians(lat1))
        * math.cos(math.radians(lat2))
        * math.sin(d_lng / 2.0) ** 2
    )
    c = 2.0 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

    return R * c
