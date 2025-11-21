package com.example.s2581051.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalcDeliveryPathResponse {

    private double totalCost;
    private int totalMoves;
    private List<DronePath> DronePaths;

}
