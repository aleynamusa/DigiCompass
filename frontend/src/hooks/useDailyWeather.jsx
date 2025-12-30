import { useEffect, useState } from "react"
import { getDailyWeather } from "@/api/weatherApi"

const formatWeekdayDayMonth = (isoDate) => {
    if (!isoDate) return "";

    return new Intl.DateTimeFormat("en-US", {
        weekday: "short",  // Mon, Tue, Wed
        day: "numeric",    // 30
        month: "short"     // Dec
    }).format(new Date(isoDate));
};



const DAYS = 16

export const useDailyWeather = (location) => {
    const [dailyWeather, setDailyWeather] = useState([])
    const [dailyLoading, setDailyLoading] = useState(true)

    useEffect(() => {
        if (!location) return

        setDailyLoading(true)

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
            .finally(() => setDailyLoading(false))
    }, [location])

    return { dailyWeather, dailyLoading }
}
