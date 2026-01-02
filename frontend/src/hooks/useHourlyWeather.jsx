import { useEffect, useState } from "react"
import { getHourlyWeather } from "@/api/weatherApi"
import {mapHourlyWeather} from "@/utils/weatherMapper.jsx";

const getCurrentHourIndex = (times) => {
    const now = new Date()
    const currentHour = now.getHours()

    return times.findIndex((iso) => {
        const date = new Date(iso)
        return date.getHours() === currentHour
    })
}

export const useHourlyWeather = (location) => {
    const [data, setData] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        if (!location) return

        setLoading(true)
        setError(null)

        getHourlyWeather(location.latitude, location.longitude)
            .then(res => {
                const dto = res.data
                const startIndex = getCurrentHourIndex(dto.time)

                const slicedDto = {
                    ...dto,
                    time: dto.time.slice(startIndex, startIndex + 8),
                    temperature: dto.temperature.slice(startIndex, startIndex + 8),
                    weatherCode: dto.weatherCode.slice(startIndex, startIndex + 8),
                    uvIndex: dto.uvIndex.slice(startIndex, startIndex + 8),
                    precipitation: dto.precipitation.slice(startIndex, startIndex + 8),
                }

                setData(mapHourlyWeather(slicedDto))
            })
            .catch(err => {
                console.error("Failed to fetch hourly weather:", err)
                setError("Failed to load hourly weather data")
                setData([])
            })


    .finally(() => setLoading(false))
    }, [location])

    return { hourlyWeather: data, hourlyLoading: loading, hourlyError: error }
}
