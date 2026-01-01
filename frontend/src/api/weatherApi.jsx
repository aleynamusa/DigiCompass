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

export const getHourlyWeather = async (latitude, longitude) => {
    return axiosClient.get('/weather/hourly', {
        params: { latitude, longitude }
    });
}

export const getDailyWeather = async (latitude, longitude) => {
    return axiosClient.get('/weather/daily', {
        params: { latitude, longitude }
    });
}

export const getRecommendedDays = async (lat, lon) => {
    if (!lat || !lon) return []

    const res = await axiosClient.get(
        "/weather/recommendations",
        { params: { lat, lon } }
    )

    return res.data
}
