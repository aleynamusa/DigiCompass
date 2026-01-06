import { useState} from "react"

import {TripCreation} from "@/components/trip/tripCreation.jsx";
import {Button} from "@/components/ui/button.jsx"

export function TripPlanning() {

    const [openedPop, setOpenedPop] = useState(false);


    function openCreateTrip() {
        setOpenedPop(true)
    }

    return (
        <div className="space-y-6 overflow-y-auto min-h-screen">

            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold text-balance text-cyan-950 text-left">
                        Trip Planning
                    </h1>
                    <p className="text-muted-foreground text-pretty pt-2">
                        Organize your outdoor adventures with integrated weather and route planning
                    </p>
                </div>

                <Button onClick={() => openCreateTrip()} className="bg-cyan-950">Create New Trip</Button>

            </div>

            <TripCreation openedPop={openedPop} setOpenedPop={setOpenedPop} />

        </div>
    )
}
