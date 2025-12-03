package com.digicompass.backend.application.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FavouriteRoute {

    private User user;

    private Route route;

    private LocalDateTime createdAt;
}
