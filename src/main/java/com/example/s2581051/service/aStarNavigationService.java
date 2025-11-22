package com.example.s2581051.service;

import com.example.s2581051.model.AstarNode;
import com.example.s2581051.model.AstarPath;
import com.example.s2581051.model.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class aStarNavigationService {

    private final distanceService distanceService;

    private static final double STEP = 0.00015;

    private static final double[] ANGLES = {
            // Degrees: E, ENE, NE, NNE, N, NNW, NW, WNW, 
            // W, WSW, SW, SSW, S, SSE, SE, ESE
            0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5,
            180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5
    };

    private static final List<double[]> DIRECTIONS = new ArrayList<>();

    static {
        for (double deg : ANGLES) {
            double rad = Math.toRadians(deg);
            double dx = STEP * Math.cos(rad);
            double dy = STEP * Math.sin(rad);
            DIRECTIONS.add(new double[] { dx, dy });
        }
    }

    public AstarPath findPath(Position start, Position goal) {

        PriorityQueue<AstarNode> open = new PriorityQueue<>(
                Comparator.comparingDouble(n -> n.getGCost() + n.getHCost())
        );

        Map<String, AstarNode> visited = new HashMap<>();

        AstarNode startNode = new AstarNode(
                start,
                0,
                heuristic(start, goal),
                null
        );

        open.add(startNode);

        while (!open.isEmpty()) {

            AstarNode current = open.poll();
            String key = key(current.getPosition());

            if (distanceService.euclideanDistance(current.getPosition(), goal) <= STEP) {
                return reconstructPath(current, true);
            }

            visited.put(key, current);

            for (double[] move : DIRECTIONS) {

                Position nextPos = new Position(
                        current.getPosition().getLng() + move[0],
                        current.getPosition().getLat() + move[1]
                );

                String nextKey = key(nextPos);

                if (visited.containsKey(nextKey)) {
                    continue;
                }

                double g = current.getGCost() + 1;
                double h = heuristic(nextPos, goal);

                AstarNode nextNode = new AstarNode(nextPos, g, h, current);

                open.add(nextNode);
            }
        }

        return new AstarPath(new ArrayList<>(), 0, false);
    }

    private double heuristic(Position a, Position b) {
        double dist = distanceService.euclideanDistance(a, b);
        return dist / STEP;
    }

    private String key(Position p) {
        return String.format("%.6f_%.6f", p.getLng(), p.getLat());
    }

    private AstarPath reconstructPath(AstarNode node, boolean success) {
        List<Position> path = new ArrayList<>();

        AstarNode current = node;
        while (current != null) {
            path.add(current.getPosition());
            current = current.getParent();
        }

        Collections.reverse(path);

        return new AstarPath(
                path,
                path.size() - 1,
                success
        );
    }
}
