import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
    MapPinIcon,
    SearchIcon,
    FilterIcon,
    MountainIcon,
    BikeIcon,
    FootprintsIcon,
    StarIcon,
    ClockIcon,
    TrendingUpIcon,
    UsersIcon,
    HeartIcon,
    ShareIcon,
} from "lucide-react"
import { CgAdd } from "react-icons/cg";



const RouteDiscovery = () => {
    return (
        // haeder and create button
        <div className="size-full">
            <div className="flex items-center justify-between text-left">
                <div>
                    <h1 style={{color:"#3C5862"}} className="text-2xl font-bold">Discover Routes</h1>

                    <p className="pb-3 pt-3" style={{color:"#88928F"}}>Find your next adventure from thousands of curated routes</p>
                </div>
                <Button  style={{backgroundColor:"#105174"}} className="bg-cyan-950 text-primary-foreground hover:bg-primary/90  gap-2">
                    <CgAdd className="h-4 w-4" />
                    Create Route
                </Button>
            </div>

            {/* Search and Filters */}
            <Card style={{ backgroundColor: "#E3E0E0" }}>
                <CardContent className="p-5">
                    <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">

                        {/* Search Input */}
                        <div className="relative flex-1 min-w-[250px]">
                            <SearchIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                            <Input
                                placeholder="Search routes by name, location, or tags..."
                                className="pl-10 w-full"
                            />
                        </div>

                        {/* Filters */}
                        <div className="flex flex-wrap md:flex-nowrap gap-3 justify-between md:justify-end w-full md:w-auto">
                            <Select className="size-32">
                                <SelectTrigger className="min-w-[150px]">
                                    <SelectValue placeholder="Type" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="all">All Types</SelectItem>
                                    <SelectItem value="hiking">Hiking</SelectItem>
                                    <SelectItem value="cycling">Cycling</SelectItem>
                                    <SelectItem value="walking">Walking</SelectItem>
                                </SelectContent>
                            </Select>

                            <Select className="size-52">
                                <SelectTrigger className="min-w-[150px]">
                                    <SelectValue placeholder="Difficulty" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="all">All Levels</SelectItem>
                                    <SelectItem value="easy">Easy</SelectItem>
                                    <SelectItem value="moderate">Moderate</SelectItem>
                                    <SelectItem value="hard">Hard</SelectItem>
                                </SelectContent>
                            </Select>

                            <Button variant="outline" size="icon">
                                <FilterIcon className="h-4 w-4" />
                            </Button>
                        </div>
                    </div>
                </CardContent>
            </Card>

<Tabs defaultValue="all" className="p-3">
    <TabsList className="flex w-full bg-zinc-400 gap-12 p-1">
        <TabsTrigger value="all" default>All Routes</TabsTrigger>
        <TabsTrigger value="popular">Popular</TabsTrigger>
        <TabsTrigger value="nearby">Nearby</TabsTrigger>
        <TabsTrigger value="saved">Saved</TabsTrigger>
    </TabsList>


    <TabsContent value="all" className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

                <Card key={1} className="overflow-hidden hover:shadow-lg transition-shadow">
                    <div className="relative">
                        <img src={"vite.svg" || "/placeholder.svg"} alt={"Somewhere_test"} className="w-full h-48 object-cover" />
                        <div className="absolute top-2 right-2 flex gap-1">
                            <Button size="icon" variant="secondary" className="h-8 w-8 bg-white/80 hover:bg-white">
                                <HeartIcon className="h-4 w-4" />
                            </Button>
                            <Button size="icon" variant="secondary" className="h-8 w-8 bg-white/80 hover:bg-white">
                                <ShareIcon className="h-4 w-4" />
                            </Button>
                        </div>
                        <div className="absolute top-2 left-2">
                            <Badge className={`${"regular"} border-0`}>{"regular"}</Badge>
                        </div>
                    </div>

                    <CardHeader className="pb-2">
                        <div className="flex items-start justify-between">
                            <CardTitle className="text-lg">{"Somewhere_test"}</CardTitle>
                            <div className="flex items-center gap-1 text-sm">
                                <StarIcon className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                                <span className="font-medium">{4.5}</span>
                            </div>
                        </div>
                        <CardDescription className="text-sm">{"Somewhere_test"}</CardDescription>
                    </CardHeader>

                    <CardContent className="space-y-3">
                        <div className="flex items-center justify-between text-sm">
                            <div className="flex items-center gap-1">
                                "icon"
                                <span className="capitalize">{"cycling"}</span>
                            </div>
                            <div className="flex items-center gap-1">
                                <UsersIcon className="h-4 w-4 text-muted-foreground" />
                                <span>{"hello evey review"} reviews</span>
                            </div>
                        </div>

                        <div className="grid grid-cols-3 gap-2 text-sm">
                            <div className="flex items-center gap-1">
                                <MapPinIcon className="h-4 w-4 text-muted-foreground" />
                                <span>{16}</span>
                            </div>
                            <div className="flex items-center gap-1">
                                <ClockIcon className="h-4 w-4 text-muted-foreground" />
                                <span>{5}</span>
                            </div>
                            <div className="flex items-center gap-1">
                                <TrendingUpIcon className="h-4 w-4 text-muted-foreground" />
                                <span>{5}</span>
                            </div>
                        </div>

                        {/*<div className="flex flex-wrap gap-1">*/}
                        {/*    /!*{route.tags.map((tag, index) => (*!/*/}
                        {/*    /!*    <Badge key={index} variant="outline" className="text-xs">*!/*/}
                        {/*    /!*        {tag}*!/*/}
                        {/*    /!*    </Badge>*!/*/}
                        {/*    /!*))}*!/*/}
                        {/*</div>*/}

                        <div className="flex gap-2 pt-2">
                            <Button className="flex-1" size="sm">
                                View Details
                            </Button>
                            <Button variant="outline" size="sm">
                                Add to Trip
                            </Button>
                        </div>
                    </CardContent>
                </Card>

        </div>
    </TabsContent>

    <TabsContent value="popular">...</TabsContent>
    <TabsContent value="nearby">...</TabsContent>
    <TabsContent value="saved">...</TabsContent>

</Tabs>


        </div>
    );
};

export default RouteDiscovery;
