import {useState} from "react";
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
import {RouteDetails} from "@/components/route_details"
import {Carousel} from "@mantine/carousel";
import classes from '@/components/card.module.css';

export function RouteCard({ route, onViewDetails }) {
    const [current, setCurrent] = useState(0);
    const routeNew = route;

    const nextImage = () => {
        if (!route.images || route.images.length === 0) return;
        setCurrent((prev) => (prev + 1) % route.images.length);
    };

    const prevImage = () => {
        if (!route.images || route.images.length === 0) return;
        setCurrent((prev) => (prev - 1 + route.images.length) % route.images.length);
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
                    <Button size="icon" variant="secondary" className="h-8 w-8 bg-white/80 hover:bg-white">
                        <HeartIcon className="h-4 w-4" />
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
                        <span>{route.createdByUserId?.username || "Unknown"}</span>
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

