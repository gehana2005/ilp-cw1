package com.example.s2581051.model;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Request containing a start position and angle
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionRequest {
    /** Start Position */
    @Valid
    @NotNull(message = "Start position is required")
    private Position start;

    /** Movement angle in Degrees */
    @DecimalMin(value = "0", message = "Angle must be >= 0")
    @DecimalMax(value = "360", message = "Angle must be <= 360")
    @NotNull(message = "Angle is required ")
    private Double angle;
}
