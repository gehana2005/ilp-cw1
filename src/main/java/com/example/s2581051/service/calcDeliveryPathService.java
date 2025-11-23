package com.example.s2581051.service;

import com.example.s2581051.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class calcDeliveryPathService {

    private pathFindingService pathFindingService;
    private availableDronesService availableDronesService;
    private distanceService distanceService;
    private aStarNavigationService aStarNavigationService;
    public polygonService polygonService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String ilpEndpoint;

    public calcDeliveryPathService(
            String ilpEndpoint,
            pathFindingService pathFindingService,
            availableDronesService availableDronesService,
            distanceService distanceService,
            aStarNavigationService aStarNavigationService,
            polygonService polygonService
    ) {
        this.ilpEndpoint = ilpEndpoint;
        this.pathFindingService = pathFindingService;
        this.availableDronesService = availableDronesService;
        this.distanceService = distanceService;
        this.aStarNavigationService = aStarNavigationService;
        this.polygonService = polygonService;
    }

    public CalcDeliveryPathResponse calcDeliveryPath(List<MedDispatchRec> records) {

        if (records == null || records.isEmpty()) {
            return new CalcDeliveryPathResponse(0, 0, new ArrayList<>());
        }

        if (hasRestrictedDelivery(records)) {
            return new CalcDeliveryPathResponse(0, 0, new ArrayList<>());
        }

        List<MedDispatchRec> sorted = sortDeliveriesByDate(records);

        List<Drone> candidateDrones = findCandidateDrones(sorted);

        Map<ServicePoint, List<MedDispatchRec>> initialAssignments =
                assignDeliveriesToNearestServicePoint(sorted);

        Map<ServicePoint, Drone> chosenDrones =
                chooseCheapestDroneForEachSP(initialAssignments, candidateDrones);

        Map<ServicePoint, List<MedDispatchRec>> finalAssignments =
                reassignDeliveries(initialAssignments, chosenDrones);

        List<HighLevelPath> highLevelPaths = new ArrayList<>();
        for (ServicePoint sp : finalAssignments.keySet()) {
            Drone d = chosenDrones.get(sp);
            if (d == null) continue;
            List<MedDispatchRec> delivs = finalAssignments.get(sp);

            HighLevelPath hp = buildHighLevelPath(sp, d, delivs);
            highLevelPaths.add(hp);
        }

        CalcDeliveryTotals totals = new CalcDeliveryTotals();
        List<DronePath> dronePaths =
                buildDronePathResults(finalAssignments, chosenDrones, highLevelPaths, totals);

        return assembleFinalResponse(dronePaths, totals);
    }

    public List<Drone> findCandidateDrones(List<MedDispatchRec> records) {

        List<String> ids = availableDronesService.getAvailableDrones(records);
        Drone[] drones = restTemplate.getForObject(ilpEndpoint + "/drones", Drone[].class);
        List<Drone> droneList = new ArrayList<>();

        if (drones == null) return droneList;

        for (Drone drone : drones) {
            if (ids.contains(drone.getId())) {
                droneList.add(drone);
            }
        }

        return droneList;
    }

    public ServicePoint findServicePointForDrone(Drone drone) {

        ServicePointDrones[] spDrones = restTemplate.getForObject(
                ilpEndpoint + "/drones-for-service-points",
                ServicePointDrones[].class);

        ServicePoint[] servicePoints = restTemplate.getForObject(
                ilpEndpoint + "/service-points",
                ServicePoint[].class);

        if (servicePoints == null || spDrones == null) return null;

        for (ServicePointDrones spd : spDrones) {
            if (spd.getDrones() == null) continue;

            for (ServicePointDrone spDrone : spd.getDrones()) {
                if (drone.getId().equals(spDrone.getId())) {
                    for (ServicePoint sp : servicePoints) {
                        if (sp.getId().equals(spd.getServicePointId())) {
                            return sp;
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<MedDispatchRec> sortDeliveriesByDate(List<MedDispatchRec> records) {
        return records.stream()
                .sorted(Comparator.comparing(MedDispatchRec::getDate)
                        .thenComparing(MedDispatchRec::getTime))
                .toList();
    }

    private Map<ServicePoint, List<MedDispatchRec>> assignDeliveriesToNearestServicePoint(
            List<MedDispatchRec> deliveries) {

        ServicePoint[] servicePoints = restTemplate.getForObject(
                ilpEndpoint + "/service-points",
                ServicePoint[].class
        );

        Map<ServicePoint, List<MedDispatchRec>> assignmentMap = new HashMap<>();

        if (servicePoints == null || deliveries == null) {
            return assignmentMap;
        }

        for (ServicePoint sp : servicePoints) {
            assignmentMap.put(sp, new ArrayList<>());
        }

        for (MedDispatchRec record : deliveries) {

            Position deliveryPos = record.getDelivery();
            if (deliveryPos == null) continue;

            ServicePoint nearest = null;
            double minDist = Double.MAX_VALUE;

            for (ServicePoint sp : servicePoints) {
                double dist = distanceService.euclideanDistance(deliveryPos, sp.getLocation());
                if (dist < minDist) {
                    minDist = dist;
                    nearest = sp;
                }
            }

            if (nearest != null) {
                assignmentMap.get(nearest).add(record);
            }
        }

        return assignmentMap;
    }

    private Map<ServicePoint, Drone> chooseCheapestDroneForEachSP(
            Map<ServicePoint, List<MedDispatchRec>> assignments,
            List<Drone> candidateDrones) {

        ServicePointDrones[] spDrones = restTemplate.getForObject(
                ilpEndpoint + "/drones-for-service-points",
                ServicePointDrones[].class
        );

        if (spDrones == null) return new HashMap<>();

        Map<String, Drone> candidateMap = new HashMap<>();
        for (Drone d : candidateDrones) {
            candidateMap.put(d.getId(), d);
        }

        Map<ServicePoint, Drone> chosen = new HashMap<>();

        for (ServicePoint sp : assignments.keySet()) {

            List<MedDispatchRec> deliveries = assignments.get(sp);
            if (deliveries.isEmpty()) continue;

            List<String> droneIdsAtSP = new ArrayList<>();

            for (ServicePointDrones spd : spDrones) {
                if (spd.getServicePointId().equals(sp.getId())) {
                    for (ServicePointDrone spDrone : spd.getDrones()) {
                        droneIdsAtSP.add(spDrone.getId());
                    }
                }
            }

            List<Drone> usableDrones = new ArrayList<>();
            for (String droneId : droneIdsAtSP) {
                if (candidateMap.containsKey(droneId)) {
                    usableDrones.add(candidateMap.get(droneId));
                }
            }

            if (usableDrones.isEmpty()) {
                chosen.put(sp, null);
                continue;
            }

            Drone cheapest = usableDrones.stream()
                    .min(Comparator.comparing(d -> d.getCapability().getCostPerMove()))
                    .orElse(null);

            chosen.put(sp, cheapest);
        }

        return chosen;
    }


    private Map<ServicePoint, List<MedDispatchRec>> reassignDeliveries(
            Map<ServicePoint, List<MedDispatchRec>> assignments,
            Map<ServicePoint, Drone> chosenDrones) {

        Map<ServicePoint, List<MedDispatchRec>> resultMap = new HashMap<>(assignments);
        List<MedDispatchRec> unassigned = new ArrayList<>();

        // remove from SPs that have no drone
        for (ServicePoint sp : assignments.keySet()) {
            if (chosenDrones.get(sp) == null) {
                unassigned.addAll(assignments.get(sp));
                resultMap.get(sp).clear();
            }
        }

        // reassign unassigned deliveries
        for (MedDispatchRec record : unassigned) {

            ServicePoint best = null;
            double minDist = Double.MAX_VALUE;
            Position deliveryPos = record.getDelivery();

            for (ServicePoint sp : chosenDrones.keySet()) {
                Drone d = chosenDrones.get(sp);
                if (d == null) continue;

                double dist = distanceService.euclideanDistance(deliveryPos, sp.getLocation());
                if (dist < minDist) {
                    minDist = dist;
                    best = sp;
                }
            }

            if (best != null) {
                resultMap.get(best).add(record);
            }
        }

        return resultMap;
    }

    private HighLevelPath buildHighLevelPath(
            ServicePoint sp,
            Drone drone,
            List<MedDispatchRec> deliveries) {

        Map<Position, Double> path =
                pathFindingService.findPath(sp.getLocation(), deliveries);

        List<Position> orderedPositions = new ArrayList<>(path.keySet());


        HighLevelPath dto = new HighLevelPath();
        dto.setDroneId(drone.getId());
        dto.setServicePoint(sp);
        dto.setOrderedDeliveries(new ArrayList<>(deliveries));
        dto.setNodePath(orderedPositions);

        return dto;
    }

    private List<DeliveryPath> buildAStarPathsFromHighLevel(HighLevelPath high) {

        List<DeliveryPath> results = new ArrayList<>();

        List<Position> nodes = high.getNodePath();
        List<MedDispatchRec> deliveries = high.getOrderedDeliveries();

        if (nodes == null || nodes.size() < 2) {
            return results;
        }

        int deliveryIndex = 0;

        for (int i = 0; i < nodes.size() - 1; i++) {

            Position start = nodes.get(i);
            Position goal = nodes.get(i + 1);

            MedDispatchRec currentDelivery = null;
            if (deliveryIndex < deliveries.size()) {
                currentDelivery = deliveries.get(deliveryIndex);
            }

            AstarPath aPath = aStarNavigationService.findPath(start, goal);
            List<Position> flightPath = new ArrayList<>(aPath.getPath());

            if (flightPath.isEmpty()) {
                flightPath.add(start);
                flightPath.add(goal);
            } else if (!positionsEqual(flightPath.get(0), start)) {
                flightPath.add(0, start);
            }

            // Ensure path ends at goal
            if (!flightPath.isEmpty() && !positionsEqual(flightPath.get(flightPath.size() - 1), goal)) {
                flightPath.add(goal);
            }

            // Add hover for deliveries
            if (currentDelivery != null) {
                flightPath.add(goal);
            }

            DeliveryPath dto = new DeliveryPath(
                    currentDelivery != null ? currentDelivery.getId() : -1,
                    flightPath
            );

            results.add(dto);

            if (currentDelivery != null) {
                deliveryIndex++;
            }
        }

        // Append return path to last delivery
        if (!results.isEmpty()) {
            Position lastDeliveryPos = nodes.get(nodes.size() - 1);
            Position servicePointPos = high.getServicePoint().getLocation();

            if (!positionsEqual(lastDeliveryPos, servicePointPos)) {
                AstarPath returnPath = aStarNavigationService.findPath(lastDeliveryPos, servicePointPos);
                List<Position> returnFlightPath = returnPath.getPath();

                DeliveryPath lastDelivery = results.get(results.size() - 1);

                if (returnFlightPath.isEmpty()) {
                    lastDelivery.getFlightPath().add(servicePointPos);
                } else {
                    for (int i = 1; i < returnFlightPath.size(); i++) {
                        lastDelivery.getFlightPath().add(returnFlightPath.get(i));
                    }

                    List<Position> lastPath = lastDelivery.getFlightPath();
                    if (!lastPath.isEmpty()) {
                        Position lastPos = lastPath.get(lastPath.size() - 1);
                        if (!positionsEqual(lastPos, servicePointPos)) {
                            lastPath.set(lastPath.size() - 1, servicePointPos);
                        }
                    }
                }
            }
        }

        return results;
    }

    static class CalcDeliveryTotals {
        int totalMoves = 0;
        double totalCost = 0.0;
    }

    private List<DronePath> buildDronePathResults(
            Map<ServicePoint, List<MedDispatchRec>> finalAssignments,
            Map<ServicePoint, Drone> chosenDrones,
            List<HighLevelPath> highLevelPaths,
            CalcDeliveryTotals totalsOut
    ) {

        List<DronePath> list = new ArrayList<>();

        for (HighLevelPath high : highLevelPaths) {

            ServicePoint sp = high.getServicePoint();
            Drone drone = chosenDrones.get(sp);

            if (drone == null) continue;

            List<DeliveryPath> deliveryPaths = buildAStarPathsFromHighLevel(high);

            int movesForDrone = 0;
            double costForDrone = 0.0;

            for (DeliveryPath dp : deliveryPaths) {
                List<Position> path = dp.getFlightPath();

                // Count actual moves (excluding hover - consecutive duplicate positions)
                for (int i = 0; i < path.size() - 1; i++) {
                    Position current = path.get(i);
                    Position next = path.get(i + 1);

                    // Only count as a move if positions are different
                    if (!positionsEqual(current, next)) {
                        movesForDrone++;
                        costForDrone += drone.getCapability().getCostPerMove();
                    }
                }
            }

            // No need for separate return calculation - already included in buildAStarPathsFromHighLevel
            costForDrone += drone.getCapability().getCostInitial();
            costForDrone += drone.getCapability().getCostFinal();

            totalsOut.totalMoves += movesForDrone;
            totalsOut.totalCost += costForDrone;

            list.add(new DronePath(drone.getId(), deliveryPaths));
        }

        return list;
    }

    private CalcDeliveryPathResponse assembleFinalResponse(
            List<DronePath> dronePaths,
            CalcDeliveryTotals totals
    ) {

        CalcDeliveryPathResponse response = new CalcDeliveryPathResponse();

        response.setTotalMoves(totals.totalMoves);
        response.setTotalCost(totals.totalCost);
        response.setDronePaths(dronePaths);

        return response;
    }

    public boolean hasRestrictedDelivery(List<MedDispatchRec> records) {

        RestrictedArea[] restricted = restTemplate.getForObject(
                ilpEndpoint + "/restricted-areas",
                RestrictedArea[].class
        );

        if (restricted == null) return false;

        for (MedDispatchRec rec : records) {
            Position dest = rec.getDelivery();
            if (dest == null) continue;

            for (RestrictedArea area : restricted) {
                List<Position> poly = area.getVertices();
                if (poly == null || poly.size() < 4) continue;

                if (polygonService.pointInPolygon(dest, poly)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean positionsEqual(Position p1, Position p2) {
        if (p1 == null || p2 == null) return false;
        return Double.compare(p1.getLng(), p2.getLng()) == 0 &&
                Double.compare(p1.getLat(), p2.getLat()) == 0;
    }
}