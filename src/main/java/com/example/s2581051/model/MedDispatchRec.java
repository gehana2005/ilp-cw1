package com.example.s2581051.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedDispatchRec {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private MedRequirements requirements;
    private Position delivery;
}
