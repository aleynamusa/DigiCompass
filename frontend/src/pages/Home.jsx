// Dashboard
import React, {useState} from "react";
import { MapContainer, Marker, Popup, TileLayer, useMap } from "react-leaflet";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";

const Home = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        Cookies.remove("auth");
        console.log("You have logged out.");
        navigate("/login");
    };

    return (
        <div>
            <MapContainer center={[51.505, -0.09]} zoom={13} scrollWheelZoom={false}>
                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                <Marker position={[51.505, -0.09]}>
                    <Popup>
                        This is a popup
                    </Popup>
                </Marker>
            </MapContainer>

            <button onClick={handleLogout}>Log Out</button>
        </div>


        // <button onClick={handleLogout}>Log Out</button>


    );
}

export default Home;