import { Text } from "@mantine/core";
import { Share2 } from "lucide-react";
import { RouteCard } from "@/components/routeDetails/route_card";

export default function ProfileSharedRoutes({ routes, onViewDetails, isOwnProfile }) {
    return (
        <div className="w-full flex justify-center">
            <div className="w-full max-w-[1400px]">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 md:gap-6">
                    {(!routes || routes.length === 0) ? (
                        <div className="col-span-full w-full text-center py-10">
                            <Share2 size={48} className="mx-auto" />
                            <Text c="dimmed" size="lg">
                                {isOwnProfile
                                    ? "You haven't shared any routes yet"
                                    : "No shared routes yet"}
                            </Text>
                        </div>
                    ) : (
                        routes.map((route) => (
                            <RouteCard
                                key={route.id}
                                route={route}
                                onViewDetails={onViewDetails}
                            />
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}
