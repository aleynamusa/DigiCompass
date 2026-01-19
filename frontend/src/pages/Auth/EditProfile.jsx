import React, { useState, useEffect } from "react";
import {
    Title,
    Button,
    FileButton,
    Group,
    Avatar,
    Stack,
    Alert,
    Divider,
} from "@mantine/core";
import { useAuth } from "@/context/AuthContext";
import { useNavigate } from "react-router-dom";
import { AlertCircle, ArrowLeft } from "lucide-react";
import {getUserProfile, updateProfilePicture} from "@/api/userApi.jsx";
import { Textarea, Switch } from "@mantine/core";
import { updateBio, updateProfileVisibility } from "@/api/userApi.jsx";

const EditProfile = () => {
    const { user } = useAuth();
    const navigate = useNavigate();

    const [avatarPreview, setAvatarPreview] = useState(null);
    const [avatarFile, setAvatarFile] = useState(null);
    const [bio, setBio] = useState("");
    const [isPublic, setIsPublic] = useState(true);


    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);


    useEffect(() => {
        if (!user) {
            setLoading(false);
            return;
        }

        const fetchProfile = async () => {
            try {
                setLoading(true);
                const response = await getUserProfile(user.id);
                const profile = response.data;

                setBio(profile.bio || "");
                setIsPublic(profile.publicProfile ?? true);  // depends on backend field name

                setAvatarPreview(profile.imageUrl);
                setSuccess(null);
            } catch (err) {
                console.warn("Could not fetch full profile:", err);
                setError("Could not load profile");
            } finally {
                setLoading(false);
            }
        };

        fetchProfile();
    }, [user]);



    const handleSubmit = async () => {
        setLoading(true);
        setError(null);
        setSuccess(null);

        try {
            if (avatarFile) {
                const uploadData = new FormData();
                uploadData.append("file", avatarFile);
                await updateProfilePicture(user.id, uploadData);
            }

            await updateBio(user.id, bio);
            await updateProfileVisibility(user.id, isPublic);

            setSuccess("Profile updated successfully!");
        } catch (err) {
            console.error(err);
            setError("Could not update profile.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen p-6 w-[calc(95vw-var(--sidebar-width))] text-left">
            <Stack gap="lg">
                <Button
                    variant="subtle"
                    leftSection={<ArrowLeft size={16} />}
                    onClick={() => navigate(-1)}
                    style={{ alignSelf: "flex-start" }}
                >
                    Back
                </Button>

                <Title order={1} style={{ color: "#3C5862" }}>
                    Edit Profile
                </Title>

                <Divider />

                {error && (
                    <Alert icon={<AlertCircle />} color="red">
                        {error}
                    </Alert>
                )}
                {success && (
                    <Alert icon={<AlertCircle />} color="green">
                        {success}
                    </Alert>
                )}

                <Group>
                    <Avatar
                        data-cy="avatar-preview"
                        size={120}
                        radius="xl"
                        src={avatarPreview}
                        alt="Profile picture"
                    />


                    <Stack>

                        <FileButton
                            accept="image/png,image/jpeg,image/jpg"
                            inputProps={{ 'data-cy': 'avatar-upload' }}
                            onChange={(file) => {
                                setAvatarFile(file);
                                setAvatarPreview(URL.createObjectURL(file));
                            }}
                        >
                            {(props) => <Button {...props}>Upload Photo</Button>}
                        </FileButton>




                        {avatarPreview && (
                            <Button
                                variant="light"
                                color="red"
                                onClick={() => {
                                    setAvatarFile(null);
                                    setAvatarPreview(user.avatarUrl || null);
                                }}
                            >
                                Remove Photo
                            </Button>
                        )}
                    </Stack>
                </Group>

                <Title order={4} className="text-gray-800">Bio</Title>
                <Textarea
                    placeholder="Tell everyone something about yourself..."
                    minRows={3}
                    value={bio}
                    onChange={(e) => setBio(e.target.value)}
                />

                <Title order={4} className="text-gray-800">Make your profile private</Title>
                <Switch
                    checked={isPublic}
                    onChange={(e) => setIsPublic(e.currentTarget.checked)}
                    color="blue"
                    description="If disabled, only you can view your profile."
                />

                <Divider />

                <Group>
                    <Button
                        color="blue"
                        onClick={handleSubmit}
                        loading={loading}
                    >
                        Save Changes
                    </Button>

                    <Button
                        variant="light"
                        color="gray"
                        onClick={() => navigate(-1)}
                    >
                        Cancel
                    </Button>
                </Group>
            </Stack>
        </div>
    );
};

export default EditProfile;
