package com.example.s2581051.service;

import com.example.s2581051.model.Capability;
import com.example.s2581051.model.Drone;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class queryAsPathService {

    private final String ilpEndpoint;
    private final RestTemplate restTemplate = new RestTemplate();

    public queryAsPathService(String ilpEndpoint) {
        this.ilpEndpoint = ilpEndpoint;
    }

    public List<String> getQuery(String attribute, String value) {

        String url = ilpEndpoint + "/drones";
        Drone[] drones = restTemplate.getForObject(url, Drone[].class);

        List<String> results = new ArrayList<>();
        if (drones == null) return results;

        if (!isValidAttribute(attribute)) {
            return results;
        }

        for (Drone drone : drones) {
            Capability capability = drone.getCapability();
            if (capability == null) continue;

            Object fieldValue = getValueByAttribute(capability, attribute);
            if (fieldValue == null) continue;

            if (fieldValue.toString().equals(value)) {
                results.add(drone.getId());
            }
        }

        return results;
    }

    private boolean isValidAttribute(String attribute) {
        return Set.of(
                "cooling", "heating",
                "capacity", "maxMoves",
                "costPerMove", "costInitial", "costFinal"
        ).contains(attribute);
    }

    private Object getValueByAttribute(Capability cap, String attribute) {
        return switch (attribute) {
            case "cooling"      -> cap.getCooling();
            case "heating"      -> cap.getHeating();
            case "capacity"     -> cap.getCapacity();
            case "maxMoves"     -> cap.getMaxMoves();
            case "costPerMove"  -> cap.getCostPerMove();
            case "costInitial"  -> cap.getCostInitial();
            case "costFinal"    -> cap.getCostFinal();
            default             -> null;
        };
    }
}
