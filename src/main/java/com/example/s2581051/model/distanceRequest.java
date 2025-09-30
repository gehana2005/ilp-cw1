package com.example.s2581051.model;

public class distanceRequest {

    private position position1;
    private position position2;

    public distanceRequest() {}

    public distanceRequest(position position1, position position2){
        this.position1 = position1;
        this.position2 = position2;
    }

    public position getPosition1() {
        return position1;
    }

    public position getPosition2() {
        return position2;
    }

    public void setPosition1(position position1) {
        this.position1 = position1;
    }

    public void setPosition2(position position2) {
        this.position2 = position2;
    }
}
