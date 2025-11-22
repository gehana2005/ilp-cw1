package com.example.s2581051.service;

import com.example.s2581051.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class calcDeliveryPathAsGeoJsonService {

    private String ilpEndpoint;
    private RestTemplate restTemplate = new RestTemplate();

    private pathFindingService pathFindingService;
    private calcDeliveryPathService calcDeliveryPathService;
    private aStarNavigationService aStarNavigationService;
    private availableDronesService availableDronesService;

    public calcDeliveryPathAsGeoJsonService(String ilpEndpoint, calcDeliveryPathService calcDeliveryPathService,
                                            pathFindingService pathFindingService,
                                            aStarNavigationService aStarNavigationService,
                                            availableDronesService availableDronesService) {
        this.ilpEndpoint = ilpEndpoint;
        this.calcDeliveryPathService = calcDeliveryPathService;
        this.aStarNavigationService = aStarNavigationService;
        this.availableDronesService = availableDronesService;
        this.pathFindingService = pathFindingService;
    }

    public Map<String, Object> calcDeliveryPathAsGeoJson(List<MedDispatchRec> records) {

        if (records == null || records.isEmpty()) {
            return emptyGeoJson();
        }

        // Sort deliveries by date and time
        List<MedDispatchRec> sorted = calcDeliveryPathService.sortDeliveriesByDate(records);

        if (calcDeliveryPathService.hasRestrictedDelivery(sorted)) {
            return emptyGeoJson();
        }

        // Choose drone
        List<String> availableIds = availableDronesService.getAvailableDrones(sorted);
        if (availableIds.isEmpty()) return emptyGeoJson();

        Drone[] drones = restTemplate.getForObject(ilpEndpoint + "/drones", Drone[].class);
        if (drones == null) return emptyGeoJson();

        Drone chosen = null;
        for (Drone d : drones) {
            if (availableIds.contains(d.getId())) {
                chosen = d;
                break;
            }
        }
        if (chosen == null) return emptyGeoJson();

        // Find the service point for this drone
        ServicePoint sp = calcDeliveryPathService.findServicePointForDrone(chosen);
        if (sp == null) return emptyGeoJson();

        Position servicePointPos = sp.getLocation();

        // Get the high-level path sequence (service point -> deliveries)
        Map<Position, Double> seq = pathFindingService.findPath(servicePointPos, sorted);
        List<Position> nodes = new ArrayList<>(seq.keySet());

        if (nodes.size() < 2) return emptyGeoJson();

        // Build the full route with A* navigation
        List<Position> fullRoute = buildFullRoute(nodes, sorted, servicePointPos);

        return makeGeoJson(fullRoute);
    }

    private List<Position> buildFullRoute(List<Position> nodes, List<MedDispatchRec> deliveries, Position servicePointPos) {

        List<Position> fullRoute = new ArrayList<>();
        int deliveryIndex = 0;

        // Navigate through each segment
        for (int i = 0; i < nodes.size() - 1; i++) {
            Position start = nodes.get(i);
            Position goal = nodes.get(i + 1);

            // Get A* path for this segment
            AstarPath aPath = aStarNavigationService.findPath(start, goal);
            List<Position> segmentPath = aPath.getPath();

            if (segmentPath.isEmpty()) continue;

            // Add segment to route
            if (i == 0) {
                // First segment: add all positions
                fullRoute.addAll(segmentPath);
            } else {
                // Subsequent segments: skip first position to avoid duplicate
                fullRoute.addAll(segmentPath.subList(1, segmentPath.size()));
            }

            // Check if this goal is a delivery point (not the service point)
            boolean isDelivery = deliveryIndex < deliveries.size();

            if (isDelivery) {
                // Add hover at delivery point (repeat position twice)
                fullRoute.add(goal);
                fullRoute.add(goal);
                deliveryIndex++;
            }
        }

        // After all deliveries, return to service point
        if (!fullRoute.isEmpty()) {
            Position lastPos = fullRoute.get(fullRoute.size() - 1);

            // Only add return path if we're not already at the service point
            if (!positionsEqual(lastPos, servicePointPos)) {
                AstarPath returnPath = aStarNavigationService.findPath(lastPos, servicePointPos);
                List<Position> returnSegment = returnPath.getPath();

                // Skip first position to avoid duplicate
                if (returnSegment.size() > 1) {
                    fullRoute.addAll(returnSegment.subList(1, returnSegment.size()));
                }
            }

            // Ensure the final position is exactly the service point
            Position finalPos = fullRoute.get(fullRoute.size() - 1);
            if (!positionsEqual(finalPos, servicePointPos)) {
                fullRoute.set(fullRoute.size() - 1, servicePointPos);
            }
        }

        return fullRoute;
    }

    private boolean positionsEqual(Position p1, Position p2) {
        if (p1 == null || p2 == null) return false;
        return Double.compare(p1.getLng(), p2.getLng()) == 0 &&
                Double.compare(p1.getLat(), p2.getLat()) == 0;
    }

    // Helper functions

    private Map<String, Object> emptyGeoJson() {
        Map<String, Object> geo = new LinkedHashMap<>();
        geo.put("type", "FeatureCollection");
        geo.put("features", new ArrayList<>());
        return geo;
    }

    private Map<String, Object> makeGeoJson(List<Position> route) {

        List<List<Double>> coords = new ArrayList<>();

        for (Position p : route) {
            coords.add(Arrays.asList(p.getLng(), p.getLat()));
        }

        Map<String, Object> geometry = new LinkedHashMap<>();
        geometry.put("type", "LineString");
        geometry.put("coordinates", coords);

        Map<String, Object> feature = new LinkedHashMap<>();
        feature.put("type", "Feature");
        feature.put("geometry", geometry);
        feature.put("properties", new LinkedHashMap<>());

        List<Map<String, Object>> features = new ArrayList<>();
        features.add(feature);

        Map<String, Object> geo = new LinkedHashMap<>();
        geo.put("type", "FeatureCollection");
        geo.put("features", features);

        return geo;
    }
}