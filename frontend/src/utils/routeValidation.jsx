export const validateRouteForm = (formData, points) => {
    const errors = {};

    if (!formData.name.trim()) {
        errors.name = 'Route name is required';
    } else if (formData.name.trim().length < 3) {
        errors.name = 'Route name must be at least 3 characters';
    }

    if (!formData.description.trim()) {
        errors.description = 'Description is required';
    } else if (formData.description.trim().length < 10) {
        errors.description = 'Description must be at least 10 characters';
    }

    if (!formData.difficulty) {
        errors.difficulty = 'Difficulty level is required';
    }

    if (!formData.type) {
        errors.type = 'Route type is required';
    }

    if (points.length < 2) {
        errors.points = 'At least 2 points are required to create a route';
    }

    return errors;
};