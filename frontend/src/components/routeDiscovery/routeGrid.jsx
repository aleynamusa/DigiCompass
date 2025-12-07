import { RouteCard } from "@/components/routeDetails/route_card.jsx";

export default function RouteGrid({ routes, onViewDetails, onLoginRequired }) {
    if (routes.length === 0) {
        return (
            <div className="text-center text-gray-600 col-span-full py-10 px-4">
                <p className="text-lg font-medium">No routes found</p>
                <p className="text-sm text-gray-500 mt-2">Try adjusting filters or search keywords.</p>
            </div>
        );
    }

    return routes.map((route) => (
        <RouteCard
            key={route.id}
            route={route}
            onViewDetails={onViewDetails}
            onLoginRequired={onLoginRequired}
        />
    ));
}
