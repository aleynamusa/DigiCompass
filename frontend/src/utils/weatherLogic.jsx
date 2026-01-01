import {
    SunIcon,
    CloudIcon,
    CloudRainIcon,
    SnowflakeIcon,
    SunMoonIcon, CloudFogIcon, CloudLightningIcon,
} from "lucide-react"

export const getWeatherIcon = (condition, size = 24) => {

    switch (condition) {
        case "Sunny":
            return <SunIcon size={size} className="text-yellow-500" />

        case "Clear":
        case "Mostly Clear":
            return <SunMoonIcon size={size} className="text-gray-500" />

        case "Rain":
        case "Drizzle":
        case "Rain Showers":
        case "Freezing Rain":
            return <CloudRainIcon size={size} className="text-blue-500" />

        case "Snow":
        case "Snow Grains":
        case "Snow Showers":
            return <SnowflakeIcon size={size} className="text-blue-300" />

        case "Cloudy":
        case "Partly-Cloudy":
            return <CloudIcon size={size} className="text-gray-500" />

        case "Thunderstorm":
        case "Thunderstorm with Hail":
            return <CloudLightningIcon size={size} className="text-gray-500" />

        case "Fog":
            return <CloudFogIcon size={size} className="text-gray-400" />

        default:
            return <CloudIcon size={size} className="text-gray-400" />
    }
}

export const getConditionRecommendation = (precipitation, uvIndex) => {
    if (precipitation > 70)
        return { text: "Not Recommended", color: "bg-red-100 text-red-800" }

    if (precipitation > 30)
        return { text: "Use Caution", color: "bg-yellow-100 text-yellow-800" }

    if (uvIndex >= 7)
        return { text: "UV Caution", color: "bg-orange-100 text-orange-800" }

    return { text: "Good", color: "bg-green-100 text-green-800" }
}

export const BADGE_COLORS = {
    Perfect: "green",
    Good: "blue",
    Caution: "yellow",
    Avoid: "red",
}
