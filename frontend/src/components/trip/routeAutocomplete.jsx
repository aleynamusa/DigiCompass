import { Autocomplete } from "@mantine/core";
import { MapPin } from "lucide-react";
import { useEffect, useState } from "react";
import { searchRoutesByKeyword } from "@/api/routeApi.jsx";

export default function RouteAutocomplete({
                                              value,
                                              disabled = false,
                                              onSelect,
                                          }) {
    const [routes, setRoutes] = useState([]);
    const [keyword, setKeyword] = useState("");

    useEffect(() => {
        if (value !== undefined) {
            setKeyword(value || "");
        }
    }, [value]);

    useEffect(() => {
        if (disabled || !keyword || keyword.length < 2) {
            setRoutes([]);
            return;
        }

        const fetchRoutes = async () => {
            try {
                const res = await searchRoutesByKeyword(keyword);
                setRoutes(Array.isArray(res?.data) ? res.data : []);
            } catch {
                setRoutes([]);
            }
        };

        const timeoutId = setTimeout(fetchRoutes, 300);
        return () => clearTimeout(timeoutId);
    }, [keyword, disabled]);

    const data = routes.map(route => route.name).filter(Boolean);

    return (
        <Autocomplete
            label="Search Routes"
            placeholder="Type at least 2 characters..."
            value={keyword}
            onChange={setKeyword}
            data={data}
            disabled={disabled}
            leftSection={<MapPin size={18} />}
            comboboxProps={{ withinPortal: true, zIndex: 2000 }}
            onOptionSubmit={(value) => {
                const selected = routes.find(r => r.name === value);
                if (selected && onSelect) onSelect(selected);
            }}
        />
    );
}
