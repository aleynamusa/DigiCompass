import React, { useState, useEffect } from "react";
import { useAuth } from "@/context/AuthContext";
import { useParams, useNavigate } from "react-router-dom";
import { getUserProfile } from "@/api/userApi";
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
import { User, Mail, Calendar, AlertCircle, ArrowLeft, Settings, Heart, Share2, MapPin } from "lucide-react";
import dayjs from "dayjs";

const Profile = () => {
    const { user } = useAuth();
    const { userId } = useParams();
    const navigate = useNavigate();
    const [profileData, setProfileData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Determine which user's profile to show
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
                setProfileData(response.data);
                setError(null);
            } catch (err) {
                // If endpoint doesn't exist or fails, use available data
                console.warn("Could not fetch full profile:", err);
                setError("Full profile data unavailable");
                
                // If viewing own profile, use JWT data
                if (isOwnProfile && user) {
                    setProfileData({
                        id: user.id,
                        username: user.username,
                        email: null,
                        birthDate: null,
                    });
                } else {
                    // For other users, we can't get data from JWT
                    setProfileData(null);
                }
            } finally {
                setLoading(false);
            }
        };

        fetchProfile();
    }, [targetUserId, isOwnProfile, user]);

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

    const displayData = profileData || (isOwnProfile && user ? {
        id: user.id,
        username: user.username,
        email: null,
        birthDate: null,
    } : null);

    if (!loading && !displayData) {
        return (
            <div className="w-full p-6">
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
        <div className="w-full min-h-screen p-6">
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

            <Stack gap="xl">
                {/* Header Section */}
                <div className="flex items-start justify-between">
                    <Group gap="lg">
                        {!isOwnProfile && (
                            <Button
                                variant="subtle"
                                leftSection={<ArrowLeft size={16} />}
                                onClick={() => navigate(-1)}
                            >
                                Back
                            </Button>
                        )}
                        <Avatar
                            size={120}
                            radius="xl"
                            color="blue"
                            variant="light"
                        >
                            {displayData.username?.charAt(0).toUpperCase() || "U"}
                        </Avatar>
                        <Stack gap="xs">
                            <Group gap="md" align="center">
                                <Title style={{ color: "#3C5862" }} order={1}>
                                    {displayData.username || "User"}
                                </Title>
                                {isOwnProfile && (
                                    <Badge color="blue" variant="light">
                                        Your Profile
                                    </Badge>
                                )}
                            </Group>
                            {displayData.birthDate && (
                                <Badge color="blue" variant="light" size="lg">
                                    {calculateAge(displayData.birthDate)} years old
                                </Badge>
                            )}
                        </Stack>
                    </Group>
                    {isOwnProfile && (
                        <Button
                            variant="light"
                            leftSection={<Settings size={16} />}
                            onClick={() => {
                                // TODO: Navigate to profile settings/edit page
                                console.log("Edit profile");
                            }}
                        >
                            Edit Profile
                        </Button>
                    )}
                </div>

                <Divider />

                {/* Profile Information Section */}
                <div>
                    <Title style={{ color: "#3C5862" }} order={2} mb="md">
                        Profile Information
                    </Title>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <div>
                            <Text size="sm" c="dimmed" mb={4}>
                                Username
                            </Text>
                            <Text c="dimmed" size="md" fw={500}>
                                {displayData.username || "Not available"}
                            </Text>
                        </div>

                        {/* Only show email for own profile */}
                        {isOwnProfile && (
                            <div>
                                <Text size="sm" c="dimmed" mb={4}>
                                    Email
                                </Text>
                                <Text c="dimmed" size="md" fw={500}>
                                    {displayData.email || "Not available"}
                                </Text>
                            </div>
                        )}

                        <div>
                            <Text c="dimmed" size="sm" c="dimmed" mb={4}>
                                Birth Date
                            </Text>
                            <Text c="dimmed" size="md" fw={500}>
                                {formatDate(displayData.birthDate)}
                            </Text>
                        </div>
                    </div>
                </div>

                <Divider />

                {/* Routes Sections */}
                <div>
                    <Tabs defaultValue="shared" variant="pills">
                        <Tabs.List>
                            <Tabs.Tab value="shared" leftSection={<Share2 size={16} />}>
                                {isOwnProfile ? "My Shared Routes" : "Shared Routes"}
                            </Tabs.Tab>
                            {isOwnProfile && (
                                <Tabs.Tab value="liked" leftSection={<Heart size={16} />}>
                                    Liked Routes
                                </Tabs.Tab>
                            )}
                            <Tabs.Tab value="all" leftSection={<MapPin size={16} />}>
                                {isOwnProfile ? "All My Routes" : "All Routes"}
                            </Tabs.Tab>
                        </Tabs.List>

                        <Tabs.Panel value="shared" pt="lg">
                            <div className="text-center py-12">
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
                        </Tabs.Panel>

                        {isOwnProfile && (
                            <Tabs.Panel value="liked" pt="lg">
                                <div className="text-center py-12">
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
        </div>
    );
};

export default Profile;

