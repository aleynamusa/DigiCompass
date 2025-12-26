import axiosClient from "./axiosClient";

export const writeReview = async (data) => {
    const formData = new FormData();

    formData.append("review", data.review || "");
    formData.append("userId.id", data.userId.id);
    formData.append("userId.username", data.userId.username);
    formData.append("routeId", data.routeId);
    formData.append("createdAt", data.createdAt);
    formData.append("updatedAt", data.updatedAt);

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
export const handleEditRating = async (id, data) => {
    return axiosClient.put(`/rating/update/${id}`, data);
};

export const isLiked = (data) =>
    axiosClient.get(`/action/isLiked`, {
        params: {
            userId: data.userId,
            routeId: data.routeId
        }
    });

export const getLikedRoutesByUser = (userId) =>
    axiosClient.get(`/route/liked/${userId}`);

export const getRoutes = () => axiosClient.get("/route");

export const getRouteGeometry = (routeId) => axiosClient.get(`/route/${routeId}/geometry`);

export const searchRoutesByKeyword = (keyword) => axiosClient.get(`$/route/keyword?keyword=${keyword}`);

export const getFilteredRoutes = (type, difficulty, distanceRange) => {
    const params = new URLSearchParams();

    if (type !== "all") params.append("type", type);
    if (difficulty !== "all") params.append("difficulty", difficulty);

    if (distanceRange !== "all") {
        if (distanceRange === "short") params.append("distance", "5");
        if (distanceRange === "medium") params.append("distance", "15");
        if (distanceRange === "long") params.append("distance", "100");
    }

    return axiosClient.get(`/route/filter?${params.toString()}`);
};



export const saveRoute = async (data) => {
    const formData = new FormData();

    formData.append("name", data.name);
    formData.append("description", data.description);
    formData.append("routeType", data.routeType);
    formData.append("difficulty", data.difficulty);
    formData.append("distance", data.distance);
    formData.append("duration", data.estimatedTime);


    formData.append("geometry", JSON.stringify(data.geometry));


    if (data.images?.length) {
        data.images.forEach(file => {
            formData.append("images", file);
        });
    }

    return axiosClient.post("/route/create", formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });
};

// Add delete route API call
export const deleteRoute = (routeId) =>
    axiosClient.delete(`/route/delete/${routeId}`);


export const mapRoute = (data) => {
    return axiosClient.post("/map", data);
};