package com.example.s2581051.model;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request to check whether a point is within a region
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionRequest {
    /** Point to test against the region */
    @Valid
    @NotNull(message = "Position is required")
    private Position position;

    /** Region containing boundary vertices */
    @Valid
    @NotNull(message = "Region is required")
    private Region region;
}
