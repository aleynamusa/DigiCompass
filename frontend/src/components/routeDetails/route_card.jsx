import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.jsx";
import {
    ClockIcon,
    HeartIcon,
    MapPinIcon,
    ShareIcon,
    StarIcon, TrendingUpIcon,
    UsersIcon
} from "lucide-react";
import {Button} from "@/components/ui/button.jsx";
import {Badge} from "@/components/ui/badge.jsx";
import {Carousel} from "@mantine/carousel";
import { useNavigate } from "react-router-dom";
import classes from '@/components/card.module.css';
import {useAuth} from "@/context/AuthContext.jsx";
import {favouriteRoute, unfavoriteRoute} from "@/api/userApi.jsx";
import {useState} from "react";

export function RouteCard({ route, onViewDetails }) {
    const navigate = useNavigate();

    const [isFavorite, setIsFavorite] = useState(route.isFavorite || false);
    const { user } = useAuth();

    const handleToggleFavourite = async (e) => {
        e.stopPropagation(); //prevent card click

        const payload = { userId: Number(user.id), routeId: route.id };

        try {
            if (!isFavorite) {
                await favouriteRoute(payload);
                setIsFavorite(true);
            } else {
                await unfavoriteRoute(payload);
                setIsFavorite(false);
            }
        } catch (err) {
            console.error("Failed to toggle favorite:", err);
        }
    };
    
    return (
        <div>
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
                    <Button size="icon" variant="secondary" className="h-8 w-8 bg-white/80 hover:bg-white">
                        <ShareIcon className="h-4 w-4" />
                    </Button>
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
                    <Button className="flex-1" size="sm" onClick={() => onViewDetails(route)}>
                        View Details
                    </Button>
                    <Button variant="outline" size="sm">
                        Add to Trip
                    </Button>
                </div>
            </CardContent>
        </Card>



        </div>

    );
}

