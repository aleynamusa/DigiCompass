import axiosClient from "@/api/axiosClient.jsx";

export const createTrip = (tripData) => {
    return axiosClient.post("/trip", tripData);
};
