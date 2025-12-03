import axiosClient from "./axiosClient";

export const getUserProfile = async (userId) => axiosClient.get(`/users/${userId}`);


export const updateProfilePicture = (id, formData) =>
    axiosClient.post(`/users/profilePictureUpdate/${id}`, formData, {
    headers: {
        "Content-Type": "multipart/form-data"
    },
});

export const getUsersByUsername = async (username) =>
    axiosClient.get(`/users/search`, {
        params: { username }
    });

export const updateBio = (id, bio) =>
    axiosClient.post(`/users/${id}/bio`, { bio });

export const updateProfileVisibility = (id, isPublicProfile) =>
    axiosClient.post(`/users/${id}/visibility`, { isPublicProfile });


export const routesCreatedByUserId = (id) =>
    axiosClient.get(`/users/${id}/routes`);

// const res = await axios.get(`${API_URL}/route`);

export const favouriteRoute = (ids) => axiosClient.post(`/action/favorite`,ids);
export const unfavoriteRoute = (ids) => axiosClient.post(`/action/unfavorite`,ids);
