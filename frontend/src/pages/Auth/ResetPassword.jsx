import React from "react";
import { useResetPassword } from "@/hooks/useResetPassword.jsx";
import ShowPassword from "@/components/showPassword.jsx";

const ResetPassword = () => {
    const {
        password,
        confirm,
        passwordRules,
        passwordMatch,
        message,
        error,
        handlePasswordChange,
        handleConfirmChange,
        handleSubmit,
    } = useResetPassword();

    return (
        <div className="bg-cyan-700 rounded-md shadow-2xl p-6"
             style={{ maxWidth: "550px", margin: "auto", padding: "20px" }}>
            <h1 className="pb-4">Reset Password</h1>

            {message && <p style={{ color: "darkgreen" }}>{message}</p>}
            {error && <p style={{ color: "darkred" }}>{error}</p>}

            <form onSubmit={handleSubmit}>
                <ShowPassword
                    name="password"
                    value={password}
                    onChange={handlePasswordChange}
                />

                <ShowPassword
                    name="confirmPassword"
                    label="Confirm Password"
                    value={confirm}
                    onChange={handleConfirmChange}
                />

                {!passwordMatch && confirm.length > 0 && (
                    <p style={{ color: "red" }}>Passwords do not match</p>
                )}

                {/* Password rules */}
                {password.length > 0 && (
                    <ul className="mt-2 text-sm">
                        <li style={{ color: passwordRules.length ? "darkgreen" : "darkred" }}>
                            {passwordRules.length ? "✔" : "✘"} At least 8 characters
                        </li>
                        <li style={{ color: passwordRules.upper ? "darkgreen" : "darkred" }}>
                            {passwordRules.upper ? "✔" : "✘"} At least 1 uppercase letter
                        </li>
                        <li style={{ color: passwordRules.lower ? "darkgreen" : "darkred" }}>
                            {passwordRules.lower ? "✔" : "✘"} At least 1 lowercase letter
                        </li>
                        <li style={{ color: passwordRules.number ? "darkgreen" : "darkred" }}>
                            {passwordRules.number ? "✔" : "✘"} At least 1 number
                        </li>
                        <li style={{ color: passwordRules.special ? "darkgreen" : "darkred" }}>
                            {passwordRules.special ? "✔" : "✘"} At least 1 special character
                        </li>
                    </ul>
                )}

                <button type="submit"
                        className="text-white bg-gradient-to-r from-cyan-500 to-blue-500 hover:bg-gradient-to-bl
                                   focus:ring-4 focus:outline-none focus:ring-cyan-300 dark:focus:ring-cyan-800
                                   font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2">
                    Reset Password
                </button>
            </form>
        </div>
    );
};

export default ResetPassword;
