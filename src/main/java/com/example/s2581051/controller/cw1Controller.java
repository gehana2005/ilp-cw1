package com.example.s2581051.controller;

import com.example.s2581051.model.*;
import com.example.s2581051.service.GeoService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.List;
import java.util.Map;

@RestController
public class cw1Controller {

    private final GeoService geoService;

    public cw1Controller(GeoService geoService){
        this.geoService = geoService;
    }

    @GetMapping("/actuator/health")
    public Health customHealth(){
        return Health.up().build();
    }

    @GetMapping("/api/v1/uid")
    public String getUID(){
        return "s2581051";
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleBadJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("error: bad json");
    }

    @PostMapping("/api/v1/distanceTo")
    public distanceResponse distanceTo(@RequestBody distanceRequest request){

        double distance = geoService.euclideanDistance(request.getPosition1(), request.getPosition2());
        return new distanceResponse(distance);
    }

    @PostMapping("/api/v1/isCloseTo")
    public boolean isCloseTo(@RequestBody distanceRequest request){

        position p1 = request.getPosition1();
        position p2 = request.getPosition2();

        return geoService.euclideanDistance(p1, p2) < 0.00015;
    }

    @PostMapping("/api/v1/nextPoint")
    public position nextPoint(@RequestBody positionRequest request){

        double ds = 0.00015;

        position start = request.getStart();
        double angle = request.getAngle();

        double lng = start.getLng() + ds * Math.cos(Math.toRadians(angle));
        double lat = start.getLat() + ds * Math.sin(Math.toRadians(angle)) ;

        return new position(lng, lat);
    }

    @PostMapping("api/v1/isInRegion")
    public ResponseEntity<Map<String, Object>> isInRegion(@RequestBody regionRequest request){

        position point = request.getPosition();
        List<position> vertices = request.getRegion().getVertices();

        if (!(geoService.isClosedPolygon(vertices))) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("region", request.getRegion().getName()));
        }

        return ResponseEntity.ok(Map.of(
                "region", request.getRegion().getName(),
                "inside", geoService.pointInPolygon(point, vertices))
        );
    }

}
