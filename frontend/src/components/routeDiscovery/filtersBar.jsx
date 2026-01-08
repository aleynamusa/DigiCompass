import { Input } from "@/components/ui/input.jsx";
import { Button } from "@/components/ui/button.jsx";
import { SearchIcon } from "lucide-react";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select.jsx";

export default function FiltersBar({
                                       searchTerm,
                                       setSearchTerm,
                                       selectedType,
                                       setSelectedType,
                                       selectedDifficulty,
                                       setSelectedDifficulty,
                                       selectedDistanceRange,
                                       setSelectedDistanceRange,
                                       onReset
                                   }) {
    return (
        <div className="flex flex-col gap-4">
            <div className="pt-5">
            <div className="relative w-full">
                <SearchIcon className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                    placeholder="Search routes..."
                    className="pl-10 w-full"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>
        </div>


            <div className="flex flex-col sm:flex-row gap-3 w-full">
                <div className="flex flex-wrap sm:flex-nowrap gap-3 flex-1">

                    <Select value={selectedType} onValueChange={setSelectedType}>
                        <SelectTrigger>
                            <SelectValue placeholder="Type" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">All Types</SelectItem>
                            <SelectItem value="hiking">Hiking</SelectItem>
                            <SelectItem value="cycling">Cycling</SelectItem>
                            <SelectItem value="walking">Walking</SelectItem>
                            <SelectItem value="running">Running</SelectItem>
                        </SelectContent>
                    </Select>

                    <Select value={selectedDifficulty} onValueChange={setSelectedDifficulty}>
                        <SelectTrigger>
                            <SelectValue placeholder="Difficulty" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">All Levels</SelectItem>
                            <SelectItem value="EASY">Easy</SelectItem>
                            <SelectItem value="MEDIUM">Medium</SelectItem>
                            <SelectItem value="HARD">Hard</SelectItem>
                        </SelectContent>
                    </Select>

                    <Select value={selectedDistanceRange} onValueChange={setSelectedDistanceRange}>
                        <SelectTrigger>
                            <SelectValue placeholder="Distance" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">All Distances</SelectItem>
                            <SelectItem value="short">0–5 km</SelectItem>
                            <SelectItem value="medium">5–15 km</SelectItem>
                            <SelectItem value="long">15+ km</SelectItem>
                        </SelectContent>
                    </Select>
                </div>

                <Button variant="outline" onClick={onReset}>
                    Reset
                </Button>
            </div>
        </div>
    );
}
