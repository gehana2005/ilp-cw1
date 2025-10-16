package com.example.s2581051.service;

import com.example.s2581051.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
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


    @Test
    @DisplayName("POST /api/v1/distanceTo -> should return 400 for invalid coordinates")
    void testDistanceToInvalidCoordinates() throws Exception {
        Position p1 = new Position(1000.0, 2000.0); // invalid
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

    // isCloseTo TESTs

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
        Position p1 = new Position(-3.192473,55.946233);
        Position p2 = new Position(-3.192472,55.946234);
        DistanceRequest request = new DistanceRequest(p1, p2);

        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect((content().string("true")));
    }

    // NEXT POSITION TESTs

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
    @DisplayName("POST /api/v1/nextPosition -> should return 200 for valid input")
    void testNextPositionValidCoordinates() throws Exception {
        Position start = new Position( -3.192473, 55.946233);

        PositionRequest request = new PositionRequest(start, 45.0);

        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lng", closeTo(-3.192366933982822, 1e-12)))
                .andExpect(jsonPath("$.lat", closeTo(55.946339066017174, 1e-12)));
    }

    // REGION TESTs
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
        Position point = new Position(55.946233, -3.192473);
        String name = "test";
        Region region = new Region(name, List.of(
                new Position(55.946000, -3.192800),
                new Position(55.946000, -3.192000),
                new Position(55.946500, -3.192000),
                new Position(55.946500, -3.192800),
                new Position(55.946000, -3.192800)
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
        Position point = new Position(0.5,1.0);
        String name = "small";
        Region region = new Region(name, List.of(
                new Position(1.0,0.0),
                new Position(0.0,0.0),
                new Position(0.0,1.0),
                new Position(1.0,1.0),
                new Position(1.0,0.0)
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
        Position point = new Position(0.0,0.0);
        String name = "small";
        Region region = new Region(name, List.of(
                new Position(1.0,0.0),
                new Position(0.0,0.0),
                new Position(0.0,1.0),
                new Position(1.0,1.0),
                new Position(1.0,0.0)
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
    void testIsInRegionOpen() throws Exception {
        Position point = new Position(0.0,0.0);
        String name = "small";
        Region region = new Region(name, List.of(
                new Position(1.0,0.0),
                new Position(0.0,0.0),
                new Position(0.0,1.0),
                new Position(0.5,0.5)
        ));

        RegionRequest request = new RegionRequest(point, region);
        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());
    }


}
