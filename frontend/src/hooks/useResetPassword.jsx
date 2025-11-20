import  {useState} from "react";
import {useLocation, useNavigate} from "react-router-dom";
import {resetPassword} from "@/api/authApi.jsx";

export function useResetPassword(){
    const location = useLocation();
    const navigate = useNavigate();
    const queryParams = new URLSearchParams(location.search);
    const token = queryParams.get("token");

    const [password, setPassword] = useState("");
    const [confirm, setConfirm] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (password !== confirm) {
            setError("Passwords do not match!");
            return;
        }

        try {
            const response = await resetPassword(  token, password );
            setMessage(response.data || "Password reset successfully!");
            setTimeout(() => navigate("/login"), 3000);
        } catch (err) {
            setError(err.response?.data?.message || "Invalid or expired token.");
        }
    };
    return {
        password,
        setPassword,
        confirm,
        setConfirm,
        message,
        error,
        handleSubmit,
    };

}