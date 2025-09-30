package com.example.s2581051.service;

import com.example.s2581051.exception.BadRequestException;
import com.example.s2581051.model.position;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GeoService {

    public double euclideanDistance(position p1, position p2){

        if (p1 == null || p2 == null ||
            p1.getLat() == null || p2.getLat() == null ||
            p1.getLng() == null || p2.getLng() == null) {
            throw new BadRequestException("Null fields encountered");
        }

        double x = p1.getLng() - p2.getLng();
        double y = p1.getLat() - p2.getLat();

        return Math.sqrt(x*x + y*y);
    }

    public boolean pointInPolygon(position point, List<position> vertices){
        int intersections = 0;

        for (int i=0; i < vertices.size()-1; i++ ){
            position v1 = vertices.get(i);
            position v2 = vertices.get(i+1);
            boolean condY = (v1.getLat() > point.getLat()) != (v2.getLat() > point.getLat());
            if (condY) {
                double slope = (point.getLat() - v1.getLat()) * (v2.getLng() - v1.getLng()) /
                        (v2.getLat() - v1.getLat()) + v1.getLng();
                if (point.getLng() < slope) {
                    intersections++;
                }
            }
        }

        if (!isClosedPolygon(vertices)){
            throw new BadRequestException("Region is not closed");
        }

        return intersections % 2 == 1;
    }

    public boolean isClosedPolygon(List<position> vertices){
        if (vertices.size() < 4){
            return false;
        }
        position first = vertices.get(0);
        position last = vertices.get(vertices.size()-1);

        return Double.compare(first.getLat(), last.getLat()) == 0 &&
                Double.compare(first.getLng(), last.getLng()) == 0;
    }

}
