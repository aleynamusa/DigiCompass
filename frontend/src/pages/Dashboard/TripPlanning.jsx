import { useState} from "react"

import {TripCreation} from "@/components/trip/tripCreation.jsx";
import {Button} from "@/components/ui/button.jsx"
import {useAuth} from "@/context/AuthContext.jsx";
import LoginPopup from "@/components/routeDiscovery/LoginPopUp.jsx";
import {useLogin} from "@/hooks/useLogin.jsx";

export function TripPlanning() {
    const {
        loginData,
        handleChange,
        handleSubmit,
        isChecked,
        setChecked,
        errors
    } = useLogin(() => {
        setShowLoginPopup(false);
    });
    const [openedPop, setOpenedPop] = useState(false);
    const [showLoginPopup, setShowLoginPopup] = useState(false);
    const { user } = useAuth();

    function openCreateTrip() {
        if (!user || !user.id) {
            setShowLoginPopup(true);
        } else {
            setOpenedPop(true);
        }
    }

    return (
        <div className="space-y-6 overflow-y-auto min-h-screen">

            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold text-cyan-950 text-left">
                        Trip Planning
                    </h1>
                    <p className="text-muted-foreground pt-2">
                        Organize your outdoor adventures with integrated weather and route planning
                    </p>
                </div>

                <Button onClick={openCreateTrip} className="bg-cyan-950">
                    Create New Trip
                </Button>
            </div>

            <TripCreation
                openedPop={openedPop}
                setOpenedPop={setOpenedPop}
            />

            <LoginPopup
                show={showLoginPopup}
                onClose={() => setShowLoginPopup(false)}
                {...{ loginData, handleChange, handleSubmit, isChecked, setChecked, errors }}
            />


        </div>
    );
}
