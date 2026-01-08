import React, {useEffect} from "react";
import ShowPassword from "@/components/showPassword.jsx";
import ShowCalendarForBirthDate from "@/components/BirthDate.jsx";
import {useSignUp} from "@/hooks/useSignUp.jsx";
import {useNavigate} from "react-router-dom";

const SignUpForm = () => {
    const {
        formData,
        handleChange,
        handleSubmit,
        handlePasswordChange,
        isEmail,
        isOldEnough,
        passwordMatch,
        passwordRules,
        availability,
        errors,
        success,
        checkAvailability,
        setErrors,
        handleConfirmChange,
    } = useSignUp();

    const navigate = useNavigate();

    useEffect(() => {
        if (success) {
            navigate("/logIn");
        }
    }, [success, navigate]);



    return (
        // center horizontally and vertically within viewport
        <div className="min-h-screen flex items-center justify-center p-4">
            {/* card */}
            <div className="bg-cyan-700 rounded-md shadow-2xl w-full max-w-[550px] p-6">
                <h1 className="pb-2">Sign Up</h1>
                {errors.general && <p style={{ color: "red" }}>{errors.general}</p>}
                {success && <p style={{ color: "darkgreen" }}>{success}</p>}
                <form onSubmit={handleSubmit} className="max-w-md mx-auto bg-cyan-700 rounded-md m-3 p-3"
                      style={{width: "500px"}}
                >

                    <div className="text-left relative z-0 w-full mb-5 group ">
                        <input type="email" name="email" id="email"
                               className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-slate-400 dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer"
                               placeholder=" "
                               value={formData.email}
                               onChange={(e) => {
                                   handleChange(e);

                                   if (!isEmail(e.target.value)) {
                                       setErrors((prev) => ({ ...prev, email: "Invalid email format" }));
                                   } else {
                                       setErrors((prev) => {
                                           const { email:_email, ...rest } = prev;
                                           return rest;
                                       });
                                       checkAvailability("email", e.target.value);
                                   }
                               }}
                               required/>
                        <label htmlFor="email"
                               className="peer-focus:font-medium absolute text-sm text-gray-800 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-amber-50 peer-focus:dark:text-amber-50 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">Email
                            address</label>
                        {errors.email && <p style={{color: "red"}}>{errors.email}</p>
                        }

                        {availability.email === "taken" && (
                            <p style={{ color: "red" }}>Email is already registered in our system</p>
                        )}
                    </div>

                    <div className="text-left relative z-0 w-full mb-5 group">
                        <input type="text" name="username" id="username"
                               className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-slate-400 dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer"
                               placeholder=" "
                               value={formData.username}
                               onChange={(e) => {
                                   handleChange(e);
                                   if (formData.password !== formData.confirmPassword) {
                                       setErrors((prev) => ({ ...prev, confirmPassword: "Passwords do not match" }));
                                   }

                                   checkAvailability("username", e.target.value);
                               }}
                               required/>
                        <label htmlFor="username"
                               className="peer-focus:font-medium absolute text-sm text-gray-800 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-amber-50 peer-focus:dark:text-amber-50 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">Username</label>
                        {errors.username && <p style={{color: "red"}}>{errors.username}</p>
                        }

                        {availability.username === "taken" && (
                            <p style={{ color: "red" }}>Username already taken</p>
                        )}
                    </div>


                    <ShowCalendarForBirthDate
                        value={formData.birthDate}
                        onChange={(e) => {
                            handleChange(e);
                            const date = e.target.value;

                            if (!isOldEnough(date)) {
                                setErrors((prev) => ({
                                    ...prev,
                                    birthDate: "You must be at least 14 years old",
                                }));
                            } else {
                                setErrors((prev) => {
                                    const {  birthDate:_birthDate, ...rest } = prev;
                                    return rest;
                                });
                            }
                        }}
                        error={errors.birthDate}
                    />

                    <ShowPassword
                        name="password"
                        value={formData.password}
                        onChange={(e) => {
                            const value = e.target.value;
                            handlePasswordChange(value);
                            handleChange(e);
                        }}
                    />

                    <div className="text-left relative z-0 w-full  group">
                        <ShowPassword
                            name="confirmPassword"
                            label="Confirm Password"
                            value={formData.confirmPassword}
                            onChange={(e) => {
                                const value = e.target.value;
                                handleConfirmChange(value);
                                handleChange(e);
                            }}
                        />
                        {passwordMatch === false && (
                            <p style={{ color: "red" }}>Passwords do not match</p>
                        )}


                    </div>

                    {formData.password.length > 0 && (
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
                    </ul>)}
                    <br/>

                    <button type="submit"
                            className="text-white bg-gradient-to-r from-cyan-500 to-blue-500 hover:bg-gradient-to-bl focus:ring-4 focus:outline-none focus:ring-cyan-300 dark:focus:ring-cyan-800 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2">
                        Sign Up
                    </button>
                </form>
            </div>
        </div>
    );
};

export default SignUpForm;
