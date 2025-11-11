import {useState} from "react";
import {forgotPassword} from "@/api/authApi.jsx";

export function useForgotPassword(){
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setError("");

        try {
            const response = await forgotPassword(

                 email
            );
            setMessage(response.data || "Password reset link sent to your email.");
        } catch (err) {
            console.error(err);
            setError(
                err.response?.data?.message ||
                "Something went wrong. Please try again later."
            );
        }

    };
    return{
        email, setEmail,
        message, setMessage,
        error, setError,
        handleSubmit,

    }
}
