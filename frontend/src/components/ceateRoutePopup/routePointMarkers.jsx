import { Marker } from "react-leaflet";

const RoutePointMarkers = ({ points, startIcon, endIcon }) => {
    return points.map(point => (
        <Marker
            key={point.id}
            position={[point.lat, point.lng]}
            icon={point.isStart ? startIcon : point.isEnd ? endIcon : undefined}
        />
    ));
};

export default RoutePointMarkers;
