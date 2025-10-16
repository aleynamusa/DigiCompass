import React, { useState } from "react";
import axios from "axios";

const API_URL = import.meta.env.VITE_BACKEND_URL;

const ForgotPassword = () => {
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setError("");

        try {
            const response = await axios.post(
                `${API_URL}/auth/forgot-password`,
                null, // no body needed if you use @RequestParam
                { params: { email } }
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

    return (
        <div className="bg-cyan-700 rounded-md shadow-2xl"
             style={{ maxWidth: "550px", margin: "auto", padding: "20px" }}>
            <h1 className="pb-3">Forgot Password</h1>
            <p className="text-xs pb-3">Enter your email address, and we’ll send you a reset link.</p>

            {message && <p style={{ color: "green" }}>{message}</p>}
            {error && <p style={{ color: "red" }}>{error}</p>}

            <form onSubmit={handleSubmit}>
                <div className="text-left relative z-0 w-full mb-5 group">
                    <input
                        type="email"
                        id="email"
                        name="email"
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-slate-400 dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer"
                        placeholder=" "
                    />
                    <label htmlFor="email"
                           className="peer-focus:font-medium absolute text-sm text-gray-800 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-amber-50 peer-focus:dark:text-amber-50 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">Email
                        address</label>
                    {error.email && <p style={{color: "red"}}>{error.email}</p>}
                </div>

                <button type="submit"
                        className="text-white bg-gradient-to-r from-cyan-500 to-blue-500 hover:bg-gradient-to-bl focus:ring-4 focus:outline-none focus:ring-cyan-300 dark:focus:ring-cyan-800 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2">
                Send Reset Link
                </button>

            </form>
        </div>
    );
};

export default ForgotPassword;
