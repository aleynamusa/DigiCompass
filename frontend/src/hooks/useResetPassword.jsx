import { usePasswordValidation } from "@/hooks/usePasswordValidation";
import {resetPassword} from "@/api/authApi.jsx";
import {useState} from "react";
import {useLocation, useNavigate} from "react-router-dom";

export function useResetPassword() {
    const {
        password,
        confirm,
        passwordRules,
        passwordMatch,
        handlePasswordChange,
        handleConfirmChange
    } = usePasswordValidation();

    const navigate = useNavigate();
    const token = new URLSearchParams(useLocation().search).get("token");

    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!passwordMatch) {
            setError("Passwords do not match!");
            return;
        }

        try {
            const response = await resetPassword(token, password);
            setMessage(response.data || "Password reset successfully!");
            setTimeout(() => navigate("/login"), 3000);
        } catch (err) {
            setError(err.response?.data?.message || "Invalid or expired token.");
        }
    };

    return {
        password,
        confirm,
        passwordRules,
        passwordMatch,
        message,
        error,
        handlePasswordChange: (e) => handlePasswordChange(e.target.value),
        handleConfirmChange: (e) => handleConfirmChange(e.target.value),
        handleSubmit,
    };
}
