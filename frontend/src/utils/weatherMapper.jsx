const formatHour = (isoString) => {
    const date = new Date(isoString)
    const now = new Date()

    if (date.getHours() === now.getHours()) {
        return "Now"
    }

    return date.toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
        hour12:false,
    })
}


export const mapHourlyWeather = (dto) => {
    return dto.time.map((time, index) => ({
        time: formatHour(time),   // ✅ hour only
        temp: Math.round(dto.temperature[index]),
        condition: dto.weatherCode[index],
        uvIndex: dto.uvIndex[index],
    }))
}
