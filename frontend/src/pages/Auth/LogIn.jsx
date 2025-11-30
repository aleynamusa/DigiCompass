import React from "react";
import { useLogin } from "@/hooks/useLogin.jsx";
import LoginForm from "@/components/auth/loginForm.jsx";

const LogIn = () => {
    const {
        loginData,
        handleChange,
        handleSubmit,
        isChecked,
        setChecked,
        errors
    } = useLogin();

    return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="bg-cyan-700 rounded-md shadow-2xl p-5 w-[500px] h-[500px] flex flex-col justify-center">

                <h1 className="pb-2 text-xl font-semibold text-white text-center">Log In</h1>

                <LoginForm
                    loginData={loginData}
                    handleChange={handleChange}
                    handleSubmit={handleSubmit}
                    isChecked={isChecked}
                    setChecked={setChecked}
                    errors={errors}
                />
            </div>
        </div>
    );
};

export default LogIn;
