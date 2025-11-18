package com.example.s2581051.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Capability {

    private Boolean cooling;

    private Boolean heating;

    private Double capacity;

    private Integer maxMoves;

    private Double costPerMove;

    private Double costInitial;

    private Double costFinal;
}
