import {Dialog, DialogContent, DialogHeader, DialogTitle} from "@/components/ui/dialog";
import {useEffect, useState} from "react";
import {getRatingsByRoute, getReviewByRoute, writeReview, addRating} from "@/api/routeApi.jsx";
import {useAuth} from "@/context/AuthContext.jsx";
import RouteMap from "@/components/routeDetails/routeMap.jsx";
import RouteReviewForm from "@/components/routeDetails/routeReviewForm.jsx";
import RouteTabsReviewRating from "@/components/routeDetails/routeTabs.jsx";

export function RouteDetails({selectedRoute, onOpenChange}) {
    const {user} = useAuth();
    const [reviews, setReviews] = useState([]);
    const [ratings, setRatings] = useState([]);
    const [errors, setErrors] = useState("");

    useEffect(() => {
        if (!selectedRoute?.id) return;
        (async () => {
            try {
                const [reviewsRes, ratingsRes] = await Promise.all([
                    getReviewByRoute(selectedRoute.id),
                    getRatingsByRoute(selectedRoute.id)
                ]);
                setReviews(reviewsRes.data);
                setRatings(ratingsRes.data);
            } catch (err) {
                console.error("Error loading route details:", err);
            }
        })();
    }, [selectedRoute]);

    const handleSubmit = async ({ratingValue, text, images}) => {
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
            const baseUser = {id: user?.id, username: user?.username};

            if (text && text.trim()) {
                await writeReview({
                    review: text.trim(),
                    userId: {
                        id: user.id,
                        username: user.username
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
                    routeId: selectedRoute.id
                });
                console.log("Rating submitted successfully");
            }

            setErrors("");

            const [reviewsRes, ratingsRes] = await Promise.all([
                getReviewByRoute(selectedRoute.id),
                getRatingsByRoute(selectedRoute.id)
            ]);
            setReviews(reviewsRes.data);
            setRatings(ratingsRes.data);

        } catch (error) {
            console.error("Error submitting review or rating:", error);
            setErrors("Failed to submit review or rating. Please try again.");
        }
    };



    return (
        <Dialog open={!!selectedRoute} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-3xl">
                {selectedRoute && (
                    <>
                        <DialogHeader>
                            <DialogTitle>{selectedRoute.name}</DialogTitle>
                            <p className="text-sm text-gray-600">{selectedRoute.description}</p>
                        </DialogHeader>

                        <div className="space-y-3">
                            <div className="flex justify-between text-sm text-gray-700">
                                <p><strong>Distance:</strong> {selectedRoute.distance} km</p>
                                <p><strong>Duration:</strong> {selectedRoute.duration}</p>
                                <p><strong>Difficulty:</strong> {selectedRoute.difficulty}</p>
                            </div>

                            <RouteMap routeGeometry={selectedRoute.routeGeometry} category={selectedRoute.category} />

                            {errors && (
                                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                                    {errors}
                                </div>
                            )}

                            <RouteReviewForm onSubmit={handleSubmit}/>
                            <RouteTabsReviewRating
                                reviews={reviews}
                                ratings={ratings}
                                routeId={selectedRoute.id}
                            />

                        </div>
                    </>
                )}
            </DialogContent>
        </Dialog>
    );


}
