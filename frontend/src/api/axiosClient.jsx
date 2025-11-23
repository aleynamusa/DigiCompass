import axios from "axios";

const axiosClient = axios.create({
    baseURL: import.meta.env.VITE_BACKEND_URL,
    withCredentials: true,
    headers: { "Content-Type": "application/json" },
});

// Add request interceptor to include JWT token
axiosClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("accessToken") || sessionStorage.getItem("accessToken");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default axiosClient;
