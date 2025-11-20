import {Tabs, Flex, FloatingIndicator, Rating, Modal, Dialog} from "@mantine/core";
import {useEffect, useRef, useState} from "react";
import classes from "@/components/card.module.css";
import {useAuth} from "@/context/AuthContext.jsx";
import {getRatingsByRoute, getReviewByRoute, handleDeleteRating, handleDeleteReview} from "@/api/routeApi.jsx";
import { Trash, Pencil } from 'lucide-react';
import ReviewUpdateForm from "@/components/routeDetails/reviewUpdateForm.jsx";


export default function RouteTabs({reviews, ratings, onEditRating}) {
    const [value, setValue] = useState("1");
    const [rootRef, setRootRef] = useState(null);
    const controlsRefs = useRef({});
    const [editingReview, setEditingReview] = useState(null);
    const [isFormOpen, setIsFormOpen] = useState(false);
    const { user } = useAuth();
    const [reviewList, setReviewList] = useState(reviews);
    const [ratingList, setRatingList] = useState(ratings);


    const setControlRef = (val) => (node) => {
        if (node) controlsRefs.current[val] = node;
    };

    const handleFormClose = () => {
        setIsFormOpen(false);
        setEditingReview(null);
    };

    const handleEdit = (review) => {
        setEditingReview(review);
        setIsFormOpen(true);
    };

    const onClickDeleteReview = (review) => {
        handleDeleteReview(review.id);
        setReviewList(reviews);
    }

    useEffect(() => {
        setReviewList(reviews);
    }, [reviews]);

    useEffect(() => {
        setRatingList(ratings);
    }, [ratings]);

    const deleteRating = (rating) => {
        handleDeleteRating(rating.id);
        setRatingList(ratings);
    }


    return (
        <Tabs variant="none" value={value} onChange={setValue}>
            <Tabs.List ref={setRootRef} className={classes.list}>
                <Flex gap="md" justify="center" align="flex-start" wrap="wrap">
                    <Tabs.Tab value="1" ref={setControlRef("1")} className={classes.tab}>Reviews</Tabs.Tab>
                    <Tabs.Tab value="2" ref={setControlRef("2")} className={classes.tab}>Ratings</Tabs.Tab>
                </Flex>
                <FloatingIndicator
                    target={value ? controlsRefs.current[value] : null}
                    parent={rootRef}
                    className={classes.indicator}
                />
            </Tabs.List>

            <Tabs.Panel value="1">
                <div className="space-y-4 mt-4">
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
                                        <p className="font-semibold">{review.userId?.username || "Unknown"}</p>
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

                {isFormOpen && (
                    <ReviewUpdateForm
                        selectedRoute={editingReview}
                        onOpenChange={handleFormClose}
                    />
                )}
            </Tabs.Panel>


            <Tabs.Panel value="2">
                <div className="space-y-4 mt-4">
                    {ratingList.length > 0 ? ratingList.map((r, i) => (
                        <div key={i} className="border border-gray-200 rounded-xl p-4 shadow-sm">
                            <div className="flex items-center justify-between">
                                <div className="flex items-center space-x-3">
                                    <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-blue-500 to-cyan-500 flex items-center justify-center text-white font-medium">
                                        {r.userId?.username?.[0]?.toUpperCase() || "U"}
                                    </div>
                                    <div>
                                        <p className="font-semibold">{r.userId?.username || "Unknown"}</p>
                                        <p className="text-xs text-gray-500">{new Date(r.createdAt).toLocaleDateString()}</p>
                                    </div>
                                    {user && user.id === r.userId?.id && (
                                        <div className="flex gap-2 mt-2">
                                            <button onClick={() => onEditRating(r)}
                                                    className="text-sm text-black hover:underline">
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
                </div>


            </Tabs.Panel>
        </Tabs>
    );
}
