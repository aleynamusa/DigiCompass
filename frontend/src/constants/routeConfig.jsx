export const PROFILE_MAP = {
    hiking: "foot",
    walking: "foot",
    running: "foot",
    cycling: "bike"
};

export const SPEED_MATRIX = {
    hiking: { easy: 4, medium: 3.5, hard: 3 },
    walking: { easy: 5, medium: 4.5, hard: 4 },
    running: { easy: 10, medium: 9, hard: 8 },
    cycling: { easy: 20, medium: 18, hard: 15 }
};

export const GRAPHHOPPER_CONFIG = {
    baseUrl: "https://graphhopper.com/api/1/route",
    apiKey: "44647a0b-7fbf-4e36-afa3-6e8cf03c2dda"
};

export const DEFAULT_LOCATION = {
    latitude: 52.3676,
    longitude: 4.9041
};

export const GEOLOCATION_OPTIONS = {
    enableHighAccuracy: true,
    timeout: 15000,
    maximumAge: 0
};