import React, { useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import Cookies from "js-cookie";

const API_URL = import.meta.env.VITE_BACKEND_URL;

const LogIn = () => {
    const [loginData, setLoginData] = useState({
        username: "",
        password: "",
    });
    const [isChecked, setChecked] = useState(true);
    const [errors, setErrors] = useState({});
    const [success, setSuccess] = useState("");
    const navigate = useNavigate();

    const handleChange = (e) => {
        setLoginData({ ...loginData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors({});
        setSuccess("");

        try {
            const response = await axios.post(`${API_URL}/users/logIn`, loginData);

            if (response.data.username) {
                setSuccess("Login successful!");
                Cookies.set("username", response.data.username);
                Cookies.set("auth", "th679"); // TODO: replace with real token


                if (isChecked) {
                    Cookies.set("remember", "true", { expires: 365 });
                }

                navigate("/");
            } else {
                setErrors({
                    general: response.data.message || "Invalid username or password",
                });
            }
        } catch (err) {
            if (err.response && err.response.data) {
                setErrors(err.response.data);
            } else {
                setErrors({ general: "Something went wrong" });
            }
        }
    };

    return (
        <div
            className="bg-cyan-700 rounded-md shadow-2xl"
            style={{ maxWidth: "550px", margin: "auto", padding: "20px" }}
        >
            <h1 className="pb-2">Log In</h1>
            {errors.general && <p style={{ color: "red" }}>{errors.general}</p>}
            {success && <p style={{ color: "green" }}>{success}</p>}

            <form
                onSubmit={handleSubmit}
                className="max-w-md mx-auto bg-cyan-700 rounded-md m-3 p-3"
                style={{ width: "500px" }}
            >
                {/* Username */}
                <div className="text-left relative z-0 w-full mb-5 group">
                    <input
                        type="text"
                        name="username"
                        id="username"
                        className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-slate-400 dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer"
                        placeholder=" "
                        value={loginData.username}
                        onChange={handleChange}
                        required
                    />
                    <label
                        htmlFor="username"
                        className="peer-focus:font-medium absolute text-sm text-gray-800 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-amber-50 peer-focus:dark:text-amber-50 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                        Username
                    </label>
                    {errors.username && <p style={{ color: "red" }}>{errors.username}</p>}
                </div>

                {/* Password */}
                <div className="text-left relative z-0 w-full mb-5 group">
                    <input
                        type="password"
                        name="password"
                        id="password"
                        className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-slate-400 dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer"placeholder=" "
                        required
                        value={loginData.password}
                        onChange={handleChange}
                        minLength={8}
                    />
                    <label
                        htmlFor="password"
                        className="peer-focus:font-medium absolute text-sm text-gray-800 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-amber-50 peer-focus:dark:text-amber-50 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                    >
                        Password
                    </label>
                    {errors.password && <p style={{ color: "red" }}>{errors.password}</p>}
                </div>

                <div className="checkbox-wrapper mb-3 justify-items-center">
                    <input
                        onChange={(e) => {
                            setChecked(e.target.checked);
                            if (e.target.checked) {
                                Cookies.set("auth", "token-gFVn244", { expires: 365, secure: true });
                            } else {
                                Cookies.remove("auth");
                            }
                        }}
                        type="checkbox"
                        className="check"
                        id="check1-61"
                    />
                    <label htmlFor="check1-61" className="label flex items-center gap-2">
                        <svg width="20" height="20" viewBox="0 0 95 95">
                            <rect
                                x="30"
                                y="20"
                                width="50"
                                height="50"
                                stroke="black"
                                fill="none"
                            ></rect>
                            <g transform="translate(0,-952.36222)">
                                <path
                                    d="m 56,963 c -102,122 6,9 7,9 17,-5 -66,69 -38,52 122,-77 -7,14 18,4 29,-11 45,-43 23,-4"
                                    stroke="#cc2323"
                                    strokeWidth="3"
                                    fill="none"
                                    className="path1"
                                ></path>
                            </g>
                        </svg>
                        <span>Remember me</span>
                    </label>
                </div>

                <Link
                    to="/signup"
                >
                    Don't have an account?
                </Link>
                <br />
                <button
                    type="submit"
                    className="text-white bg-gradient-to-r from-cyan-500 to-blue-500 hover:bg-gradient-to-bl focus:ring-4 focus:outline-none focus:ring-cyan-300 dark:focus:ring-cyan-800 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2">
                    Log In
                </button>
                <br />
                <Link
                    to="/forgot-password"
                >
                    Forgot password?
                </Link>

            </form>
        </div>
    );
};

export default LogIn;
