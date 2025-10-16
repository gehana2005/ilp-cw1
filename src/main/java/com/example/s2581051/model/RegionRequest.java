package com.example.s2581051.model;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionRequest {
    @Valid
    @NotNull(message = "Position is required")
    private Position position;

    @Valid
    @NotNull(message = "Region is required")
    private Region region;
}
