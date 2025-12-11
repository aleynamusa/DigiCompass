// hooks/useGraphHopperRoute.js
import { useState, useEffect } from 'react';
import polyline from "@mapbox/polyline";
import debounce from "lodash.debounce";
import { GRAPHHOPPER_CONFIG, PROFILE_MAP } from '../constants/routeConfig';

const fetchGraphHopperRoute = async (points, routeType) => {
    const profile = PROFILE_MAP[routeType] || "foot";
    const { baseUrl, apiKey } = GRAPHHOPPER_CONFIG;

    const params = new URLSearchParams({
        profile,
        points_encoded: true,
        locale: "en",
    });

    points.forEach(p => {
        params.append("point", `${p.lat},${p.lng}`);
    });

    const url = `${baseUrl}?${params.toString()}&key=${apiKey}`;
    const res = await fetch(url);

    if (!res.ok) {
        const errorData = await res.json().catch(() => ({}));
        throw new Error(errorData.message || "Failed to fetch GraphHopper route");
    }

    const data = await res.json();
    return data.paths?.[0];
};

// Split points into segments if there are too many
const segmentPoints = (points, maxPointsPerSegment = 5) => {
    if (points.length <= maxPointsPerSegment) {
        return [points];
    }

    const segments = [];
    for (let i = 0; i < points.length - 1; i += maxPointsPerSegment - 1) {
        const end = Math.min(i + maxPointsPerSegment, points.length);
        segments.push(points.slice(i, end));
    }

    return segments;
};

export const useGraphHopperRoute = (points, routeType) => {
    const [ghRoute, setGhRoute] = useState(null);
    const [distance, setDistance] = useState(0);
    const [estimatedTime, setEstimatedTime] = useState('');
    const [ghError, setGhError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (points.length < 2) {
            setGhRoute(null);
            setDistance(0);
            setEstimatedTime('');
            setGhError(null);
            return;
        }

        const updateRoute = debounce(async () => {
            setIsLoading(true);
            setGhError(null);

            try {
                // Split into segments if needed (max 5 points per request for reliability)
                const segments = segmentPoints(points, 5);

                // Fetch all segments
                const segmentResults = await Promise.all(
                    segments.map(segment => fetchGraphHopperRoute(segment, routeType))
                );

                // Check if all segments succeeded
                if (segmentResults.some(result => !result || !result.points)) {
                    throw new Error("One or more route segments failed");
                }

                // Combine all route coordinates
                let allCoordinates = [];
                let totalDistance = 0;
                let totalTime = 0;

                segmentResults.forEach((segment, index) => {
                    const decoded = polyline.decode(segment.points);

                    // For segments after the first, skip the first point to avoid duplication
                    if (index > 0 && decoded.length > 0) {
                        decoded.shift();
                    }

                    allCoordinates = allCoordinates.concat(decoded);
                    totalDistance += segment.distance || 0;
                    totalTime += segment.time || 0;
                });

                setGhRoute(allCoordinates.map(([lat, lng]) => ({ lat, lng })));
                setDistance(totalDistance / 1000); // Convert to km
                setEstimatedTime(Math.round(totalTime / 60000) + " min"); // Convert to minutes
                setGhError(null);

            } catch (error) {
                console.error("GraphHopper error:", error);
                setGhRoute(null);
                setGhError(error.message || "Failed to calculate route");
            } finally {
                setIsLoading(false);
            }
        }, 800);

        updateRoute();

        return () => {
            updateRoute.cancel?.();
        };
    }, [points, routeType]);

    return { ghRoute, distance, estimatedTime, ghError, isLoading };
};