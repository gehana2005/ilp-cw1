package com.example.s2581051.model;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    @NotNull(message = "Name is required")
    private String name;
    @Valid
    @NotNull(message = "List of vertices is required")
    @Size(min = 4, message = "A region must have at least 4 vertices")
    private List<Position> vertices;
}
