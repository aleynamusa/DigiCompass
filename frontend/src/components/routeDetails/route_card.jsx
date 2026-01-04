import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.jsx";
import {
    ClockIcon,
    HeartIcon,
    MapPinIcon,
    StarIcon, TrendingUpIcon,
    UsersIcon, Trash
} from "lucide-react";
import {Button} from "@/components/ui/button.jsx";
import {Badge} from "@/components/ui/badge.jsx";
import {Carousel} from "@mantine/carousel";
import { useNavigate } from "react-router-dom";
import classes from '@/components/card.module.css';
import {useAuth} from "@/context/AuthContext.jsx";
import {favouriteRoute, unfavoriteRoute} from "@/api/userApi.jsx";
import { deleteRoute } from "@/api/routeApi.jsx";
import React, {useEffect, useState} from "react";
import {isLiked} from "@/api/routeApi.jsx";

export function RouteCard({ route, onViewDetails, onLoginRequired, onDelete, onAddToTrip }) {
    const navigate = useNavigate();
    const [isFavorite, setIsFavorite] = useState(false);
    const { user } = useAuth();

    // Normalize roles/authorities from different backend shapes:
    const getRolesFromUser = (u) => {
        if (!u) return [];
        if (Array.isArray(u.roles) && u.roles.length) return u.roles.map(r => String(r)).filter(Boolean);
        if (Array.isArray(u.authorities) && u.authorities.length) {
            return u.authorities.map(a => {
                if (!a) return "";
                if (typeof a === "string") return a;
                return String(a.authority || a.role || "");
            }).filter(Boolean);
        }
        if (u.role) {
            if (typeof u.role === "string") return [u.role].filter(Boolean);
            if (typeof u.role === "object") {
                const r = String(u.role.role || u.role.name || u.role.authority || "");
                return r ? [r] : [];
            }
            return [];
        }
        return [];
    };

    const roles = getRolesFromUser(user);

    // update isCreator/isAdmin checks to use normalized roles
    const isCreator = user && route && (Number(user.id) === Number(route?.createdByUserId?.id));
    const isAdmin = roles.includes("ROLE_ADMIN") || roles.includes("ADMIN");
    const canDelete = Boolean(isCreator || isAdmin);

    const payload =
        route?.id
            ? {routeId: route.id}
            : null;

    useEffect(() => {
        if (!payload) return;
        const fetchLiked = async () => {
            try {
                const res = await isLiked(payload);
                setIsFavorite(res.data);
            } catch (err) {
                console.error(err);
            }
        };
        fetchLiked();
    }, [route?.id, user?.id]);

    const handleToggleFavourite = async (e) => {
        e.stopPropagation();
        try {
            if (!isFavorite) {
                if (!user || !user.id) {
                    onLoginRequired();
                    return;
                }
                await favouriteRoute(route.id);
                setIsFavorite(true);
            } else {
                await unfavoriteRoute(route.id);
                setIsFavorite(false);
            }
        } catch (err) {
            console.error("Failed to toggle favorite:", err);
        }
    };


    const handleDelete = async (e) => {
        e.stopPropagation();
        if (!user || !user.id) {
            onLoginRequired?.();
            return;
        }

        if (!canDelete) {
            return;
        }

        const confirmed = window.confirm("Are you sure you want to delete this route?");
        if (!confirmed) return;

        try {
            await deleteRoute(route.id);
            // notify parent to remove from list if provided, otherwise reload as fallback
            if (typeof onDelete === "function") {
                onDelete(route.id);
            } else {
                window.location.reload();
            }
        } catch (err) {
            console.error("Failed to delete route:", err);
        }
    };


    const handleCheckWeather = () => {
        navigate(
            `/weather?lat=${route.startLatitude}&lon=${route.startLongitude}&name=${encodeURIComponent(route.name)}`
        )
    }
    const handleCreateTrip = () => {

    }

    return (
        <Card className="overflow-hidden hover:shadow-lg transition-shadow">

                <div className="relative">
                    <Carousel withIndicators height={200} classNames={classes}>
                        {route.images.map((img, index) => (
                            <Carousel.Slide key={index}>
                                <img
                                    src={String(img) || "/vite.svg"}
                                    alt={`${route.name} image ${index + 1}`}
                                    className="w-full h-48 object-cover rounded-lg transition-all duration-300"
                                />
                            </Carousel.Slide>
                        ))}
                    </Carousel>
                    <div className="absolute top-2 right-2 flex gap-1">
                        <Button
                            size="icon"
                            variant="secondary"
                            className="h-8 w-8 bg-white/80 hover:bg-white"
                            onClick={handleToggleFavourite}
                        >
                            <HeartIcon
                                className={`h-4 w-4 transition-all 
                                ${isFavorite ? "fill-red-900 text-red-900" : ""}`}
                            />
                        </Button>
                        {canDelete && (
                            <Button
                                size="icon"
                                variant="secondary"
                                onClick={handleDelete}
                                className="h-8 w-8 bg-white/80 hover:bg-white"
                            >
                                <Trash/>
                            </Button>
                        )}
                    </div>

                    <div className="absolute top-2 left-2">
                        <Badge className="border-0">{route.difficulty || "Unknown"}</Badge>
                    </div>
                </div>

                <CardHeader className="pb-2">
                    <div className="flex items-start justify-between">
                        <CardTitle className="text-lg">{route.name}</CardTitle>
                        <div className="flex items-center gap-1 text-sm">
                            <StarIcon className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                            <span className="font-medium">{route.averageRating || "no rating"}</span>
                        </div>
                    </div>
                    <CardDescription className="text-sm text-left">{route.description}</CardDescription>
                </CardHeader>

                <CardContent className="space-y-3">
                    <div className="flex items-center justify-between text-sm">
                        <div className="flex items-center gap-1">
                            <span className="capitalize">{route.routeType}</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <UsersIcon className="h-4 w-4 text-muted-foreground" />
                            <button
                                onClick={(e) => {
                                    e.stopPropagation();
                                    if (route.createdByUserId?.id) {
                                        navigate(`/profile/${route.createdByUserId.id}`);
                                    }
                                }}
                                className="hover:text-blue-600 hover:underline transition-colors"
                            >
                                {route.createdByUserId?.username || "Unknown"}
                            </button>
                        </div>
                    </div>

                    <div className="grid grid-cols-3 gap-2 text-sm">
                        <div className="flex items-center gap-1">
                            <MapPinIcon className="h-4 w-4 text-muted-foreground" />
                            <span>{route.distance} km</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <ClockIcon className="h-4 w-4 text-muted-foreground" />
                            <span>{route.duration}</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <TrendingUpIcon className="h-4 w-4 text-muted-foreground" />
                            <span>{route.difficulty}</span>
                        </div>
                    </div>

                    <div className="flex gap-2 pt-2">
                        <Button className="w-1/2" size="xs" onClick={() => onViewDetails(route)}>
                            View Details
                        </Button>
                        <Button
                            className="w-1/2 bg-gray-100"
                            variant="outline"
                            size="xs"
                            onClick={handleCheckWeather}
                        >
                            Check Weather
                        </Button>
                    </div>

                    <Button
                        variant="outline"
                        size="sm"
                        className="w-full bg-cyan-950 text-white"
                        onClick={() => onAddToTrip(route)}
                    >
                        Add to Trip
                    </Button>


                </CardContent>
            </Card>
    );
}