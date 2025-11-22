package com.example.s2581051.service;

import com.example.s2581051.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class pathFindingService {

    private final String ilpEndpoint;
    private final RestTemplate restTemplate = new RestTemplate();
    private final distanceService distanceService;

    public pathFindingService(String ilpEndpoint, distanceService distanceService) {
        this.ilpEndpoint = ilpEndpoint;
        this.distanceService = distanceService;
    }

    public double findMaxCost(List<MedDispatchRec> records, Drone drone) {

        ServicePointDrones[] servicePointDrones =
                restTemplate.getForObject(ilpEndpoint + "/drones-for-service-points",
                        ServicePointDrones[].class);

        ServicePoint[] servicePoints =
                restTemplate.getForObject(ilpEndpoint + "/service-points",
                        ServicePoint[].class);

        if (servicePointDrones == null || servicePoints == null) {
            return Double.POSITIVE_INFINITY;
        }

        for (ServicePointDrones spd : servicePointDrones) {
            for (ServicePointDrone d : spd.getDrones()) {

                if (d.getId().equals(drone.getId())) {

                    for (ServicePoint sp : servicePoints) {

                        if (sp.getId().equals(spd.getServicePointId())) {

                            Position start = sp.getLocation();

                            Map<Position, Double> path = findPath(start, records);
                            int moves = calculateMaxMoves(path);

                            if (moves > drone.getCapability().getMaxMoves()) {
                                return Double.POSITIVE_INFINITY;
                            }

                            return calculateMaxCost(moves, drone, records);
                        }
                    }
                }
            }
        }

        return Double.POSITIVE_INFINITY;
    }


    public Map<Position, Double> findPath(Position sp, List<MedDispatchRec> records) {

        ArrayList<Position> remaining = new ArrayList<>();
        Map<Position, Double> result = new LinkedHashMap<>();

        result.put(sp, 0.0);

        for (MedDispatchRec record : records) {
            Position dest = record.getDelivery();
            remaining.add(dest);
        }

        Position current = sp;

        while (!remaining.isEmpty()) {

            Map<Position, Double> distances = new HashMap<>();

            for (Position p : remaining) {
                double dist = distanceService.euclideanDistance(current, p);
                distances.put(p, dist);
            }

            Map.Entry<Position, Double> minEntry =
                    distances.entrySet().stream()
                            .min(Map.Entry.comparingByValue())
                            .orElse(null);

            if (minEntry == null) break;

            Position next = minEntry.getKey();
            double dist = minEntry.getValue();

            result.put(next, dist);
            remaining.remove(next);

            current = next;
        }

        double returnDist = distanceService.euclideanDistance(current, sp);
        result.put(sp, returnDist);


        return result;
    }


    public Integer calculateMaxMoves(Map<Position, Double> path) {

        double sum = 0.0;

        for (Double d : path.values()) {
            double moves = d / 0.00015;
            sum += moves;
        }

        return (int) Math.ceil(sum);
    }

    public Double calculateMaxCost(int moves, Drone drone, List<MedDispatchRec> records) {

        Double costInitial = drone.getCapability().getCostInitial();
        Double costFinal = drone.getCapability().getCostFinal();
        Double costPerMove = drone.getCapability().getCostPerMove();

        return (costInitial + costFinal + moves * costPerMove) / records.size();
    }
}
