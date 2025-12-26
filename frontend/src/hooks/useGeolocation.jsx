import { useState, useEffect, useMemo } from 'react';
import { GEOLOCATION_OPTIONS, DEFAULT_LOCATION } from '../constants/routeConfig';

export const useGeolocation = (isActive = true) => {
    const [location, setLocation] = useState({
        latitude: null,
        longitude: null,
        accuracy: null,
        error: null,
        ready: false
    });

    useEffect(() => {
        let watchId;

        if (!isActive) return;

        if (!navigator.geolocation) {
            setLocation(loc => ({
                ...loc,
                error: 'Geolocation is not available in this browser.'
            }));
            return;
        }

        const onSuccess = (pos) => {
            setLocation({
                latitude: pos.coords.latitude,
                longitude: pos.coords.longitude,
                accuracy: pos.coords.accuracy,
                error: null,
                ready: true
            });
        };

        const onError = (err) => {
            if (err?.code === 3) return; // timeout keeps last

            let message = err?.message || 'Unable to retrieve location';
            if (err?.code === 1) message = 'Geolocation permission denied';
            if (err?.code === 2) message = 'Location unavailable';

            setLocation(loc => ({
                ...loc,
                error: message,
                ready: false
            }));
        };

        navigator.geolocation.getCurrentPosition(onSuccess, onError, GEOLOCATION_OPTIONS);
        watchId = navigator.geolocation.watchPosition(onSuccess, onError, GEOLOCATION_OPTIONS);

        return () => {
            if (watchId !== undefined) navigator.geolocation.clearWatch(watchId);
        };
    }, [isActive]);

    const effectiveLocation = useMemo(() => {
        if (location.ready) return location;

        return {
            latitude: DEFAULT_LOCATION.latitude,
            longitude: DEFAULT_LOCATION.longitude,
            ready: false,
            isFallback: true
        };
    }, [location]);

    return {
        ...location,
        effectiveLocation
    };
};
