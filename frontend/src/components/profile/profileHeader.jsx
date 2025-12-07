import { Avatar, Group, Stack, Title, Badge, Text, Button } from "@mantine/core";
import { Lock, LockOpen, Settings } from "lucide-react";
import dayjs from "dayjs";

export default function ProfileHeader({ profileData, isOwnProfile, isPrivate, navigate }) {
    const calculateAge = (birthDate) =>
        birthDate ? dayjs().diff(dayjs(birthDate), "year") : null;

    return (
        <div className="flex items-start justify-between">
            <Group>
                <Avatar size={120} radius="xl" src={profileData.imageUrl} />

                <Stack gap="xs">
                    <Group gap="md">
                        <Title order={1} style={{ color: "#3C5862" }}>
                            {profileData.username}
                        </Title>

                        {isOwnProfile && (
                            <Badge color="blue" variant="light">
                                {isPrivate ? <Lock size={16} /> : <LockOpen size={16} />}
                            </Badge>
                        )}
                    </Group>

                    {profileData.birthDate && (
                        <Badge color="blue" variant="light" size="lg">
                            {calculateAge(profileData.birthDate)} years old
                        </Badge>
                    )}

                    <Title order={4}>Bio</Title>
                    <Text>{profileData.bio}</Text>
                </Stack>
            </Group>

            {isOwnProfile && (
                <Button
                    variant="light"
                    leftSection={<Settings size={16} />}
                    onClick={() => navigate("/edit-profile")}
                >
                    Edit Profile
                </Button>
            )}
        </div>
    );
}
