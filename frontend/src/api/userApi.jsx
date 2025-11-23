import axiosClient from "./axiosClient";

export const getUserProfile = (userId) => axiosClient.get(`/users/${userId}`);

