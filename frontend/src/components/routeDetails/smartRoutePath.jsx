import {useEffect, useState} from "react";
import {Polyline, Marker, Popup, useMap} from "react-leaflet";
import L from "leaflet";

// Start and end icons
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

export default function SmartRoutePath({geojson}) {
    const map = useMap();
    const [routedCoords, setRoutedCoords] = useState([]);

    useEffect(() => {
        if (!geojson?.coordinates || geojson.coordinates.length < 2) return;

        const coordsStr = geojson.coordinates.map(c => `${c[0]},${c[1]}`).join(";");
        const url = `https://router.project-osrm.org/route/v1/foot/${coordsStr}?overview=full&geometries=geojson`;

        fetch(url)
            .then(res => res.json())
            .then(data => {
                if (data.routes?.[0]) {
                    const coords = data.routes[0].geometry.coordinates.map(([lon, lat]) => [lat, lon]);
                    setRoutedCoords(coords);
                    map.fitBounds(coords);
                }
            })
            .catch(err => console.error("Routing failed:", err));
    }, [geojson, map]);

    if (!routedCoords.length) return null;

    const start = routedCoords[0];
    const end = routedCoords[routedCoords.length - 1];

    return (
        <>
            <Polyline positions={routedCoords} color="blue" weight={4}/>
            {start && (
                <Marker position={start} icon={startIcon}>
                    <Popup><b>Start</b></Popup>
                </Marker>
            )}
            {end && (
                <Marker position={end} icon={endIcon}>
                    <Popup><b>End</b></Popup>
                </Marker>
            )}
        </>
    );
}
