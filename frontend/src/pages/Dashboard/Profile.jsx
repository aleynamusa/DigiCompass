import React, { useEffect, useState } from "react";
import { useAuth } from "@/context/AuthContext";
import { useParams, useNavigate } from "react-router-dom";
import { getUserProfile, routesCreatedByUserId } from "@/api/userApi";
import { getLikedRoutesByUser } from "@/api/routeApi";
import { Alert, Divider, Stack } from "@mantine/core";
import { AlertCircle } from "lucide-react";
import ProfileHeader from "@/components/profile/ProfileHeader";
import ProfileInfo from "@/components/profile/ProfileInfo";
import ProfileTabs from "@/components/profile/ProfileTabs";
import { RouteDetails } from "@/components/routeDetails/route_details";

export default function Profile() {
    const { user } = useAuth();
    const { userId } = useParams();
    const navigate = useNavigate();

    const targetUserId = userId ? parseInt(userId) : user?.id;
    const isOwnProfile = user?.id === targetUserId;

    const [profileData, setProfileData] = useState(null);
    const [baseRoutes, setBaseRoutes] = useState([]);
    const [likedRoutes, setLikedRoutes] = useState([]);
    const [selectedRoute, setSelectedRoute] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        const loadProfile = async () => {
            try {
                const profile = await getUserProfile(targetUserId);
                setProfileData(profile.data);

                const created = await routesCreatedByUserId(targetUserId);
                setBaseRoutes(created.data);

            } catch (err) {
                setError("Could not load profile");
            }
        };
        loadProfile();
    }, [targetUserId]);

    const fetchLikedRoutes = async () => {
        try {
            const response = await getLikedRoutesByUser(targetUserId);
            setLikedRoutes(response.data);
        } catch {
            setError("Could not load liked routes");
        }
    };

    const handleViewDetails = (route) => {
        setSelectedRoute(route);
    };

    if (!profileData) return <div>Loading...</div>;

    return (
        <div className="min-h-screen p-6">
            {error && (
                <Alert icon={<AlertCircle />} color="yellow" mb="md">
                    {error}
                </Alert>
            )}

            <Stack>
                <ProfileHeader
                    profileData={profileData}
                    isOwnProfile={isOwnProfile}
                    isPrivate={profileData.isPublicProfile}
                    navigate={navigate}
                />

                <Divider />

                <ProfileInfo profileData={profileData} isOwnProfile={isOwnProfile} />

                <Divider />

                <ProfileTabs
                    isOwnProfile={isOwnProfile}
                    baseRoutes={baseRoutes}
                    likedRoutes={likedRoutes}
                    onFetchLiked={fetchLikedRoutes}
                    onViewDetails={handleViewDetails}
                />
            </Stack>

            <RouteDetails
                selectedRoute={selectedRoute}
                onOpenChange={(open) => !open && setSelectedRoute(null)}
            />
        </div>
    );
}
