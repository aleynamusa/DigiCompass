import { RouteCard } from "@/components/routeDetails/route_card.jsx";
import {TripCreation} from "@/components/trip/tripCreation.jsx"
import {useState} from "react";
import {useAuth} from "@/context/AuthContext.jsx";

export default function RouteGrid({ routes, onViewDetails, onLoginRequired }) {
    const [tripModalOpen, setTripModalOpen] = useState(false);
    const [selectedRouteForTrip, setSelectedRouteForTrip] = useState(null);

    if (routes.length === 0) {
        return (
            <div className="text-center text-gray-600 col-span-full py-10 px-4">
                <p className="text-lg font-medium">No routes found</p>
                <p className="text-sm text-gray-500 mt-2">
                    Try adjusting filters or search keywords.
                </p>
            </div>
        );
    }

    return (
        <>
            {routes.map((route) => (
                <RouteCard
                    key={route.id}
                    route={route}
                    onViewDetails={onViewDetails}
                    onLoginRequired={onLoginRequired}
                    onAddToTrip={(route) => {
                        setSelectedRouteForTrip(route);
                        setTripModalOpen(true);
                    }}
                />
            ))}

            {/* ✅ ONE modal, controlled here */}
            <TripCreation
                openedPop={tripModalOpen}
                setOpenedPop={setTripModalOpen}
                preselectedRoute={selectedRouteForTrip}
            />
        </>
    );
}
