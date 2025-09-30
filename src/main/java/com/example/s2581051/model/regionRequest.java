package com.example.s2581051.model;

public class regionRequest {

    private com.example.s2581051.model.position position;
    private com.example.s2581051.model.region region;

    public regionRequest(){}

    public regionRequest(position position1, region region1) {
        this.position = position1;
        this.region = region1;
    }

    public region getRegion(){
        return region;
    }
    public position getPosition() {
        return position;
    }
}
