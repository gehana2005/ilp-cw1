package com.example.s2581051.controller;

import com.example.s2581051.model.Drone;
import com.example.s2581051.service.droneCoolingService;
import com.example.s2581051.service.queryAsPathService;
import com.example.s2581051.service.droneDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class cw2Controller {

    @Autowired
    private droneCoolingService droneCoolingService;

    @Autowired
    private droneDetailsService droneDetailsService;

    @Autowired
    private queryAsPathService queryAsPathService;

    @GetMapping("/dronesWithCooling/{state}")
    public List<String> getDronesWithCooling(@PathVariable boolean state) {
        return droneCoolingService.getDronesWithCooling(state);
    }

    @GetMapping("/droneDetails/{id}")
    public Drone getDroneDetails(@PathVariable String id) {

        Drone drone =  droneDetailsService.getDroneDetailsById(id);

        if (drone == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Drone Details not found");
        }

        return drone;
    }

    @GetMapping("/queryAsPath/{attribute}/{value}")
    public List<String> queryAsPath(@PathVariable String attribute, @PathVariable String value) {
        return queryAsPathService.getQuery(attribute, value);
    }
}

