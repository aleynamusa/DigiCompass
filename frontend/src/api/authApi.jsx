import axiosClient from "./axiosClient";

export const loginApi = (data) => axiosClient.post("/users/logIn", data);
export const signup = (data) => axiosClient.post("/users/signUp", data);
export const signUpCheck = (field, value) => {
    const endpoint = field === "username" ? "usernames" : "emails";
    return axiosClient.get(`/users/${endpoint}`, {
        params: { [field]: value },
    });
};
export const logoutApi = () => axiosClient.post("/users/logout", {}, { withCredentials: true });
export const forgotPassword = (email) =>
    axiosClient.post("/auth/forgot-password", null, { params: { email } });

export const refreshApi = async () => {
    return axiosClient.post("/users/refresh", {}, { withCredentials: true });
};

export const resetPassword = (token, password) =>
    axiosClient.post("/auth/reset-password", null, {params: {token, password}});