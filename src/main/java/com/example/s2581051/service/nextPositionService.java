package com.example.s2581051.service;

import com.example.s2581051.model.Position;
import org.springframework.stereotype.Service;

@Service
public class nextPositionService {

    public Position nextPosition(Position start , Double angle) {

        double ds = 0.00015;

        double lng = start.getLng() + ds * Math.cos(Math.toRadians(angle));
        double lat = start.getLat() + ds * Math.sin(Math.toRadians(angle)) ;

        if (Math.abs(lng)<1e-18) {lng = 0;}
        if (Math.abs(lat)<1e-18) {lat = 0;}

        return new Position(lng, lat);
    }

}
