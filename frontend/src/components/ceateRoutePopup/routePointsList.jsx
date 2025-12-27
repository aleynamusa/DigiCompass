// components/createRoutePopup/RoutePointsList.jsx
import React from 'react';
import { Badge } from "@/components/ui/badge.jsx";
import { Button } from "@/components/ui/button.jsx";
import { Card, CardContent } from "@/components/ui/card.jsx";
import { ScrollArea } from "@mantine/core";
import { Alert } from "@mantine/core";
import { Separator } from "@radix-ui/react-select";
import {
    MapPin,
    Trash2,
    Flag,
    Target,
    AlertCircle,
    Navigation,
    Clock
} from 'lucide-react';

const RoutePointsList = ({
                             points,
                             distance,
                             estimatedTime,
                             onRemovePoint,
                             onClearAll,
                             disabled,
                             isLoading = false
                         }) => {
    return (
        <Card className="flex-1 flex flex-col">
            <CardContent className="pt-6">
                <div className="flex justify-between items-center mb-4">
                    <div className="flex items-center gap-2">
                        <MapPin className="h-4 w-4" />
                        <h3 className="font-semibold">Route Points</h3>
                    </div>
                    <Badge variant={points.length >= 2 ? "default" : "destructive"}>
                        {points.length} points
                    </Badge>
                </div>


                {points.length > 0 ? (
                    <>
                        <div className="grid grid-cols-2 gap-4 mb-4">
                            <div>
                                <p className="text-sm text-gray-500">Distance</p>
                                <div className="flex items-center gap-2">
                                    <Navigation className="h-4 w-4" />
                                    <p className="font-semibold">
                                        {distance > 0 ? `${distance.toFixed(2)} km` : 'Calculating...'}
                                    </p>
                                </div>
                            </div>
                            <div>
                                <p className="text-sm text-gray-500">Est. Time</p>
                                <div className="flex items-center gap-2">
                                    <Clock className="h-4 w-4" />
                                    <p className="font-semibold">
                                        {estimatedTime || 'Calculating...'}
                                    </p>
                                </div>
                            </div>
                        </div>

                        <Separator className="my-4" />

                        <ScrollArea className="h-[220px] mb-4">
                            <div className="space-y-2">
                                {points.map((point, index) => (
                                    <div
                                        key={point.id}
                                        className="flex items-center justify-between p-2 hover:bg-gray-50 rounded"
                                    >
                                        <div className="flex items-center gap-2">
                                            {point.isStart ? (
                                                <Flag className="h-4 w-4 text-green-600" />
                                            ) : point.isEnd ? (
                                                <Target className="h-4 w-4 text-red-600" />
                                            ) : (
                                                <MapPin className="h-4 w-4 text-gray-400" />
                                            )}
                                            <span className="text-sm">
                        Point {index + 1}: {point.lat.toFixed(4)}, {point.lng.toFixed(4)}
                      </span>
                                        </div>
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="sm"
                                            onClick={() => onRemovePoint(point.id)}
                                            disabled={disabled}
                                            className="h-8 w-8 p-0"
                                        >
                                            <Trash2 className="h-4 w-4" />
                                        </Button>
                                    </div>
                                ))}
                            </div>
                        </ScrollArea>

                        <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            onClick={onClearAll}
                            disabled={disabled}
                            className="w-full"
                        >
                            <Trash2 className="h-4 w-4 mr-2" />
                            Clear All Points
                        </Button>
                    </>
                ) : (
                    <div className="text-center py-8">
                        <MapPin className="h-8 w-8 text-gray-400 mx-auto mb-2" />
                        <p className="text-gray-500 text-sm">
                            No points added yet. Click on the map to add points.
                        </p>
                    </div>
                )}

                <p className="text-xs text-gray-500 mt-4">
                    First point is start (🏁), last point is end (🎯). Minimum 2 points required.
                </p>
            </CardContent>
        </Card>
    );
};

export default RoutePointsList;