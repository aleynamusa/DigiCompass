import {useEffect, useState} from "react";
import { LockKeyhole} from "lucide-react";
import {createTrip} from "@/api/tripApi.jsx";
import {Button, Checkbox, Group, Modal, Textarea, TextInput} from "@mantine/core";
import RouteAutocomplete from "@/components/trip/routeAutocomplete.jsx";
import {DateTimePicker} from "@mantine/dates";

export const TripCreation = ({
                                 userId,
                                 openedPop,
                                 setOpenedPop,
                                 preselectedRoute
                             }) =>  {
    const CheckboxIcon = ({ indeterminate, ...others }) =>
        indeterminate ? <LockKeyhole {...others} /> : <LockKeyhole {...others} />;

    const [tripTitle, setTripTitle] = useState("");
    const [description, setDescription] = useState("");
    const [plannedDate, setPlannedDate] = useState(new Date());
    const [routeId, setRouteId] = useState(null);
    const [routeName, setRouteName] = useState("");
    const [accessibility, setAccessibility] = useState(false);


    const handleCreateTrip = () => {
        createTrip({
            name: tripTitle,
            description,
            plannedDate,
            routeId,
            accessibility,
            userId,
        })
            .then(() => {
                setOpenedPop(false);
            })
            .catch(error => {
                console.error("Failed to create trip:", error);
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
                    {/* Remove the duplicate h1 */}
                    <p className="mb-4">
                        Plan your next outdoor adventure with detailed route and weather information.
                    </p>

                    <div className="space-y-4">
                        <TextInput
                            label="Trip Title"
                            placeholder="Enter trip title..."
                            value={tripTitle}
                            onChange={(e) => setTripTitle(e.target.value)}
                        />

                        <RouteAutocomplete
                            value={routeName}
                            disabled={!!preselectedRoute}
                            onSelect={(route) => {
                                setRouteId(route.id);
                                setRouteName(route.name);
                            }}
                        />


                        <DateTimePicker
                            label="Pick date and time"
                            placeholder="Pick date and time"
                            value={plannedDate}
                            onChange={(date) => {
                                console.log("Date selected:", date, typeof date);
                                // Always convert to Date object
                                const dateObj = typeof date === 'string' ? new Date(date) : date;
                                setPlannedDate(dateObj);
                            }}
                            minDate={new Date()}
                            clearable
                            popoverProps={{
                                withinPortal: true,
                                zIndex: 10000,
                            }}
                        />

                        <Checkbox
                            icon={CheckboxIcon}
                            label="Make your route private"
                            checked={accessibility}
                            onChange={(e) => setAccessibility(e.target.checked)}
                        />

                        <Textarea
                            label="Description"
                            placeholder="Describe your trip..."
                            rows={3}
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                        />

                        <div className="flex justify-end gap-2 mt-4">
                            <Button variant="outline" onClick={() => setOpenedPop(false)}>
                                Cancel
                            </Button>
                            <Button onClick={() => {
                                handleCreateTrip()
                                setOpenedPop(false);
                            }}>
                                Create Trip
                            </Button>
                        </div>
                    </div>
                </Modal>
            </div>
    )
}
