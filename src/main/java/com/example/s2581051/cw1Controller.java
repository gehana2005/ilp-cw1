package com.example.s2581051;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class cw1Controller {

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

    public double euclideanDistance(position p1, position p2){

        double x = p1.getLng() - p2.getLng();
        double y = p1.getLat() - p2.getLat();

        return Math.sqrt(x*x + y*y);
    }

    @PostMapping("/api/v1/distanceTo")
    public distanceResponse distanceTo(@RequestBody distanceRequest request){

        double distance = euclideanDistance(request.getPosition1(), request.getPosition2());
        return new distanceResponse(distance);
    }

    @PostMapping("/api/v1/isCloseTo")
    public boolean isCloseTo(@RequestBody distanceRequest request){

        position p1 = request.getPosition1();
        position p2 = request.getPosition2();

        return euclideanDistance(p1, p2) < 0.00015;
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

    private boolean pointInPolygon(position point, List<position> vertices){
        int intersections = 0;

        for (int i=0; i < vertices.size()-1; i++ ){
            position v1 = vertices.get(i);
            position v2 = vertices.get(i+1);
            boolean condY = (v1.getLat() > point.getLat()) != (v2.getLat() > point.getLat());
            if (condY) {
                double slope = (point.getLat() - v1.getLat()) * (v2.getLng() - v1.getLng()) /
                        (v2.getLat() - v1.getLat()) + v1.getLng();
                if (point.getLng() < slope) {
                    intersections++;
                }
            }
        }
        return intersections % 2 == 1;
    }

    private boolean isClosedPolygon(List<position> vertices){
        if (vertices.size() < 4){
            return false;
        }
        position first = vertices.get(0);
        position last = vertices.get(vertices.size()-1);

        return Double.compare(first.getLat(), last.getLat()) == 0 &&
               Double.compare(first.getLng(), last.getLng()) == 0;
    }

    @PostMapping("api/v1/isInRegion")
    public ResponseEntity<Map<String, Object>> isInRegion(@RequestBody regionRequest request){

        position point = request.getPosition();
        List<position> vertices = request.getRegion().getVertices();

        if (!isClosedPolygon(vertices)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("region", request.getRegion().getName()));
        }

        return ResponseEntity.ok(Map.of(
                "region", request.getRegion().getName(),
                "inside", pointInPolygon(point, vertices))
        );
    }

}
