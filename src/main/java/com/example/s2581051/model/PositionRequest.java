package com.example.s2581051.model;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionRequest {
    @Valid
    @NotNull(message = "Start position is required")
    private Position start;

    @NotNull(message = "Angle is required ")
    @DecimalMin(value = "0.0", message = "Angle must be >= 0")
    @DecimalMax(value = "360.0", message = "Angle must be <= 360")
    private Double angle;
}
