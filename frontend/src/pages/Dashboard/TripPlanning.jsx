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

            <TripCreation openedPop={openedPop} setOpenedPop={setOpenedPop} />

        </div>
    )
}
