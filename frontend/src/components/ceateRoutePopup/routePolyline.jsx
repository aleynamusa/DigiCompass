import { Polyline } from "react-leaflet";

const RoutePolyline = ({ points }) => {
    if (!points || points.length < 2) return null;

    const positions = points.map(p => [p.lat, p.lng]);

    return (
        <Polyline
            positions={positions}
            color="#3b82f6"
            weight={4}
            opacity={0.7}
        />
    );
};

export default RoutePolyline;
