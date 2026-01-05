import { SPEED_MATRIX } from '../constants/routeConfig';

export const calculateEstimatedTime = (distanceKm, difficulty, routeType) => {
    const speed = SPEED_MATRIX[routeType]?.[difficulty] || 4;
    const hours = distanceKm / speed;
    const totalMinutes = Math.round(hours * 60);

    const displayHours = Math.floor(totalMinutes / 60);
    const displayMinutes = totalMinutes % 60;

    if (displayHours > 0) {
        return `${displayHours}h ${displayMinutes}m`;
    }
    return `${displayMinutes}m`;
};

export const createGeoJSON = (points) => {
    if (points.length < 2) return null;

    const coordinates = points.map(point => [point.lng, point.lat]);

    return {
        type: "LineString",
        coordinates: coordinates
    };
};