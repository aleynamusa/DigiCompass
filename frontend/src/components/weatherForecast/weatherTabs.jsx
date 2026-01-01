import { Badge, Tabs } from "@mantine/core"
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card"
import { getWeatherIcon, getConditionRecommendation } from "@/utils/weatherLogic"
import { useHourlyWeather } from "@/hooks/useHourlyWeather"
import { useGeolocation } from "@/hooks/useGeolocation"
import {useDailyWeather} from "@/hooks/useDailyWeather.jsx";

const WeatherTabs = ({ overrideLocation }) => {
    const geo = useGeolocation(!overrideLocation)
    const location = overrideLocation ?? geo.effectiveLocation

    const { hourlyWeather, hourlyLoading } = useHourlyWeather(location)
    const { dailyWeather, dailyLoading } = useDailyWeather(location)


    if (hourlyLoading || dailyLoading) return <div>Loading forecast...</div>


    return (
        <Tabs defaultValue="hourly">
            <Tabs.List grow>
                <Tabs.Tab value="hourly" c="dimmed">Hourly</Tabs.Tab>
                <Tabs.Tab value="weekly" c="dimmed">16-Day</Tabs.Tab>
            </Tabs.List>

            <Tabs.Panel value="hourly">
                <Card>
                    <CardHeader className="pt-2">
                        <CardTitle>Next 8 Hours</CardTitle>
                        <CardDescription>Hourly weather outlook for trip planning</CardDescription>
                    </CardHeader>

                    <CardContent className="grid grid-cols-4 lg:grid-cols-8 gap-4">
                        {hourlyWeather.slice(0, 8).map((hour, i) => {
                            const rec = getConditionRecommendation(hour.precipitation, hour.uvIndex)

                            return (
                                <div key={i} className="text-center p-3 border rounded-lg">
                                    <p className="text-sm">{hour.time}</p>
                                    <div className="flex justify-center my-2">
                                        {getWeatherIcon(hour.condition, 32)}
                                    </div>
                                    <p className="font-bold">{hour.temp}°C</p>
                                    <p className="text-sm">
                                        {hour.precipitation ?? 0}%
                                    </p>



                                </div>
                            )
                        })}
                    </CardContent>
                </Card>
            </Tabs.Panel>

            <Tabs.Panel value="weekly" className="space-y-4">
                <Card>
                    <CardHeader className="pt-2">
                        <CardTitle>16-Day Forecast</CardTitle>
                        <CardDescription>Daily weather outlook for trip planning</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-3">
                            {dailyWeather.map((day, index) => {
                                const recommendation = getConditionRecommendation(day.weatherCode, 0)
                                return (
                                    <div key={index} className="flex items-center justify-between p-4 border rounded-lg">
                                        <div className="flex items-center gap-4">
                                            <div className="w-24">
                                                <p className="font-medium">{day.time}</p>
                                            </div>
                                            <div className="flex items-center gap-2">
                                                {getWeatherIcon(day.weatherCode, 32)}
                                                <span className="text-sm text-muted-foreground capitalize">
                            {day.weatherCode}
                          </span>
                                            </div>
                                        </div>
                                        <div className="flex items-center gap-4">
                                            <div className="text-right">
                                                <p className="font-medium">
                                                    {day.maxTemperature}°C / {day.minTemperature}°C
                                                </p>
                                                <p className="text-xs text-muted-foreground">{day.precipitation}% rain</p>
                                            </div>
                                        </div>
                                    </div>
                                )
                            })}
                        </div>
                    </CardContent>
                </Card>
            </Tabs.Panel>
        </Tabs>
    )
}

export default WeatherTabs
