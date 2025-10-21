import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Badge} from "@/components/ui/badge"
import {Input} from "@/components/ui/input"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { MapContainer, TileLayer, Polyline, Marker, Popup, useMap } from "react-leaflet"
import {
    MapPinIcon,
    SearchIcon,
    FilterIcon,
    MountainIcon,
    BikeIcon,
    FootprintsIcon,
    StarIcon,
    ClockIcon,
    TrendingUpIcon,
    UsersIcon,
    HeartIcon,
    ShareIcon,
} from "lucide-react"
import {CgAdd} from "react-icons/cg";
import {Sidebar} from "@/components/sidebar.jsx";
import {useEffect, useState} from "react";
import axios from "axios";
import L, { LatLngBounds }  from "leaflet";



const API_URL = import.meta.env.VITE_BACKEND_URL;
const RouteDiscovery = () => {
    const [routes, setRoutes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedRoute, setSelectedRoute] = useState(null);
    // selectedRoute.routeGeometry = undefined;

    useEffect(() => {
        const fetchRoutes = async () => {
            try {
                const response = await axios.get(`${API_URL}/route`);
                setRoutes(response.data);
            } catch (err) {
                console.error(err);
                setError(err.response?.data?.message || "Failed to fetch routes");
            } finally {
                setLoading(false);
            }
        };

        fetchRoutes();
    }, []);

    if (loading) {
        return (
            <div className="flex items-center justify-center h-screen">
                <p>Loading routes...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex items-center justify-center h-screen text-red-500">
                <p>{error}</p>
            </div>
        );
    }

    const handleViewDetails = async (route) => {
        try {
            const response = await axios.get(`${API_URL}/route/${route.id}/geometry`);
            const geojson = response.data.geojson;

            console.log("Fetched GeoJSON:", geojson);

            setSelectedRoute({
                ...route,
                routeGeometry: geojson,
            });
        } catch (err) {
            console.error("Failed to fetch route geometry:", err);
        }
    };

    const getRouteCoords = (geojson) => {
        if (!geojson) return [];
        if (geojson.type === "LineString") return geojson.coordinates.map(c => [c[1], c[0]]);
        if (geojson.type === "MultiLineString")
            return geojson.coordinates.flat().map(c => [c[1], c[0]]);
        return [];
    };

    const getRouteBounds = (geojson) => {
        const coords = getRouteCoords(geojson);
        if (!coords.length) return null;
        return new LatLngBounds(coords);
    };

    const FitBoundsOnLoad = ({ geojson }) => {
        const map = useMap();

        useEffect(() => {
            const coords = getRouteCoords(geojson);
            if (coords.length > 0) {
                map.fitBounds(coords, { padding: [20, 20] });
            }
        }, [geojson, map]);

        return null;
    };

    const smallIcon = L.divIcon({
        className: "custom-marker",
        html: '<div style="background-color:#007bff;width:10px;height:10px;border-radius:50%;border:1px solid white;"></div>',
        iconSize: [10, 10],
    });

    return (
        <div className="h-screen w-full relative">
            {/* Fixed sidebar */}
            <Sidebar />

            {/* Main content shifted to the right */}
            <main
                className="h-full overflow-y-auto p-3"
                style={{ marginLeft: "var(--sidebar-width)" }}
            >
                <div className=" flex items-center justify-between text-left">
                    <div>
                        <h1 style={{color: "#3C5862"}} className="text-2xl font-bold">Discover Routes</h1>

                        <p className="pb-3 pt-3" style={{color: "#88928F"}}>Find your next adventure from thousands of
                            curated routes</p>
                    </div>
                    <Button style={{backgroundColor: "#105174"}}
                            className="bg-cyan-950 text-primary-foreground hover:bg-primary/90  gap-2">
                        <CgAdd className="h-4 w-4"/>
                        Create Route
                    </Button>
                </div>

                {/* Search and Filters */}
                <Card style={{backgroundColor: "#E3E0E0"}}>
                    <CardContent className="p-5">
                        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">

                            {/* Search Input */}
                            <div className="relative flex-1 min-w-[250px]">
                                <SearchIcon
                                    className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground"/>
                                <Input
                                    placeholder="Search routes by name, location, or tags..."
                                    className="pl-10 w-full"
                                />
                            </div>

                            {/* Filters */}
                            <div
                                className="flex flex-wrap md:flex-nowrap gap-3 justify-between md:justify-end w-full md:w-auto">
                                <Select className="size-32">
                                    <SelectTrigger className="min-w-[150px]">
                                        <SelectValue placeholder="Type"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="all">All Types</SelectItem>
                                        <SelectItem value="hiking">Hiking</SelectItem>
                                        <SelectItem value="cycling">Cycling</SelectItem>
                                        <SelectItem value="walking">Walking</SelectItem>
                                    </SelectContent>
                                </Select>

                                <Select className="size-52">
                                    <SelectTrigger className="min-w-[150px]">
                                        <SelectValue placeholder="Difficulty"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="all">All Levels</SelectItem>
                                        <SelectItem value="easy">Easy</SelectItem>
                                        <SelectItem value="moderate">Moderate</SelectItem>
                                        <SelectItem value="hard">Hard</SelectItem>
                                    </SelectContent>
                                </Select>

                                <Button variant="outline" size="icon">
                                    <FilterIcon className="h-4 w-4"/>
                                </Button>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Tabs defaultValue="all" className="p-3">
                    <TabsList className="flex w-full bg-zinc-400 gap-12 p-1">
                        <TabsTrigger value="all" default>All Routes</TabsTrigger>
                        <TabsTrigger value="popular">Popular</TabsTrigger>
                        <TabsTrigger value="nearby">Nearby</TabsTrigger>
                        <TabsTrigger value="saved">Saved</TabsTrigger>
                    </TabsList>


                    <TabsContent value="all" className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

                            {routes.map((route) => (
                                <Card
                                    key={route.id}
                                    className="overflow-hidden hover:shadow-lg transition-shadow"
                                >
                                    <div className="relative">
                                        <img
                                            src={"/vite.svg"}
                                            alt={route.name}
                                            className="w-full h-48 object-cover"
                                        />
                                        <div className="absolute top-2 right-2 flex gap-1">
                                            <Button
                                                size="icon"
                                                variant="secondary"
                                                className="h-8 w-8 bg-white/80 hover:bg-white"
                                            >
                                                <HeartIcon className="h-4 w-4"/>
                                            </Button>
                                            <Button
                                                size="icon"
                                                variant="secondary"
                                                className="h-8 w-8 bg-white/80 hover:bg-white"
                                            >
                                                <ShareIcon className="h-4 w-4"/>
                                            </Button>
                                        </div>
                                        <div className="absolute top-2 left-2">
                                            <Badge className="border-0">
                                                {route.difficulty || "Unknown"}
                                            </Badge>
                                        </div>
                                    </div>

                                    <CardHeader className="pb-2">
                                        <div className="flex items-start justify-between">
                                            <CardTitle className="text-lg">
                                                {route.name}
                                            </CardTitle>
                                            <div className="flex items-center gap-1 text-sm">
                                                <StarIcon className="h-4 w-4 fill-yellow-400 text-yellow-400"/>
                                                <span className="font-medium">
                                                    {route.rating || "4.5"}
                                    </span>
                                            </div>
                                        </div>
                                        <CardDescription className="text-sm text-left">
                                            {route.description}
                                        </CardDescription>
                                    </CardHeader>

                                    <CardContent className="space-y-3">
                                        <div className="flex items-center justify-between text-sm">
                                            <div className="flex items-center gap-1">
                                                <span className="capitalize">
                                                    {route.routeType}
                                                </span>
                                            </div>
                                            <div className="flex items-center gap-1">
                                                <UsersIcon className="h-4 w-4 text-muted-foreground"/>
                                                <span>
                                                    {route.createdByUserId?.username || "Unknown"}
                                                </span>
                                            </div>
                                        </div>

                                        <div className="grid grid-cols-3 gap-2 text-sm">
                                            <div className="flex items-center gap-1">
                                                <MapPinIcon className="h-4 w-4 text-muted-foreground"/>
                                                <span>{route.distance} km</span>
                                            </div>
                                            <div className="flex items-center gap-1">
                                                <ClockIcon className="h-4 w-4 text-muted-foreground"/>
                                                <span>{route.duration}</span>
                                            </div>
                                            <div className="flex items-center gap-1">
                                                <TrendingUpIcon className="h-4 w-4 text-muted-foreground"/>
                                                <span>{route.difficulty}</span>
                                            </div>
                                        </div>

                                        <div className="flex gap-2 pt-2">
                                            <Button className="flex-1" size="sm" onClick={() => handleViewDetails(route)}>
                                                View Details
                                            </Button>
                                            <Button variant="outline" size="sm">
                                                Add to Trip
                                            </Button>
                                        </div>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>
                    </TabsContent>

                    <TabsContent value="popular">...</TabsContent>
                    <TabsContent value="nearby">...</TabsContent>
                    <TabsContent value="saved">...</TabsContent>

                </Tabs>


            </main>


            {/* Route Details Popup */}
            <Dialog open={!!selectedRoute} onOpenChange={() => setSelectedRoute(null)}>
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
                                        style={{ height: "100%", width: "100%" }}
                                    >
                                        <TileLayer
                                            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                                            attribution="© OpenStreetMap contributors"
                                        />

                                        {/* Auto-fit map to the route */}
                                        {selectedRoute.routeGeometry && <FitBoundsOnLoad geojson={selectedRoute.routeGeometry} />}

                                        {/* Draw the polyline */}
                                        {selectedRoute.routeGeometry && (
                                            <Polyline positions={getRouteCoords(selectedRoute.routeGeometry)} color="blue" />
                                        )}


                                        {selectedRoute.routeGeometry?.coordinates && (
                                            getRouteCoords(selectedRoute.routeGeometry).map((point, index) => (
                                                <Marker key={index} position={point} icon={smallIcon}>
                                                    <Popup>Point {index + 1}</Popup>
                                                </Marker>
                                            ))
                                        )}

                                    </MapContainer>

                                </div>
                            </div>
                        </>
                    )}
                </DialogContent>
            </Dialog>
        </div>
    );
};

export default RouteDiscovery;
