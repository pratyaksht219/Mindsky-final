package com.example.mentalhealth.emergency_service.ReverseGeoTagging.Repository;

import com.example.mentalhealth.emergency_service.ReverseGeoTagging.Entity.State;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface StateRepository extends Repository<State, Long> {

    @Query(value = """
        SELECT states.st_nm FROM states
        WHERE ST_Contains(
            geom,
            ST_SetSRID(ST_Point(:lon, :lat), 4326)
        )
    """, nativeQuery = true)
    String findStateByLocation(double lat, double lon);

    @Query(value = """
    SELECT DISTINCT districts.district, districts.st_nm
    FROM districts
    WHERE ST_Intersects(
        geom,
        ST_SetSRID(ST_Point(:lon, :lat), 4326)
    )
""", nativeQuery = true)
    List<Object[]> findDistrictAndState(double lat, double lon);
}