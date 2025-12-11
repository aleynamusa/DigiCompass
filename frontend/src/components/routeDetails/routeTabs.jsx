import {Tabs, Flex, FloatingIndicator, Rating     } from "@mantine/core";
import {useEffect, useRef, useState} from "react";
import { useNavigate } from "react-router-dom";
import classes from "@/components/card.module.css";
import {useAuth} from "@/context/AuthContext.jsx";
import {getRatingsByRoute, getReviewByRoute, handleDeleteRating, handleDeleteReview} from "@/api/routeApi.jsx";
import { Trash, Pencil } from 'lucide-react';
import ReviewUpdateForm from "@/components/routeDetails/reviewUpdateForm.jsx";
import RatingUpdateForm from "@/components/routeDetails/ratingUpdateForm.jsx";


export default function RouteTabs({reviews, ratings, routeId}) {
    const [value, setValue] = useState("1");
    const [rootRef, setRootRef] = useState(null);
    const controlsRefs = useRef({});
    const [editingReview, setEditingReview] = useState(null);
    const [editingRating, setEditingRating] = useState(false);
    const [isFormOpenReview, setIsFormOpenReview] = useState(false);
    const [isFormOpenRating, setIsFormOpenRating] = useState(false);
    const { user } = useAuth();
    const navigate = useNavigate();
    const [reviewList, setReviewList] = useState(reviews || []);
    const [ratingList, setRatingList] = useState(ratings || []);
    const [successMessage, setSuccessMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");




    const setControlRef = (val) => (node) => {
        if (node) controlsRefs.current[val] = node;
    };

    const handleFormClose = async () => {
        setIsFormOpenReview(false);
        setEditingReview(null);

        const updated = await getReviewByRoute(routeId);
        setReviewList(updated.data);

        setSuccessMessage("Review updated successfully!");
        setTimeout(() => setSuccessMessage(""), 3000);
    };


    const handleFormCloseRating = async () => {
        setIsFormOpenRating(false);
        setEditingRating(null);

        const updated = await getRatingsByRoute(routeId);
        setRatingList(updated.data);

        setSuccessMessage("Rating updated successfully!");
        setTimeout(() => setSuccessMessage(""), 3000);
    };

    const handleEdit = (review) => {
        setEditingReview(review);
        setIsFormOpenReview(true);

    };

    const handleEditRating = (rating) => {
        setEditingRating(rating);
        setIsFormOpenRating(true);
    };


    const onClickDeleteReview = async (review) => {
        try {
            await handleDeleteReview(review.id);

            const updated = await getReviewByRoute(routeId);
            setReviewList(updated.data);

            setSuccessMessage("Review deleted successfully!");
            setTimeout(() => setSuccessMessage(""), 3000);

        } catch (err) {
            setErrorMessage("Failed to delete review.");
            setTimeout(() => setErrorMessage(""), 3000);
        }
    };


    useEffect(() => {
        setReviewList(Array.isArray(reviews) ? reviews : []);
    }, [reviews]);

    useEffect(() => {
        setRatingList(Array.isArray(ratings) ? ratings : []);
    }, [ratings]);


    const deleteRating = async (rating) => {
        try {
            console.log("Deleting rating with id:", rating.id, rating);

            await handleDeleteRating(rating.id);

            const updated = await getRatingsByRoute(routeId);
            setRatingList(updated.data);

            setSuccessMessage("Rating deleted successfully!");
            setTimeout(() => setSuccessMessage(""), 3000);

        } catch (err) {
            setErrorMessage("Failed to delete rating.");
            setTimeout(() => setErrorMessage(""), 3000);
        }
    };


    return (
        <>
            {successMessage && (
                <div className="bg-green-100 text-green-800 border border-green-300 px-4 py-2 rounded">
                    {successMessage}
                </div>
            )}

            {errorMessage && (
                <div className="bg-red-100 text-red-800 border border-red-300 px-4 py-2 rounded">
                    {errorMessage}
                </div>
            )}

            <div style={{ display: 'flex', justifyContent: 'center', width: '100%' }}>
                <Tabs variant="none" value={value} onChange={setValue} style={{ width: '100%' }}>
                    <Tabs.List ref={setRootRef} className={classes.list} style={{ justifyContent: 'center', width: '100%' }}>
                        <Flex gap="sm" justify="center" align="center" wrap="nowrap" style={{ width: '100%', justifyContent: 'center' }}>
                            <Tabs.Tab 
                                value="1" 
                                ref={setControlRef("1")} 
                                className={classes.tab}
                            >
                                <span>
                                    Reviews
                                </span>
                            </Tabs.Tab>
                            <Tabs.Tab 
                                value="2" 
                                ref={setControlRef("2")} 
                                className={classes.tab}
                            >
                                <span>
                                    Ratings
                                </span>
                            </Tabs.Tab>
                        </Flex>
                        <FloatingIndicator
                            target={value ? controlsRefs.current[value] : null}
                            parent={rootRef}
                            className={classes.indicator}
                        />
                    </Tabs.List>

                <Tabs.Panel value="1">
                    <div className="space-y-4 mt-4 max-h-[400px] md:max-h-[500px] overflow-y-auto pr-2 reviews-ratings-scroll">
                        {reviewList.length > 0 ? (
                            reviewList.map((review, i) => (
                                <div
                                    key={i}
                                    className="border border-gray-200 rounded-xl p-4 shadow-sm hover:shadow-md transition-shadow"
                                >
                                    <div className="flex items-center space-x-3">
                                        <div className="relative w-10 h-10 rounded-full overflow-hidden bg-gradient-to-tr from-blue-500 to-cyan-500 flex items-center justify-center text-white font-medium">
                                            {
                                                review.userId?.profileImage ? (
                                                    <img
                                                        src={review.userId.profileImage}
                                                        alt={review.userId?.username || "User"}
                                                        className="w-full h-full object-cover"
                                                        onError={(e) => {
                                                            e.target.style.display = "none";
                                                            e.target.parentNode.textContent =
                                                                review.userId?.username?.[0]?.toUpperCase() || "U";
                                                        }}
                                                    />
                                                ) : (
                                                    review.userId?.username?.[0]?.toUpperCase() || "U"
                                                )}
                                        </div>

                                        <div>
                                            <button
                                                onClick={() => review.userId?.id && navigate(`/profile/${review.userId.id}`)}
                                                className="font-semibold hover:text-blue-600 hover:underline transition-colors text-left"
                                            >
                                                {review.userId?.username || "Unknown"}
                                            </button>
                                            <p className="text-xs text-gray-500">
                                                {new Date(review.createdAt).toLocaleDateString()}
                                            </p>
                                        </div>

                                        {user && user.id === review.userId?.id && (
                                            <div className="flex gap-2 mt-2">
                                                <button
                                                    onClick={() => handleEdit(review)}
                                                    className="text-sm text-black hover:underline"
                                                >
                                                    <Pencil />
                                                </button>
                                                <button
                                                    onClick={() => onClickDeleteReview(review)}
                                                    className="text-xs text-black hover:underline"
                                                >
                                                    <Trash />
                                                </button>
                                            </div>
                                        )}

                                    </div>

                                    <p className="mt-2 text-sm text-gray-700">{review.review}</p>

                                    {review.images?.length > 0 && (
                                        <div className="mt-3 grid grid-cols-2 sm:grid-cols-3 gap-2">
                                            {review.images.map((img, j) => (
                                                <img
                                                    key={j}
                                                    src={img}
                                                    alt={`Review image ${j + 1}`}
                                                    className="rounded-lg object-cover w-full h-32 cursor-pointer hover:opacity-90 transition"
                                                />
                                            ))}
                                        </div>
                                    )}
                                </div>
                            ))
                        ) : (
                            <p className="text-center text-gray-500">No reviews yet.</p>
                        )}
                    </div>

                    {isFormOpenReview && (
                        <ReviewUpdateForm
                            selectedRoute={editingReview}
                            onOpenChange={handleFormClose}
                        />
                    )}
                </Tabs.Panel>


                <Tabs.Panel value="2">
                    <div className="space-y-4 mt-4 max-h-[400px] md:max-h-[500px] overflow-y-auto pr-2 reviews-ratings-scroll">
                        {ratingList.length > 0 ? ratingList.map((r, i) => (
                            <div key={i} className="border border-gray-200 rounded-xl p-4 shadow-sm">
                                <div className="flex items-center justify-between">
                                    <div className="flex items-center space-x-3">
                                        <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-blue-500 to-cyan-500 flex items-center justify-center text-white font-medium">
                                            {r.userId?.username?.[0]?.toUpperCase() || "U"}
                                        </div>
                                        <div>
                                            <button
                                                onClick={() => r.userId?.id && navigate(`/profile/${r.userId.id}`)}
                                                className="font-semibold hover:text-blue-600 hover:underline transition-colors text-left"
                                            >
                                                {r.userId?.username || "Unknown"}
                                            </button>
                                            <p className="text-xs text-gray-500">{new Date(r.createdAt).toLocaleDateString()}</p>
                                        </div>
                                        {user && user.id === r.userId?.id && (
                                            <div className="flex gap-2 mt-2">
                                                <button  onClick={() => handleEditRating(r)}
                                                        className="text-sm text-black hover:underline"
                                                >
                                                    <Pencil />
                                                </button>

                                                <button
                                                    onClick={() => deleteRating(r)}
                                                    className="text-sm text-black hover:underline"
                                                >
                                                    <Trash />
                                                </button>
                                            </div>
                                        )}

                                    </div>
                                    <Rating value={r.rating} readOnly fractions={2} size="sm"/>
                                </div>
                            </div>
                        )) : <p className="text-center text-gray-500">No ratings yet.</p>}
                        {isFormOpenRating && (
                            <RatingUpdateForm
                                selectedRoute={editingRating}
                                onOpenChange={handleFormCloseRating}
                            />
                        )}
                    </div>


                </Tabs.Panel>
                </Tabs>
            </div>

        </>

    );
}
