import { Title, Text } from "@mantine/core";
import dayjs from "dayjs";

export default function ProfileInfo({ profileData, isOwnProfile }) {
    const formatDate = (date) =>
        date ? dayjs(date).format("MMMM DD, YYYY") : "Not available";

    return (
        <div>
            <Title style={{ color: "#3C5862" }} className="text-center" order={2} mb="md">
                Profile Information
            </Title>

            <div className={`grid gap-6 place-items-center ${isOwnProfile ? "grid-cols-1 md:grid-cols-2" : "grid-cols-1"}`}>
                {isOwnProfile && (
                    <div className="text-center">
                        <Text size="sm" c="dimmed" mb={4}>Email</Text>
                        <Text size="md" c="dimmed" fw={500}>{profileData.email}</Text>
                    </div>
                )}

                <div className="text-center">
                    <Text size="sm" c="dimmed" mb={4}>Birth Date</Text>
                    <Text size="md" c="dimmed" fw={500}>{formatDate(profileData.birthDate)}</Text>
                </div>
            </div>
        </div>
    );
}
