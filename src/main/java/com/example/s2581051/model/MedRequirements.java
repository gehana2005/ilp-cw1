package com.example.s2581051.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedRequirements {
    private Double capacity;
    private Boolean cooling;
    private Boolean heating;
    private Double maxCost;
}
