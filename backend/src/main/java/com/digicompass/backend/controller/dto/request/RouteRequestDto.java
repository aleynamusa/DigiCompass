package com.digicompass.backend.controller.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDto {
    private String name;
    private String description;
    private String routeType;
    private String difficulty;
    private float distance;
    private String duration;
    private String geometry;
    private List<MultipartFile> images;

}
