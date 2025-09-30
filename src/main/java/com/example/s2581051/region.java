package com.example.s2581051;

import java.util.List;

public class region {

    private String name;
    private List<position> vertices;

    public region(){}

    public region(String name, List<position> vertices){
        this.name = name;
        this.vertices = vertices;
    }

    public String getName(){
        return name;
    }

    public List<position> getVertices(){
        return vertices;
    }
}
