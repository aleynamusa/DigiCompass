import {MapContainer, TileLayer} from "react-leaflet";
import SmartRoutePath from "@/components/routeDetails/smartRoutePath.jsx";


export default function RouteMap({routeGeometry, category}) {
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
                    <SmartRoutePath geojson={routeGeometry} category={category} />
                )}


            </MapContainer>
        </div>
    );
}
