import { Modal, Group, Text, Button } from "@mantine/core";
import { useEffect, useState } from "react";
import { getRatingsByRoute, getReviewByRoute, writeReview, addRating } from "@/api/routeApi.jsx";
import { useAuth } from "@/context/AuthContext.jsx";
import RouteMap from "@/components/routeDetails/routeMap.jsx";
import RouteReviewForm from "@/components/routeDetails/routeReviewForm.jsx";
import RouteTabsReviewRating from "@/components/routeDetails/routeTabs.jsx";

export function RouteDetails({ selectedRoute, onOpenChange }) {
    const { user } = useAuth();
    const [reviews, setReviews] = useState([]);
    const [ratings, setRatings] = useState([]);
    const [errors, setErrors] = useState("");

    useEffect(() => {
        if (!selectedRoute?.id) return;
        (async () => {
            try {
                const [reviewsRes, ratingsRes] = await Promise.all([
                    getReviewByRoute(selectedRoute.id),
                    getRatingsByRoute(selectedRoute.id),
                ]);
                setReviews(reviewsRes.data);
                setRatings(ratingsRes.data);
            } catch (err) {
                console.error("Error loading route details:", err);
            }
        })();
    }, [selectedRoute]);

    const handleSubmit = async ({ ratingValue, text, images }) => {
        try {
            if (!ratingValue && !text) {
                setErrors("Please add a review or rating.");
                return;
            }

            if (text && text.trim() === "") {
                setErrors("Review text cannot be empty.");
                return;
            }

            const now = new Date().toISOString().slice(0, 19);
            const baseUser = { id: user?.id, username: user?.username };

            if (text && text.trim()) {
                await writeReview({
                    review: text.trim(),
                    userId: {
                        id: user.id,
                        username: user.username,
                    },
                    createdAt: now,
                    updatedAt: now,
                    images: images || [],
                    routeId: selectedRoute.id,
                });
                console.log("Review submitted successfully");
            }

            if (ratingValue) {
                await addRating({
                    rating: ratingValue,
                    userId: baseUser,
                    createdAt: now,
                    updatedAt: now,
                    routeId: selectedRoute.id,
                });
                console.log("Rating submitted successfully");
            }

            setErrors("");

            const [reviewsRes, ratingsRes] = await Promise.all([
                getReviewByRoute(selectedRoute.id),
                getRatingsByRoute(selectedRoute.id),
            ]);
            setReviews(reviewsRes.data);
            setRatings(ratingsRes.data);
        } catch (error) {
            console.error("Error submitting review or rating:", error);
            setErrors("Failed to submit review or rating. Please try again.");
        }
    };

    return (
        <Modal
            opened={!!selectedRoute}
            onClose={() => onOpenChange(false)}
            size="lg"
            centered
            overlayProps={{ blur: 3 }}
            withCloseButton
            title={selectedRoute?.name}
        >
            {selectedRoute && (
                <div className="space-y-3">
                    <Text size="sm" color="dimmed" mb="md">
                        {selectedRoute.description}
                    </Text>

                    <Group position="apart" spacing="xs" mb="md" className="text-gray-700 text-sm">
                        <Text>
                            <strong>Distance:</strong> {selectedRoute.distance} km
                        </Text>
                        <Text>
                            <strong>Duration:</strong> {selectedRoute.duration}
                        </Text>
                        <Text>
                            <strong>Difficulty:</strong> {selectedRoute.difficulty}
                        </Text>
                    </Group>

                    <RouteMap routeGeometry={selectedRoute.routeGeometry} category={selectedRoute.category} />

                    {errors && (
                        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                            {errors}
                        </div>
                    )}

                    <RouteReviewForm onSubmit={handleSubmit} />
                    <RouteTabsReviewRating reviews={reviews} ratings={ratings} routeId={selectedRoute.id} />
                </div>
            )}
        </Modal>
    );
}
