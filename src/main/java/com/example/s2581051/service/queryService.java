package com.example.s2581051.service;

import com.example.s2581051.model.Capability;
import com.example.s2581051.model.Drone;
import com.example.s2581051.model.QueryRule;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class queryService {

    private final String ilpEndpoint;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final Set<String> NUMERIC_ATTRIBUTES = Set.of(
            "capacity", "maxMoves", "costPerMove", "costInitial", "costFinal"
    );

    private static final Set<String> BOOLEAN_ATTRIBUTES = Set.of(
            "cooling", "heating"
    );

    public queryService(String ilpEndpoint) {
        this.ilpEndpoint = ilpEndpoint;
    }

    public List<String> filter(List<QueryRule> rules) {

        String url = ilpEndpoint + "/drones";
        Drone[] drones = restTemplate.getForObject(url, Drone[].class);

        List<String> result = new ArrayList<>();
        if (drones == null) return result;

        for (Drone drone : drones) {
            if (matchesAllRules(drone, rules)) {
                result.add(drone.getId());
            }
        }

        return result;
    }


    private boolean matchesAllRules(Drone drone, List<QueryRule> rules) {

        Capability cap = drone.getCapability();
        if (cap == null) return false;

        for (QueryRule rule : rules) {
            if (!matchesRule(cap, rule)) {
                return false;
            }
        }
        return true;
    }


    private boolean matchesRule(Capability cap, QueryRule rule) {

        String attribute = rule.getAttribute();
        String operator = rule.getOperator();
        String value = rule.getValue();

        Object fieldValue = getValueByAttribute(cap, attribute);
        if (fieldValue == null) return false;

        if (BOOLEAN_ATTRIBUTES.contains(attribute)) {
            boolean lhs = (Boolean) fieldValue;
            boolean rhs = Boolean.parseBoolean(value);
            return operator.equals("=") && lhs == rhs;
        }

        if (NUMERIC_ATTRIBUTES.contains(attribute)) {
            double lhs = Double.parseDouble(fieldValue.toString());
            double rhs = Double.parseDouble(value);

            return switch (operator) {
                case "="  -> lhs == rhs;
                case "!=" -> lhs != rhs;
                case "<"  -> lhs < rhs;
                case ">"  -> lhs > rhs;
                default   -> false;
            };
        }

        return false;
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
