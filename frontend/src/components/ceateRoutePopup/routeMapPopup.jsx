import {useEffect, useRef, useState} from "react";
import { MapContainer, TileLayer, Polyline, useMap, useMapEvents } from "react-leaflet";
import UserLocationMarker from "./UserLocationMarker";
import RoutePolyline from "./RoutePolyline";
import RoutePointMarkers from "./RoutePointMarkers";
import {DEFAULT_LOCATION} from "@/constants/routeConfig.jsx";

function FitBoundsToPoints({ points }) {
    const map = useMap();

    useEffect(() => {
        if (points.length === 0) return;

        const bounds = points.map(p => [p.lat, p.lng]);

        try {
            map.fitBounds(bounds, {
                padding: [50, 50],
                maxZoom: 15
            });
        } catch (e) {
            console.warn('Could not fit bounds:', e);
        }
    }, [points, map]);

    return null;
}

function MapEventsWrapper({ onMapClick }) {
    useMapEvents({
        click(e) {
            if (typeof onMapClick === 'function') onMapClick(e);
        },
    });
    return null;
}

function ReenterOnRealLocation({ location }) {
    const map = useMap();
    const hasCenteredOnGPS = useRef(false);

    useEffect(() => {
        if (!location?.ready) return;
        if (hasCenteredOnGPS.current) return;

        map.flyTo(
            [location.latitude, location.longitude],
            16,
            { animate: true, duration: 1.2 }
        );

        hasCenteredOnGPS.current = true;
    }, [location, map]);

    return null;
}


const RouteMapPopup = ({ center, points, ghRoute, ghError, onMapClick, startIcon, endIcon, isLoading }) => {
    const lat = Number(center?.latitude);
    const lng = Number(center?.longitude);

    const [initialCenter, setInitialCenter] = useState(null);

    useEffect(() => {
        if (initialCenter) return;
        // only set initialCenter when a real center prop is provided
        if (!center) return;
        if (lat === 0 && lng === 0) return;
        setInitialCenter([lat, lng]);
    }, [lat, lng, initialCenter, center]);




    const mapCenter = center?.ready
        ? [center.latitude, center.longitude]
        : [DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude];

    const initialZoom = center.ready ? 18 : 12;

    return (
        <MapContainer
            center={mapCenter}
            zoom={initialZoom}
            style={{ height: "100%", width: "100%" }}
        >
            <TileLayer
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                attribution="&copy; OpenStreetMap contributors"
            />

            <ReenterOnRealLocation location={center} />
            <FitBoundsToPoints points={points} />
            <MapEventsWrapper onMapClick={onMapClick} />
            <UserLocationMarker location={center} />

            {points.length >= 2 && (
                ghRoute ? (
                    <Polyline
                        positions={ghRoute.map(p => [p.lat, p.lng])}
                        color="#0d9488"
                        weight={5}
                    />
                ) : ghError ? (
                    <RoutePolyline points={points} />
                ) : null
            )}

            <RoutePointMarkers points={points} startIcon={startIcon} endIcon={endIcon} />
        </MapContainer>
    );
};

export default RouteMapPopup;