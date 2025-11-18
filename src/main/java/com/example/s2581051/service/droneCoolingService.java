package com.example.s2581051.service;

import  com.example.s2581051.model.Drone;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class droneCoolingService {
    private final String ilpEndpoint;
    private final RestTemplate restTemplate = new RestTemplate();

    public droneCoolingService(String ilpEndpoint) {
        this.ilpEndpoint = ilpEndpoint;
    }

    public List<String> getDronesWithCooling(boolean state) {
        String url = ilpEndpoint + "/drones";

        Drone[] drones = restTemplate.getForObject(url, Drone[].class);
        List<String> result = new ArrayList<>();

        if (drones == null) {
            return result;
        }

        for (Drone drone : drones) {
            if (drone.getCapability() != null && Objects.equals(drone.getCapability().getCooling(), state)) {
                result.add(drone.getId());
            }
        }

        return result;
    }

}
