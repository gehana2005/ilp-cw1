package com.example.s2581051.service;

import com.example.s2581051.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class availableDronesService {

    private final String ilpEndpoint;
    private final RestTemplate restTemplate = new RestTemplate();
    public pathFindingService pathFindingService;

    public availableDronesService(String ilpEndpoint) {
        this.ilpEndpoint = ilpEndpoint;
    }

    public List<String> getAvailableDrones(List<MedDispatchRec> records) {

        List<String> requirementFilter = satisfiedRequirements(records);
        List<String> timeFilter = satisfiedTime(records, requirementFilter);

        requirementFilter.retainAll(timeFilter);
        Map<String, Double> max_cost = MaxCost(records, requirementFilter);
        List<String> costFilter = satisfiesMaxCost(max_cost, records);

        requirementFilter.retainAll(costFilter);

        return requirementFilter;

    }

    public List<String> satisfiedRequirements(List<MedDispatchRec> records) {

        Drone[] drones = restTemplate.getForObject(ilpEndpoint + "/drones", Drone[].class);

        if (drones == null || records == null) {
            return List.of();
        }

        double totalRequiredCapacity = 0.0;

        boolean requiresCooling = false;
        boolean requiresHeating = false;

        for (MedDispatchRec rec : records) {
            MedRequirements req = rec.getRequirements();

            if (req == null) continue;

            if (req.getCapacity() != null) {
                totalRequiredCapacity += req.getCapacity();
            }

            if (Boolean.TRUE.equals(req.getCooling())) {
                requiresCooling = true;
            }

            if (Boolean.TRUE.equals(req.getHeating())) {
                requiresHeating = true;
            }
        }


        List<String> matches = new ArrayList<>();

        for (Drone drone : drones) {

            if (drone.getCapability() == null) continue;

            double droneCapacity = drone.getCapability().getCapacity();
            boolean droneCooling = Boolean.TRUE.equals(drone.getCapability().getCooling());
            boolean droneHeating = Boolean.TRUE.equals(drone.getCapability().getHeating());

            if (droneCapacity < totalRequiredCapacity) continue;
            if (requiresCooling && !droneCooling) continue;
            if (requiresHeating && !droneHeating) continue;

            matches.add(drone.getId());
        }

        return matches;
    }


    public List<String> satisfiedTime(List<MedDispatchRec> records, List<String> allowedDroneIds) {

        ServicePointDrones[] servicePoints =
                restTemplate.getForObject(ilpEndpoint + "/drones-for-service-points",
                        ServicePointDrones[].class);

        if (servicePoints == null || allowedDroneIds == null) {
            return List.of();
        }

        List<String> intersection = null;

        for (MedDispatchRec record : records) {

            LocalDate date = record.getDate();
            LocalTime time = record.getTime();

            if (date == null || time == null) continue;

            DayOfWeek dispatchDay = date.getDayOfWeek();
            List<String> matches = new ArrayList<>();

            for (ServicePointDrones sp : servicePoints) {

                for (ServicePointDrone drone : sp.getDrones()) {

                    if (!allowedDroneIds.contains(drone.getId())) continue;

                    List<AvailabilitySlot> slots = drone.getAvailability();
                    if (slots == null) continue;

                    for (AvailabilitySlot slot : slots) {

                        boolean sameDay = slot.getDayOfWeek() == dispatchDay;
                        boolean insideTime = !time.isBefore(slot.getFrom()) &&
                                !time.isAfter(slot.getUntil());

                        if (sameDay && insideTime) {
                            matches.add(drone.getId());
                            break;
                        }
                    }
                }
            }

            if (intersection == null) {
                intersection = matches;
            } else {
                intersection.retainAll(matches);
            }
        }

        return intersection == null ? List.of() : intersection;
    }

    public Map<String, Double> MaxCost(List<MedDispatchRec> records, List<String> allowedDroneIds) {

        Drone[] drones = restTemplate.getForObject(ilpEndpoint + "/drones", Drone[].class);
        Map<String, Double> maxcost = new HashMap<>();


        for (String id :  allowedDroneIds) {
            for (Drone drone : drones) {
                if (drone.getId().equals(id)) {
                    maxcost.put(id, pathFindingService.findMaxCost(records, drone));
                }
            }
        }

        return maxcost;

    }

    public List<String> satisfiesMaxCost(Map<String, Double> costmap, List<MedDispatchRec> records) {
        List<String> matches = new ArrayList<>();
        for (MedDispatchRec record : records) {
            for (String id : costmap.keySet()) {
                if (costmap.get(id) <= record.getRequirements().getMaxCost()){
                    matches.add(id);
                }
            }
        }
        return matches;
    }

}
