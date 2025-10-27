import { Sidebar } from "@/components/sidebar.jsx";
import { Outlet } from "react-router-dom";
import { useState } from "react";

export default function Layout({ user, onLogout }) {
    const [activeTab, setActiveTab] = useState("dashboard");

    return (
        <div className="flex">
            <Sidebar
                activeTab={activeTab}
                setActiveTab={setActiveTab}
                user={user}
                onLogout={onLogout}
            />
            <main className="flex-1 p-6 ml-[var(--sidebar-width)]">
                <Outlet />
            </main>
        </div>
    );
}
