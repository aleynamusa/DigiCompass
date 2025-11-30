import { Button } from "@/components/ui/button"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
    Avatar
} from "@mantine/core";
import {
    MapIcon,
    CloudIcon,
    CalendarIcon,
    UsersIcon,
    LogInIcon,
    UserIcon,
    LogOutIcon,
    HomeIcon,
    CompassIcon,
    X,
} from "lucide-react"
import '/src/index.css';
import '/src/App.css'
import { useNavigate } from "react-router-dom";
import {useAuth} from "@/context/AuthContext.jsx";
import React, {useEffect, useState} from "react";
import {getUserProfile} from "@/api/userApi.jsx";

export function Sidebar({ activeTab, setActiveTab, isMobileMenuOpen = false, closeMobileMenu }) {

    const [profilePic, setProfilePic] = useState(null);

    const navItems = [
        { id: "dashboard", label: "Dashboard", icon: HomeIcon, path: "/" },
        { id: "routes", label: "Route Discovery", icon: MapIcon, path: "/routeDiscovery" },
        { id: "weather", label: "Weather", icon: CloudIcon, path: "/weather" },
        { id: "trips", label: "Trip Planning", icon: CalendarIcon, path: "/trips" },
        { id: "community", label: "Community", icon: UsersIcon, path: "/community" },
    ];

    const { user, logout, isAuthenticated } = useAuth();

    useEffect(() => {
        if (!user?.id) return;

        const fetchProfile = async () => {
            try {
                const response = await getUserProfile(user.id);

                if (response?.data?.imageUrl) {
                    setProfilePic(response.data.imageUrl);
                }
            } catch (err) {
                console.error("Failed to load profile picture", err);
            }
        };

        fetchProfile();
    }, [user]);

    const navigate = useNavigate();


    const handleNavClick = (item) => {
        setActiveTab(item.id);
        navigate(item.path);
        if (closeMobileMenu) {
            closeMobileMenu();
        }
    };

    return (
        <div
            className={`fixed left-0 top-0 h-full bg-sidebar border-r border-sidebar-border p-4 flex flex-col transition-transform duration-300 ease-in-out z-[60]
                ${isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
            `}
            style={{
                width: "var(--sidebar-width)",
            }}
        >
            <div className="flex items-center justify-between gap-2 mb-8 relative z-10">
                <div className="flex items-center gap-2">
                    <CompassIcon style={{ width: "60px", height: "60px",strokeWidth:"1px", color:"#3C5862", size:"50px"}} className=" text-primary relative z-10" />
                    <h1
                        style={{ fontFamily: "Urbanist", fontWeight: "lighter", color:"#465E67"}}
                        className="font-bold text-sidebar-foreground drop-shadow-md"
                    >
                        DigiCompass
                    </h1>
                </div>
                {closeMobileMenu && (
                    <Button
                        variant="ghost"
                        size="icon"
                        className="lg:hidden"
                        onClick={closeMobileMenu}
                    >
                        <X size={20} />
                    </Button>
                )}
            </div>


            <nav className=" flex-1">
                {navItems.map((item) => {
                    const Icon = item.icon
                    const isActive = activeTab === item.id

                    return (
                        <Button
                            key={item.id}
                            variant={isActive ? "default" : "ghost"}
                            className={`w-full justify-start gap-3 ${
                                isActive
                                    ? "bg-primary text-primary-foreground hover:bg-primary/90"
                                    : "text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
                            }`}
                            onClick={() => handleNavClick(item)}

                        >
                            <Icon className="h-5 w-5" />
                            {item.label}
                        </Button>
                    )
                })}
            </nav>

            <div className="border-t border-sidebar-border pt-4">
                {isAuthenticated ? (
                    <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                            <Button
                                variant="ghost"
                                className="w-full justify-start gap-3 p-2 hover:bg-sidebar-accent"
                            >
                                {/*<Avatar className="h-8 w-8">*/}
                                {/*    <AvatarFallback className="bg-primary text-primary-foreground">*/}
                                {/*        {profilePic}*/}
                                {/*    </AvatarFallback>*/}
                                {/*</Avatar>*/}

                                <Avatar
                                    src={profilePic}
                                    alt="Profile picture"
                                    size={32}        // equivalent to h-8 w-8
                                    radius="xl"
                                    styles={{
                                        image: { objectFit: "cover" }
                                    }}
                                />

                                <div className="flex-1 text-left">
                                    <p className="text-sm font-medium text-sidebar-foreground">
                                        {user?.username}
                                    </p>
                                </div>
                            </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent
                            align="end"
                            className="w-56 z-[100]"
                            side="right"
                            sideOffset={5}
                        >
                            <DropdownMenuItem onClick={() => {
                                navigate("/profile");
                                if (closeMobileMenu) closeMobileMenu();
                            }}>
                                <UserIcon className="h-4 w-4 mr-2" />
                                Profile Settings
                            </DropdownMenuItem>
                            <DropdownMenuSeparator />
                            <DropdownMenuItem onClick={() => {
                                logout();
                                if (closeMobileMenu) closeMobileMenu();
                            }}>
                                <LogOutIcon className="h-4 w-4 mr-2" />
                                Sign Out
                            </DropdownMenuItem>
                        </DropdownMenuContent>
                    </DropdownMenu>
                ) : (
                    <Button
                        variant="ghost"
                        size="sm"
                        className="w-full justify-start gap-3 text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
                        onClick={() => {
                            navigate("/login");
                            if (closeMobileMenu) closeMobileMenu();
                        }}
                    >
                        <LogInIcon className="h-4 w-4" />
                        Sign In
                    </Button>
                )}
            </div>

        </div>
    )
}
