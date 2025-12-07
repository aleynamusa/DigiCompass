import React, { useState, useEffect, useRef } from 'react';
import { MapContainer, TileLayer, Marker, Polyline, useMapEvents } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";

import { Card, CardContent } from "@/components/ui/card";
import {
    MapPin,
    Trash2,
    Flag,
    Target,
    CheckCircle,
    AlertCircle,
    Plus,
    X,
    Navigation,
    Loader2
} from 'lucide-react';
import {Alert, ScrollArea, Textarea} from "@mantine/core";
import {Separator} from "@radix-ui/react-select";
import ImageDropzone from "@/components/imageDropzone.jsx";
import {getCalculatedDistance} from "@/api/routeApi.jsx";
import {useAuth} from "@/context/AuthContext.jsx";

// Fix for default markers in React Leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

// Custom icons
const startIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-green.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

const endIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

const CreateRoutePopup = ({
                              isOpen,
                              onClose,
                              onSave,
                              loading = false
                          }) => {
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        difficulty: 'medium',
    });

    const [points, setPoints] = useState([]);
    const [errors, setErrors] = useState({});
    const [saveError, setSaveError] = useState(null);
    const [mapError, setMapError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const mapRef = useRef(null);
    const [files, setFiles] = useState([]);
    const [distance, setDistance] = useState(0);

    const user = useAuth();


    const calculateDistance = async (points) => {
        return await getCalculatedDistance(points);
    };


    const createGeoJSON = (points) => {
        if (points.length < 2) return null;

        const coordinates = points.map(point => [point.lng, point.lat]);

        return {
            type: "LineString",
            coordinates: coordinates
        };
    };

    const validateForm = () => {
        const newErrors = {};

        if (!formData.name.trim()) {
            newErrors.name = 'Route name is required';
        }
        if (!formData.description.trim()) {
            newErrors.description = 'Description is required';
        }
        if (points.length < 2) {
            newErrors.points = 'At least 2 points are required to create a route';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleMapClick = (e) => {
        const { lat, lng } = e.latlng;
        const newPoint = {
            lat,
            lng,
            isStart: points.length === 0,
            isEnd: false,
            id: Date.now() + Math.random()
        };

        const updatedPoints = points.map((point, index) => ({
            ...point,
            isEnd: index === points.length - 1 ? false : point.isEnd
        }));

        newPoint.isEnd = true;

        setPoints([...updatedPoints, newPoint]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        setSaveError(null);
        setSuccessMessage(null);

        try {
            const distance = await calculateDistance(points);
            const geometry = createGeoJSON(points);

            const routeData = {
                ...formData,
                points: points.map(p => [p.lat, p.lng]),
                geometry: JSON.stringify(geometry),
                distance: parseFloat(distance),
                estimatedTime: calculateEstimatedTime(distance, formData.difficulty),
                createdBy: user.id,
                status: 'pending'
            };

            await onSave(routeData);

            setSuccessMessage('Route created successfully! It is now pending admin approval.');

            setFormData({ name: '', description: '', difficulty: 'medium' });
            setPoints([]);
            setErrors({});

            setTimeout(() => {
                onClose();
                setSuccessMessage(null);
            }, 2000);

        } catch (error) {
            setSaveError(error.message || 'Failed to save route. Please check your connection and try again.');
        }
    };

    useEffect(() => {

            (async () => {
                const dist = await getCalculatedDistance(points);
                setDistance(dist);
            })();

    }, [points]);


    const calculateEstimatedTime = (distance, difficulty) => {
        const baseSpeed = {
            easy: 4, // km/h
            medium: 3,
            hard: 2
        };

        const timeInHours = distance / baseSpeed[difficulty];
        return Math.ceil(timeInHours * 60); // Convert to minutes
    };

    const handleRemovePoint = (id) => {
        const newPoints = points.filter(point => point.id !== id);

        if (newPoints.length > 0) {
            newPoints[0].isStart = true;
            newPoints[newPoints.length - 1].isEnd = true;

            newPoints.forEach((point, i) => {
                if (i > 0 && i < newPoints.length - 1) {
                    point.isStart = false;
                    point.isEnd = false;
                }
            });
        }

        setPoints(newPoints);
    };

    const handleClearPoints = () => {
        setPoints([]);
    };

    const MapClickHandler = () => {
        useMapEvents({
            click: handleMapClick,
        });
        return null;
    };

    useEffect(() => {
        if (!isOpen) {
            setFormData({ name: '', description: '', difficulty: '' });
            setPoints([]);
            setErrors({});
            setSaveError(null);
            setSuccessMessage(null);
            setMapError(null);
        }
    }, [isOpen]);

    if (!isOpen) return null;

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-[1200px] max-h-[90vh] overflow-hidden">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        <Navigation className="h-5 w-5" />
                        Create New Route
                    </DialogTitle>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="flex flex-col h-full">
                    <div className="flex-1 overflow-auto">
                        {successMessage && (
                            <Alert className="mb-4 bg-green-50 text-green-800 border-green-200">
                                <CheckCircle className="h-4 w-4" />
                                {successMessage}
                            </Alert>
                        )}

                        {saveError && (
                            <Alert className="mb-4 bg-red-50 text-red-800 border-red-200">
                                <AlertCircle className="h-4 w-4" />
                                {saveError}
                            </Alert>
                        )}

                        {mapError && (
                            <Alert className="mb-4 bg-yellow-50 text-yellow-800 border-yellow-200">
                                <AlertCircle className="h-4 w-4" />
                                {mapError}
                            </Alert>
                        )}

                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            {/* Left Column - Form */}
                            <div className="space-y-4">
                                <div className="space-y-2">
                                    <label htmlFor="name" className="text-sm font-medium">
                                        Route Name <span className="text-red-500">*</span>
                                    </label>
                                    <Input
                                        id="name"
                                        placeholder="Enter route name"
                                        value={formData.name}
                                        onChange={(e) => setFormData({...formData, name: e.target.value})}
                                        className={errors.name ? "border-red-500" : ""}
                                    />
                                    {errors.name && (
                                        <p className="text-sm text-red-500">{errors.name}</p>
                                    )}
                                </div>

                                <div className="space-y-2">
                                    <label htmlFor="description" className="text-sm font-medium">
                                        Description <span className="text-red-500">*</span>
                                    </label>
                                    <Textarea
                                        id="description"
                                        placeholder="Describe your route..."
                                        value={formData.description}
                                        onChange={(e) => setFormData({...formData, description: e.target.value})}
                                        className={`min-h-[100px] ${errors.description ? "border-red-500" : ""}`}
                                    />
                                    {errors.description && (
                                        <p className="text-sm text-red-500">{errors.description}</p>
                                    )}
                                </div>

                                <div className="space-y-2">
                                    <label htmlFor="difficulty" className="text-sm font-medium">
                                        Difficulty
                                    </label>
                                    <Select
                                        value={formData.difficulty}
                                        onValueChange={(value) => setFormData({...formData, difficulty: value})}
                                    >
                                        <SelectTrigger>
                                            <SelectValue placeholder="Select difficulty" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="easy">Easy</SelectItem>
                                            <SelectItem value="medium">Medium</SelectItem>
                                            <SelectItem value="hard">Hard</SelectItem>
                                        </SelectContent>
                                    </Select>


                                    <label htmlFor="difficulty" className="text-sm font-medium">
                                        Route Type
                                    </label>
                                    <Select
                                        value={formData.type}
                                        onValueChange={(value) => setFormData({...formData, type: value})}
                                    >
                                        <SelectTrigger>
                                            <SelectValue placeholder="Select route type" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="hiking">Hiking</SelectItem>
                                            <SelectItem value="cycling">Cycling</SelectItem>
                                            <SelectItem value="running">Running</SelectItem>
                                            <SelectItem value="walking">Walking</SelectItem>
                                        </SelectContent>
                                    </Select>

                                    <label htmlFor="difficulty" className="text-sm font-medium">
                                        Route Type
                                    </label>
                                    <Select
                                        value={formData.type}
                                        onValueChange={(value) => setFormData({...formData, type: value})}
                                    >
                                        <SelectTrigger>
                                            <SelectValue placeholder="Select route type" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="hiking">Hiking</SelectItem>
                                            <SelectItem value="cycling">Cycling</SelectItem>
                                            <SelectItem value="running">Running</SelectItem>
                                            <SelectItem value="walking">Walking</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>

                                {/* Points Summary */}
                                <Card>
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

                                        {errors.points && (
                                            <Alert className="mb-4 bg-red-50 text-red-800 border-red-200">
                                                <AlertCircle className="h-4 w-4" />
                                                <AlertDescription>{errors.points}</AlertDescription>
                                            </Alert>
                                        )}

                                        {points.length > 0 && (
                                            <>
                                                <div className="grid grid-cols-2 gap-4 mb-4">
                                                    <div>
                                                        <p className="text-sm text-gray-500">Distance</p>
                                                        <div className="flex items-center gap-2">
                                                            <Navigation className="h-4 w-4" />
                                                            <p className="font-semibold">{distance.toFixed(2)} km</p>

                                                        </div>
                                                    </div>
                                                    <div>
                                                        <p className="text-sm text-gray-500">Est. Time</p>
                                                        <div className="flex items-center gap-2">
                                                            <Target className="h-4 w-4" />
                                                            <p className="font-semibold">
                                                                {calculateEstimatedTime(calculateDistance(points), formData.difficulty)} min
                                                            </p>
                                                        </div>
                                                    </div>
                                                </div>

                                                <Separator className="my-4" />

                                                <ScrollArea className="h-[150px] mb-4">
                                                    <div className="space-y-2">
                                                        {points.map((point, index) => (
                                                            <div key={point.id} className="flex items-center justify-between p-2 hover:bg-gray-50 rounded">
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
                                                                    onClick={() => handleRemovePoint(point.id)}
                                                                    disabled={points.length <= 2}
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
                                                    onClick={handleClearPoints}
                                                    className="w-full"
                                                >
                                                    <Trash2 className="h-4 w-4 mr-2" />
                                                    Clear All Points
                                                </Button>
                                            </>
                                        )}

                                        {points.length === 0 && (
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
                            </div>

                            {/* Right Column - Map */}
                            <div className="space-y-2">
                                <div className="flex items-center gap-2">
                                    <MapPin className="h-4 w-4" />
                                    <p className="text-sm text-gray-500">
                                        Click on the map to add route points
                                    </p>
                                </div>

                                <Card className="overflow-hidden">
                                    <div className="h-[400px]">
                                        <MapContainer
                                            center={[51.505, -0.09]}
                                            zoom={13}
                                            style={{ height: '100%', width: '100%' }}
                                            ref={mapRef}
                                            whenCreated={(mapInstance) => {
                                                mapRef.current = mapInstance;
                                                mapInstance.on('tileerror', () => {
                                                    setMapError('Failed to load map tiles. Please check your connection.');
                                                });
                                            }}
                                        >
                                            <TileLayer
                                                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                                                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                                            />

                                            <MapClickHandler />

                                            {/* Render markers */}
                                            {points.map((point) => (
                                                <Marker
                                                    key={point.id}
                                                    position={[point.lat, point.lng]}
                                                    icon={point.isStart ? startIcon : point.isEnd ? endIcon : undefined}
                                                    eventHandlers={{
                                                        click: () => handleRemovePoint(point.id)
                                                    }}
                                                />
                                            ))}

                                            {/* Render polyline */}
                                            {points.length >= 2 && (
                                                <Polyline
                                                    positions={points.map(p => [p.lat, p.lng])}
                                                    color="#3b82f6"
                                                    weight={4}
                                                    opacity={0.7}
                                                />
                                            )}
                                        </MapContainer>
                                    </div>
                                </Card>

                                <p className="text-xs text-gray-500">
                                    Click on markers to remove points
                                </p>

                                <ImageDropzone
                                    existingImages={[]}
                                    onExistingImagesChange={() => {}}
                                    newFiles={files}
                                    onNewFilesChange={setFiles}
                                    label="Drop images here"
                                />
                            </div>
                        </div>
                    </div>

                    {/* Action Buttons */}
                    <DialogFooter className="mt-6">
                        <Button
                            type="button"
                            variant="outline"
                            onClick={onClose}
                            disabled={loading}
                        >
                            <X className="h-4 w-4 mr-2" />
                            Cancel
                        </Button>
                        <Button
                            type="submit"
                            disabled={loading || points.length < 2}
                        >
                            {loading ? (
                                <>
                                    <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                                    Creating...
                                </>
                            ) : (
                                <>
                                    <Plus className="h-4 w-4 mr-2" />
                                    Create Route
                                </>
                            )}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
};

export default CreateRoutePopup;