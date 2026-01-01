import {useGeolocation} from "@/hooks/useGeolocation.jsx";
import {getCurrentCity, getCurrentWeather} from "@/api/weatherApi.jsx";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.jsx";
import {CloudIcon, DropletIcon, MapPinIcon, WindIcon} from "lucide-react";
import {useEffect, useState} from "react";
import {getWeatherIcon} from "@/utils/weatherLogic.jsx";


const CurrentInfo = ({ overrideLocation, trailName }) => {
    const geo = useGeolocation(!overrideLocation)
    const location = overrideLocation ?? geo.effectiveLocation

    const [city, setCity] = useState("Loading...");
    const [weather, setWeather] = useState(null);

    useEffect(() => {
        if (!location) return

        const { latitude, longitude } = location

        getCurrentCity(latitude, longitude)
            .then(res => setCity(res.data))
            .catch(() => setCity("Unknown location"))

        getCurrentWeather(latitude, longitude)
            .then(res => setWeather(res.data))
    }, [location]);



    if (!weather) {
        return (
            <Card className="lg:col-span-3 p-4" withBorder>
                <CardContent>Loading weather...</CardContent>
            </Card>
        );
    }

    return(
        <>
            <Card className="p-4">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2 pt-3">
                        <MapPinIcon className="h-5 w-5" />
                        {trailName ?? city ?? "Unknown location"}
                    </CardTitle>

                    <CardDescription className="text-left">Current conditions</CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="flex items-center justify-between mb-6">
                        <div>
                            <div className="text-4xl font-bold">{weather.temperature}°C</div>
                            <p className="text-muted-foreground">{weather.condition}</p>
                        </div>
                        <div >
                            <div className="h-16 w-16">{getWeatherIcon(weather.condition, 64)}</div>
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
        </>
    )
}

export default CurrentInfo;