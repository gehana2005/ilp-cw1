package com.example.s2581051.model;

import com.example.s2581051.model.position;

public class positionRequest {
    private position start;
    private double angle;

    public positionRequest() {}

    public positionRequest(position start, double angle) {
        this.start = start;
        this.angle = angle;
    }

    public position getStart() {
        return start;
    }

    public double getAngle() {
        return angle;
    }

}
