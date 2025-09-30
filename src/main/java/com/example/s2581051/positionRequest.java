package com.example.s2581051;

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
