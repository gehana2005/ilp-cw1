package com.example.s2581051.controller;

import com.example.s2581051.model.*;
import com.example.s2581051.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private queryService queryService;

    @Autowired
    private availableDronesService availableDronesService;

    @Autowired
    private calcDeliveryPathService calcDeliveryPathService;

    @Autowired
    private calcDeliveryPathAsGeoJsonService calcDeliveryPathAsGeoJsonService;

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

    @PostMapping("/query")
    public List<String> query(@RequestBody List<QueryRule> rules) {
        return queryService.filter(rules);
    }

    @PostMapping("/queryAvailableDrones")
    public List<String> queryAvailableDrones(@RequestBody List<MedDispatchRec> dispatches) {
        return availableDronesService.getAvailableDrones(dispatches);
    }

    @PostMapping("/calcDeliveryPath")
    public CalcDeliveryPathResponse calcDeliveryPath(@RequestBody List<MedDispatchRec> records) {
        return calcDeliveryPathService.calcDeliveryPath(records);
    }

    @PostMapping("/calcDeliveryPathAsGeoJson")
    public Object calcDeliveryPathAsGeoJson(@RequestBody List<MedDispatchRec> recs) {
        return calcDeliveryPathAsGeoJsonService.calcDeliveryPathAsGeoJson(recs);
    }

}

