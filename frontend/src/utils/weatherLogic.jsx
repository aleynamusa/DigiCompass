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
            return <SunIcon data-testid="weather-icon-sunny" size={size} className="text-yellow-500" />

        case "Clear":
        case "Mostly Clear":
            return <SunMoonIcon data-testid="weather-icon-sunMoon" size={size} className="text-gray-500" />

        case "Rain":
        case "Drizzle":
        case "Rain Showers":
        case "Freezing Rain":
            return <CloudRainIcon data-testid="weather-icon-cloudRain" size={size} className="text-blue-500" />

        case "Snow":
        case "Snow Grains":
        case "Snow Showers":
            return <SnowflakeIcon data-testid="weather-icon-snowflake" size={size} className="text-blue-300" />

        case "Cloudy":
        case "Partly-Cloudy":
            return <CloudIcon data-testid="weather-icon-cloudy" size={size} className="text-gray-500" />

        case "Thunderstorm":
        case "Thunderstorm with Hail":
            return <CloudLightningIcon data-testid="weather-icon-lightning" size={size} className="text-gray-500" />

        case "Fog":
            return <CloudFogIcon data-testid="weather-icon-fog" size={size} className="text-gray-400" />

        default:
            return <CloudIcon data-testid="weather-icon-cloudy" size={size} className="text-gray-400" />
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
