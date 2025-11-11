import {MapContainer, TileLayer, Marker, Popup} from "react-leaflet";
import L from "leaflet";
import SmartRoutePath from "@/components/routeDetails/smartRoutePath.jsx";

const smallIcon = L.divIcon({
    className: "custom-marker",
    html: '<div style="background-color:#007bff;width:10px;height:10px;border-radius:50%;border:1px solid white;"></div>',
    iconSize: [10, 10],
});

const getRouteCoords = (geojson) => {
    if (!geojson) return [];
    if (geojson.type === "LineString") return geojson.coordinates.map(c => [c[1], c[0]]);
    if (geojson.type === "MultiLineString") return geojson.coordinates.flat().map(c => [c[1], c[0]]);
    return [];
};

export default function RouteMap({routeGeometry}) {
    return (
        <div className="h-80 w-full rounded-md overflow-hidden">
            <MapContainer
                center={[45.4642, 9.19]}
                zoom={13}
                style={{height: "100%", width: "100%"}}
            >
                <TileLayer
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    attribution="© OpenStreetMap contributors"
                />

                {routeGeometry && (
                    <>
                        <SmartRoutePath geojson={routeGeometry} />

                        {getRouteCoords(routeGeometry).map((point, index) => (
                            <Marker key={index} position={point} icon={smallIcon}>
                                <Popup>Point {index + 1}</Popup>
                            </Marker>
                        ))}
                    </>
                )}
            </MapContainer>
        </div>
    );
}
