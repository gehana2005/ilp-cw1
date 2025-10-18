package com.example.s2581051.service;

import com.example.s2581051.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.closeTo;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class controllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // health actuator
    @Test
    @DisplayName("GET /actuator/health -> should return 200")
    void testHealthActuator() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    // uid
    @Test
    @DisplayName("GET /api/v1/uid -> should return 200 and the student id")
    void testUID() throws Exception {
        mockMvc.perform(get("/api/v1/uid"))
                .andExpect(status().isOk())
                .andExpect(content().string("s2581051"));
    }

    // distanceTo tests
    @Test
    @DisplayName("POST /api/v1/distanceTo -> should return 400 for invalid coordinates")
    void testDistanceToInvalidCoordinates() throws Exception {
        Position p1 = new Position(181.0, 2000.0); // invalid coordinates
        Position p2 = new Position(1003.0, 2004.0);
        DistanceRequest request = new DistanceRequest(p1, p2);

        mockMvc.perform(post("/api/v1/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/distanceTo -> should return 400 for null values")
    void testDistanceToNullValues() throws Exception {
        DistanceRequest request = new DistanceRequest();

        mockMvc.perform(post("/api/v1/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/distanceTo -> should return 400 for bad json")
    void testDistanceToBadJson() throws Exception {

        String badJson = """
          {
          "position1": {
          "lng": -3.192473,
          "lat": 55.946233
          },
          "position2": {
          "lng": -3.192473,
          "lat": 55.942617
          }""";

        mockMvc.perform(post("/api/v1/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badJson)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("POST /api/v1/distanceTo -> should return 200 for correct precision digits")
    void testDistanceToPrecision() throws Exception {
        Position p1 = new Position(-3.192473, 55.946233);
        Position p2 = new Position(-3.192473, 55.942617);
        DistanceRequest request = new DistanceRequest(p1, p2);

        mockMvc.perform(post("/api/v1/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("0.003616000000000952"));
    }

    @Test
    @DisplayName("POST /api/v1/distanceTo -> should return 200 for valid coordinates")
    void testDistanceToValidCoordinates() throws Exception {
        Position p1 = new Position(10.0, 20.0);
        Position p2 = new Position(13.0, 24.0);
        DistanceRequest request = new DistanceRequest(p1, p2);

        mockMvc.perform(post("/api/v1/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("5.0"));
    }

    // isCloseTo Tests
    @Test
    @DisplayName("POST /api/v1/isCloseTo -> should return 400 for invalid coordinates")
    void testIsCloseToInvalidCoordinates() throws Exception {
        Position p1 = new Position(500.0, 500.0);
        Position p2 = new Position(600.0, 600.0);
        DistanceRequest request = new DistanceRequest(p1, p2);

        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/isCloseTo -> should return 200 for valid coordinates")
    void testIsCloseToValidCoordinates() throws Exception {
        Position p1 = new Position(-3.192473, 55.946233);
        Position p2 = new Position(-3.192472, 55.946234);
        DistanceRequest request = new DistanceRequest(p1, p2);

        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect((content().string("true")));
    }

    @Test
    @DisplayName("POST /api/v1/isCloseTo -> should return 200 (true) for identical points")
    void testIsCloseToIdentical() throws Exception {
        Position p = new Position(-3.192473, 55.946233);
        DistanceRequest request = new DistanceRequest(p, p);

        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /api/v1/isCloseTo -> should return 200 (false) for points exactly at threshold distance")
    void testIsCloseToThresholdBoundary() throws Exception {
        Position p1 = new Position(0.0, 0.0);
        Position p2 = new Position(0.00015, 0.0);
        DistanceRequest request = new DistanceRequest(p1, p2);

        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("POST /api/v1/isCloseTo -> should return 200 (true) for points just below threshold distance")
    void testIsCloseToJustBelowThreshold() throws Exception {
        Position p1 = new Position(0.0, 0.0);
        Position p2 = new Position(0.000149999, 0.0);
        DistanceRequest request = new DistanceRequest(p1, p2);

        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /api/v1/isCloseTo -> should handle points across the equator correctly")
    void testIsCloseToAcrossEquator() throws Exception {
        Position p1 = new Position(-0.00007, 0.0);
        Position p2 = new Position(0.00007, 0.0);
        DistanceRequest request = new DistanceRequest(p1, p2);

        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /api/v1/isCloseTo -> should return 400 for bad JSON format")
    void testIsCloseToBadJson() throws Exception {
        String badJson = """
                {
                "position1": {
                "lng": -3.192473,
                "lat": 55.946233
                },
                "position2": {
                "lng": -3.192473,
                "lat": 55.942617
                }""";

        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }


    // nextPosition Tests

    @Test
    @DisplayName("POST /api/v1/nextPosition -> should return 400 for invalid start position")
    void testNextPositionInvalidCoordinates() throws Exception {
        Position start = new Position(200.0, 200.0);
        PositionRequest request = new PositionRequest(start, 45.0);

        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/nextPosition -> should return 400 for null start position")
    void testNextPositionNullStart() throws Exception {
        Double angle = 45.0;
        PositionRequest request = new PositionRequest(null, angle);

        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/nextPosition -> should return 400 for null angle")
    void testNextPositionNullAngle() throws Exception {
        Position start = new Position(-3.192472, 55.946234);
        PositionRequest request = new PositionRequest(start, null);

        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/nextPosition -> should return 400 for syntactically invalid JSON")
    void testNextPositionBadJson() throws Exception {

        String badJson = """ 
                {"start": { "lng": -3.192473, "lat": 55.946233 },
                "angle": 45.0""";

        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/nextPosition -> should handle negative angles correctly")
    void testNextPositionNegativeAngle() throws Exception {
        Position start = new Position(0.0, 0.0);
        PositionRequest request = new PositionRequest(start, -90.0);

        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lng", closeTo(0.0, 1e-12)))
                .andExpect(jsonPath("$.lat", closeTo(-0.00015, 1e-12)));
    }

    @Test
    @DisplayName("POST /api/v1/nextPosition -> should handle very small angles correctly")
    void testNextPositionTinyAngle() throws Exception {
        Position start = new Position(-3.192473, 55.946233);
        PositionRequest request = new PositionRequest(start, 0.000001);

        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("POST /api/v1/nextPosition -> should return 200 for valid input")
    void testNextPositionValidCoordinates() throws Exception {
        Position start = new Position(-3.192473, 55.946233);

        PositionRequest request = new PositionRequest(start, 45.0);

        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lng", closeTo(-3.192366933982822, 1e-12)))
                .andExpect(jsonPath("$.lat", closeTo(55.946339066017174, 1e-12)));
    }

    // isInRegion Tests

    @Test
    @DisplayName("POST /api/v1/isInRegion -> should return 400 for null values in region")
    void testIsInRegionNullValuesRegion() throws Exception {
        Position point = new Position(10.0, 10.0);
        String name = "test";

        Region region = new Region(name, List.of(
                new Position(null, 55.946000),
                new Position(-3.192000, 55.946000),
                new Position(-3.192000, 55.946500),
                new Position(-3.192800, 55.946500),
                new Position(-3.192800, null)
        ));

        RegionRequest request = new RegionRequest(point, region);

        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/isInRegion -> should return 400 for null point")
    void testIsInRegionNullPoint() throws Exception {
        Position point = null;
        String name = "test";

        Region region = new Region(name, List.of(
                new Position(-3.192800, 55.946000),
                new Position(-3.192000, 55.946000),
                new Position(-3.192000, 55.946500),
                new Position(-3.192800, 55.946500),
                new Position(-3.192800, 55.946000)
        ));

        RegionRequest request = new RegionRequest(point, region);

        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/isInRegion -> should return 400 for bad json")
    void testIsInRegionBadJson() throws Exception {
        String BadJson = """
                {
                "position": {
                "lng": 1.234,
                "lat": 1.222 },
                "region": {
                "name": "central",
                "vertices": [
                {
                "lng": -3.192473,
                "lat": 55.946233},
                {
                "lng": -3.192473,
                "lat": 55.942617
                },
                {
                "lng": -3.184319,
                "lat": 55.942617
                },
                {
                "lng": -3.184319,
                "lat": 55.946233
                },
                {
                "lng": -3.192473,
                "lat": 55.946233
                }
                ]
                }""";


        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BadJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("POST /api/v1/isInRegion -> should return 400 for invalid vertex coordinates")
    void testIsInRegionInvalidCoordinates() throws Exception {
        Position point = new Position(10.0, 10.0);
        String name = "test";

        Position p1 = new Position(1000.0, 1000.0);
        Position p2 = new Position(1010.0, 1000.0);
        Position p3 = new Position(1010.0, 1010.0);
        Position p4 = new Position(1000.0, 1010.0);
        Region region = new Region(name, List.of(p1, p2, p3, p4));
        RegionRequest request = new RegionRequest(point, region);

        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/isInRegion -> should return 200 for valid region and point")
    void testIsInRegionValidCoordinates() throws Exception {
        Position point = new Position(-3.192473, 55.946233);
        String name = "test";
        Region region = new Region(name, List.of(
                new Position(-3.192800, 55.946000),
                new Position(-3.192000, 55.946000),
                new Position(-3.192000, 55.946500),
                new Position(-3.192800, 55.946500),
                new Position(-3.192800, 55.946000)
        ));

        RegionRequest request = new RegionRequest(point, region);

        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /api/v1/isInRegion -> should return 200 for point on boundary")
    void testIsInRegionOnBoundary() throws Exception {
        Position point = new Position(0.5, 1.0);
        String name = "small";
        Region region = new Region(name, List.of(
                new Position(1.0, 0.0),
                new Position(0.0, 0.0),
                new Position(0.0, 1.0),
                new Position(1.0, 1.0),
                new Position(1.0, 0.0)
        ));

        RegionRequest request = new RegionRequest(point, region);
        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

    }

    @Test
    @DisplayName("POST /api/v1/isInRegion -> should return 200 for point on vertex")
    void testIsInRegionOnVertex() throws Exception {
        Position point = new Position(0.0, 0.0);
        String name = "small";
        Region region = new Region(name, List.of(
                new Position(1.0, 0.0),
                new Position(0.0, 0.0),
                new Position(0.0, 1.0),
                new Position(1.0, 1.0),
                new Position(1.0, 0.0)
        ));

        RegionRequest request = new RegionRequest(point, region);
        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

    }

    @Test
    @DisplayName("POST /api/v1/isInRegion -> should return 400 for open polygon")
    void testIsInRegionOpen() throws Exception {
        Position point = new Position(0.0, 0.0);
        String name = "open";
        Region region = new Region(name, List.of(
                new Position(1.0, 0.0),
                new Position(0.0, 0.0),
                new Position(0.0, 1.0),
                new Position(0.5, 0.5)
        ));

        RegionRequest request = new RegionRequest(point, region);
        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
