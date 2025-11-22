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

    public  calcDeliveryPathAsGeoJsonService(String ilpEndpoint, calcDeliveryPathService calcDeliveryPathService, pathFindingService pathFindingService,
                                             aStarNavigationService aStarNavigationService, availableDronesService availableDronesService) {
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

        // sort
        List<MedDispatchRec> sorted = calcDeliveryPathService.sortDeliveriesByDate(records);

        // chose drone
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

        // find the service point
        ServicePoint sp = calcDeliveryPathService.findServicePointForDrone(chosen);
        if (sp == null) return emptyGeoJson();

        Position start = sp.getLocation();

        // get the path sequence
        Map<Position, Double> seq = pathFindingService.findPath(start, sorted);

        List<Position> nodes = new ArrayList<>(seq.keySet());
        if (nodes.size() < 2) return emptyGeoJson();

        List<Position> fullRoute = new ArrayList<>();

        for (int i = 0; i < nodes.size() - 1; i++) {
            Position a = nodes.get(i);
            Position b = nodes.get(i + 1);

            AstarPath p = aStarNavigationService.findPath(a, b);

            if (p.getPath().isEmpty()) continue;

            if (i == 0) {
                fullRoute.addAll(p.getPath());
            } else {
                fullRoute.addAll(p.getPath().subList(1, p.getPath().size()));
            }
        }

        return makeGeoJson(fullRoute);
    }

    // helper functions

    private Map<String, Object> emptyGeoJson(){
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
