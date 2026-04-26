package com.example.mentalhealth.emergency_service.ReverseGeoTagging.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table(name = "states")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "geometry(MultiPolygon, 4326)")
    private MultiPolygon geom;

    // getters & setters
}