#!/bin/sh

echo "⏳ Waiting for PostGIS..."
sleep 10

echo "📂 Debug: Listing files inside container..."
ls -R /data

echo "📥 Importing states..."
ogr2ogr -progress -f "PostgreSQL" \
PG:"host=postgis-db dbname=crisis_db user=postgres password=postgres" \
/data/india_states.geojson \
-nln states -nlt MULTIPOLYGON -lco GEOMETRY_NAME=geom -overwrite

echo "📥 Importing districts..."
ogr2ogr -progress -f "PostgreSQL" \
PG:"host=postgis-db dbname=crisis_db user=postgres password=postgres" \
/data/dists11.geojson \
-nln districts -nlt MULTIPOLYGON -lco GEOMETRY_NAME=geom -overwrite

echo "✅ Data import complete"
