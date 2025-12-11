ALTER TABLE routes
DROP COLUMN route_type;

ALTER TABLE routes
DROP COLUMN difficulty;

ALTER TABLE routes
    ADD COLUMN route_type VARCHAR(255) NOT NULL DEFAULT 'Easy';


ALTER TABLE routes
    ADD COLUMN difficulty VARCHAR(255) NOT NULL DEFAULT 'Walking';

