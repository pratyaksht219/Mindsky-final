# Google Maps Psychologist/Psychiatrist Scraper

A scalable Python-based web scraping service that extracts data of psychologists
and psychiatrists from Google Maps for any given geographic region and exposes
it via a FastAPI endpoint, returning the results as a downloadable CSV file.

## Features

- **Dual input modes**: Accepts latitude/longitude or district + state (auto-geocoded)
- **Grid-based search**: Divides the area into multiple coordinate points for full coverage
- **Stealth scraping**: Uses `playwright-stealth`, randomized user-agents, and smart delays
- **Fault-tolerant**: Handles missing fields, timeouts, retries, and listing failures gracefully
- **Deduplication**: Removes duplicates by Maps URL and name+address composite key
- **Customizable keywords**: Search for any profession (therapist, mental health clinic, etc.)
- **CSV + JSON output**: Download results as CSV, optionally save to disk

## Quick Start

### 1. Install Dependencies

```bash
pip install -r requirements.txt
playwright install chromium
```

### 2. Run the Server

```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### 3. Make a Request

**Using lat/lng:**

```bash
curl -X POST http://localhost:8000/scrape \
  -H "Content-Type: application/json" \
  -d '{"lat": 28.6139, "lng": 77.2090, "radius_km": 5}' \
  --output results.csv
```

**Using district + state:**

```bash
curl -X POST http://localhost:8000/scrape \
  -H "Content-Type: application/json" \
  -d '{"district": "Patna", "state": "Bihar", "radius_km": 10}' \
  --output results.csv
```

**Custom keywords:**

```bash
curl -X POST http://localhost:8000/scrape \
  -H "Content-Type: application/json" \
  -d '{"lat": 28.6139, "lng": 77.2090, "keywords": ["therapist", "mental health clinic"]}' \
  --output results.csv
```

### 4. API Docs

Visit `http://localhost:8000/docs` for interactive Swagger documentation.

## API Reference

### `GET /health`

Health check endpoint.

**Response:**
```json
{"status": "healthy", "timestamp": "2026-04-11T11:00:00+00:00"}
```

### `POST /scrape`

Scrape Google Maps and return a CSV download.

**Request Body:**

| Field        | Type       | Required | Default                            | Description                           |
|-------------|------------|----------|------------------------------------|---------------------------------------|
| `lat`       | float      | *        | —                                  | Latitude of search center             |
| `lng`       | float      | *        | —                                  | Longitude of search center            |
| `district`  | string     | *        | —                                  | District/city name (alternative)      |
| `state`     | string     | *        | —                                  | State/province name (alternative)     |
| `country`   | string     | No       | `"India"`                          | Country for geocoding                 |
| `radius_km` | int        | No       | `10`                               | Search radius in km                   |
| `step_km`   | float      | No       | `5.0`                              | Grid step size in km                  |
| `keywords`  | list[str]  | No       | `["psychologist", "psychiatrist"]` | Search terms                          |
| `save_to_disk` | bool    | No       | `false`                            | Also save CSV to `output/` directory |

> \* Provide either `(lat, lng)` **or** `(district, state)`.

**Response:** CSV file download with columns: `Name, Address, Phone, Website, Maps Link`

## Project Structure

```
GoogleMapsScraper/
├── app/
│   ├── __init__.py
│   ├── main.py                 # FastAPI entry point
│   ├── scraper/
│   │   ├── __init__.py
│   │   ├── maps_scraper.py     # Core Playwright scraping engine
│   │   └── parser.py           # Data extraction from detail panels
│   ├── services/
│   │   ├── __init__.py
│   │   ├── csv_service.py      # CSV generation via Pandas
│   │   └── geo_service.py      # Geocoding & grid generation
│   └── utils/
│       ├── __init__.py
│       └── deduplicator.py     # Deduplication logic
├── requirements.txt
└── README.md
```

## ⚠️ Disclaimer

This tool is for **educational and research purposes only**. Scraping Google Maps
may violate Google's Terms of Service. For production-grade data access, consider
using the official [Google Places API](https://developers.google.com/maps/documentation/places/web-service/overview).
