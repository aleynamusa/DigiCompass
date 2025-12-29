import axiosClient from "@/api/axiosClient.jsx";

export const getCurrentCity = async (latitude, longitude) => {
    return axiosClient.get('/map/coords', {
        params: { latitude, longitude }
    });
};

export const getCurrentWeather = async (latitude, longitude) => {
    return axiosClient.get('/weather/current', {
        params: { latitude, longitude }
    });
}