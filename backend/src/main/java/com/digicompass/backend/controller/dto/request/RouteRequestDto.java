package com.digicompass.backend.controller.dto.request;

import com.digicompass.backend.controller.dto.UserDto;
import com.digicompass.backend.types.Difficulty;
import com.digicompass.backend.types.RouteType;
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
