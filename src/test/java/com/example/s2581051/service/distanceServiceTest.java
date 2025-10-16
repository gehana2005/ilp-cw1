package com.example.s2581051.service;

import com.example.s2581051.model.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class distanceServiceTest {

    private distanceService service;
    private Validator validator;

    @BeforeEach
    void setUp() {
        service = new distanceService();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // EUCLIDEAN DISTANCE TESTSs
    @Test
    @DisplayName("Euclidean distance with known 3-4-5 triangle")
    void testEuclideanDistanceKnownValues() {
        Position p1 = new Position(3.0, 0.0);
        Position p2 = new Position(0.0, 4.0);

        double result = service.euclideanDistance(p1, p2);
        assertEquals(5.0, result, 1e-9, "Distance should match expected Euclidean distance");
    }

    @Test
    @DisplayName("Distance between identical points should be zero")
    void testEuclideanDistanceWithSamePoints() {
        Position p = new Position(55.946233, -3.192473);
        double result = service.euclideanDistance(p, p);
        assertEquals(0.0, result, 1e-9, "Distance between identical points must be zero");
    }

    @Test
    @DisplayName("Distance with negative coordinates")
    void testEuclideanDistanceNegativeCoordinates() {
        Position p1 = new Position(-10.0, -10.0);
        Position p2 = new Position(-13.0, -14.0);

        double result = service.euclideanDistance(p1, p2);
        assertEquals(5.0, result, 1e-9, "Distance should handle negative coordinates correctly");
    }

    @Test
    @DisplayName("Distance calculation is symmetric")
    void testDistanceSymmetry() {
        Position p1 = new Position(55.946, -3.192);
        Position p2 = new Position(55.947, -3.190);

        double d1 = service.euclideanDistance(p1, p2);
        double d2 = service.euclideanDistance(p2, p1);

        assertEquals(d1, d2, 1e-9, "Distance must be symmetric");
    }

    @Test
    @DisplayName("Distance only along latitude axis")
    void testDistanceAlongLatitude() {
        Position p1 = new Position(0.0, 0.0);
        Position p2 = new Position(5.0, 0.0);

        double result = service.euclideanDistance(p1, p2);
        assertEquals(5.0, result, 1e-9, "Should calculate distance correctly along latitude axis");
    }

    @Test
    @DisplayName("Distance only along longitude axis")
    void testDistanceAlongLongitude() {
        Position p1 = new Position(0.0, 0.0);
        Position p2 = new Position(0.0, 5.0);

        double result = service.euclideanDistance(p1, p2);
        assertEquals(5.0, result, 1e-9, "Should calculate distance correctly along longitude axis");
    }

    @Test
    @DisplayName("Distance with very small coordinates")
    void testDistanceWithSmallCoordinates() {
        Position p1 = new Position(0.0001, 0.0001);
        Position p2 = new Position(0.0002, 0.0002);

        double result = service.euclideanDistance(p1, p2);
        double expected = Math.sqrt(0.0001 * 0.0001 + 0.0001 * 0.0001);
        assertEquals(expected, result, 1e-9, "Should handle very small coordinates");
    }

    @Test
    @DisplayName("Distance with mixed positive and negative coordinates")
    void testDistanceMixedCoordinates() {
        Position p1 = new Position(3.0, -4.0);
        Position p2 = new Position(-3.0, 4.0);

        double result = service.euclideanDistance(p1, p2);
        double expected = Math.sqrt(36 + 64); // sqrt(6^2 + 8^2) = 10
        assertEquals(expected, result, 1e-9, "Should handle mixed positive/negative coordinates");
    }

    @Test
    @DisplayName("Distance with decimal coordinates")
    void testDistanceWithDecimals() {
        Position p1 = new Position(1.5, 2.5);
        Position p2 = new Position(4.5, 6.5);

        double result = service.euclideanDistance(p1, p2);
        double expected = Math.sqrt(9 + 16); // sqrt(3^2 + 4^2) = 5
        assertEquals(expected, result, 1e-9, "Should handle decimal coordinates correctly");
    }

    // IS CLOSE TO TESTS

    @Test
    @DisplayName("Points within threshold should be considered close")
    void testIsCloseWithClosePoints() {
        Position p1 = new Position(55.946233, -3.192473);
        Position p2 = new Position(55.946250, -3.192470);
        assertTrue(service.isCloseTo(p1, p2), "Nearby points should be considered close");
    }

    @Test
    @DisplayName("Points beyond threshold should not be considered close")
    void testIsCloseWithFarPoints() {
        Position p1 = new Position(55.946233, -3.192473);
        Position p2 = new Position(55.940000, -3.192473);
        assertFalse(service.isCloseTo(p1, p2), "Distant points should not be considered close");
    }

    @Test
    @DisplayName("Identical points should be considered close")
    void testIsCloseWithIdenticalPoints() {
        Position p = new Position(55.946233, -3.192473);
        assertTrue(service.isCloseTo(p, p), "Identical points should be considered close");
    }

    @Test
    @DisplayName("Points exactly at threshold boundary")
    void testIsCloseAtThreshold() {
        Position p1 = new Position(0.0, 0.0);
        Position p2 = new Position(0.00015, 0.0);

        // Distance is exactly 0.00015, which should NOT be close (< threshold)
        assertFalse(service.isCloseTo(p1, p2), "Points at threshold should not be close");
    }

    @Test
    @DisplayName("Points just below threshold")
    void testIsCloseJustBelowThreshold() {
        Position p1 = new Position(0.0, 0.0);
        Position p2 = new Position(0.00014, 0.0);

        assertTrue(service.isCloseTo(p1, p2), "Points just below threshold should be close");
    }

    @Test
    @DisplayName("Points just above threshold")
    void testIsCloseJustAboveThreshold() {
        Position p1 = new Position(0.0, 0.0);
        Position p2 = new Position(0.00016, 0.0);

        assertFalse(service.isCloseTo(p1, p2), "Points just above threshold should not be close");
    }

    @Test
    @DisplayName("isCloseTo is symmetric")
    void testIsCloseSymmetry() {
        Position p1 = new Position(55.946233, -3.192473);
        Position p2 = new Position(55.946240, -3.192475);

        assertEquals(service.isCloseTo(p1, p2), service.isCloseTo(p2, p1),
                "isCloseTo should be symmetric");
    }

    @Test
    @DisplayName("Very close points with negative coordinates")
    void testIsCloseNegativeCoordinates() {
        Position p1 = new Position(-55.946233, -3.192473);
        Position p2 = new Position(-55.946235, -3.192474);

        assertTrue(service.isCloseTo(p1, p2), "Should handle negative coordinates in isCloseTo");
    }

    @Test
    @DisplayName("Diagonal distance within threshold")
    void testIsCloseDiagonal() {
        Position p1 = new Position(0.0, 0.0);
        // Create point at diagonal distance just under threshold
        double offset = 0.00015 / Math.sqrt(2) - 0.00001;
        Position p2 = new Position(offset, offset);

        assertTrue(service.isCloseTo(p1, p2), "Diagonal distance within threshold should be close");
    }

    @Test
    @DisplayName("Points at origin")
    void testDistanceAtOrigin() {
        Position origin = new Position(0.0, 0.0);
        Position p = new Position(3.0, 4.0);

        double result = service.euclideanDistance(origin, p);
        assertEquals(5.0, result, 1e-9, "Should calculate distance from origin correctly");
    }

    // EDGE CASES

    @Test
    @DisplayName("Distance with very large coordinates")
    void testDistanceWithLargeCoordinates() {
        Position p1 = new Position(1000.0, 2000.0);
        Position p2 = new Position(1003.0, 2004.0);

        double result = service.euclideanDistance(p1, p2);
        assertEquals(5.0, result, 1e-9, "Should handle large coordinates");
    }

    @Test
    @DisplayName("Distance across prime meridian")
    void testDistanceAcrossMeridian() {
        Position p1 = new Position(51.5, -0.1);
        Position p2 = new Position(51.5, 0.1);

        double result = service.euclideanDistance(p1, p2);
        assertEquals(0.2, result, 1e-9, "Should calculate distance across prime meridian");
    }

    @Test
    @DisplayName("Distance across equator")
    void testDistanceAcrossEquator() {
        Position p1 = new Position(-0.1, 0.0);
        Position p2 = new Position(0.1, 0.0);

        double result = service.euclideanDistance(p1, p2);
        assertEquals(0.2, result, 1e-9, "Should calculate distance across equator");
    }

    @Test
    @DisplayName("Distance with coordinates at valid boundary limits")
    void testDistanceAtValidBoundaries() {
        Position p1 = new Position(90.0, 180.0);
        Position p2 = new Position(-90.0, -180.0);

        double result = service.euclideanDistance(p1, p2);
        double expected = Math.sqrt(180.0 * 180.0 + 360.0 * 360.0);
        assertEquals(expected, result, 1e-9, "Should handle valid boundary coordinates");
    }

    @Test
    @DisplayName("Distance calculation precision with many decimal places")
    void testDistancePrecision() {
        Position p1 = new Position(55.946233123, -3.192473456);
        Position p2 = new Position(55.946234789, -3.192474123);

        boolean result = service.isCloseTo(p1, p2);
        assertTrue(result, "Should calculate very small distances with precision");
        assertTrue(result , "Result should be appropriately small");
    }

    // VALIDATION TESTS

    @Test
    @DisplayName("Position validation: null latitude should fail")
    void testNullLatitudeValidation() {
        Position p = new Position(null, 10.0);
        Set<ConstraintViolation<Position>> violations = validator.validate(p);
        assertFalse(violations.isEmpty(), "Null latitude should cause validation error");
    }

    @Test
    @DisplayName("Position validation: null longitude should fail")
    void testNullLongitudeValidation() {
        Position p = new Position(10.0, null);
        Set<ConstraintViolation<Position>> violations = validator.validate(p);
        assertFalse(violations.isEmpty(), "Null longitude should cause validation error");
    }

    @Test
    @DisplayName("Position validation: latitude below -90 should fail")
    void testLatitudeBelowMinValidation() {
        Position p = new Position(0.0, -91.0);
        Set<ConstraintViolation<Position>> violations = validator.validate(p);
        assertFalse(violations.isEmpty(), "Latitude below -90 should cause validation error");
    }

    @Test
    @DisplayName("Position validation: latitude above 90 should fail")
    void testLatitudeAboveMaxValidation() {
        Position p = new Position(90.0, 91.0);
        Set<ConstraintViolation<Position>> violations = validator.validate(p);
        assertFalse(violations.isEmpty(), "Latitude above 90 should cause validation error");
    }

    @Test
    @DisplayName("Position validation: longitude below -180 should fail")
    void testLongitudeBelowMinValidation() {
        Position p = new Position( -181.0,0.0);
        Set<ConstraintViolation<Position>> violations = validator.validate(p);
        assertFalse(violations.isEmpty(), "Longitude below -180 should cause validation error");
    }

    @Test
    @DisplayName("Position validation: longitude above 180 should fail")
    void testLongitudeAboveMaxValidation() {
        Position p = new Position(181.0,0.0);
        Set<ConstraintViolation<Position>> violations = validator.validate(p);
        assertFalse(violations.isEmpty(), "Longitude above 180 should cause validation error");
    }

    @Test
    @DisplayName("Position validation: valid position at boundaries should pass")
    void testValidBoundaryPosition() {
        Position p1 = new Position(180.0, 90.0);
        Position p2 = new Position(-180.0, -90.0);

        Set<ConstraintViolation<Position>> violations1 = validator.validate(p1);
        Set<ConstraintViolation<Position>> violations2 = validator.validate(p2);

        assertTrue(violations1.isEmpty(), "Valid max boundary position should pass validation");
        assertTrue(violations2.isEmpty(), "Valid min boundary position should pass validation");
    }

    @Test
    @DisplayName("Position validation: valid position should pass")
    void testValidPositionValidation() {
        Position p = new Position(55.946233, -3.192473);
        Set<ConstraintViolation<Position>> violations = validator.validate(p);
        assertTrue(violations.isEmpty(), "Valid position should pass validation");
    }

    @Test
    @DisplayName("Position validation: latitude exactly at -90 should pass")
    void testLatitudeAtMinBoundary() {
        Position p = new Position(0.0,-90.0);
        Set<ConstraintViolation<Position>> violations = validator.validate(p);
        assertTrue(violations.isEmpty(), "Latitude exactly at -90 should pass validation");
    }

    @Test
    @DisplayName("Position validation: latitude exactly at 90 should pass")
    void testLatitudeAtMaxBoundary() {
        Position p = new Position(0.0, 90.0);
        Set<ConstraintViolation<Position>> violations = validator.validate(p);
        assertTrue(violations.isEmpty(), "Latitude exactly at 90 should pass validation");
    }

    @Test
    @DisplayName("Position validation: longitude exactly at -180 should pass")
    void testLongitudeAtMinBoundary() {
        Position p = new Position(-180.0, 0.0);
        Set<ConstraintViolation<Position>> violations = validator.validate(p);
        assertTrue(violations.isEmpty(), "Longitude exactly at -180 should pass validation");
    }

    @Test
    @DisplayName("Position validation: longitude exactly at 180 should pass")
    void testLongitudeAtMaxBoundary() {
        Position p = new Position(180.0, 0.0);
        Set<ConstraintViolation<Position>> violations = validator.validate(p);
        assertTrue(violations.isEmpty(), "Longitude exactly at 180 should pass validation");
    }
}