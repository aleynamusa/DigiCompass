import React from 'react';
import { Input } from "@/components/ui/input.jsx";
import { Textarea, Select } from "@mantine/core";

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
                        data={['Easy', 'Medium', 'Hard']}
                        placeholder="Select difficulty"
                        value={formData.difficulty}
                        onChange={(value) => handleChange('difficulty', value)}
                        disabled={disabled}
                        error={errors.difficulty}
                        withinPortal
                        portalTarget={document.body}
                        styles={{
                            dropdown: {
                                zIndex: 9999,
                            },
                        }}
                    />
                </div>

                <div className="space-y-2">
                    <label htmlFor="routeType" className="text-sm font-medium">
                        Route Type <span className="text-red-500">*</span>
                    </label>
                    <Select
                        id="routeType"
                        data={['Hiking', 'Cycling', 'Running', 'Walking']}
                        placeholder="Select route type"
                        value={formData.routeType}
                        onChange={(value) => handleChange('routeType', value)}
                        disabled={disabled}
                        error={errors.routeType}
                        withinPortal
                        portalTarget={document.body}
                        styles={{
                            dropdown: {
                                zIndex: 9999,
                            },
                        }}
                    />
                </div>
            </div>
        </div>
    );
};

export default RouteForm;
