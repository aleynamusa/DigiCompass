import React, { useState, useEffect } from 'react';
import 'leaflet/dist/leaflet.css';
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogFooter,
} from "@/components/ui/dialog.jsx";
import { Button } from "@/components/ui/button.jsx";
import { Card } from "@/components/ui/card.jsx";
import {
    MapPin,
    CheckCircle,
    AlertCircle,
    Plus,
    X,
    Navigation,
    Loader2
} from 'lucide-react';
import { Alert } from "@mantine/core";
import ImageDropzone from "@/components/imageDropzone.jsx";
import { useAuth } from "@/context/AuthContext.jsx";
import RouteMapPopup from "@/components/ceateRoutePopup/routeMapPopup.jsx";
import { startIcon, endIcon } from '../../utils/leafletIcons';
import { calculateEstimatedTime, createGeoJSON } from '../../utils/routeCalculations';
import { validateRouteForm } from '../../utils/routeValidation';
import { useGeolocation } from '../../hooks/useGeolocation';
import { useGraphHopperRoute } from '../../hooks/useGraphHopperRoute';
import RouteForm from './RouteForm';
import RoutePointsList from './RoutePointsList';

const CreateRoutePopup = ({ isOpen, onClose, onSave, loading = false }) => {

    const { user } = useAuth();
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        difficulty: 'Medium',
        routeType: 'Hiking',
        distance: 0,
        duration: '',
        createdAt: new Date().toISOString().slice(0, 19),
        updatedAt: new Date().toISOString().slice(0, 19)
    });

    const [points, setPoints] = useState([]);
    const [errors, setErrors] = useState({});
    const [saveError, setSaveError] = useState(null);
    const [mapError, setMapError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [files, setFiles] = useState([]);

    const currentLocation = useGeolocation(isOpen);
    const { ghRoute, distance, estimatedTime, ghError, isLoading: isLoadingRoute } = useGraphHopperRoute(points, formData.routeType);

    useEffect(() => {
        let msg = null;
        if (currentLocation?.error) {
            msg = currentLocation.error;
        } else if (ghError) {
            msg = `Route preview error: ${ghError}`;
        }
        setMapError(msg);
    }, [currentLocation, ghError]);

    useEffect(() => {
        if (isOpen && !user) {
            setSaveError('You must be logged in to create routes');
            setTimeout(() => {
                onClose();
            }, 2000);
        }
    }, [isOpen, user, onClose]);

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

        const validationErrors = validateRouteForm(formData, points);
        setErrors(validationErrors);

        if (Object.keys(validationErrors).length > 0) {
            return;
        }

        if (!user) {
            console.log('No user');
            setSaveError('You must be logged in to create routes');
            return;
        }

        console.log('Validation passed, proceeding...');
        setSaveError(null);
        setSuccessMessage(null);
        setIsSubmitting(true);

        try {

            const geometry = createGeoJSON(points);

            const estimatedTimeStr = calculateEstimatedTime(
                distance,
                formData.difficulty,
                formData.routeType
            );

            const routeData = {
                ...formData,
                points: points.map(p => [p.lat, p.lng]),
                geometry: geometry,
                distance: parseFloat(distance.toFixed(2)),
                estimatedTime: estimatedTimeStr,
                createdBy: user.id,
                status: 'pending',
                images: files
            };


            await onSave(routeData);

            setSuccessMessage('Route created successfully! It is now pending admin approval.');

            setFormData({
                name: '',
                description: '',
                difficulty: 'Medium',
                routeType: 'Hiking'
            });
            setPoints([]);
            setFiles([]);
            setErrors({});

            setTimeout(() => {
                onClose();
                setSuccessMessage(null);
            }, 2000);

        } catch (error) {
            console.error('Error in handleSubmit:', error);
            setSaveError(
                error.message ||
                'Failed to save route. Please check your connection and try again.'
            );
        } finally {
            setIsSubmitting(false);
        }
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
        setErrors({ ...errors, points: undefined });
    };

    const handleFormChange = (newFormData) => {
        setFormData(newFormData);
        const changedFields = Object.keys(newFormData).filter(
            key => newFormData[key] !== formData[key]
        );
        const newErrors = { ...errors };
        changedFields.forEach(field => delete newErrors[field]);
        setErrors(newErrors);
    };

    useEffect(() => {
        if (!isOpen) {
            setFormData({
                name: '',
                description: '',
                difficulty: 'Medium',
                routeType: 'Hiking'
            });
            setPoints([]);
            setFiles([]);
            setErrors({});
            setSaveError(null);
            setSuccessMessage(null);
            setMapError(null);
            setIsSubmitting(false);
        }
    }, [isOpen]);

    useEffect(() => {
        console.log("Geolocation:", currentLocation);
    }, [currentLocation]);



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
                                <RouteForm
                                    formData={formData}
                                    onChange={handleFormChange}
                                    errors={errors}
                                    disabled={isSubmitting}
                                />

                                <RoutePointsList
                                    points={points}
                                    distance={distance}
                                    estimatedTime={estimatedTime}
                                    errors={errors}
                                    onRemovePoint={handleRemovePoint}
                                    onClearAll={handleClearPoints}
                                    disabled={isSubmitting}
                                    isLoading={isLoadingRoute}
                                />
                            </div>

                            {/* Right Column - Map & Images */}
                            <div className="space-y-2">
                                <div className="flex items-center gap-2">
                                    <MapPin className="h-4 w-4" />
                                    <p className="text-sm text-gray-500">
                                        Click on the map to add route points
                                    </p>
                                </div>

                                <Card className="overflow-hidden">
                                    <div className="h-[290px]">

                                        <RouteMapPopup
                                            center={currentLocation.effectiveLocation}
                                            points={points}
                                            ghRoute={ghRoute}
                                            ghError={ghError}
                                            onMapClick={handleMapClick}
                                            startIcon={startIcon}
                                            endIcon={endIcon}
                                            isLoading={isLoadingRoute}
                                        />

                                    </div>
                                </Card>

                                <ImageDropzone
                                    existingImages={[]}
                                    onExistingImagesChange={() => {}}
                                    newFiles={files}
                                    onNewFilesChange={setFiles}
                                    label="Drop route images here (optional)"
                                />
                            </div>
                        </div>
                    </div>

                    <DialogFooter className="mt-6">
                        <Button
                            type="button"
                            variant="outline"
                            onClick={onClose}
                            disabled={isSubmitting}
                        >
                            <X className="h-4 w-4 mr-2" />
                            Cancel
                        </Button>
                        <Button
                            type="submit"
                            disabled={isSubmitting || points.length < 2 || !user}
                        >
                            {isSubmitting ? (
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


