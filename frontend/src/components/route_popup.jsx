import React from "react";
import { MapContainer, TileLayer, Marker, Popup, Polyline } from "react-leaflet";


const RoutePopUp = () => {
    const path = [
        [37.7749, -122.4194],   // San Francisco
        [37.8049, -122.2694],   // East Bay
        [37.3382, -121.8863],   // San Jose
    ];

    return (
        // This wrapper div ensures the map gets a real height
        <div style={{ height: "100vh", width: "100%" }}>
            <MapContainer
                center={[37.7749, -122.4194]}
                zoom={13}
                scrollWheelZoom={false}
                style={{ height: "100%", width: "100%" }}
            >
                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                <Marker position={[37.7749, -122.4194]}>
                </Marker>
                <Marker position={[37.8049, -122.2694]}>
                </Marker>
                <Marker position={[37.3382, -121.8863]}>
                </Marker>
                <Polyline positions={path} color="blue" weight={4} />
            </MapContainer>
        </div>
    );
};

export default RoutePopUp;
