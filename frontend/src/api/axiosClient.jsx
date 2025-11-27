import axios from "axios";

const axiosClient = axios.create({
    baseURL: import.meta.env.VITE_BACKEND_URL,
    withCredentials: true,
    headers: { "Content-Type": "application/json" },
});

axiosClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("accessToken") || sessionStorage.getItem("accessToken");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);//placeholder for future data pending, fulfilled, rejected: helps avoid callback
    }
);


export default axiosClient;
