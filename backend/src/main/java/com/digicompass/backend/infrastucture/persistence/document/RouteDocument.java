package com.digicompass.backend.infrastucture.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("routes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDocument {
    @Id
    private String id;
    private String name;
    private String mode;
    private List<String> coordinates;

}