package com.example.s2581051.service;
import com.example.s2581051.model.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class serviceTest {

    private final distanceService distanceService = new distanceService();
    private final nextPositionService nextPositionService = new nextPositionService();
    private final polygonService polygonService = new polygonService();

    @Test
    @DisplayName("Should return 5.0 as the distance")
    void testEuclideanDistanceKnownTriangle() {
        Position p1 = new Position(3.0, 0.0);
        Position p2 = new Position(0.0, 4.0);
        assertEquals(5.0, distanceService.euclideanDistance(p1, p2), 1e-9);
    }

    @Test
    @DisplayName("Should return 0.0 for identical points")
    void testEuclideanDistanceSamePoint() {
        Position p = new Position(55.946233, -3.192473);
        assertEquals(0.0, distanceService.euclideanDistance(p, p), 1e-9);
    }

    @Test
    @DisplayName("Should handle negative coordinates correctly")
    void testEuclideanDistanceNegativeCoordinates() {
        Position p1 = new Position(-10.0, -10.0);
        Position p2 = new Position(-13.0, -14.0);
        assertEquals(5.0, distanceService.euclideanDistance(p1, p2), 1e-9);
    }

    @Test
    @DisplayName("Points below the thresh-hold closeness")
    void testIsCloseToPointsWithinThreshold() {
        Position p1 = new Position(0.0, 0.0);
        Position p2 = new Position(0.00014, 0.0);
        assertTrue(distanceService.isCloseTo(p1, p2));
    }

    // NextPosition Tests
    @Test
    @DisplayName("Moving North should increase the latitude")
    void testNextPositionNorth() {
        Position start = new Position(-3.192473, 55.946233);
        Position result = nextPositionService.nextPosition(start, 90.0);
        assertTrue(result.getLat() > start.getLat());
    }

    @Test
    @DisplayName("Moving East should increase the longitude")
    void testNextPositionEast() {
        Position start = new Position(-3.192473, 55.946233);
        Position result = nextPositionService.nextPosition(start, 0.0);
        assertTrue(result.getLng() > start.getLng());
    }

    // isInsideRegion Tests
    @Test
    @DisplayName("Point inside a simple square polygon")
    void testPointInsidePolygon() {
        List<Position> square = List.of(
                new Position(0.0, 0.0),
                new Position(10.0, 0.0),
                new Position(10.0, 10.0),
                new Position(0.0, 10.0),
                new Position(0.0, 0.0) // closed polygon
        );

        Position point = new Position(5.0, 5.0);
        assertTrue(polygonService.pointInPolygon(point, square));
    }

    @Test
    @DisplayName("Point outside a simple square polygon")
    void testPointOutsidePolygon() {
        List<Position> square = List.of(
                new Position(0.0, 0.0),
                new Position(10.0, 0.0),
                new Position(10.0, 10.0),
                new Position(0.0, 10.0),
                new Position(0.0, 0.0)
        );

        Position point = new Position(15.0, 5.0);
        assertFalse(polygonService.pointInPolygon(point, square));
    }

    @Test
    @DisplayName("Point exactly on the edge of the polygon")
    void testPointOnEdge() {
        List<Position> square = List.of(
                new Position(0.0, 0.0),
                new Position(10.0, 0.0),
                new Position(10.0, 10.0),
                new Position(0.0, 10.0),
                new Position(0.0, 0.0)
        );

        Position point = new Position(10.0, 5.0);
        assertTrue(polygonService.pointInPolygon(point, square));
    }

    @Test
    @DisplayName("Point at a vertex of the polygon")
    void testPointOnVertex() {
        List<Position> square = List.of(
                new Position(0.0, 0.0),
                new Position(10.0, 0.0),
                new Position(10.0, 10.0),
                new Position(0.0, 10.0),
                new Position(0.0, 0.0)
        );

        Position point = new Position(0.0, 0.0);
        assertTrue(polygonService.pointInPolygon(point, square));
    }

    @Test
    @DisplayName("Invalid polygon (not closed) should return error")
    void testInvalidPolygon() {
        List<Position> openPolygon = List.of(
                new Position(0.0, 0.0),
                new Position(10.0, 0.0),
                new Position(10.0, 10.0),
                new Position(0.0, 10.0)
        );

        Position point = new Position(5.0, 5.0);

        assertThrows( RuntimeException.class, ()->polygonService.pointInPolygon(point, openPolygon));
    }
}
