package com.example.s2581051.service;
import com.example.s2581051.model.Position;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class polygonService {

    public boolean pointInPolygon(Position point, List<Position> vertices) {

        if (!isClosedPolygon(vertices)) {
            throw new IllegalArgumentException("Invalid Polygon or Position");
        }

        boolean inside = false;
        int n = vertices.size();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            Position a = vertices.get(i);
            Position b = vertices.get(j);

            // Check if point is exactly on an edge
            if (isOnEdge(point, a, b)) {
                return true;
            }

            // Standard ray-casting intersection check
            boolean intersects = ((a.getLat() > point.getLat()) != (b.getLat() > point.getLat())) &&
                    (point.getLng() < (b.getLng() - a.getLng()) * (point.getLat() - a.getLat()) / (b.getLat() - a.getLat()) + a.getLng());

            if (intersects) {
                inside = !inside;
            }
        }
        return inside;
    }
    private boolean isOnEdge(Position p, Position a, Position b) {
        double x = p.getLng(), y = p.getLat();
        double x1 = a.getLng(), y1 = a.getLat();
        double x2 = b.getLng(), y2 = b.getLat();

        // Check collinear using cross-product = 0
        double cross = (y - y1) * (x2 - x1) - (y2 - y1) * (x - x1);
        if (Math.abs(cross) > 0) return false;

        // Check within bounding box of the edge
        return x >= Math.min(x1, x2) && x <= Math.max(x1, x2)
                && y >= Math.min(y1, y2) && y <= Math.max(y1, y2);
    }

    public boolean isClosedPolygon(List<Position> vertices){
        if (vertices.size() < 4){
            return false;
        }
        Position first = vertices.get(0);
        Position last = vertices.get(vertices.size()-1);

        return Double.compare(first.getLat(), last.getLat()) == 0 &&
                Double.compare(first.getLng(), last.getLng()) == 0;
    }

}
