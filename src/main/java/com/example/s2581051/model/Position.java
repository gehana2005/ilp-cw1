package com.example.s2581051.model;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


/**
 * Represents a position with longitude and latitude
 * Includes validation constraints for valid coordinate ranges
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    /** Longitude in degrees (-180 to 180) */
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    @NotNull(message = "Longitude can not be null")
    private Double lng;

    /** Latitude in degrees (-90 to 90) */
    @DecimalMin(value = "-90.0", message = "Latitude must be >=-90")
    @DecimalMax(value = "90.0", message = "Latitude must be <=90")
    @NotNull(message = "Latitude can not be null" )
    private Double lat;
}
