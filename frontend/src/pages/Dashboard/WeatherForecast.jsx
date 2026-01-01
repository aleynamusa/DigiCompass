import CurrentInfo from "@/components/weatherForecast/currentInfo.jsx"
import WeatherTabs from "@/components/weatherForecast/weatherTabs.jsx"
import RecommendedDays from "@/components/weatherForecast/recommendedDays.jsx"
import { useSearchParams } from "react-router-dom"
import { useEffect, useState } from "react"
import { getRecommendedDays } from "@/api/weatherApi.jsx"

const WeatherForecast = () => {
    const [searchParams] = useSearchParams()
    const [recommendedDays, setRecommendedDays] = useState([])
    const [loadingRecommendations, setLoadingRecommendations] = useState(false)

    const lat = searchParams.get("lat")
    const lon = searchParams.get("lon")
    const routeName = searchParams.get("name")

    const routeLocation =
        lat && lon ? { latitude: Number(lat), longitude: Number(lon) } : null

    useEffect(() => {
        if (!lat || !lon) return

        const fetchRecommendations = async () => {
            try {
                setLoadingRecommendations(true)
                const data = await getRecommendedDays(lat, lon)
                setRecommendedDays(data)
            } catch (err) {
                console.error("Failed to load recommendations", err)
            } finally {
                setLoadingRecommendations(false)
            }
        }

        fetchRecommendations()
    }, [lat, lon])

    return (
        <div className="min-h-screen">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold text-left text-cyan-950">
                        Weather Forecast
                    </h1>
                    <p className="text-muted-foreground pb-4">
                        Real-time weather conditions and forecasts for your planned routes
                    </p>
                </div>
            </div>

            {/* Current Weather */}
            <CurrentInfo
                    overrideLocation={routeLocation}
                    trailName={routeName}
            />


            {/* Recommended Days */}
            {loadingRecommendations ? (
                <div className="p-3 text-muted-foreground">
                    Calculating best days…
                </div>
            ) : (
                <RecommendedDays days={recommendedDays} />
            )}

            {/* WeatherTabs - 8Hours and 16-Days */}
            <div className="mt-5">
                <WeatherTabs overrideLocation={routeLocation} />
            </div>

        </div>
    )
}

export default WeatherForecast
