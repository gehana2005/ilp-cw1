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

        if (drone == null || records == null || records.isEmpty()) {
            return 0.0;
        }

        ServicePointDrones[] servicePointDrones =
                restTemplate.getForObject(ilpEndpoint + "/drones-for-service-points",
                        ServicePointDrones[].class);
        ServicePoint[] servicePoints =
                restTemplate.getForObject(ilpEndpoint + "/service-points",
                        ServicePoint[].class);

        if (servicePointDrones == null || servicePoints == null) {
            return 0.0;
        }

        for (ServicePointDrones spd : servicePointDrones) {
            if (spd == null || spd.getDrones() == null) continue;

            for (ServicePointDrone d : spd.getDrones()) {
                if (d == null || d.getId() == null) continue;

                if (d.getId().equals(drone.getId())) {
                    // Match service point
                    for (ServicePoint sp : servicePoints) {
                        if (sp == null || sp.getId() == null) continue;

                        if (sp.getId().equals(spd.getServicePointId())) {
                            Position start = sp.getLocation();
                            if (start == null) return 0.0;

                            // Compute path
                            Map<Position, Double> path = findPath(start, records);
                            int moves = calculateMaxMoves(path);

                            return calculateMaxCost(moves, drone, records.size());
                        }
                    }
                }
            }
        }

        return 0.0;
    }

    public Map<Position, Double> findPath(Position sp, List<MedDispatchRec> records) {

        Map<Position, Double> result = new LinkedHashMap<>();
        result.put(sp, 0.0);

        if (records == null || records.isEmpty()) {
            return result;
        }

        List<Position> remaining = new ArrayList<>();
        for (MedDispatchRec record : records) {
            if (record != null && record.getDelivery() != null) {
                remaining.add(record.getDelivery());
            }
        }

        Position current = sp;

        while (!remaining.isEmpty()) {

            Map<Position, Double> candidates = new HashMap<>();

            for (Position p : remaining) {
                Double dist = distanceService.euclideanDistance(current, p);
                candidates.put(p, dist);
            }

            // nearest neighbour step
            Map.Entry<Position, Double> minEntry = candidates.entrySet()
                    .stream()
                    .min(Map.Entry.comparingByValue())
                    .orElse(null);

            if (minEntry == null) break;

            Position closest = minEntry.getKey();
            Double distance = minEntry.getValue();

            result.put(closest, distance);
            remaining.remove(closest);

            current = closest;
        }

        return result;
    }

    public int calculateMaxMoves(Map<Position, Double> path) {

        if (path == null || path.isEmpty()) return 0;

        double sum = 0.0;

        for (Double d : path.values()) {
            if (d == null) continue;
            sum += (d / 0.00015);
        }

        return (int) Math.ceil(sum);
    }

    public double calculateMaxCost(int moves, Drone drone, int recordCount) {

        if (drone == null || drone.getCapability() == null || recordCount <= 0) {
            return 0.0;
        }

        double costInitial = drone.getCapability().getCostInitial();
        double costFinal = drone.getCapability().getCostFinal();
        double costPerMove = drone.getCapability().getCostPerMove();

        double totalCost = costInitial + costFinal + moves * costPerMove;

        return totalCost;
    }
}
