import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import {
    CalendarIcon,
    MapPinIcon,
    ClockIcon,
    UsersIcon,
    EditIcon,
    TrashIcon,
    ShareIcon,
    CloudIcon,
    MountainIcon,
    BikeIcon,
    FootprintsIcon, CirclePlus,
} from "lucide-react"
import { useState} from "react"
import { Tabs} from "@mantine/core";
import ShowCalendarForBirthDate from "@/components/BirthDate.jsx";
import {Calendar} from '@mantine/dates';
import {TripCreation} from "@/components/trip/tripCreation.jsx";
import {useAuth} from "@/context/AuthContext.jsx";
import {Button} from "@/components/ui/button.jsx"

export function TripPlanning() {
    const [selectedDate, setSelectedDate] = useState();
    const { user } = useAuth();

    const [selectedRoute, setSelectedRoute] = useState(null);
    const [openedPop, setOpenedPop] = useState(false);


    function openCreateTrip(routeId = null) {
        setSelectedRoute(routeId);
        setOpenedPop(true)
    }

    const upcomingTrips = [
        {
            id: 1,
            title: "Weekend Mountain Adventure",
            date: "2024-12-16",
            duration: "2 days",
            routes: ["Mountain Ridge Trail", "Alpine Challenge"],
            participants: 4,
            status: "confirmed",
            weather: "Partly Cloudy, 22°C",
            description: "A challenging weekend hike through mountain trails with overnight camping.",
        },
        {
            id: 2,
            title: "Coastal Cycling Tour",
            date: "2024-12-24",
            duration: "1 day",
            routes: ["Coastal Cycling Route", "River Valley Cycle"],
            participants: 2,
            status: "planning",
            weather: "Sunny, 25°C",
            description: "Scenic coastal ride with lunch stop at the lighthouse.",
        },
        {
            id: 3,
            title: "Family Nature Walk",
            date: "2024-12-30",
            duration: "Half day",
            routes: ["Forest Walk Path", "Urban Discovery Walk"],
            participants: 6,
            status: "draft",
            weather: "Cloudy, 18°C",
            description: "Easy family-friendly walks through local parks and nature areas.",
        },
    ]

    const pastTrips = [
        {
            id: 4,
            title: "Solo Hiking Challenge",
            date: "2024-11-28",
            duration: "1 day",
            routes: ["Mountain Ridge Trail"],
            participants: 1,
            status: "completed",
            rating: 5,
            notes: "Amazing views from the summit. Weather was perfect for hiking.",
        },
        {
            id: 5,
            title: "Group Cycling Adventure",
            date: "2024-11-15",
            duration: "1 day",
            routes: ["Coastal Cycling Route"],
            participants: 8,
            status: "completed",
            rating: 4,
            notes: "Great group ride, but windy conditions made it challenging.",
        },
    ]

    const getStatusColor = (status) => {
        switch (status) {
            case "confirmed":
                return "bg-green-100 text-green-800"
            case "planning":
                return "bg-yellow-100 text-yellow-800"
            case "draft":
                return "bg-gray-100 text-gray-800"
            case "completed":
                return "bg-blue-100 text-blue-800"
            default:
                return "bg-gray-100 text-gray-800"
        }
    }

    const getRouteIcon = (routeName) => {
        if (routeName.toLowerCase().includes("mountain") || routeName.toLowerCase().includes("alpine")) {
            return <MountainIcon className="h-4 w-4" />
        }
        if (routeName.toLowerCase().includes("cycling") || routeName.toLowerCase().includes("cycle")) {
            return <BikeIcon className="h-4 w-4" />
        }
        return <FootprintsIcon className="h-4 w-4" />
    }

    return (
        <div className="space-y-6 overflow-y-auto min-h-screen">

            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold text-balance text-cyan-950 text-left">
                        Trip Planning
                    </h1>
                    <p className="text-muted-foreground text-pretty pt-2">
                        Organize your outdoor adventures with integrated weather and route planning
                    </p>
                </div>

                <Button onClick={() => openCreateTrip()} className="bg-cyan-950">Create New Trip</Button>

            </div>

            {/*<div className="flex justify-between mb-6">*/}
            {/*    <h1 className="text-3xl font-bold text-cyan-950">Discover Routes</h1>*/}
            {/*    <Button onClick={() => setShowRoutePopup(true)} className="bg-cyan-950"> <CirclePlus></CirclePlus> Create Route</Button>*/}
            {/*</div>*/}

            <TripCreation userId={user?.id} openedPop={openedPop} setOpenedPop={setOpenedPop} />


            {/* Quick Stats */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Upcoming Trips</CardTitle>
                        <CalendarIcon className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">3</div>
                        <p className="text-xs text-muted-foreground">Next 30 days</p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Completed Trips</CardTitle>
                        <MapPinIcon className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">12</div>
                        <p className="text-xs text-muted-foreground">This year</p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Total Distance</CardTitle>
                        <ClockIcon className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">284 km</div>
                        <p className="text-xs text-muted-foreground">Planned routes</p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Group Size</CardTitle>
                        <UsersIcon className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">4.2</div>
                        <p className="text-xs text-muted-foreground">Average participants</p>
                    </CardContent>
                </Card>
            </div>

            {/* Trip Management */}
            <Tabs defaultValue="upcoming" className="w-full">
                <Tabs.List className="grid w-full grid-cols-3">
                    <Tabs.Tab value="upcoming">Upcoming Trips</Tabs.Tab>
                    <Tabs.Tab value="past">Past Trips</Tabs.Tab>
                    <Tabs.Tab value="calendar">Calendar View</Tabs.Tab>
                </Tabs.List>

                <Tabs.Panel value="upcoming" className="space-y-4">
                    <div className="space-y-4">
                        {upcomingTrips.map((trip) => (
                            <Card key={trip.id} className="hover:shadow-md transition-shadow">
                                <CardHeader>
                                    <div className="flex items-start justify-between">
                                        <div>
                                            <CardTitle className="text-xl">{trip.title}</CardTitle>
                                            <CardDescription className="mt-1">{trip.description}</CardDescription>
                                        </div>
                                        <div className="flex gap-2">
                                            <Badge className={`${getStatusColor(trip.status)} border-0 capitalize`}>{trip.status}</Badge>
                                        </div>
                                    </div>
                                </CardHeader>
                                <CardContent className="space-y-4">
                                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                        <div className="flex items-center gap-2">
                                            <CalendarIcon className="h-4 w-4 text-muted-foreground" />
                                            {/*<span className="text-sm">{format(new Date(trip.date), "PPP")}</span>*/}
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <ClockIcon className="h-4 w-4 text-muted-foreground" />
                                            <span className="text-sm">{trip.duration}</span>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <UsersIcon className="h-4 w-4 text-muted-foreground" />
                                            <span className="text-sm">{trip.participants} participants</span>
                                        </div>
                                    </div>

                                    <div className="space-y-2">
                                        <h4 className="text-sm font-medium">Planned Routes</h4>
                                        <div className="flex flex-wrap gap-2">
                                            {trip.routes.map((route, index) => (
                                                <div key={index} className="flex items-center gap-1 px-2 py-1 bg-muted rounded-md text-sm">
                                                    {getRouteIcon(route)}
                                                    <span>{route}</span>
                                                </div>
                                            ))}
                                        </div>
                                    </div>

                                    <div className="flex items-center justify-between pt-2 border-t">
                                        <div className="flex items-center gap-2">
                                            <CloudIcon className="h-4 w-4 text-muted-foreground" />
                                            <span className="text-sm text-muted-foreground">{trip.weather}</span>
                                        </div>
                                        <div className="flex gap-2">
                                            <Button variant="outline" size="sm">
                                                <EditIcon className="h-4 w-4 mr-1" />
                                                Edit
                                            </Button>
                                            <Button variant="outline" size="sm">
                                                <ShareIcon className="h-4 w-4 mr-1" />
                                                Share
                                            </Button>
                                            <Button variant="outline" size="sm" className="text-red-600 hover:text-red-700 bg-transparent">
                                                <TrashIcon className="h-4 w-4 mr-1" />
                                                Delete
                                            </Button>
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        ))}
                    </div>
                </Tabs.Panel>

                <Tabs.Panel value="past" className="space-y-4">
                    <div className="space-y-4">
                        {pastTrips.map((trip) => (
                            <Card key={trip.id} className="hover:shadow-md transition-shadow">
                                <CardHeader>
                                    <div className="flex items-start justify-between">
                                        <div>
                                            <CardTitle className="text-xl">{trip.title}</CardTitle>
                                            <CardDescription className="mt-1">{trip.notes}</CardDescription>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <div className="flex items-center gap-1">
                                                {[...Array(5)].map((_, i) => (
                                                    <span key={i} className={`text-sm ${i < trip.rating ? "text-yellow-400" : "text-gray-300"}`}>
                            ★
                          </span>
                                                ))}
                                            </div>
                                            <Badge className={`${getStatusColor(trip.status)} border-0 capitalize`}>{trip.status}</Badge>
                                        </div>
                                    </div>
                                </CardHeader>
                                <CardContent className="space-y-4">
                                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                        <div className="flex items-center gap-2">
                                            <CalendarIcon className="h-4 w-4 text-muted-foreground" />
                                            {/*<span className="text-sm">{format(new Date(trip.date), "PPP")}</span>*/}
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <ClockIcon className="h-4 w-4 text-muted-foreground" />
                                            <span className="text-sm">{trip.duration}</span>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <UsersIcon className="h-4 w-4 text-muted-foreground" />
                                            <span className="text-sm">{trip.participants} participants</span>
                                        </div>
                                    </div>

                                    <div className="space-y-2">
                                        <h4 className="text-sm font-medium">Completed Routes</h4>
                                        <div className="flex flex-wrap gap-2">
                                            {trip.routes.map((route, index) => (
                                                <div key={index} className="flex items-center gap-1 px-2 py-1 bg-muted rounded-md text-sm">
                                                    {getRouteIcon(route)}
                                                    <span>{route}</span>
                                                </div>
                                            ))}
                                        </div>
                                    </div>

                                    <div className="flex justify-end pt-2 border-t">
                                        <div className="flex gap-2">
                                            <Button variant="outline" size="sm">
                                                View Details
                                            </Button>
                                            <Button variant="outline" size="sm">
                                                <ShareIcon className="h-4 w-4 mr-1" />
                                                Share Experience
                                            </Button>
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        ))}
                    </div>
                </Tabs.Panel>

                <Tabs.Panel value="calendar" className="space-y-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Trip Calendar</CardTitle>
                            <CardDescription>View all your trips in calendar format</CardDescription>
                        </CardHeader>
                        <CardContent>
                            <div className="flex justify-center">
                                <Calendar
                                    mode="single"
                                    selected={selectedDate}
                                    onSelect={setSelectedDate}
                                    className="rounded-md border"
                                />
                            </div>
                            <div className="mt-6 space-y-2">
                                <h4 className="font-medium">Upcoming Events</h4>
                                <div className="space-y-2">
                                    {upcomingTrips.slice(0, 3).map((trip) => (
                                        <div key={trip.id} className="flex items-center justify-between p-2 border rounded">
                                            <div>
                                                <ShowCalendarForBirthDate/>
                                                {/*<p className="font-medium text-sm">{trip.title}</p>*/}
                                                {/*<p className="text-xs text-muted-foreground">{format(new Date(trip.date), "PPP")}</p>*/}
                                            </div>
                                            <Badge className={`${getStatusColor(trip.status)} border-0 text-xs`}>{trip.status}</Badge>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </Tabs.Panel>
            </Tabs>
        </div>
    )
}
