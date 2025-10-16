package com.example.s2581051.service;

import com.example.s2581051.model.Position;
import org.springframework.stereotype.Service;

@Service
public class distanceService {
    public Double euclideanDistance(Position p1, Position p2){
        double x = p1.getLng() - p2.getLng();
        double y = p1.getLat() - p2.getLat();
        return Math.sqrt(x*x + y*y);
    }
    public boolean isCloseTo(Position p1, Position p2){
        double threshold = 0.00015;
        return euclideanDistance(p1,p2) < threshold;
    }
}
