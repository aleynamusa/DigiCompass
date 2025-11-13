import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.jsx"
import {Button} from "@/components/ui/button.jsx"
import {RouteCard} from "@/components/route_card.jsx"
import {Input} from "@/components/ui/input.jsx"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.jsx"
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs.jsx"
import {
    SearchIcon
} from "lucide-react"
import {CgAdd} from "react-icons/cg";
import {Sidebar} from "@/components/sidebar.jsx";
import {useCallback, useEffect, useState} from "react";
import axios from "axios";
import {RouteDetails} from "@/components/routeDetails/route_details.jsx";

const API_URL = import.meta.env.VITE_BACKEND_URL;
const RouteDiscovery = () => {
    const [routes, setRoutes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedRoute, setSelectedRoute] = useState(null);
    const [searchRoutes, setSearchRoutes] = useState([]);
    const [filteredRoutes, setFilteredRoutes] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");

    const [selectedType, setSelectedType] = useState("all");
    const [selectedDifficulty, setSelectedDifficulty] = useState("all");
    const [selectedDistanceRange, setSelectedDistanceRange] = useState("all");

    const fetchRoutes = useCallback(async () => {
        try {
            const response = await axios.get(`${API_URL}/route`);
            setRoutes(response.data);
        } catch (err) {
            setError("There has been a problem and the data is unavailable at the moment.");
        } finally {
            setLoading(false);
        }
    }, [API_URL]);


    useEffect(() => {
        fetchRoutes();
    }, []);

    const handleViewDetails = async (route) => {
        try {
            const response = await axios.get(`${API_URL}/route/${route.id}/geometry`);
            const geojson = response.data.geojson;

            console.log("Fetched GeoJSON:", geojson);

            console.log("Review", response.data)

            setSelectedRoute({
                ...response.data,
                routeGeometry: geojson,
            });
        } catch (err) {
            console.error(err);
            setError("There has been a problem and the data is unavailable at the moment.");
        }

    };

    const handleSearch = async (keyword) => {
        try {
            const response = await axios.get(`${API_URL}/route/keyword?keyword=${keyword}`);
            setSearchRoutes(response.data);
        } catch (err) {
            console.error(err);
            setError("There has been a problem and the data is unavailable at the moment.");
        }
    };

    const handleFilter = async (type, difficulty, distanceRange) => {
        try {
            if (type === "all" && difficulty === "all" && distanceRange === "all") {
                const res = await axios.get(`${API_URL}/route`);
                setFilteredRoutes(res.data);
                return;
            }

            const params = new URLSearchParams();
            if (type !== "all") params.append("type", type);
            if (difficulty !== "all") params.append("difficulty", difficulty);

            if (distanceRange !== "all") {
                if (distanceRange === "short") params.append("distance", "5");
                if (distanceRange === "medium") params.append("distance", "15");
                if (distanceRange === "long") params.append("distance", "100");
            }

            const res = await axios.get(`${API_URL}/route/filter?${params.toString()}`);
            setFilteredRoutes(res.data);
        } catch (err) {
            console.error(err);
            setError("There has been a problem and the data is unavailable at the moment.");
        }

    };

    useEffect(() => {
        const delay = setTimeout(() => {
            if (searchTerm.trim()) {
                handleSearch(searchTerm);
            } else {
                setSearchRoutes([]);
            }
        }, 500); //waits 0.5s after typing stops

        return () => clearTimeout(delay);
    }, [searchTerm]);

    useEffect(() => {
        handleFilter(selectedType, selectedDifficulty, selectedDistanceRange);
    }, [selectedType, selectedDifficulty, selectedDistanceRange]);



    const handleResetFilters = async () => {
        setSelectedType("all");
        setSelectedDifficulty("all");
        setSelectedDistanceRange("all");
        setSearchTerm("");
        setSearchRoutes([]);

        // Load all routes again
        await fetchRoutes();
        setFilteredRoutes(res.data);
    };

    const isFiltering =
        selectedType !== "all" ||
        selectedDifficulty !== "all" ||
        selectedDistanceRange !== "all";

    const isSearching = searchTerm.trim().length > 0;

    const baseRoutes = isFiltering ? filteredRoutes : routes;

    const displayRoutes = isSearching
        ? baseRoutes.filter((route) =>
            route.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            route.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            route.routeType?.toLowerCase().includes(searchTerm.toLowerCase())
        )
        : baseRoutes;

    // Needs to be used after every hook is defined others doesnt work
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

    return (
        <div className="h-screen w-full relative">
            <Sidebar />

            <div
                className="h-full overflow-y-auto p-3"
                // style={{ marginLeft: "var(--sidebar-width)" }}
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

                <Card style={{backgroundColor: "#E3E0E0"}}>
                    <CardContent className="p-5">
                        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">

                            <div className="relative flex-1 min-w-[250px]">
                                <SearchIcon
                                    className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground"/>
                                <Input
                                    placeholder="Search routes by name, location, or tags..."
                                    className="pl-10 w-full"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                            </div>

                            <div
                                className="flex flex-wrap md:flex-nowrap gap-3 justify-between md:justify-end w-full md:w-auto">
                                <Select value={selectedType} onValueChange={setSelectedType} className="size-32"
                                >
                                    <SelectTrigger className="min-w-[150px]">
                                        <SelectValue placeholder="Type"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="all">All Types</SelectItem>
                                        <SelectItem value="hiking">Hiking</SelectItem>
                                        <SelectItem value="cycling">Cycling</SelectItem>
                                        <SelectItem value="walking">Walking</SelectItem>
                                        <SelectItem value="kayaking">Kayaking</SelectItem>
                                    </SelectContent>
                                </Select>

                                <Select className="size-52"
                                        value={selectedDifficulty} onValueChange={setSelectedDifficulty}>
                                    <SelectTrigger className="min-w-[150px]">
                                        <SelectValue placeholder="Difficulty"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="all">All Levels</SelectItem>
                                        <SelectItem value="EASY">Easy</SelectItem>
                                        <SelectItem value="MEDIUM">Medium</SelectItem>
                                        <SelectItem value="HARD">Hard</SelectItem>
                                    </SelectContent>
                                </Select>

                                <Select value={selectedDistanceRange} onValueChange={setSelectedDistanceRange}>
                                    <SelectTrigger className="min-w-[150px]">
                                        <SelectValue placeholder="Distance" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="all">All Distances</SelectItem>
                                        <SelectItem value="short">0–5 km</SelectItem>
                                        <SelectItem value="medium">5–15 km</SelectItem>
                                        <SelectItem value="long">15+ km</SelectItem>
                                    </SelectContent>
                                </Select>

                                <Button variant="outline" onClick={handleResetFilters}>
                                    Reset
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

                            {displayRoutes.length === 0 ? (
                                <div className="text-center text-gray-600 col-span-full py-10">
                                    <p className="text-lg font-medium">No routes found</p>
                                    <p className="text-sm text-gray-500">Try adjusting your filters or search keywords.</p>
                                </div>
                            ) : (
                                displayRoutes.map((route) => (
                                    <RouteCard key={route.id} route={route} onViewDetails={handleViewDetails} />

                                )))}
                        </div>
                    </TabsContent>

                    <TabsContent value="popular">...</TabsContent>
                    <TabsContent value="nearby">...</TabsContent>
                    <TabsContent value="saved">...</TabsContent>
                </Tabs>
            </div>

            <RouteDetails
                selectedRoute={selectedRoute}
                onOpenChange={() => setSelectedRoute(null)}
            />


        </div>
    );
};

export default RouteDiscovery;
