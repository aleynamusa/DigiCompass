import { useEffect, useState } from "react"
import { getDailyWeather } from "@/api/weatherApi"

const formatWeekdayDayMonth = (isoDate) => {
    if (!isoDate) return "";

    return new Intl.DateTimeFormat("en-US", {
        weekday: "short",
        day: "numeric",
        month: "short"
    }).format(new Date(isoDate));
};



const DAYS = 16

export const useDailyWeather = (location) => {
    const [dailyWeather, setDailyWeather] = useState([])
    const [dailyLoading, setDailyLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        if (!location) return

        setDailyLoading(true)
        setError(null)

        getDailyWeather(location.latitude, location.longitude)
            .then(res => {
                const dto = res.data

                const normalized = Array.from({ length: DAYS }, (_, i) => ({
                    time: formatWeekdayDayMonth(dto.time[i]),
                    maxTemperature: dto.maxTemperature[i],
                    minTemperature: dto.minTemperature[i],
                    weatherCode: dto.weatherCode[i],
                    precipitation: dto.precipitation[i],
                    condition: dto.condition?.[i], // if backend sends it
                }))

                setDailyWeather(normalized)
            })
            .catch(err => {
                console.error("Failed to fetch daily weather:", err)
                setError("Failed to load daily weather data")
                setDailyWeather([])
            })
            .finally(() => setDailyLoading(false))
    }, [location])

    return { dailyWeather: dailyWeather, dailyLoading: dailyLoading, dailyError: error }
}
