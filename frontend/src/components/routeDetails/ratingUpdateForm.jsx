import { Rating } from "@mantine/core";
import { useState, useEffect } from "react";

export default function RatingUpdateForm({ opened, onClose, editingRating, onSave }) {
    const [value, setValue] = useState(0);

    useEffect(() => {
        if (editingRating) {
            setValue(editingRating.rating);
        }
    }, [editingRating]);

    const handleSave = () => {
        if (!editingRating) return;

        const updated = {
            ...editingRating,
            rating: value,
            updatedAt: new Date().toISOString(),
        };

        onSave(updated);
    };

    if (!opened) return null;

    return (
        <div className="fixed inset-0 z-[99999] flex items-center justify-center bg-black/40">
            <div className="bg-white p-6 rounded-xl shadow-xl max-w-md w-full space-y-4 relative z-[100000]">
                <h2 className="text-lg font-semibold">Edit Rating</h2>

                <p className="text-gray-700 font-medium">
                    Update your rating:
                </p>

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
                        onClick={onClose}
                        className="px-4 py-2 rounded-md bg-gray-200 hover:bg-gray-300"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleSave}
                        className="px-4 py-2 rounded-md bg-blue-600 text-white hover:bg-blue-700"
                    >
                        Save Rating
                    </button>
                </div>
            </div>
        </div>
    );
}