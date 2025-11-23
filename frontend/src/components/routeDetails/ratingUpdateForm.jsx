import { Rating } from "@mantine/core";
import { useState, useEffect } from "react";
import { handleEditRating } from "@/api/routeApi.jsx";

export default function RatingUpdateForm({ selectedRoute, onOpenChange }) {
    const [value, setValue] = useState(0);

    useEffect(() => {
        if (selectedRoute) {
            setValue(selectedRoute.rating || 0);
        }
    }, [selectedRoute]);

    const handleSubmit = async () => {
        const updatedRating = {
            id: selectedRoute.id,
            rating: value,
            userId: {
                id: selectedRoute.userId.id,
                username: selectedRoute.userId.username,
            },
            routeId: selectedRoute.routeId,
            createdAt: selectedRoute.createdAt,
            updatedAt: new Date().toISOString().slice(0, 19),
        };

        try {
            await handleEditRating(selectedRoute.id, updatedRating);
            onOpenChange(); // close modal
        } catch (err) {
            console.error("Rating update failed:", err.response?.data || err);
        }
    };

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40">
            <div className="bg-white p-6 rounded-xl shadow-xl max-w-lg w-full space-y-4">
                <h2 className="text-lg font-semibold">Edit Rating</h2>

                <p className="text-gray-700">Update your rating:</p>

                <div className="flex justify-center py-4">
                    <Rating
                        value={value}
                        onChange={setValue}
                        fractions={2}
                        size="lg"
                    />
                </div>

                <div className="flex justify-end gap-2 pt-2">
                    <button
                        onClick={onOpenChange}
                        className="px-4 py-2 rounded-md bg-gray-200 hover:bg-gray-300"
                    >
                        Cancel
                    </button>

                    <button
                        onClick={handleSubmit}
                        className="px-4 py-2 rounded-md bg-blue-600 text-white hover:bg-blue-700"
                    >
                        Save Rating
                    </button>
                </div>
            </div>
        </div>
    );
}
