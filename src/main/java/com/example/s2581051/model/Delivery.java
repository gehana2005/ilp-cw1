package com.example.s2581051.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    private int deliveryId;
    List<Position> flightPath;

}
