package com.example.s2581051.service;

import com.example.s2581051.model.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class polygonServiceTest {

    private polygonService service;

    @BeforeEach
    void setUp() {
        service = new polygonService();
    }

    // POINT IN POLYGON TESTS - BASIC CASES

    @Test
    @DisplayName("Point clearly inside a square polygon")
    void testPointInsideSquare() {
        List<Position> square = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)  // Closed polygon
        );
        Position inside = new Position(5.0, 5.0);

        assertTrue(service.pointInPolygon(inside, square),
                "Point in center of square should be inside");
    }

    @Test
    @DisplayName("Point clearly outside a square polygon")
    void testPointOutsideSquare() {
        List<Position> square = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );
        Position outside = new Position(15.0, 15.0);

        assertFalse(service.pointInPolygon(outside, square),
                "Point far outside should not be inside polygon");
    }

    @Test
    @DisplayName("Point on vertex of polygon")
    void testPointOnVertex() {
        List<Position> square = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );
        Position onVertex = new Position(0.0, 0.0);

        assertTrue(service.pointInPolygon(onVertex, square),
                "Point on vertex should be considered inside");
    }

    @Test
    @DisplayName("Point on edge of polygon")
    void testPointOnEdge() {
        List<Position> square = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );
        Position onEdge = new Position(5.0, 0.0);

        assertTrue(service.pointInPolygon(onEdge, square),
                "Point on edge should be considered inside");
    }

    @Test
    @DisplayName("Point on edge midpoint")
    void testPointOnEdgeMidpoint() {
        List<Position> square = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );
        Position onEdge = new Position(0.0, 5.0);

        assertTrue(service.pointInPolygon(onEdge, square),
                "Point on edge midpoint should be inside");
    }

    // TRIANGLE TESTS

    @Test
    @DisplayName("Point inside a triangle")
    void testPointInsideTriangle() {
        List<Position> triangle = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(10.0, 0.0),
                new Position(5.0, 10.0),
                new Position(0.0, 0.0)
        );
        Position inside = new Position(5.0, 3.0);

        assertTrue(service.pointInPolygon(inside, triangle),
                "Point inside triangle should be detected");
    }

    @Test
    @DisplayName("Point outside a triangle")
    void testPointOutsideTriangle() {
        List<Position> triangle = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(10.0, 0.0),
                new Position(5.0, 10.0),
                new Position(0.0, 0.0)
        );
        Position outside = new Position(15.0, 5.0);

        assertFalse(service.pointInPolygon(outside, triangle),
                "Point outside triangle should not be inside");
    }

    // COMPLEX POLYGON TESTS

    @Test
    @DisplayName("Point inside a pentagon")
    void testPointInsidePentagon() {
        List<Position> pentagon = Arrays.asList(
                new Position(5.0, 0.0),
                new Position(10.0, 3.5),
                new Position(8.0, 9.5),
                new Position(2.0, 9.5),
                new Position(0.0, 3.5),
                new Position(5.0, 0.0)
        );
        Position inside = new Position(5.0, 5.0);

        assertTrue(service.pointInPolygon(inside, pentagon),
                "Point inside pentagon should be detected");
    }

    @Test
    @DisplayName("Point outside a pentagon")
    void testPointOutsidePentagon() {
        List<Position> pentagon = Arrays.asList(
                new Position(5.0, 0.0),
                new Position(10.0, 3.5),
                new Position(8.0, 9.5),
                new Position(2.0, 9.5),
                new Position(0.0, 3.5),
                new Position(5.0, 0.0)
        );
        Position outside = new Position(0.0, 0.0);

        assertFalse(service.pointInPolygon(outside, pentagon),
                "Point outside pentagon should not be inside");
    }

    @Test
    @DisplayName("Point inside a hexagon")
    void testPointInsideHexagon() {
        List<Position> hexagon = Arrays.asList(
                new Position(2.0, 0.0),
                new Position(4.0, 0.0),
                new Position(5.0, 2.0),
                new Position(4.0, 4.0),
                new Position(2.0, 4.0),
                new Position(1.0, 2.0),
                new Position(2.0, 0.0)
        );
        Position inside = new Position(3.0, 2.0);

        assertTrue(service.pointInPolygon(inside, hexagon),
                "Point inside hexagon should be detected");
    }

    // CONCAVE POLYGON TESTS

    @Test
    @DisplayName("Point inside concave polygon")
    void testPointInsideConcavePolygon() {
        // L-shaped polygon
        List<Position> lShape = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(5.0, 10.0),
                new Position(5.0, 5.0),
                new Position(10.0, 5.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );
        Position inside = new Position(2.0, 2.0);

        assertTrue(service.pointInPolygon(inside, lShape),
                "Point inside concave polygon should be detected");
    }

    @Test
    @DisplayName("Point in concave indentation should be outside")
    void testPointInConcaveIndentation() {
        // L-shaped polygon - point in the "missing corner"
        List<Position> lShape = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(5.0, 10.0),
                new Position(5.0, 5.0),
                new Position(10.0, 5.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );
        Position inIndentation = new Position(7.0, 7.0);

        assertFalse(service.pointInPolygon(inIndentation, lShape),
                "Point in concave indentation should be outside");
    }

    // EDGE CASES WITH COORDINATES

    @Test
    @DisplayName("Point inside polygon with negative coordinates")
    void testPointInPolygonNegativeCoordinates() {
        List<Position> square = Arrays.asList(
                new Position(-10.0, -10.0),
                new Position(-10.0, 0.0),
                new Position(0.0, 0.0),
                new Position(0.0, -10.0),
                new Position(-10.0, -10.0)
        );
        Position inside = new Position(-5.0, -5.0);

        assertTrue(service.pointInPolygon(inside, square),
                "Should handle negative coordinates correctly");
    }

    @Test
    @DisplayName("Point inside polygon crossing origin")
    void testPointInPolygonCrossingOrigin() {
        List<Position> square = Arrays.asList(
                new Position(-5.0, -5.0),
                new Position(-5.0, 5.0),
                new Position(5.0, 5.0),
                new Position(5.0, -5.0),
                new Position(-5.0, -5.0)
        );
        Position atOrigin = new Position(0.0, 0.0);

        assertTrue(service.pointInPolygon(atOrigin, square),
                "Point at origin inside polygon should be detected");
    }

    @Test
    @DisplayName("Point with decimal coordinates inside polygon")
    void testPointWithDecimalCoordinates() {
        List<Position> square = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );
        Position inside = new Position(5.5, 5.5);

        assertTrue(service.pointInPolygon(inside, square),
                "Should handle decimal coordinates correctly");
    }

    // BOUNDARY TESTS

    @Test
    @DisplayName("Point just inside polygon boundary")
    void testPointJustInsideBoundary() {
        List<Position> square = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );
        Position justInside = new Position(0.001, 0.001);

        assertTrue(service.pointInPolygon(justInside, square),
                "Point just inside boundary should be inside");
    }

    @Test
    @DisplayName("Point just outside polygon boundary")
    void testPointJustOutsideBoundary() {
        List<Position> square = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );
        Position justOutside = new Position(-0.001, 0.0);

        assertFalse(service.pointInPolygon(justOutside, square),
                "Point just outside boundary should be outside");
    }

    // REAL-WORLD GEOGRAPHIC COORDINATES

    @Test
    @DisplayName("Point inside Edinburgh city center region")
    void testEdinburghCityCenter() {
        // Approximate Edinburgh city center boundary
        List<Position> edinburgh = Arrays.asList(
                new Position(55.945, -3.200),
                new Position(55.945, -3.185),
                new Position(55.955, -3.185),
                new Position(55.955, -3.200),
                new Position(55.945, -3.200)
        );
        Position royalMile = new Position(55.950, -3.192);

        assertTrue(service.pointInPolygon(royalMile, edinburgh),
                "Royal Mile should be inside Edinburgh city center");
    }

    @Test
    @DisplayName("Point outside Edinburgh city center region")
    void testOutsideEdinburghCityCenter() {
        List<Position> edinburgh = Arrays.asList(
                new Position(55.945, -3.200),
                new Position(55.945, -3.185),
                new Position(55.955, -3.185),
                new Position(55.955, -3.200),
                new Position(55.945, -3.200)
        );
        Position leith = new Position(55.976, -3.176);

        assertFalse(service.pointInPolygon(leith, edinburgh),
                "Leith should be outside city center boundary");
    }

    // IS CLOSED POLYGON TESTS

    @Test
    @DisplayName("Valid closed polygon with 4 vertices")
    void testIsClosedPolygonValid() {
        List<Position> square = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );

        assertTrue(service.isClosedPolygon(square),
                "Valid closed square should be recognized");
    }

    @Test
    @DisplayName("Polygon with only 3 vertices should be invalid")
    void testPolygonTooFewVertices() {
        List<Position> line = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(10.0, 10.0),
                new Position(0.0, 0.0)
        );

        assertFalse(service.isClosedPolygon(line),
                "Polygon with only 3 vertices should be invalid");
    }

    @Test
    @DisplayName("Polygon that is not closed should be invalid")
    void testPolygonNotClosed() {
        List<Position> notClosed = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0)
                // Missing closing vertex
        );

        assertFalse(service.isClosedPolygon(notClosed),
                "Polygon that doesn't close should be invalid");
    }

    @Test
    @DisplayName("Empty polygon list should be invalid")
    void testEmptyPolygon() {
        List<Position> empty = new ArrayList<>();

        assertFalse(service.isClosedPolygon(empty),
                "Empty polygon should be invalid");
    }

    @Test
    @DisplayName("Polygon with almost closed coordinates should be invalid")
    void testAlmostClosedPolygon() {
        List<Position> almostClosed = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.001, 0.0)  // Not exactly closed
        );

        assertFalse(service.isClosedPolygon(almostClosed),
                "Polygon that is almost but not exactly closed should be invalid");
    }

    @Test
    @DisplayName("Closed triangle (4 vertices) should be valid")
    void testClosedTriangle() {
        List<Position> triangle = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(10.0, 0.0),
                new Position(5.0, 10.0),
                new Position(0.0, 0.0)
        );

        assertTrue(service.isClosedPolygon(triangle),
                "Closed triangle should be valid");
    }

    @Test
    @DisplayName("Closed polygon with many vertices should be valid")
    void testClosedPolygonManyVertices() {
        List<Position> octagon = Arrays.asList(
                new Position(3.0, 0.0),
                new Position(7.0, 0.0),
                new Position(10.0, 3.0),
                new Position(10.0, 7.0),
                new Position(7.0, 10.0),
                new Position(3.0, 10.0),
                new Position(0.0, 7.0),
                new Position(0.0, 3.0),
                new Position(3.0, 0.0)
        );

        assertTrue(service.isClosedPolygon(octagon),
                "Closed octagon should be valid");
    }

    // EXCEPTION TESTS

    @Test
    @DisplayName("Invalid polygon should throw exception")
    void testInvalidPolygonThrowsException() {
        List<Position> invalid = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(10.0, 10.0)
                // Only 2 vertices, not closed
        );
        Position point = new Position(5.0, 5.0);

        assertThrows(IllegalArgumentException.class,
                () -> service.pointInPolygon(point, invalid),
                "Invalid polygon should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Unclosed polygon should throw exception")
    void testUnclosedPolygonThrowsException() {
        List<Position> unclosed = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0)
                // Not closed
        );
        Position point = new Position(5.0, 5.0);

        assertThrows(IllegalArgumentException.class,
                () -> service.pointInPolygon(point, unclosed),
                "Unclosed polygon should throw IllegalArgumentException");
    }

    // SPECIAL GEOMETRIC CASES

    @Test
    @DisplayName("Point on horizontal edge")
    void testPointOnHorizontalEdge() {
        List<Position> square = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );
        Position onHorizontal = new Position(5.0, 10.0);

        assertTrue(service.pointInPolygon(onHorizontal, square),
                "Point on horizontal edge should be inside");
    }

    @Test
    @DisplayName("Point on vertical edge")
    void testPointOnVerticalEdge() {
        List<Position> square = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(0.0, 10.0),
                new Position(10.0, 10.0),
                new Position(10.0, 0.0),
                new Position(0.0, 0.0)
        );
        Position onVertical = new Position(10.0, 5.0);

        assertTrue(service.pointInPolygon(onVertical, square),
                "Point on vertical edge should be inside");
    }

    @Test
    @DisplayName("Point on diagonal edge of triangle")
    void testPointOnDiagonalEdge() {
        List<Position> triangle = Arrays.asList(
                new Position(0.0, 0.0),
                new Position(10.0, 0.0),
                new Position(5.0, 10.0),
                new Position(0.0, 0.0)
        );
        Position onDiagonal = new Position(2.5, 5.0);

        assertTrue(service.pointInPolygon(onDiagonal, triangle),
                "Point on diagonal edge should be inside");
    }
}