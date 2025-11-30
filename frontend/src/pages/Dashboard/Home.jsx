import React, { useEffect, useState } from "react";
import { Autocomplete, Avatar, Group, Text } from "@mantine/core";
import { getUsersByUsername } from "@/api/userApi.jsx";
import { Search } from "lucide-react";
import { useNavigate } from "react-router-dom";

const Home = () => {
    const [userData, setUserData] = useState([]);
    const [keyword, setKeyword] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (!keyword) {
            setUserData([]);
            setError(null);
            return;
        }

        const fetchUsers = async () => {
            const res = await getUsersByUsername(keyword);

            setUserData(res.data ?? []);

            if (res.data.length === 0) {
                setError("No users match your search.");
            } else {
                setError(null);
            }
        };

        fetchUsers();
    }, [keyword]);

    const renderAutocompleteOption = ({ option }) => {
        const user = option.user;

        return (
            <Group gap="sm">
                <Avatar
                    src={`http://localhost:8080/${user.imageUrl}`}
                    size={36}
                    radius="xl"
                />
                <Text size="sm">{user.username}</Text>
            </Group>
        );
    };

    const autocompleteData = userData.map((user) => ({
        value: user.username,
        label: user.username,
        user,
    }));

    const handleSelect = (value) => {
        const selected = autocompleteData.find((u) => u.value === value);
        if (selected) {
            navigate(`/profile/${selected.user.id}`);
        }
    };

    return (
        <div>
            <Autocomplete
                data={autocompleteData}
                value={keyword}
                onChange={setKeyword}
                onOptionSubmit={handleSelect}
                renderOption={renderAutocompleteOption}
                leftSection={<Search size={18} strokeWidth={1.5} />}
                maxDropdownHeight={300}
                label="Search Users"
                placeholder="Search..."
            />

            {error && (
                <Text size="sm" c="red" mt="xs">
                    {error}
                </Text>
            )}
        </div>
    );
};

export default Home;
