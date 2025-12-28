import { Text } from "@mantine/core";
import {Heart, Share2} from "lucide-react";
import { RouteCard } from "@/components/routeDetails/route_card.jsx";

export default function ProfileLikedRoutes({ likedRoutes, onViewDetails }) {
    return (
        <div className="w-full flex justify-center">
            <div className="w-full max-w-[1400px]">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 md:gap-6">
                    {(!likedRoutes || likedRoutes.length === 0) ? (
                        <div className="text-center">
                            <Heart size={48} className="mx-auto" />
                            <Text c="dimmed">No liked routes yet</Text>
                        </div>
                    ) : (
                        likedRoutes.map((route) => (
                            <RouteCard key={route.id} route={route} onViewDetails={onViewDetails} />
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}
