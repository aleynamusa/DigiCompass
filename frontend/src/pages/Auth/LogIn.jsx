import React from "react";
import { Link } from "react-router-dom";
import { Checkbox } from "@mantine/core";
import ShowPassword from "@/components/showPassword.jsx";
import { useLogin } from "@/hooks/useLogin.jsx";

const LogIn = () => {
    const { loginData, handleChange, handleSubmit,
        isChecked, setChecked, errors,  } = useLogin();

    return (
        <div className="flex items-center justify-center min-h-screen ">
            <div className="bg-cyan-700 rounded-md shadow-2xl p-5 w-[500px] h-[500px] flex flex-col justify-center">
                <h1 className="pb-2 text-xl font-semibold text-white text-center">Log In</h1>

                {errors.general && <p className="text-red-400 text-center">{errors.general}</p>}

                <form onSubmit={handleSubmit} className="bg-cyan-700 rounded-md p-3 space-y-4">
                    <div className="text-left relative z-0 w-full mb-5 group">
                        <input
                            type="text"
                            name="username"
                            id="username"
                            className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0
                             border-b-2 border-gray-300 appearance-none dark:text-white dark:border-slate-400
                             dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer"

                            placeholder=" "
                            value={loginData.username}
                            onChange={handleChange}
                            required
                        />
                        <label
                            htmlFor="username"
                            className="peer-focus:font-medium absolute text-sm text-gray-800 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-amber-50 peer-focus:dark:text-amber-50
                             peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                            Username
                        </label>
                    </div>

                    <ShowPassword
                        name="password"
                        value={loginData.password}
                        onChange={handleChange}
                        error={errors.password}
                    />

                    <Checkbox
                        checked={isChecked}
                        onChange={(e) => setChecked(e.target.checked)}
                        className="justify-items-center"
                        label="Remember me"
                        radius="xs"
                    />

                    <Link to="/signup" className="text-sm text-blue-200 hover:underline block mt-2">
                        Don’t have an account?
                    </Link>

                    <button
                        type="submit"
                        className="w-full mt-4 text-white bg-gradient-to-r from-cyan-500 to-blue-500 hover:bg-gradient-to-bl
            focus:ring-4 focus:outline-none focus:ring-cyan-300 font-medium rounded-lg text-sm px-5 py-2.5"
                    >
                        Log In
                    </button>

                    <Link to="/forgot-password" className="text-sm text-blue-200 hover:underline block mt-3">
                        Forgot password?
                    </Link>
                </form>
            </div>
        </div>
    );
};

export default LogIn;
