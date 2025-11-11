package com.digicompass.backend.application.models;


import com.digicompass.backend.domain.entity.RouteEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteImage {
    private Long id;

    private String presignedUrl;

    private Long route;


}
