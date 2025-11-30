import { useState, useEffect } from "react";
import { Textarea, Text, SimpleGrid } from "@mantine/core";
import { Dropzone, IMAGE_MIME_TYPE } from "@mantine/dropzone";
import { handleEditReview } from "@/api/routeApi.jsx";

export default function ReviewUpdateForm({ selectedRoute, onOpenChange }) {
    const [text, setText] = useState("");
    const [files, setFiles] = useState([]);
    const [existingImages, setExistingImages] = useState([]);

    useEffect(() => {
        if (selectedRoute) {
            setText(selectedRoute.review || "");
            setExistingImages(selectedRoute.images || []);
        }
    }, [selectedRoute]);

    const handleSubmit = async () => {
        const formData = new FormData();
        formData.append("review", text);
        formData.append("userId.id", selectedRoute.userId.id);
        formData.append("userId.username", selectedRoute.userId.username);
        formData.append("routeId", selectedRoute.routeId);
        formData.append("createdAt", selectedRoute.createdAt);
        formData.append("updatedAt", new Date().toISOString().slice(0, 19));

        files.forEach((file) => formData.append("images", file));

        formData.append("existingImageUrls", JSON.stringify(existingImages));

        try {
            await handleEditReview(selectedRoute.id, formData);
            onOpenChange();
        } catch (err) {
            console.error("Update failed:", err.response?.data || err);
        }
    };

    const removeExistingImage = (index) => {
        setExistingImages(existingImages.filter((_, i) => i !== index));
    };

    const removeNewFile = (index) => {
        setFiles(files.filter((_, i) => i !== index));
    };

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40">
            <div className="bg-white p-6 rounded-xl shadow-xl max-w-lg w-full space-y-4">
                <h2 className="text-lg font-semibold">Edit Your Review</h2>

                <Textarea
                    variant="filled"
                    radius="xs"
                    label="Your thoughts about the route:"
                    placeholder="You can type your thoughts here."
                    autosize
                    minRows={2}
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                />

                <Dropzone
                    accept={IMAGE_MIME_TYPE}
                    onDrop={(acceptedFiles) => {
                        setFiles([...files, ...acceptedFiles]);
                    }}
                >
                    <Text ta="center">Drop images here</Text>
                </Dropzone>

                <SimpleGrid cols={{ base: 1, sm: 4 }} mt={existingImages.length + files.length > 0 ? "xl" : 0}>
                    {existingImages.map((img, i) => (
                        <div key={`existing-${i}`} className="relative">
                            <img src={img} alt={`image-${i}`} className="rounded-md object-cover w-full h-32" />
                            <button
                                onClick={() => removeExistingImage(i)}
                                className="absolute top-1 right-1 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center hover:bg-red-600"
                            >
                                ×
                            </button>
                        </div>
                    ))}

                    {files.map((file, i) => (
                        <div key={`new-${i}`} className="relative">
                            <img
                                src={URL.createObjectURL(file)}
                                alt={`preview-${i}`}
                                className="rounded-md object-cover w-full h-32"
                            />
                            <button
                                onClick={() => removeNewFile(i)}
                                className="absolute top-1 right-1 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center hover:bg-red-600"
                            >
                                ×
                            </button>
                        </div>
                    ))}
                </SimpleGrid>

                <div className="flex justify-end gap-2 pt-2">
                    <button
                        onClick={onOpenChange}
                        className="px-3 py-1 rounded-md bg-gray-200 hover:bg-gray-300"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleSubmit}
                        className="px-3 py-1 rounded-md bg-blue-600 text-white hover:bg-blue-700"
                    >
                        Save
                    </button>
                </div>
            </div>
        </div>
    );
}