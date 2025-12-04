import { signup, signUpCheck } from "@/api/authApi.jsx";
import debounce from "lodash.debounce";
import dayjs from "dayjs";
import { useState } from "react";
import { SignUpModel } from "@/models/authModels.jsx";
import { usePasswordValidation } from "@/hooks/usePasswordValidation";

export function useSignUp() {
    const {
        password,
        confirm,
        passwordMatch,
        passwordRules,
        handlePasswordChange,
        handleConfirmChange
    } = usePasswordValidation();

    const [formData, setFormData] = useState(SignUpModel);
    const [availability, setAvailability] = useState({ username: null, email: null });
    const [errors, setErrors] = useState({});
    const [success, setSuccess] = useState("");

    const isEmail = (email) =>
        /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email);

    const isOldEnough = (birthDate) => {
        if (!birthDate) return false;
        return dayjs().diff(dayjs(birthDate), "year") >= 14;
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors({});
        setSuccess("");

        if (!passwordMatch) {
            setErrors({ confirmPassword: "Passwords do not match" });
            return;
        }

        const payload = {
            ...formData,
            password: password,
            confirmPassword: confirm,
            role: 2
        };

        try {
            const response = await signup(payload);
            setSuccess(`User ${response.data.username} registered successfully!`);

            setFormData(SignUpModel);
            setAvailability({ username: null, email: null });
        } catch (err) {
            setErrors(err.response?.data || { general: "Something went wrong" });
        }
    };

    const checkAvailability = debounce(async (field, value) => {
        if (!value) return;
        try {
            const response = await signUpCheck(field, value);
            setAvailability((prev) => ({
                ...prev,
                [field]: response.data.available ? "available" : "taken"
            }));
        } catch (error) {
            console.error("Availability check failed:", error);
        }
    }, 500);

    return {
        formData,
        handleChange,
        handleSubmit,
        handlePasswordChange,
        handleConfirmChange,
        password,
        confirm,
        passwordMatch,
        passwordRules,
        availability,
        setErrors,
        errors,
        success,
        isOldEnough,
        isEmail,
        checkAvailability,
    };
}
