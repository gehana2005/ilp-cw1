package com.example.s2581051.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DistanceRequest {
    @Valid
    @NotNull(message = "position1 is required")
    private Position position1;
    @Valid
    @NotNull(message = "position2 is required ")
    private Position position2;
}
