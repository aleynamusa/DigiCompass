import { useEffect, useState } from "react";
import { mapRoute } from "@/api/routeApi.jsx";

export const useGraphHopperRoute = (points, routeType) => {
    const [ghRoute, setGhRoute] = useState([]);
    const [distance, setDistance] = useState(0);
    const [estimatedTime, setEstimatedTime] = useState("");
    const [ghError, setGhError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (points.length < 2) {
            setGhRoute([]);
            setDistance(0);
            setEstimatedTime("");
            setGhError(null);
            return;
        }

        const fetchRoute = async () => {
            setIsLoading(true);
            setGhError(null);

            try {
                const res = await mapRoute({ points, routeType });

                // axios response
                const data = res.data;

                setGhRoute(data.route ?? []);
                setDistance(data.distanceKm ?? 0);
                setEstimatedTime(
                    data.durationMin != null ? `${data.durationMin} min` : ""
                );
            } catch (e) {
                setGhError(e.message || "Route failed");
            } finally {
                setIsLoading(false);
            }
        };

        fetchRoute();
    }, [points, routeType]);

    return {
        ghRoute,
        distance,
        estimatedTime,
        ghError,
        isLoading,
    };
};
