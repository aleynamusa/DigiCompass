import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
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
} from "lucide-react"
import '/src/index.css';
import '/src/App.css'
import { useNavigate } from "react-router-dom";


export function Sidebar({ activeTab, setActiveTab, user, onShowAuth, onLogout }) {
    const navItems = [
        { id: "dashboard", label: "Dashboard", icon: HomeIcon, path: "/" },
        { id: "routes", label: "Route Discovery", icon: MapIcon, path: "/routeDiscovery" },
        { id: "weather", label: "Weather", icon: CloudIcon, path: "/weather" },
        { id: "trips", label: "Trip Planning", icon: CalendarIcon, path: "/trips" },
        { id: "community", label: "Community", icon: UsersIcon, path: "/community" },
    ];

    const navigate = useNavigate();

    return (
        <div
            className="fixed left-0 top-0 h-full bg-sidebar border-r border-sidebar-border p-4 flex flex-col"
            style={{
                width: "var(--sidebar-width)",
                zIndex: 50,
            }}
        >
            <div className="flex items-center gap-2 mb-8 relative z-10">
                <CompassIcon style={{ width: "60px", height: "60px",strokeWidth:"1px", color:"#3C5862", size:"50px"}} className=" text-primary relative z-10" />
                <h1
                    style={{ fontFamily: "Urbanist", fontWeight: "lighter", color:"#465E67"}}
                    className="font-bold text-sidebar-foreground drop-shadow-md"
                >
                    DigiCompass
                </h1>
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
                            onClick={() => {
                                setActiveTab(item.id);
                                navigate(item.path);
                            }}

                        >
                            <Icon className="h-5 w-5" />
                            {item.label}
                        </Button>
                    )
                })}
            </nav>

            <div className="border-t border-sidebar-border pt-4">
                {user ? (
                    <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                            <Button variant="ghost" className="w-full justify-start gap-3 p-2">
                                <Avatar className="h-8 w-8">
                                    <AvatarFallback className="bg-primary text-primary-foreground">
                                        {user.name.charAt(0).toUpperCase()}
                                    </AvatarFallback>
                                </Avatar>
                                <div className="flex-1 text-left">
                                    <p className="text-sm font-medium text-sidebar-foreground">{user.name}</p>
                                    <p className="text-xs text-muted-foreground truncate">{user.email}</p>
                                </div>
                            </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" className="w-56">
                            <DropdownMenuItem>
                                <UserIcon className="h-4 w-4 mr-2" />
                                Profile Settings
                            </DropdownMenuItem>
                            <DropdownMenuSeparator />
                            <DropdownMenuItem onClick={onLogout}>
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
                        onClick={onShowAuth}
                    >
                        <LogInIcon className="h-4 w-4" />
                        Sign In
                    </Button>
                )}
            </div>
        </div>
    )
}
