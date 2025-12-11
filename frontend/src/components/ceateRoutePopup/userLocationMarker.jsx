import { CircleMarker, Circle } from "react-leaflet";

const UserLocationMarker = ({ location }) => {
    if (!location?.latitude || !location?.longitude) return null;

    return (
        <>
            <CircleMarker
                center={[location.latitude, location.longitude]}
                radius={8}
                color="blue"
                fillColor="blue"
                fillOpacity={0.9}
            />

            {location.accuracy &&
                !isNaN(location.accuracy) &&
                location.accuracy > 0 && (
                    <Circle
                        center={[location.latitude, location.longitude]}
                        radius={location.accuracy}
                        pathOptions={{
                            color: "blue",
                            fillColor: "blue",
                            fillOpacity: 0.1
                        }}
                    />
                )}
        </>
    );
};

export default UserLocationMarker;
