import axiosClient from "./axiosClient";

export const writeReview = async (data) => {
    const formData = new FormData();

    formData.append("review", data.review || "");
    // Access the nested userId object properties
    formData.append("userId.id", data.userId.id);
    formData.append("userId.username", data.userId.username);
    formData.append("routeId", data.routeId);
    formData.append("createdAt", data.createdAt);
    formData.append("updatedAt", data.updatedAt);

    // Images are optional for reviews
    if (data.images && data.images.length > 0) {
        data.images.forEach((file) => {
            formData.append("images", file);
        });
    }

    console.log("Sending FormData:");
    for (let pair of formData.entries()) {
        console.log(pair[0], pair[1]);
    }

    try {
        const response = await axiosClient.post("/review", formData, {
            headers: {
                "Content-Type": "multipart/form-data"
            },
        });
        return response;
    } catch (error) {
        console.error("Error details:", error.response?.data);
        throw error;
    }
};
export const getReviewByRoute = (routeId) => axiosClient.get(`/review/route/${routeId}`);
export const addRating = (data) => axiosClient.post("/rating", data);
export const getRatingsByRoute = (routeId) => axiosClient.get(`/rating/route/${routeId}`);

export const handleDeleteReview = (reviewId) => axiosClient.delete(`/review/delete/${reviewId}`);
export const handleEditReview = async (id, formData) => {
    return axiosClient.put(`/review/update/${id}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });
};

export const handleDeleteRating = (id) => axiosClient.delete(`/rating/delete/${id}`);