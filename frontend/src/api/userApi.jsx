import axiosClient from "./axiosClient";

const token = localStorage.getItem("accessToken");

export const getUserProfile = async (userId) => axiosClient.get(`/users/${userId}`,{
    headers: {
        Authorization: `Bearer ${token}`
    },
    withCredentials: true
});


export const updateProfilePicture = (id, formData) =>
    axiosClient.post(`/users/profilePictureUpdate/${id}`, formData);

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


export const favouriteRoute = (routeId) => axiosClient.post(`/action/favorite`, null, { params: { routeId } });
export const unfavoriteRoute = (routeId) => axiosClient.post(`/action/unfavorite`, null, { params: { routeId } });
