import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from '@mantine/core';
import { Tabs } from '@mantine/core';
import {
    CloudIcon,
    SunIcon,
    CloudRainIcon,
    AlertTriangleIcon,
    MapPinIcon
} from "lucide-react"
import {Input} from "@/components/ui/input.jsx";
import CurrentInfo from "@/components/weatherForecast/currentInfo.jsx";
import WeatherTabs from "@/components/weatherForecast/weatherTabs.jsx";

const WeatherForecast = () => {

    const alerts = [
        {
            type: "warning",
            title: "Rain Expected",
            message: "Heavy rain expected between 5-7 PM today. Consider rescheduling outdoor activities.",
            routes: ["Mountain Ridge Trail", "Forest Walk Path"],
        },
        {
            type: "info",
            title: "Perfect Conditions Tomorrow",
            message: "Ideal weather for all outdoor activities with clear skies and mild temperatures.",
            routes: ["All Routes"],
        },
    ]

    return (
        <div className=" min-h-screen ">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold text-balance text-left text-cyan-950">Weather Forecast</h1>
                    <p className="text-muted-foreground text-pretty pb-4">
                        Real-time weather conditions and forecasts for your planned routes
                    </p>
                </div>
                <div className="relative w-64">
                    <MapPinIcon className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                    <Input
                        placeholder="Search another place..."
                        className="pl-9"
                    />
                </div>

            </div>

            {/* Weather Alerts */}
            {alerts.length > 0 && (
                <div className="space-y-3 p-3">
                    {alerts.map((alert, index) => (
                        <Card
                            key={index}
                            className={`border-l-4 ${alert.type === "warning" ? "border-l-red-500" : "border-l-blue-500"}`}
                        >
                            <CardContent className="p-4">
                                <div className="flex items-start gap-3">
                                    <AlertTriangleIcon
                                        className={`h-5 w-5 mt-0.5 ${alert.type === "warning" ? "text-red-500" : "text-blue-500"}`}
                                    />
                                    <div className="flex-1">
                                        <h3 className="font-medium">{alert.title}</h3>
                                        <p className="text-sm text-muted-foreground mt-1">{alert.message}</p>
                                        <p className="text-xs text-muted-foreground mt-2">Affects: {alert.routes.join(", ")}</p>
                                    </div>
                                </div>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            )}

            {/* Current Weather */}
            <div className="grid grid-cols-1 lg:grid-cols-5 gap-10 p-3">
                <CurrentInfo />
            </div>

            {/* Detailed Forecast */}
            <WeatherTabs/>

        </div>
    )
}

export default WeatherForecast;
