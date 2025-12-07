import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs.jsx";
import RouteGrid from "./RouteGrid.jsx";

export default function RouteTabsDiscovery({ displayRoutes, onViewDetails, onLoginRequired }) {
    return (
        <Tabs defaultValue="all" className="mt-6">
            <div className="overflow-x-auto">
                <TabsList className="inline-flex bg-zinc-400 p-1 gap-4 w-full sm:w-auto">
                    <TabsTrigger value="all">All Routes</TabsTrigger>
                    <TabsTrigger value="popular">Popular</TabsTrigger>
                    <TabsTrigger value="nearby">Nearby</TabsTrigger>
                    <TabsTrigger value="saved">Saved</TabsTrigger>
                </TabsList>
            </div>

            <TabsContent value="all" className="mt-6">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                    <RouteGrid
                        routes={displayRoutes}
                        onViewDetails={onViewDetails}
                        onLoginRequired={onLoginRequired}
                    />
                </div>
            </TabsContent>

            <TabsContent value="popular">
                <p className="text-center py-10">Popular routes coming soon</p>
            </TabsContent>
            <TabsContent value="nearby">
                <p className="text-center py-10">Nearby routes coming soon</p>
            </TabsContent>
            <TabsContent value="saved">
                <p className="text-center py-10">Saved routes coming soon</p>
            </TabsContent>
        </Tabs>
    );
}
