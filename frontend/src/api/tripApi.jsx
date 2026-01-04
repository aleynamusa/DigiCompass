import axiosClient from "@/api/axiosClient.jsx";

// Modify createTrip to accept a data object
export const createTrip = (tripData) => {
    return axiosClient.post("/trip", tripData);
};
