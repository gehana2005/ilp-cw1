package com.example.s2581051.model;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a region defined by a name
 * and a List of vertex positions forming its boundary
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    /** Name of the region */
    @NotNull(message = "Name is required")
    private String name;

    /** List of vertex positions defining the region */
    @Valid
    @NotNull(message = "List of vertices is required")
    @Size(min = 4, message = "A region must have at least 4 vertices")
    private List<Position> vertices;
}
