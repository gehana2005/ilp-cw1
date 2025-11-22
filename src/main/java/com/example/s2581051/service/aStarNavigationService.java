package com.example.s2581051.service;

import com.example.s2581051.model.AstarNode;
import com.example.s2581051.model.AstarPath;
import com.example.s2581051.model.Position;
import com.example.s2581051.model.RestrictedArea;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class aStarNavigationService {

    private final distanceService distanceService;
    private final nextPositionService nextPositionService;
    private final polygonService polygonService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String ilpEndpoint;

    private List<RestrictedArea> restrictedAreas = new ArrayList<>();

    private static final double STEP = 0.00015;

    private static final double[] ANGLES = {
            0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5,
            180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5
    };

    @PostConstruct
    public void loadRestrictedAreas() {
        RestrictedArea[] arr = restTemplate.getForObject(
                ilpEndpoint + "/restricted-areas",
                RestrictedArea[].class
        );
        if (arr != null) restrictedAreas = Arrays.asList(arr);
    }

    public AstarPath findPath(Position start, Position goal) {

        PriorityQueue<AstarNode> open = new PriorityQueue<>(
                Comparator.comparingDouble(n -> n.getGCost() + n.getHCost())
        );

        Map<String, AstarNode> closed = new HashMap<>();
        Map<String, Double> bestG = new HashMap<>();

        AstarNode startNode = new AstarNode(start, 0, heuristic(start, goal), null);
        open.add(startNode);
        bestG.put(key(start), 0.0);

        int expansions = 0;
        final int MAX_EXPANSIONS = 50000;

        while (!open.isEmpty()) {

            if (expansions > MAX_EXPANSIONS) {
                return new AstarPath(new ArrayList<>(), 0, false);
            }

            AstarNode current = open.poll();
            Position currPos = current.getPosition();
            String currKey = key(currPos);

            Double knownG = bestG.get(currKey);
            if (knownG != null && current.getGCost() > knownG + 1e-9) {
                continue;
            }

            closed.put(currKey, current);
            expansions++;

            if (distanceService.isCloseTo(currPos, goal)) {
                return reconstruct(current);
            }

            for (double ang : ANGLES) {

                Position nextPos = nextPositionService.nextPosition(currPos, ang);
                String nextKey = key(nextPos);

                if (Math.abs(nextPos.getLat() - start.getLat()) > 0.01 ||
                        Math.abs(nextPos.getLng() - start.getLng()) > 0.01) {
                    continue;
                }

                if (closed.containsKey(nextKey)) continue;

                if (isIllegalMove(currPos, nextPos)) continue;

                double g = current.getGCost() + 1;

                Double prevBest = bestG.get(nextKey);
                if (prevBest != null && g >= prevBest - 1e-9) {
                    continue;
                }

                double h = heuristic(nextPos, goal) * 1.001;

                AstarNode next = new AstarNode(nextPos, g, h, current);

                bestG.put(nextKey, g);
                open.add(next);
            }
        }

        return new AstarPath(new ArrayList<>(), 0, false);
    }

    // restricted area checking
    private boolean isIllegalMove(Position curr, Position next) {

        for (RestrictedArea area : restrictedAreas) {

            List<Position> poly = area.getVertices();
            if (poly == null || poly.size() < 4) continue;

            if (polygonService.pointInPolygon(next, poly)) return true;

            if (segmentIntersectsPolygon(curr, next, poly)) return true;
        }

        return false;
    }

    private boolean segmentIntersectsPolygon(Position p1, Position p2, List<Position> poly) {

        for (int i = 0; i < poly.size() - 1; i++) {
            Position a = poly.get(i);
            Position b = poly.get(i + 1);

            if (segmentsIntersect(p1, p2, a, b)) return true;
        }

        return false;
    }

    private int orientation(Position a, Position b, Position c) {
        double val =
                (b.getLat() - a.getLat()) * (c.getLng() - b.getLng()) -
                        (b.getLng() - a.getLng()) * (c.getLat() - b.getLat());

        if (Math.abs(val) < 1e-12) return 0;
        return val > 0 ? 1 : 2;
    }

    private boolean onSegment(Position p, Position q, Position r) {
        return q.getLng() <= Math.max(p.getLng(), r.getLng()) &&
                q.getLng() >= Math.min(p.getLng(), r.getLng()) &&
                q.getLat() <= Math.max(p.getLat(), r.getLat()) &&
                q.getLat() >= Math.min(p.getLat(), r.getLat());
    }

    private boolean segmentsIntersect(Position p1, Position p2, Position q1, Position q2) {

        int o1 = orientation(p1, p2, q1);
        int o2 = orientation(p1, p2, q2);
        int o3 = orientation(q1, q2, p1);
        int o4 = orientation(q1, q2, p2);

        if (o1 != o2 && o3 != o4) return true;

        if (o1 == 0 && onSegment(p1, q1, p2)) return true;
        if (o2 == 0 && onSegment(p1, q2, p2)) return true;
        if (o3 == 0 && onSegment(q1, p1, q2)) return true;
        if (o4 == 0 && onSegment(q1, p2, q2)) return true;

        return false;
    }

    // utils
    private double heuristic(Position a, Position b) {
        return distanceService.euclideanDistance(a, b) / STEP;
    }

    private String key(Position p) {
        return String.format("%.6f_%.6f", p.getLng(), p.getLat());
    }

    private AstarPath reconstruct(AstarNode node) {
        List<Position> path = new ArrayList<>();
        AstarNode cur = node;

        while (cur != null) {
            path.add(cur.getPosition());
            cur = cur.getParent();
        }
        Collections.reverse(path);

        return new AstarPath(path, path.size() - 1, true);
    }
}
