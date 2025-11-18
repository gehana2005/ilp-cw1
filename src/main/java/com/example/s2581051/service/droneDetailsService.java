package com.example.s2581051.service;

import com.example.s2581051.model.Drone;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class droneDetailsService {

    private final String ilpEndpoint;
    private final RestTemplate restTemplate =  new RestTemplate();

    public droneDetailsService(String ilpEndpoint) {
        this.ilpEndpoint = ilpEndpoint;
    }

    public Drone getDroneDetailsById(String id) {
        String url  = ilpEndpoint + "/drones";

        Drone[] drones = restTemplate.getForObject(url, Drone[].class);

        if (drones == null) {
            return null;
        }

        for ( Drone drone : drones) {
            if (drone.getId().equals(id)) {
                return drone;
            }
        }

        return null;

    }

}
