import React, { useState, useEffect } from "react";
import { useAuth } from "@/context/AuthContext";
import { useParams, useNavigate } from "react-router-dom";
import {getUserProfile, routesCreatedByUserId} from "@/api/userApi";
import {
    Title,
    Text,
    Avatar,
    Group,
    Stack,
    Divider,
    Badge,
    Skeleton,
    Alert,
    Button,
    Tabs,
} from "@mantine/core";
import { AlertCircle, ArrowLeft, Settings, Heart, Share2, MapPin, Lock, LockOpen } from "lucide-react";
import dayjs from "dayjs";
import {RouteCard} from "@/components/routeDetails/route_card.jsx";
import axios from "axios";
import {RouteDetails} from "@/components/routeDetails/route_details.jsx";
import {getLikedRoutesByUser} from "@/api/routeApi.jsx";

const API_URL = import.meta.env.VITE_BACKEND_URL;
const Profile = () => {
    const { user } = useAuth();
    const { userId } = useParams();
    const navigate = useNavigate();
    const [profileData, setProfileData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isPrivate, setIsPrivate] = useState(false);
    const [baseRoutes, setBaseRoutes] = useState(null);
    const [selectedRoute, setSelectedRoute] = useState(null);
    const [likedRoutes, setLikedRoutes] = useState(null);



    const targetUserId = userId ? parseInt(userId) : user?.id;
    const isOwnProfile = user?.id === targetUserId;

    useEffect(() => {
        const fetchProfile = async () => {
            if (!targetUserId) {
                setLoading(false);
                return;
            }

            try {
                setLoading(true);
                const response = await getUserProfile(targetUserId);


                const data = response.data;
                setProfileData(data);

                console.log(data);

                setIsPrivate(data.isPublicProfile);

                const routesResponse = await routesCreatedByUserId(targetUserId);
                setBaseRoutes(routesResponse.data || []);

                setError(null);

            }catch (err) {
                console.warn("Could not fetch full profile:", err);
                setError("Could not load profile");
                setProfileData(null);
            } finally {
                setLoading(false);
            }
        };

        fetchProfile();
    }, [targetUserId]);

    const handleViewDetails = async (route) => {
        try {
            const response = await axios.get(`${API_URL}/route/${route.id}/geometry`);
            const geojson = response.data.geojson;

            const fullRoute = {
                ...route,
                routeGeometry: geojson,
            };

            setSelectedRoute(fullRoute);
        } catch (err) {
            console.error(err);
            setError("There has been a problem and the data is unavailable at the moment.");
        }
    };

    const handleLikedRoutes = async () => {
        try {
            const response = await getLikedRoutesByUser(targetUserId);
            setLikedRoutes(response.data || []);
        } catch (err) {
            console.error(err);
            setError("There has been a problem and the data is unavailable at the moment.");
        }
    };


    if (loading) {
        return (
            <div className="w-full p-6">
                <Stack gap="lg">
                    <Group gap="lg">
                        <Skeleton height={120} circle />
                        <Stack gap="xs">
                            <Skeleton height={32} width={200} />
                            <Skeleton height={24} width={150} />
                        </Stack>
                    </Group>
                    <Divider />
                    <Skeleton height={200} />
                </Stack>
            </div>
        );
    }

    if (!user && !targetUserId) {
        return (
            <div className="w-full p-6">
                <Alert icon={<AlertCircle size="1rem" />} title="Not Authenticated" color="red">
                    Please log in to view profiles.
                </Alert>
            </div>
        );
    }

    const displayData = profileData;

    if (!loading && !displayData) {
        return (
            <div className="min-h-screen p-6 w-[calc(95vw-var(--sidebar-width))] text-left"
            >
                <Stack gap="md">
                    <Button
                        variant="subtle"
                        leftSection={<ArrowLeft size={16} />}
                        onClick={() => navigate(-1)}
                        style={{ alignSelf: "flex-start" }}
                    >
                        Go Back
                    </Button>
                    <Alert icon={<AlertCircle size="1rem" />} title="Profile Not Found" color="red">
                        The user profile you're looking for doesn't exist or is unavailable.
                    </Alert>
                </Stack>
            </div>
        );
    }

    const formatDate = (date) => {
        if (!date) return "Not available";
        return dayjs(date).format("MMMM DD, YYYY");
    };

    const calculateAge = (birthDate) => {
        if (!birthDate) return null;
        return dayjs().diff(dayjs(birthDate), "year");
    };

    return (
        <div className="min-h-screen p-6 w-[calc(95vw-var(--sidebar-width))] text-left">

        {error && (
                <Alert
                    icon={<AlertCircle size="1rem" />}
                    title="Limited Profile Data"
                    color="yellow"
                    mb="md"
                >
                    {error}. {isOwnProfile ? "Displaying available information from your session." : "Some information may not be available."}
                </Alert>
            )}

            <Stack>

                <div className="flex items-start justify-between">
                    <Group>
                        <Avatar
                            size={120}
                            radius="xl"
                            src={displayData.imageUrl}
                            alt="Profile picture"
                        />
                        <Stack gap="xs">
                            <Group gap="md" align="center">
                                <Title style={{ color: "#3C5862" }} order={1}>
                                    {displayData.username || "User"}
                                </Title>
                                {isOwnProfile && (
                                    // Your Profile TODO display the if admin or user
                                    <Badge color="blue" variant="light">
                                        {isPrivate ? <Lock size={16} /> : <LockOpen size={16} />}
                                    </Badge>

                                )}
                            </Group>
                            {displayData.birthDate && (
                                <Badge color="blue" variant="light" size="lg">
                                    {calculateAge(displayData.birthDate)} years old
                                </Badge>
                            )}

                            <Title order={4} className="text-gray-800">Bio</Title>
                            <Text c="dark">{displayData.bio}</Text>
                        </Stack>


                    </Group>
                    {isOwnProfile && (
                        <Button
                            variant="light"
                            leftSection={<Settings size={16} />}
                            onClick={() => {
                                navigate("/edit-profile")
                                console.log("Edit profile");
                            }}
                        >
                            Edit Profile
                        </Button>
                    )}
                </div>

                <Divider />

                <div>
                    <Title style={{ color: "#3C5862" }} className="text-center" order={2} mb="md">
                        Profile Information
                    </Title>

                    <div
                        className={`
            grid gap-6 place-items-center
            ${isOwnProfile ? "grid-cols-1 md:grid-cols-2" : "grid-cols-1"}
        `}
                    >
                        {isOwnProfile && (
                            <div className="text-center">
                                <Text size="sm" c="dimmed" mb={4}>
                                    Email
                                </Text>
                                <Text c="dimmed" size="md" fw={500}>
                                    {displayData.email || "Not available"}
                                </Text>
                            </div>
                        )}


                        <div className="text-center">
                            <Text  size="sm" c="dimmed" mb={4}>
                                Birth Date
                            </Text>
                            <Text c="dimmed" size="md" fw={500}>
                                {formatDate(displayData.birthDate)}
                            </Text>
                        </div>
                    </div>
                </div>

                <Divider />

                <div>
                    <Tabs defaultValue="shared" variant="pills">
                        <Tabs.List>
                            <Tabs.Tab value="shared" leftSection={<Share2 size={16} />}>
                                {isOwnProfile ? "My Shared Routes" : "Shared Routes"}
                            </Tabs.Tab>
                            {isOwnProfile && (
                                <Tabs.Tab
                                    value="liked"
                                    leftSection={<Heart size={16} />}
                                    onClick={handleLikedRoutes}
                                >
                                    Liked Routes
                                </Tabs.Tab>

                            )}
                            <Tabs.Tab value="all" leftSection={<MapPin size={16} />}>
                                {isOwnProfile ? "All My Routes" : "All Routes"}
                            </Tabs.Tab>
                        </Tabs.List>

                        <Tabs.Panel value="shared" pt="lg">
                            <div className="text-center py-12">
                                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 md:gap-6">
                                    {(!baseRoutes || baseRoutes.length === 0) ? (
                                        <div>
                                            <div className="flex justify-center mb-4">
                                                <Share2 size={48} style={{ color: "#9ca3af" }} />
                                            </div>
                                            <Text c="dimmed" size="lg" mb="xs">
                                                {isOwnProfile ? "You haven't shared any routes yet" : "No shared routes yet"}
                                            </Text>
                                            <Text c="dimmed" size="sm">
                                                {isOwnProfile
                                                    ? "Start sharing your favorite routes with the community!"
                                                    : "This user hasn't shared any routes yet."}
                                            </Text>
                                        </div>
                                    ) : (
                                        baseRoutes.map((route) => (
                                            <RouteCard key={route.id} route={route} onViewDetails={handleViewDetails} />
                                        ))
                                    )}
                                </div>
                            </div>
                        </Tabs.Panel>

                        {isOwnProfile && (
                            <Tabs.Panel value="liked" pt="lg">
                                <div className="text-center py-12">
                                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 md:gap-6">
                                        {(!likedRoutes || likedRoutes.length === 0) ? (
                                            <div>
                                                <div className="flex justify-center mb-4">
                                                    <Heart size={48} style={{ color: "#9ca3af" }} />
                                                </div>
                                                <Text c="dimmed" size="lg" mb="xs">
                                                    No liked routes yet
                                                </Text>
                                                <Text c="dimmed" size="sm">
                                                    Routes you like will appear here
                                                </Text>
                                            </div>
                                        ) : (
                                            likedRoutes.map((route) => (
                                                <RouteCard
                                                    key={route.id}
                                                    route={route}
                                                    onViewDetails={() => handleViewDetails(route)}
                                                />
                                            ))
                                        )}
                                    </div>
                                </div>
                            </Tabs.Panel>
                        )}

                        <Tabs.Panel value="all" pt="lg">
                            <div className="text-center py-12">
                                <div className="flex justify-center mb-4">
                                    <MapPin size={48} style={{ color: "#9ca3af" }} />
                                </div>
                                <Text c="dimmed" size="lg" mb="xs">
                                    {isOwnProfile ? "You haven't created any routes yet" : "No routes yet"}
                                </Text>
                                <Text c="dimmed" size="sm">
                                    {isOwnProfile 
                                        ? "Create your first route to get started!" 
                                        : "This user hasn't created any routes yet."}
                                </Text>
                            </div>
                        </Tabs.Panel>
                    </Tabs>
                </div>
            </Stack>
            <RouteDetails
                selectedRoute={selectedRoute}
                onOpenChange={(open) => {
                    if (!open) setSelectedRoute(null);
                }}
            />

        </div>

    );
};

export default Profile;

