import {Card, CardContent} from "@/components/ui/card.jsx"
import {Button} from "@/components/ui/button.jsx"
import {useCallback, useEffect, useState} from "react";
import {RouteDetails} from "@/components/routeDetails/route_details.jsx";
import {useLogin} from "@/hooks/useLogin.jsx";
import LoginPopup from "@/components/routeDiscovery/LoginPopUp.jsx";
import FiltersBar from "@/components/routeDiscovery/filtersBar.jsx";
import {getFilteredRoutes, getRouteGeometry, getRoutes, searchRoutesByKeyword} from "@/api/routeApi.jsx";
import RouteTabsDiscovery from "@/components/routeDiscovery/routeTabs.jsx";
import {AlertCircle, CheckCircle, CirclePlus} from "lucide-react";
import CreateRoutePopup from "@/components/ceateRoutePopup/createRoutePopup.jsx";

const RouteDiscovery = () => {
    const {
        loginData,
        handleChange,
        handleSubmit,
        isChecked,
        setChecked,
        errors
    } = useLogin(() => {
        setShowLoginPopup(false);
        window.location.reload();
    });

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
    const [showLoginPopup, setShowLoginPopup] = useState(false);
    const [showRoutePopup, setShowRoutePopup] = useState(false);
    const [notifications, setNotifications] = useState([]);
    const [saveLoading, setSaveLoading] = useState(false);


    const fetchRoutes = useCallback(async () => {
        try {
            const response = await getRoutes();
            setRoutes(response.data);
            console.log(response.data);
        } catch (err) {
            setError("There has been a problem and the data is unavailable at the moment.");
        } finally {
            setLoading(false);
        }
    }, []);


    useEffect(() => {
        fetchRoutes();
    }, []);

    const handleViewDetails = async (route) => {
        try {
            const response = await getRouteGeometry(route.id);
            const geojson = response.data.geojson;

            setSelectedRoute({
                ...route,
                routeGeometry: geojson,
            });

        } catch (err) {
            console.error(err);
            setError("There has been a problem and the data is unavailable at the moment.");
        }

    };

    const handleSearch = async (keyword) => {
        try {
            const response = await searchRoutesByKeyword(keyword);
            setSearchRoutes(response.data);
        } catch (err) {
            console.error(err);
            setError("There has been a problem and the data is unavailable at the moment.");
        }
    };

    const handleFilter = async (type, difficulty, distanceRange) => {
        try {
            if (type === "all" && difficulty === "all" && distanceRange === "all") {
                const res = await getRoutes();
                setFilteredRoutes(res.data);
                return;
            }

            const res = await getFilteredRoutes(type, difficulty, distanceRange);
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

        await fetchRoutes();

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
        <div className="w-full min-h-screen p-6">

            <div className="flex justify-between mb-6">
                <h1 className="text-3xl font-bold text-cyan-950">Discover Routes</h1>
                <Button onClick={() => setShowRoutePopup(true)} className="bg-cyan-950"> <CirclePlus></CirclePlus> Create Route</Button>
            </div>

            <Card>
                <CardContent>
                    <FiltersBar
                        searchTerm={searchTerm}
                        setSearchTerm={setSearchTerm}
                        selectedType={selectedType}
                        setSelectedType={setSelectedType}
                        selectedDifficulty={selectedDifficulty}
                        setSelectedDifficulty={setSelectedDifficulty}
                        selectedDistanceRange={selectedDistanceRange}
                        setSelectedDistanceRange={setSelectedDistanceRange}
                        onReset={handleResetFilters}
                    />
                </CardContent>
            </Card>

            <RouteTabsDiscovery
                displayRoutes={displayRoutes}
                onViewDetails={handleViewDetails}
                onLoginRequired={() => setShowLoginPopup(true)}
            />

            <RouteDetails
                selectedRoute={selectedRoute}
                onOpenChange={() => setSelectedRoute(null)}
            />

            <LoginPopup
                show={showLoginPopup}
                onClose={() => setShowLoginPopup(false)}
                {...{ loginData, handleChange, handleSubmit, isChecked, setChecked, errors }}
            />

            {/* Add the CreateRoutePopup */}
            <CreateRoutePopup
                isOpen={showRoutePopup}
                onClose={() => setShowRoutePopup(false)}
                // onSave={handleSaveRoute}
                loading={saveLoading}
            />

            {/* Custom Notification Component (if you don't have one) */}
            <div className="fixed top-4 right-4 z-50 space-y-2">
                {notifications.map((notification) => (
                    <div
                        key={notification.id}
                        className={`p-4 rounded-lg shadow-lg border ${
                            notification.color === 'green'
                                ? 'bg-green-50 border-green-200 text-green-800'
                                : 'bg-red-50 border-red-200 text-red-800'
                        }`}
                    >
                        <div className="flex items-center gap-2">
                            {notification.color === 'green' ? (
                                <CheckCircle className="h-5 w-5" />
                            ) : (
                                <AlertCircle className="h-5 w-5" />
                            )}
                            <div>
                                <h4 className="font-semibold">{notification.title}</h4>
                                <p className="text-sm">{notification.message}</p>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

        </div>
    );
};

export default RouteDiscovery;
