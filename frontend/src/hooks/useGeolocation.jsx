import { useState, useEffect } from 'react';
import { DEFAULT_LOCATION, GEOLOCATION_OPTIONS } from '../constants/routeConfig';

export const useGeolocation = (isActive = true) => {
    const [location, setLocation] = useState({
        latitude: DEFAULT_LOCATION?.latitude ?? DEFAULT_LOCATION?.lat ?? 0,
        longitude: DEFAULT_LOCATION?.longitude ?? DEFAULT_LOCATION?.lng ?? 0,
        accuracy: DEFAULT_LOCATION?.accuracy ?? null,
        error: null
    });

    useEffect(() => {
        let watchId;

        if (!isActive) return;

        if (!navigator.geolocation) {
            setLocation((loc) => ({ ...loc, error: 'Geolocation is not available in this browser.' }));
            return;
        }

        const onSuccess = (pos) => {
            setLocation({
                latitude: pos.coords.latitude,
                longitude: pos.coords.longitude,
                accuracy: pos.coords.accuracy,
                error: null
            });
        };

        const onError = (err) => {
            console.error("Geolocation error:", err);
            let message = err?.message || 'Unable to retrieve location';
            if (err?.code === 1) message = 'Geolocation permission denied';
            setLocation((loc) => ({ ...loc, error: message }));
        };

        // Check permission state first if supported (gives faster feedback if denied)
        if (navigator.permissions && navigator.permissions.query) {
            navigator.permissions.query({ name: 'geolocation' }).then((perm) => {
                if (perm.state === 'denied') {
                    setLocation((loc) => ({ ...loc, error: 'Geolocation permission denied' }));
                    return;
                }
                // Request current position then start watching
                navigator.geolocation.getCurrentPosition(onSuccess, onError, GEOLOCATION_OPTIONS);
                watchId = navigator.geolocation.watchPosition(onSuccess, onError, GEOLOCATION_OPTIONS);
            }).catch(() => {
                // Fallback if permissions API is unavailable
                navigator.geolocation.getCurrentPosition(onSuccess, onError, GEOLOCATION_OPTIONS);
                watchId = navigator.geolocation.watchPosition(onSuccess, onError, GEOLOCATION_OPTIONS);
            });
        } else {
            navigator.geolocation.getCurrentPosition(onSuccess, onError, GEOLOCATION_OPTIONS);
            watchId = navigator.geolocation.watchPosition(onSuccess, onError, GEOLOCATION_OPTIONS);
        }

        return () => {
            if (watchId !== undefined) navigator.geolocation.clearWatch(watchId);
        };
    }, [isActive]);

    return location;
};