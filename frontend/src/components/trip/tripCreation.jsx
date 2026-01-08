import { useEffect, useState } from "react";
import { LockKeyhole } from "lucide-react";
import { createTrip } from "@/api/tripApi.jsx";
import { Button, Checkbox, Modal, Textarea, TextInput } from "@mantine/core";
import RouteAutocomplete from "@/components/trip/routeAutocomplete.jsx";
import { DateTimePicker } from "@mantine/dates";

export const TripCreation = ({
                                 openedPop,
                                 setOpenedPop,
                                 preselectedRoute
                             }) => {
    const CheckboxIcon = ({ indeterminate, ...others }) =>
        indeterminate ? <LockKeyhole {...others} /> : <LockKeyhole {...others} />;

    const [tripTitle, setTripTitle] = useState("");
    const [description, setDescription] = useState("");
    const [plannedDate, setPlannedDate] = useState(new Date());
    const [routeId, setRouteId] = useState(null);
    const [routeName, setRouteName] = useState("");
    const [accessibility, setAccessibility] = useState(false);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleCreateTrip = () => {
        setLoading(true);
        setError(null);

        createTrip({
            name: tripTitle,
            description,
            plannedDate,
            routeId,
            accessibility,
        })
            .then(() => {
                // Reset form state
                setTripTitle("");
                setDescription("");
                setRouteId(null);
                setRouteName("");
                setAccessibility(false);
                setError(null);

                // Close modal on success
                setOpenedPop(false);
            })
            .catch((error) => {
                console.error("Failed to create trip:", error);
                setError("Failed to create trip. Please try again.");
            })
            .finally(() => {
                setLoading(false);
            });
    };

    useEffect(() => {
        if (preselectedRoute) {
            setRouteId(preselectedRoute.id);
            setRouteName(preselectedRoute.name);
        }
    }, [preselectedRoute]);

    return (
        <div className="flex items-center justify-between">
            <Modal
                opened={openedPop}
                onClose={() => setOpenedPop(false)}
                size="lg"
                radius="md"
                centered
                title="Create New Trip"
                zIndex={1000}
            >
                <p className="mb-4">
                    Plan your next outdoor adventure with detailed route and weather information.
                </p>

                <div className="space-y-4">
                    <TextInput required
                        label="Trip Title"
                        placeholder="Enter trip title..."
                        value={tripTitle}
                        onChange={(e) => setTripTitle(e.target.value)}
                        disabled={loading}
                    />

                    <RouteAutocomplete
                        value={routeName}
                        disabled={!!preselectedRoute || loading}
                        onSelect={(route) => {
                            setRouteId(route.id);
                            setRouteName(route.name);
                        }}
                    />

                    <DateTimePicker
                        required
                        label="Pick date and time"
                        placeholder="Pick date and time"
                        value={plannedDate}
                        onChange={(date) => {
                            const dateObj = typeof date === "string" ? new Date(date) : date;
                            setPlannedDate(dateObj);
                        }}
                        minDate={new Date()}
                        clearable
                        popoverProps={{
                            withinPortal: true,
                            zIndex: 10000,
                        }}
                        disabled={loading}
                    />

                    <Checkbox

                        icon={CheckboxIcon}
                        label="Make your route private"
                        checked={accessibility}
                        onChange={(e) => setAccessibility(e.target.checked)}
                        disabled={loading}
                    />

                    <Textarea
                        required
                        label="Description"
                        placeholder="Describe your trip..."
                        rows={3}
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        disabled={loading}
                    />

                    {error && (
                        <p className="text-red-600 font-semibold mt-2">
                            {error}
                        </p>
                    )}

                    <div className="flex justify-end gap-2 mt-4">
                        <Button
                            variant="outline"
                            onClick={() => setOpenedPop(false)}
                            disabled={loading}
                        >
                            Cancel
                        </Button>
                        <Button onClick={handleCreateTrip} loading={loading}>
                            Create Trip
                        </Button>
                    </div>
                </div>
            </Modal>
        </div>
    );
};
