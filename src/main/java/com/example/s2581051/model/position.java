package com.example.s2581051.model;

public class position {
    private double lng;
    private double lat;

    public position(){}

    public position(double lng, double lat){
        this.lng = lng;
        this.lat = lat;
    }

   public double getLng(){
        return lng;
   }

   public double getLat() {
        return lat;
   }
}
