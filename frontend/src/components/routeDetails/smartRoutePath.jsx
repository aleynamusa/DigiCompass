import {useEffect, useState} from "react";
import {Polyline, Marker, Popup, useMap} from "react-leaflet";
import L from "leaflet";

const startIcon = L.divIcon({
    className: "custom-marker-start",
    html: '<div style="background-color:#00c853;width:14px;height:14px;border-radius:50%;border:2px solid white;"></div>',
    iconSize: [14, 14],
});

const endIcon = L.divIcon({
    className: "custom-marker-end",
    html: '<div style="background-color:#d32f2f;width:14px;height:14px;border-radius:50%;border:2px solid white;"></div>',
    iconSize: [14, 14],
});

const waypointIcon = L.divIcon({
    className: "custom-marker-waypoint",
    html: '<div style="background-color:#9C27B0;width:10px;height:10px;border-radius:50%;border:2px solid white;"></div>',
    iconSize: [10, 10],
});

export default function SmartRoutePath({geojson, category = 'walking'}) {
    const map = useMap();
    const [routedCoords, setRoutedCoords] = useState([]);
    const [waypoints, setWaypoints] = useState([]);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (!geojson?.coordinates || geojson.coordinates.length < 2) return;

        console.log("Original geojson:", geojson);
        setIsLoading(true);

        const profileMap = {
            walking: 'foot',
            hiking: 'foot',
            cycling: 'bike'
        };

        const profile = profileMap[category] || 'foot';

        const waypointCoords = geojson.coordinates.map(([lon, lat]) => [lat, lon]);
        setWaypoints(waypointCoords);

        const coordsStr = geojson.coordinates
            .map(([lon, lat]) => `${lon},${lat}`)
            .join(';');

        const url = `https://router.project-osrm.org/route/v1/${profile}/${coordsStr}?overview=full&geometries=geojson`;

        console.log("Requesting OSRM route:", url);

        fetch(url)
            .then(res => res.json())
            .then(data => {
                console.log("OSRM response:", data);

                if (data.code === 'Ok' && data.routes?.[0]?.geometry?.coordinates) {
                    const coords = data.routes[0].geometry.coordinates
                        .map(([lon, lat]) => [lat, lon]);

                    console.log(`Routed path has ${coords.length} points (vs ${waypoints.length} waypoints)`);
                    setRoutedCoords(coords);

                    if (coords.length > 0) {
                        map.fitBounds(coords, { padding: [50, 50] });
                    }
                    setError(null);
                } else {
                    throw new Error(data.message || "No route found");
                }
            })
            .catch(err => {
                console.error("Routing error:", err);
                setError(err.message);

                setRoutedCoords(waypointCoords);

                if (waypointCoords.length > 0) {
                    map.fitBounds(waypointCoords, { padding: [50, 50] });
                }
            })
            .finally(() => {
                setIsLoading(false);
            });
    }, [geojson, category, map]);

    if (isLoading) {
        return (
            <div style={{
                position: 'absolute',
                top: 10,
                left: '50%',
                transform: 'translateX(-50%)',
                background: 'white',
                padding: '8px 16px',
                borderRadius: 4,
                zIndex: 1000,
                boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
            }}>
                🗺️ Loading route...
            </div>
        );
    }

    if (!routedCoords.length) return null;

    const start = routedCoords[0];
    const end = routedCoords[routedCoords.length - 1];

    const routeStyles = {
        walking: { color: '#2196F3', weight: 5, opacity: 0.8 },
        hiking: { color: '#4CAF50', weight: 6, opacity: 0.8 },
        cycling: { color: '#FF9800', weight: 5, opacity: 0.8 }
    };

    const style = routeStyles[category] || routeStyles.walking;

    return (
        <>
            <Polyline
                positions={routedCoords}
                {...style}
                smoothFactor={1}
            />

            {waypoints.slice(1, -1).map((point, index) => (
                <Marker key={`waypoint-${index}`} position={point} icon={waypointIcon}>
                    <Popup>Waypoint {index + 2}</Popup>
                </Marker>
            ))}

            {start && (
                <Marker position={start} icon={startIcon}>
                    <Popup>
                        <b>Start</b>
                        {error && (
                            <>
                                <br/>
                                <small style={{color: '#d32f2f'}}>
                                     {error}
                                </small>
                            </>
                        )}
                    </Popup>
                </Marker>
            )}

            {end && (
                <Marker position={end} icon={endIcon}>
                    <Popup>
                        <b>End</b>
                        <br/>
                        <small style={{color: '#666'}}>
                            {routedCoords.length > waypoints.length
                                ? `Detailed route (${routedCoords.length} points)`
                                : 'Direct path'}
                        </small>
                    </Popup>
                </Marker>
            )}
        </>
    );
}