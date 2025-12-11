import React from "react";
import { MapContainer, TileLayer, Polyline, useMap, useMapEvents } from "react-leaflet";
import UserLocationMarker from "./UserLocationMarker";
import RoutePolyline from "./RoutePolyline";
import RoutePointMarkers from "./RoutePointMarkers";

const RouteMapPopup = ({ center, points, ghRoute, ghError, onMapClick, startIcon, endIcon, isLoading }) => {
    // normalize center keys (support both latitude/longitude and lat/lng)
    const lat = Number(center?.latitude ?? center?.lat ?? 0);
    const lng = Number(center?.longitude ?? center?.lng ?? 0);

    // Keep an immutable initial center that is set once when a valid center arrives.
    const [initialCenter, setInitialCenter] = React.useState(null);

    React.useEffect(() => {
        if (initialCenter) return;
        if (lat === 0 && lng === 0) return; // skip invalid default
        setInitialCenter([lat, lng]);
    }, [lat, lng, initialCenter]);

    // Recenter only once (initial acquisition). After any user interaction, never auto-recenter.
    function RecenterOnce({ center }) {
        const map = useMap();
        const initialized = React.useRef(false);

        React.useEffect(() => {
            if (initialized.current) return;
            if (!center) return;
            map.setView(center, map.getZoom());
            initialized.current = true;
        }, [center, map]);

        // treat many map interaction events as user interaction so auto-recenter is disabled afterwards
        React.useEffect(() => {
            const markUser = () => {
                initialized.current = true;
            };

            map.on('dragstart', markUser);
            map.on('zoomstart', markUser);
            map.on('click', markUser);
            map.on('mousedown', markUser);
            map.on('touchstart', markUser);
            map.on('movestart', markUser);

            return () => {
                map.off('dragstart', markUser);
                map.off('zoomstart', markUser);
                map.off('click', markUser);
                map.off('mousedown', markUser);
                map.off('touchstart', markUser);
                map.off('movestart', markUser);
            };
        }, [map]);

        return null;
    }

    // FIT BOUNDS when points are added - this is the key addition
    function FitBoundsToPoints({ points }) {
        const map = useMap();

        React.useEffect(() => {
            if (points.length === 0) return;

            // Get bounds of all points
            const bounds = points.map(p => [p.lat, p.lng]);

            // Add some padding
            try {
                map.fitBounds(bounds, {
                    padding: [50, 50],
                    maxZoom: 15 // Don't zoom in too much for single point
                });
            } catch (e) {
                // If bounds are invalid, do nothing
                console.warn('Could not fit bounds:', e);
            }
        }, [points, map]);

        return null;
    }

    // Inline map event handler: calls the provided onMapClick
    function MapEventsWrapper({ onMapClick }) {
        useMapEvents({
            click(e) {
                if (typeof onMapClick === 'function') onMapClick(e);
            },
        });
        return null;
    }

    // Use initialCenter if set; fallback to a safe default so MapContainer has a center on initial render.
    const mapCenter = initialCenter ?? [lat || 51.505, lng || -0.09]; // London as fallback

    return (
        <MapContainer
            center={mapCenter}
            zoom={13}
            style={{ height: "100%", width: "100%" }}
        >
            <TileLayer
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                attribution="&copy; OpenStreetMap contributors"
            />

            <RecenterOnce center={initialCenter} />
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