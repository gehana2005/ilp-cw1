package com.example.s2581051.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HighLevelPath {
    private Integer droneId;
    private ServicePoint servicePoint;
    private List<MedDispatchRec> orderedDeliveries;
    private List<Position> nodePath;
}
