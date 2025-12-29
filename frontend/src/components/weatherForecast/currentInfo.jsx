import {useGeolocation} from "@/hooks/useGeolocation.jsx";
import {getCurrentCity, getCurrentWeather} from "@/api/weatherApi.jsx";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.jsx";
import {CloudIcon, CompassIcon, DropletIcon, EyeIcon, MapPinIcon, SunIcon, WindIcon} from "lucide-react";
import {useEffect, useState} from "react";
import { Badge } from '@mantine/core';


const CurrentInfo = () => {
    const currentLoc = useGeolocation(true);
    const [city, setCity] = useState("Loading...");
    const [weather, setWeather] = useState(null);

    useEffect(() => {
        if (!currentLoc.effectiveLocation) return;

        const { latitude, longitude } = currentLoc.effectiveLocation;

        getCurrentCity(latitude, longitude)
            .then(res => setCity(res.data))
            .catch(err => {
                console.error(err);
                setCity("Unknown location");
            });

        getCurrentWeather(latitude, longitude)
            .then(res => setWeather(res.data))
                .catch(err => console.error(err));
    }, [currentLoc.effectiveLocation]);

    useEffect(() => {

    })

    const conditionToIcon = (condition) => {
        switch (condition) {
            case "Clear":
            case "Mostly Clear":
                return <SunIcon className="h-16 w-16 text-yellow-400" />;
            case "Cloudy":
                return <CloudIcon className="h-16 w-16 text-gray-500" />;
            case "Fog":
                return <CloudFog className="h-16 w-16 text-gray-400" />;
            case "Drizzle":
            case "Rain":
            case "Rain Showers":
                return <DropletIcon className="h-16 w-16 text-blue-500" />;
            case "Freezing Rain":
                return <DropletIcon className="h-16 w-16 text-blue-300" />;
            case "Snow":
            case "Snow Grains":
            case "Snow Showers":
                return <Snowflake className="h-16 w-16 text-blue-200" />;
            case "Thunderstorm":
            case "Thunderstorm with Hail":
                return <WindIcon className="h-16 w-16 text-purple-600" />;
            default:
                return <CloudIcon className="h-16 w-16 text-gray-500" />;
        }
    };

    if (!weather) {
        return (
            <Card className="lg:col-span-3 p-4" withBorder>
                <CardContent>Loading weather...</CardContent>
            </Card>
        );
    }

    return(
        <>
            <Card className="lg:col-span-3 p-4">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2 pt-3">
                        <MapPinIcon className="h-5 w-5" />
                        {city}
                    </CardTitle>
                    <CardDescription className="text-left">Current conditions</CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="flex items-center justify-between mb-6">
                        <div>
                            <div className="text-4xl font-bold">{weather.temperature}°C</div>
                            <p className="text-muted-foreground">{weather.condition}</p>
                            {/*<p className="text-sm text-primary font-medium">aaaa</p>     WHAT ID GOOD FOR HIKING\WALING]RUNNING ETC    */}
                        </div>
                        <div >
                            <div>{conditionToIcon(weather.condition)}</div>
                            <p className="text-sm text-muted-foreground">Feels like {weather.feelsLike}°C</p>
                        </div>
                    </div>

                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        <div className="flex items-center gap-2">
                            <WindIcon className="h-4 w-4 text-muted-foreground" />
                            <div>
                                <p className="text-sm font-medium">{weather.windSpeed} km/h</p>
                                <p className="text-xs text-muted-foreground">{weather.windDirection}</p>
                            </div>
                        </div>
                        <div className="flex items-center gap-2">
                            <DropletIcon className="h-4 w-4 text-muted-foreground" />
                            <div>
                                <p className="text-sm font-medium">{weather.humidity} %</p>
                                <p className="text-xs text-muted-foreground">Humidity</p>
                            </div>
                        </div>
                        <div className="flex items-center gap-2">
                            <CloudIcon className="h-4 w-4 text-muted-foreground" />
                            <div>
                                <p className="text-sm font-medium">{weather.cloudCover} %</p>
                                <p className="text-xs text-muted-foreground">Cloud Cover</p>
                            </div>
                        </div>
                        <div className="flex items-center gap-2">
                            <DropletIcon className="h-4 w-4 text-muted-foreground" />
                            <div>
                                <p className="text-sm font-medium">{weather.precipitation} mm</p>
                                <p className="text-xs text-muted-foreground">Precipitation</p>
                            </div>
                        </div>
                    </div>
                </CardContent>
            </Card>

            <Card className="p-3 lg:col-span-2">
                <CardHeader>
                    <CardTitle>Activity Recommendations</CardTitle>
                    <CardDescription>Based on current conditions</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                    <div className="flex items-center justify-between p-3 border rounded-lg">
                        <div className="flex items-center gap-2">
                            <CompassIcon className="h-4 w-4 text-primary" />
                            <span className="text-sm">Hiking</span>
                        </div>
                        <Badge className="bg-green-100 text-green-800 border-0">Perfect</Badge>
                    </div>
                    <div className="flex items-center justify-between p-3 border rounded-lg">
                        <div className="flex items-center gap-2">
                            <CompassIcon className="h-4 w-4 text-secondary" />
                            <span className="text-sm">Cycling</span>
                        </div>
                        <Badge className="bg-green-100 text-green-800 border-0">Perfect</Badge>
                    </div>
                    <div className="flex items-center justify-between p-3 border rounded-lg">
                        <div className="flex items-center gap-2">
                            <CompassIcon className="h-4 w-4 text-accent" />
                            <span className="text-sm">Walking</span>
                        </div>
                        <Badge className="bg-blue-100 text-blue-800 border-0">Good</Badge>
                    </div>
                </CardContent>
            </Card>
        </>
    )
}

export default CurrentInfo;