-- Enable PostGIS
CREATE EXTENSION IF NOT EXISTS postgis;

-- STATES TABLE
DROP TABLE IF EXISTS states;

CREATE TABLE states (
                        id SERIAL PRIMARY KEY,
                        st_nm TEXT,
                        geom GEOMETRY(MULTIPOLYGON, 4326)
);

CREATE INDEX IF NOT EXISTS states_geom_idx
    ON states USING GIST (geom);



-- DISTRICTS TABLE
DROP TABLE IF EXISTS districts;

CREATE TABLE districts (
                           id SERIAL PRIMARY KEY,
                           district TEXT,
                           st_nm TEXT,
                           geom GEOMETRY(MULTIPOLYGON, 4326)
);

CREATE INDEX IF NOT EXISTS districts_geom_idx
    ON districts USING GIST (geom);