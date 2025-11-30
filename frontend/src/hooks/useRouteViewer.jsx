import { useState, useEffect, useCallback } from "react";
import axios from "axios";

const API_URL = import.meta.env.VITE_BACKEND_URL;

export function useRouteViewer(initialRoutes = []) {
    const [routes, setRoutes] = useState(initialRoutes);
    const [filteredRoutes, setFilteredRoutes] = useState([]);
    const [searchRoutes, setSearchRoutes] = useState([]);
    const [selectedRoute, setSelectedRoute] = useState(null);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [searchTerm, setSearchTerm] = useState("");
    const [selectedType, setSelectedType] = useState("all");
    const [selectedDifficulty, setSelectedDifficulty] = useState("all");
    const [selectedDistanceRange, setSelectedDistanceRange] = useState("all");

    const fetchRoutes = useCallback(async () => {
        try {
            const response = await axios.get(`${API_URL}/route`);
            setRoutes(response.data);
            console.log(response.data);
        } catch (err) {
            setError("Failed to load routes");
        } finally {
            setLoading(false);
        }
    }, []);

    const handleViewDetails = async (route) => {
        try {
            const response = await axios.get(`${API_URL}/route/${route.id}/geometry`);
            const geojson = response.data.geojson;

            setSelectedRoute({
                ...route,
                routeGeometry: geojson,
            });
        } catch (err) {
            setError("Failed to load route details");
        }
    };


    const handleSearch = async (keyword) => {
        try {
            const response = await axios.get(`${API_URL}/route/keyword?keyword=${keyword}`);
            setSearchRoutes(response.data);
        } catch {
            setError("Search failed");
        }
    };

    // Filtering (type, difficulty, distance)
    const handleFilter = async (type, difficulty, distanceRange) => {
        try {
            const params = new URLSearchParams();

            if (type !== "all") params.append("type", type);
            if (difficulty !== "all") params.append("difficulty", difficulty);

            if (distanceRange === "short") params.append("distance", "5");
            if (distanceRange === "medium") params.append("distance", "15");
            if (distanceRange === "long") params.append("distance", "100");

            if (params.toString() === "") {
                setFilteredRoutes(routes);
                return;
            }

            const response = await axios.get(`${API_URL}/route/filter?${params.toString()}`);
            setFilteredRoutes(response.data);
        } catch {
            setError("Filter failed");
        }
    };

    // Reset filters
    const resetFilters = async () => {
        setSelectedType("all");
        setSelectedDifficulty("all");
        setSelectedDistanceRange("all");
        setSearchTerm("");
        setSearchRoutes([]);
        await fetchRoutes();
    };

    // Auto-run search
    useEffect(() => {
        const delay = setTimeout(() => {
            if (searchTerm.trim()) handleSearch(searchTerm);
            else setSearchRoutes([]);
        }, 400);
        return () => clearTimeout(delay);
    }, [searchTerm]);

    // Auto-run filter
    useEffect(() => {
        handleFilter(selectedType, selectedDifficulty, selectedDistanceRange);
    }, [selectedType, selectedDifficulty, selectedDistanceRange]);

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

    return {
        routes,
        displayRoutes,
        loading,
        error,
        selectedRoute,
        searchTerm,
        setSearchTerm,
        selectedType,
        setSelectedType,
        selectedDifficulty,
        setSelectedDifficulty,
        selectedDistanceRange,
        setSelectedDistanceRange,
        setLoading,
        fetchRoutes,
        handleViewDetails,
        resetFilters,
        setSelectedRoute,
    };
}
