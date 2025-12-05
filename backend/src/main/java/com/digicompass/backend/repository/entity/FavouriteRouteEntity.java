package com.digicompass.backend.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="favorite_route")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class    FavouriteRouteEntity {
    @EmbeddedId
    private FavouriteRouteKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @MapsId("routeId")
    @JoinColumn(name = "route_id")
    private RouteEntity route;

    private LocalDateTime createdAt = LocalDateTime.now();

    public FavouriteRouteEntity(UserEntity user, RouteEntity route) {
        this.id = new FavouriteRouteKey(user.getId(), route.getId());
        this.user = user;
        this.route = route;
    }
}
