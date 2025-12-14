// components/createRoutePopup/routeForm.jsx
import React from 'react';
import { Input } from "@/components/ui/input.jsx";
import { Textarea } from "@mantine/core";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select.jsx";

const RouteForm = ({ formData, onChange, errors, disabled }) => {
    const handleChange = (field, value) => {
        onChange({ ...formData, [field]: value });
    };

    return (
        <div className="space-y-4">
            <div className="space-y-2">
                <label htmlFor="name" className="text-sm font-medium">
                    Route Name <span className="text-red-500">*</span>
                </label>
                <Input
                    id="name"
                    placeholder="Enter route name"
                    value={formData.name}
                    onChange={(e) => handleChange('name', e.target.value)}
                    className={errors.name ? "border-red-500" : ""}
                    disabled={disabled}
                />
                {errors.name && (
                    <p className="text-sm text-red-500">{errors.name}</p>
                )}
            </div>

            <div className="space-y-2">
                <label htmlFor="description" className="text-sm font-medium">
                    Description <span className="text-red-500">*</span>
                </label>
                <Textarea
                    id="description"
                    placeholder="Describe your route..."
                    value={formData.description}
                    onChange={(e) => handleChange('description', e.target.value)}
                    className={`min-h-[100px] ${errors.description ? "border-red-500" : ""}`}
                    disabled={disabled}
                />
                {errors.description && (
                    <p className="text-sm text-red-500">{errors.description}</p>
                )}
            </div>

            <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                    <label htmlFor="difficulty" className="text-sm font-medium">
                        Difficulty <span className="text-red-500">*</span>
                    </label>
                    <Select
                        value={formData.difficulty}
                        onValueChange={(value) => handleChange('difficulty', value)}
                        disabled={disabled}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Select difficulty" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="Easy">Easy</SelectItem>
                            <SelectItem value="Medium">Medium</SelectItem>
                            <SelectItem value="Hard">Hard</SelectItem>
                        </SelectContent>
                    </Select>
                    {errors.difficulty && (
                        <p className="text-sm text-red-500">{errors.difficulty}</p>
                    )}
                </div>

                <div className="space-y-2">
                    <label htmlFor="type" className="text-sm font-medium">
                        Route Type <span className="text-red-500">*</span>
                    </label>
                    <Select
                        value={formData.routeType}
                        onValueChange={(value) => handleChange('routeType', value)}
                        disabled={disabled}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Select route type" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="Hiking">Hiking</SelectItem>
                            <SelectItem value="Cycling">Cycling</SelectItem>
                            <SelectItem value="Running">Running</SelectItem>
                            <SelectItem value="Walking">Walking</SelectItem>
                        </SelectContent>
                    </Select>
                    {errors.routeType && (
                        <p className="text-sm text-red-500">{errors.routeType}</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default RouteForm;