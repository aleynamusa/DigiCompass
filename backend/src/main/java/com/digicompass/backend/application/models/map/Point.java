package com.digicompass.backend.application.models.map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Point {
    private double lat;
    private double lng;
}
