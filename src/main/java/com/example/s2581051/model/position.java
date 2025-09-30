package com.example.s2581051.model;

public class position {
    private Double lng;
    private Double lat;

    public position(){}
    public position(double lng, double lat){
        this.lng = lng;
        this.lat = lat;
    }

   public Double getLng(){
        return lng;
   }

   public Double getLat() {
        return lat;
   }
}
