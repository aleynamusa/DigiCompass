// import {useEffect, useState} from "react";
// import {handleEditRating} from "@/api/routeApi.jsx";
//
// export function useRatingReview() {
//     const [value, setValue] = useState(0);
//     const [selectedRoute, setSelectedRoute] = useState(null);
//
//     useEffect(() => {
//         if (selectedRoute) {
//             setValue(selectedRoute.rating || 0);
//         }
//     }, [selectedRoute]);
//
//     const handleSubmit = async () => {
//         const updatedRating = {
//             id: selectedRoute.id,
//             rating: value,
//             userId: {
//                 id: selectedRoute.userId.id,
//                 username: selectedRoute.userId.username,
//             },
//             routeId: selectedRoute.routeId,
//             createdAt: selectedRoute.createdAt,
//             updatedAt: new Date().toISOString().slice(0, 19),
//         };
//
//         try {
//             await handleEditRating(selectedRoute.id, updatedRating);
//             onOpenChange();
//         } catch (err) {
//             console.error("Rating update failed:", err.response?.data || err);
//         }
//     };
// }