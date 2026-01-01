import { Card, CardHeader, CardContent, CardTitle } from "@/components/ui/card"
import { Badge } from "@mantine/core"
import {BADGE_COLORS, getWeatherIcon} from "@/utils/weatherLogic"



const RecommendedDays = ({ days }) => {
    const todayStr = new Date().toISOString().split("T")[0]

    const filteredDays = days.filter(day => day.date !== todayStr)

    if (!filteredDays.length) return null

    return (
        <Card className="mt-6">
            <CardHeader className="pt-4">
                <CardTitle>Recommended Days for This Route</CardTitle>
            </CardHeader>

            <CardContent className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {filteredDays.map((day, i) => (
                    <div
                        key={i}
                        className="border rounded-lg p-4 flex flex-col gap-2"
                    >
                        <div className="flex items-center justify-between">
                            <p className="font-medium">
                                {new Date(day.date).toLocaleDateString("en-US", {
                                    weekday: "short",
                                    day: "numeric",
                                    month: "short"
                                })}
                            </p>

                            <Badge color={BADGE_COLORS[day.label] || "bg-gray-100 text-gray-800"}>
                                {day.label}
                            </Badge>
                        </div>

                        <div className="flex items-center gap-2">
                            {getWeatherIcon(day.weatherCode, 32)}
                            <span className="text-sm text-muted-foreground">
                {Math.round(day.maxTemp)}° / {Math.round(day.minTemp)}°
              </span>
                        </div>

                        <ul className="text-xs text-muted-foreground list-disc list-inside">
                            {day.reasons.map((reason, idx) => (
                                <li className="list-none" key={idx}>{reason}</li>
                            ))}
                        </ul>
                    </div>
                ))}
            </CardContent>
        </Card>
    )
}

export default RecommendedDays
