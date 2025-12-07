import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginModel } from "@/models/authModels.jsx";
import { useAuth } from "@/context/AuthContext.jsx";

export function useLogin(onSuccess) {
    const [loginData, setLoginData] = useState(loginModel);
    const [isChecked, setChecked] = useState(false);
    const [errors, setErrors] = useState({});
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleChange = (e) => {
        setLoginData({ ...loginData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors({});
        try {
            await login(loginData.username, loginData.password, isChecked);

            // If onSuccess callback is provided, call it (for modal usage)
            if (onSuccess) {
                onSuccess();
            } else {
                // Otherwise, navigate to home (for normal login page usage)
                navigate("/");
            }
        } catch (err) {
            console.error("Login failed", err);
            setErrors({
                general: err.response?.data?.error || "Invalid username or password",
            });
        }
    };

    return { loginData, handleChange, handleSubmit, isChecked, setChecked, errors };
}