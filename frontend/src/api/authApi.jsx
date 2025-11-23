import axiosClient from "./axiosClient";

export const loginApi = (data) => axiosClient.post("/auth/logIn", data);
export const signup = (data) => axiosClient.post("/auth/signUp", data);
export const signUpCheck = (field, value) => {
    const endpoint = field === "username" ? "usernames" : "emails";
    return axiosClient.get(`/users/${endpoint}`, {
        params: { [field]: value },
    });
};
export const logoutApi = () => axiosClient.post("/auth/logout", {}, { withCredentials: true });
export const forgotPassword = (email) =>
    axiosClient.post("/password/forgot", null, { params: { email } });

export const refreshApi = async () => {
    return axiosClient.post("/auth/refresh", {}, { withCredentials: true });
};

export const resetPassword = (token, password) =>
    axiosClient.post("/password/reset", null, {params: {token, password}});