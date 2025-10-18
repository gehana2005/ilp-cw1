package com.example.s2581051.controller;

import com.example.s2581051.model.*;
import com.example.s2581051.service.distanceService;
import com.example.s2581051.service.polygonService;
import com.example.s2581051.service.nextPositionService;
import jakarta.validation.Valid;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class cw1Controller {

    private final distanceService distanceService;
    private final polygonService polyService;
    private final nextPositionService nextpositionService;

    public cw1Controller(distanceService distanceService, polygonService polyService, nextPositionService nextpositionService){
        this.distanceService = distanceService;
        this.polyService = polyService;
        this.nextpositionService = nextpositionService;
    }

    @GetMapping("/actuator/health")
    public Health customHealth() {
        return Health.up().build();
    }

    @GetMapping("/api/v1/uid")
    public String getUID() {
        return "s2581051";
    }

    @PostMapping("/api/v1/distanceTo")
    public double distanceTo(@Valid @RequestBody DistanceRequest request) {
        Position p1 = request.getPosition1();
        Position p2 = request.getPosition2();
        return distanceService.euclideanDistance(p1, p2);
    }

    @PostMapping("/api/v1/isCloseTo")
    public boolean isCloseTo(@Valid @RequestBody DistanceRequest request) {
        Position p1 = request.getPosition1();
        Position p2 = request.getPosition2();
        return distanceService.isCloseTo(p1, p2);
    }

    @PostMapping("/api/v1/nextPosition")
    public Position nextPosition(@Valid @RequestBody PositionRequest request) {
        Position start = request.getStart();
        double angle = request.getAngle();
        return nextpositionService.nextPosition(start, angle);
    }

    @PostMapping("/api/v1/isInRegion")
    public boolean isInRegion(@Valid @RequestBody RegionRequest request) {
        Position point = request.getPosition();
        List<Position> vertices = request.getRegion().getVertices();
        return polyService.pointInPolygon(point, vertices);
    }
}
