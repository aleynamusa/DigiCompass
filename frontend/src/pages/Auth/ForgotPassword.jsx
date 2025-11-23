import React from "react";
import {useForgotPassword} from "@/hooks/useForgotPassword.jsx";

const ForgotPassword = () => {

    const{
        email, setEmail,
        message,
        error,
        handleSubmit,
    } = useForgotPassword();

    return (
        <div className="bg-cyan-700 rounded-md shadow-2xl"
             style={{ maxWidth: "550px", margin: "auto", padding: "20px" }}>
            <h1 className="pb-3">Forgot Password</h1>
            <p className="text-xs pb-3">Enter your email address, and we’ll send you a reset link.</p>

            {message && <p style={{ color: "darkgreen" }}>{message}</p>}
            {error && <p style={{ color: "darkred" }}>{error}</p>}

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
