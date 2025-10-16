package com.example.s2581051.model;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    @NotNull(message = "latitude can not be null")
    private Double lng;

    @NotNull(message = "longitude can not be null" )
    @DecimalMin(value = "-90.0", message = "Latitude must be >=-90")
    @DecimalMax(value = "90.0", message = "Latitude must be <=90")
    private Double lat;
}
