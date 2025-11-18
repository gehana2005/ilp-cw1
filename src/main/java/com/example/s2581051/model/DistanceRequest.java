package com.example.s2581051.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Request to calculate the euclidean distance
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DistanceRequest {
    /** First position in distance calculation */
    @Valid
    @NotNull(message = "position1 is required")
    private Position position1;

    /** Second position in distance calculation */
    @Valid
    @NotNull(message = "position2 is required ")
    private Position position2;
}
