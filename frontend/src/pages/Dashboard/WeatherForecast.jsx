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

const WeatherForecast = () => {
    const currentWeather = {

        condition: "Partly Cloudy",
        description: "Perfect conditions for hiking",
        humidity: 65,
        windSpeed: 8,
        windDirection: "NW",
        visibility: 15,
        uvIndex: 6,
        pressure: 1013,
        feelsLike: 24,
    }

    const hourlyForecast = [
        { time: "Now", temp: 22, condition: "partly-cloudy", precipitation: 0 },
        { time: "13:00", temp: 24, condition: "sunny", precipitation: 0 },
        { time: "14:00", temp: 26, condition: "sunny", precipitation: 0 },
        { time: "15:00", temp: 25, condition: "partly-cloudy", precipitation: 10 },
        { time: "16:00", temp: 23, condition: "cloudy", precipitation: 20 },
        { time: "17:00", temp: 21, condition: "rainy", precipitation: 80 },
        { time: "18:00", temp: 19, condition: "rainy", precipitation: 90 },
        { time: "19:00", temp: 18, condition: "cloudy", precipitation: 30 },
    ]

    const weeklyForecast = [
        { day: "Today", high: 26, low: 18, condition: "partly-cloudy", precipitation: 20 },
        { day: "Tomorrow", high: 28, low: 20, condition: "sunny", precipitation: 0 },
        { day: "Thursday", high: 22, low: 16, condition: "rainy", precipitation: 85 },
        { day: "Friday", high: 24, low: 17, condition: "cloudy", precipitation: 40 },
        { day: "Saturday", high: 27, low: 19, condition: "sunny", precipitation: 5 },
        { day: "Sunday", high: 25, low: 18, condition: "partly-cloudy", precipitation: 15 },
        { day: "Monday", high: 23, low: 16, condition: "cloudy", precipitation: 60 },
    ]

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

    const getWeatherIcon = (condition) => {
        switch (condition) {
            case "sunny":
                return <SunIcon className="h-6 w-6 text-yellow-500" />
            case "partly-cloudy":
                return <CloudIcon className="h-6 w-6 text-gray-500" />
            case "cloudy":
                return <CloudIcon className="h-6 w-6 text-gray-600" />
            case "rainy":
                return <CloudRainIcon className="h-6 w-6 text-blue-500" />
            default:
                return <CloudIcon className="h-6 w-6 text-gray-500" />
        }
    }

    const getConditionRecommendation = (condition, precipitation) => {
        if (precipitation > 70) return { text: "Not Recommended", color: "bg-red-100 text-red-800" }
        if (precipitation > 30) return { text: "Use Caution", color: "bg-yellow-100 text-yellow-800" }
        if (condition === "sunny") return { text: "Perfect", color: "bg-green-100 text-green-800" }
        return { text: "Good", color: "bg-blue-100 text-blue-800" }
    }


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
            <Tabs color="cyan" defaultValue="hourly" className="w-full">
                <Tabs.List className="grid w-full grid-cols-2" justify="center">
                    <Tabs.Tab value="hourly" c="dimmed">Hourly Forecast</Tabs.Tab>
                    <Tabs.Tab value="weekly" c="dimmed">7-Day Forecast</Tabs.Tab>
                </Tabs.List>

                <Tabs.Panel value="hourly" className="space-y-4">
                    <Card className="p-3">
                        <CardHeader>
                            <CardTitle>Next 8 Hours</CardTitle>
                            <CardDescription>Hourly weather conditions and recommendations</CardDescription>
                        </CardHeader>
                        <CardContent>
                            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-8 gap-4">
                                {hourlyForecast.map((hour, index) => {
                                    const recommendation = getConditionRecommendation(hour.condition, hour.precipitation)
                                    return (
                                        <div key={index} className="text-center p-3 border rounded-lg">
                                            <p className="text-sm font-medium mb-2">{hour.time}</p>
                                            <div className="flex justify-center mb-2">{getWeatherIcon(hour.condition)}</div>
                                            <p className="text-lg font-bold mb-1">{hour.temp}°</p>
                                            <p className="text-xs text-muted-foreground mb-2">{hour.precipitation}%</p>
                                            <Badge className={`text-xs ${recommendation.color} border-0`}>{recommendation.text}</Badge>
                                        </div>
                                    )
                                })}
                            </div>
                        </CardContent>
                    </Card>
                </Tabs.Panel>

                <Tabs.Panel value="weekly" className="space-y-4">
                    <Card className="p-3">
                        <CardHeader>
                            <CardTitle>7-Day Forecast</CardTitle>
                            <CardDescription>Weekly weather outlook for trip planning</CardDescription>
                        </CardHeader>
                        <CardContent>
                            <div className="space-y-3">
                                {weeklyForecast.map((day, index) => {
                                    const recommendation = getConditionRecommendation(day.condition, day.precipitation)
                                    return (
                                        <div key={index} className="flex items-center justify-between p-4 border rounded-lg">
                                            <div className="flex items-center gap-4">
                                                <div className="w-20">
                                                    <p className="font-medium">{day.day}</p>
                                                </div>
                                                <div className="flex items-center gap-2">
                                                    {getWeatherIcon(day.condition)}
                                                    <span className="text-sm text-muted-foreground capitalize">
                            {day.condition.replace("-", " ")}
                          </span>
                                                </div>
                                            </div>
                                            <div className="flex items-center gap-4">
                                                <div className="text-right">
                                                    <p className="font-medium">
                                                        {day.high}° / {day.low}°
                                                    </p>
                                                    <p className="text-xs text-muted-foreground">{day.precipitation}% rain</p>
                                                </div>
                                                <Badge className={`${recommendation.color} border-0`}>{recommendation.text}</Badge>
                                            </div>
                                        </div>
                                    )
                                })}
                            </div>
                        </CardContent>
                    </Card>
                </Tabs.Panel>
            </Tabs>

            {/* Route-Specific Weather */}
            <Card className="p-3">
                <CardHeader>
                    <CardTitle>Route-Specific Conditions</CardTitle>
                    <CardDescription>Weather conditions for your saved and planned routes</CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        <div className="p-4 border rounded-lg">
                            <div className="flex items-center justify-between mb-3">
                                <h3 className="font-medium">Mountain Ridge Trail</h3>
                                <Badge className="bg-green-100 text-green-800 border-0">Perfect</Badge>
                            </div>
                            <div className="flex items-center justify-between text-sm">
                                <span>22°C • Partly Cloudy</span>
                                <span className="text-muted-foreground">0% rain</span>
                            </div>
                        </div>

                        <div className="p-4 border rounded-lg">
                            <div className="flex items-center justify-between mb-3">
                                <h3 className="font-medium">Coastal Cycling Route</h3>
                                <Badge className="bg-yellow-100 text-yellow-800 border-0">Windy</Badge>
                            </div>
                            <div className="flex items-center justify-between text-sm">
                                <span>24°C • Sunny</span>
                                <span className="text-muted-foreground">15 km/h wind</span>
                            </div>
                        </div>

                        <div className="p-4 border rounded-lg">
                            <div className="flex items-center justify-between mb-3">
                                <h3 className="font-medium">Forest Walk Path</h3>
                                <Badge className="bg-red-100 text-red-800 border-0">Rain Later</Badge>
                            </div>
                            <div className="flex items-center justify-between text-sm">
                                <span>20°C • Cloudy</span>
                                <span className="text-muted-foreground">80% rain at 5 PM</span>
                            </div>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}

export default WeatherForecast;
