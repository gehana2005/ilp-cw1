package com.example.s2581051;


public class regionRequest {

    private position position;
    private region region;

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
