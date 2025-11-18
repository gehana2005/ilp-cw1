package com.example.s2581051.controller;

import com.example.s2581051.model.*;
import com.example.s2581051.service.distanceService;
import com.example.s2581051.service.polygonService;
import com.example.s2581051.service.nextPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Rest Controller
 */
@RestController
public class cw1Controller {

    @Autowired
    private distanceService distanceService;

    @Autowired
    private polygonService polyService;

    @Autowired
    private nextPositionService nextpositionService;

    /**
     * Health check endpoint
     * @return hardcoded health status
     */
    @GetMapping("/actuator/health")
    public Health customHealth() {
        return Health.up().build();
    }

    /**
     * UID endpoint
     * @return hardcoded uid
     */
    @GetMapping("/api/v1/uid")
    public String getUID() {
        return "s2581051";
    }

    /**
     * Euclidean distance endpoint
     * @param request containing two positions p1 and p2
     * @return Euclidean distance as a double
     */
    @PostMapping("/api/v1/distanceTo")
    public double distanceTo(@Valid @RequestBody DistanceRequest request) {
        Position p1 = request.getPosition1();
        Position p2 = request.getPosition2();
        return distanceService.euclideanDistance(p1, p2);
    }

    /**
     * isCloseTo endpoint
     * @param request containing two positions p1 and p2
     * @return true if the points are close enough
     */
    @PostMapping("/api/v1/isCloseTo")
    public boolean isCloseTo(@Valid @RequestBody DistanceRequest request) {
        Position p1 = request.getPosition1();
        Position p2 = request.getPosition2();
        return distanceService.isCloseTo(p1, p2);
    }

    /**
     * nextPosition endpoint
     * @param request containing a start position and angle
     * @return next position
     */
    @PostMapping("/api/v1/nextPosition")
    public Position nextPosition(@Valid @RequestBody PositionRequest request) {
        Position start = request.getStart();
        double angle = request.getAngle();
        return nextpositionService.nextPosition(start, angle);
    }

    /**
     * isInRegion endpoint
     * @param request containing a point and a list of vertices
     * @return true if point is inside the region
     */
    @PostMapping("/api/v1/isInRegion")
    public boolean isInRegion(@Valid @RequestBody RegionRequest request) {
        Position point = request.getPosition();
        List<Position> vertices = request.getRegion().getVertices();
        return polyService.pointInPolygon(point, vertices);
    }
}
