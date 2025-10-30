import {useEffect, useRef, useState} from "react";
import {Dialog, DialogContent, DialogHeader, DialogTitle} from "@/components/ui/dialog"
import {MapContainer, TileLayer, Polyline, Marker, Popup, useMap} from "react-leaflet"
import L, {LatLngBounds} from "leaflet";
import {Rating, Stack, Group, Button, Textarea, Tabs, FloatingIndicator, Flex} from "@mantine/core";
import classes from '@/components/card.module.css';
import axios from "axios";

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

function SmartRoutePath({ geojson }) {
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
            <Polyline positions={routedCoords} color="blue" weight={4} />

            {start && (
                <Marker position={start} icon={startIcon}>
                    <Popup><b>Start</b></Popup>
                </Marker>
            )}

            {/* End Marker */}
            {end && (
                <Marker position={end} icon={endIcon}>
                    <Popup><b>End</b></Popup>
                </Marker>
            )}
        </>
    );
}

export function RouteDetails({selectedRoute, onOpenChange}) {
    const [openRateReview, setOpenRateReview] = useState(false);
    const [openReview, setOpenReview] = useState(false);
    const [rootRef, setRootRef] = useState(null);
    const [value, setValue] = useState('1');


    const controlsRefs = useRef({});

    const setControlRef = (val) => (node) => {
        if (node) {
            controlsRefs.current[val] = node;
        }
    };

    const smallIcon = L.divIcon({
        className: "custom-marker",
        html: '<div style="background-color:#007bff;width:10px;height:10px;border-radius:50%;border:1px solid white;"></div>',
        iconSize: [10, 10],
    });

    const getRouteBounds = (geojson) => {
        const coords = getRouteCoords(geojson);
        if (!coords.length) return null;
        return new LatLngBounds(coords);
    };

    const FitBoundsOnLoad = ({geojson}) => {
        const map = useMap();

        useEffect(() => {
            const coords = getRouteCoords(geojson);
            if (coords.length > 0) {
                map.fitBounds(coords, {padding: [20, 20]});
            }
        }, [geojson, map]);

        return null;
    };

    const getRouteCoords = (geojson) => {
        if (!geojson) return [];
        if (geojson.type === "LineString") return geojson.coordinates.map(c => [c[1], c[0]]);
        if (geojson.type === "MultiLineString") return geojson.coordinates.flat().map(c => [c[1], c[0]]);
        return [];
    };


    return (
        <Dialog open={!!selectedRoute} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-3xl">
                {selectedRoute && (
                    <>
                        <DialogHeader>
                            <DialogTitle>{selectedRoute.name}</DialogTitle>
                            <p className="text-sm text-gray-600">{selectedRoute.description}</p>
                        </DialogHeader>

                        <div className="space-y-3">
                            <div className="flex justify-between text-sm text-gray-700">
                                <p><strong>Distance:</strong> {selectedRoute.distance} km</p>
                                <p><strong>Duration:</strong> {selectedRoute.duration}</p>
                                <p><strong>Difficulty:</strong> {selectedRoute.difficulty}</p>
                            </div>

                            {/* Map Section */}
                            <div className="h-80 w-full rounded-md overflow-hidden">
                                <MapContainer
                                    center={[45.4642, 9.19]} // fallback if geometry is missing
                                    zoom={13}
                                    style={{height: "100%", width: "100%"}}
                                >
                                    <TileLayer
                                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                                        attribution="© OpenStreetMap contributors"
                                    />

                                    {selectedRoute.routeGeometry && (
                                        <>
                                            <SmartRoutePath geojson={selectedRoute.routeGeometry} />

                                            {getRouteCoords(selectedRoute.routeGeometry).map((point, index) => (
                                                <Marker key={index} position={point} icon={smallIcon}>
                                                    <Popup>Point {index + 1}</Popup>
                                                </Marker>
                                            ))}
                                        </>
                                    )}

                                </MapContainer>
                            </div>
                        </div>
                    </>
                )}


                <div>
                    <Button
                        variant="gradient"
                        gradient={{ from: "blue", to: "cyan", deg: 199 }}
                        onClick={() => setOpenRateReview((prev) => !prev)}
                    >
                        {openRateReview ? "Hide rate and review" : "Do you want to rate and review?"}
                    </Button>

                    {openRateReview && (
                        <Stack spacing="md" align="center">
                            {/* Rating */}
                            <div className="text-center text-sm font-medium">
                                Rate your experience exploring this route:
                            </div>
                            <Group justify="center">
                                <Rating fractions={2} defaultValue={0} />
                            </Group>

                            {/* Review button + textarea */}
                            <div className="w-full space-y-3">
                                <Button
                                    variant="gradient"
                                    gradient={{ from: "blue", to: "cyan", deg: 199 }}
                                    onClick={() => setOpenReview((prev) => !prev)}
                                >
                                    {openReview ? "Hide review section" : "Do you want to explain your experience?"}
                                </Button>

                                {openReview && (
                                    <Textarea
                                        variant="filled"
                                        radius="xs"
                                        label="Your thoughts about the route:"
                                        placeholder="You can type your thoughts here."
                                        autosize
                                        minRows={2}
                                    />
                                )}
                            </div>
                        </Stack>
                    )}
            </div>


            <Button
                variant="gradient"
                gradient={{from: 'indigo', to: 'violet', deg: 107}}
                // TODO onsubmit post to the db
            >
                Submit
            </Button>

                <Tabs variant="none" value={value} onChange={setValue}>
                    <Tabs.List ref={setRootRef} className={classes.list}>
                        <Flex
                            mih={50}
                            bg="rgba(0, 0, 0, .3)"
                            gap="md"
                            justify="center"
                            align="flex-start"
                            direction="row"
                            wrap="wrap"
                        >
                            <Tabs.Tab value="1" ref={setControlRef('1')} className={classes.tab}>
                                Reviews
                            </Tabs.Tab>
                            <Tabs.Tab value="2" ref={setControlRef('2')} className={classes.tab}>
                                Ratings
                            </Tabs.Tab>
                            <Tabs.Tab value="3" ref={setControlRef('3')} className={classes.tab}>
                                Images{/* TODO Show the images */}
                            </Tabs.Tab>
                        </Flex>


                        <FloatingIndicator
                            target={value ? controlsRefs[value] : null}
                            parent={rootRef}
                            className={classes.indicator}
                        />
                    </Tabs.List>

                    <Tabs.Panel value="1">
                        <div className="space-y-4 mt-4">
                            {selectedRoute?.reviews && selectedRoute.reviews.length > 0 ? (
                                selectedRoute.reviews.map((review, index) => (
                                    <div
                                        key={index}
                                        className="border border-gray-200 rounded-xl p-4 shadow-sm hover:shadow-md transition-shadow duration-200"
                                    >
                                        <div className="flex items-center justify-between mb-2">
                                            <div className="flex items-center space-x-3">
                                                {/* Avatar with fallback initials */}
                                                <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-blue-500 to-cyan-500 flex items-center justify-center text-white font-medium">
                                                    {review.userId?.username ? review.userId.username[0].toUpperCase() : "U"}
                                                </div>

                                                <div>
                                                    <p className="font-semibold text-gray-800">
                                                        {review.userId?.username || "Unknown User"}
                                                    </p>
                                                    <p className="text-xs text-gray-500">
                                                        {new Date(review.createdAt).toLocaleDateString('en-GB', { year: 'numeric', day: '2-digit', month: 'numeric' })}
                                                    </p>
                                                </div>
                                            </div>

                                            {/* Optional: small star rating if you later connect ratings */}
                                            {review.rating && (
                                                <Rating value={review.rating} readOnly size="sm" />
                                            )}
                                        </div>

                                        <p className="text-sm text-gray-700 whitespace-pre-line">
                                            {review.review}
                                        </p>
                                    </div>
                                ))
                            ) : (
                                <p className="text-gray-500 text-sm text-center mt-6">
                                    No reviews yet. Be the first to share your experience!
                                </p>
                            )}
                        </div>
                    </Tabs.Panel>

                    <Tabs.Panel key={selectedRoute?.id} value="2">

                        <div className="space-y-4 mt-4">
                            {Array.isArray(selectedRoute?.ratings) && selectedRoute.ratings.length > 0 ? (
                                selectedRoute.ratings.map((rating, index) => (

                                    <div
                                        key={index}
                                        className="border border-gray-200 rounded-xl p-4 shadow-sm hover:shadow-md transition-shadow duration-200"
                                    >
                                        <div className="flex items-center justify-between mb-2">
                                            <div className="flex items-center space-x-3">
                                                {/* Avatar */}
                                                <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-blue-500 to-cyan-500 flex items-center justify-center text-white font-medium">
                                                    {rating.userId?.username
                                                        ? rating.userId.username[0].toUpperCase()
                                                        : "U"}
                                                </div>

                                                <div>
                                                    <p className="font-semibold text-gray-800">
                                                        {rating.userId?.username || "Unknown User"}
                                                    </p>
                                                    <p className="text-xs text-gray-500">
                                                        {rating.createdAt
                                                            ? new Date(rating.createdAt).toLocaleDateString("en-GB", {
                                                                year: "numeric",
                                                                day: "2-digit",
                                                                month: "numeric",
                                                            })
                                                            : ""}
                                                    </p>
                                                </div>
                                            </div>

                                            {typeof rating.rating === "number" && (
                                                <Rating value={rating.rating} fractions={2} readOnly size="sm" />
                                            )}
                                        </div>
                                    </div>
                                ))
                            ) : selectedRoute?.ratings ? (
                                <p>No ratings yet. Be the first to share your experience!</p>
                            ) : (
                                <p>Loading ratings...</p>
                            )}
                        </div>
                    </Tabs.Panel>

                    <Tabs.Panel value="3">
                        {/*TODO Rating*/}
                    </Tabs.Panel>
                </Tabs>


        </DialogContent>
</Dialog>

)
    ;
}
