CREATE TABLE favorite_route
(
    created_at TIMESTAMP WITHOUT TIME ZONE,
    user_id    BIGINT NOT NULL,
    route_id   BIGINT NOT NULL,
    CONSTRAINT pk_favorite_route PRIMARY KEY (user_id, route_id)
);

ALTER TABLE favorite_route
    ADD CONSTRAINT FK_FAVORITE_ROUTE_ON_ROUTE FOREIGN KEY (route_id) REFERENCES routes (id);

ALTER TABLE favorite_route
    ADD CONSTRAINT FK_FAVORITE_ROUTE_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);


