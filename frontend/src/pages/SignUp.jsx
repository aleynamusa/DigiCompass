import React, { useState } from "react";
import axios from "axios";

const SignUpForm = () => {

    const isEmail = (email) => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email);


    const checkPasswordRules = (password) => {
        return {
            length: password.length >= 8,
            upper: /[A-Z]/.test(password),
            lower: /[a-z]/.test(password),
            number: /[0-9]/.test(password),
            special: /[#?!@$%^&*-]/.test(password),
        };
    };


    const [formData, setFormData] = useState({
        username: "",
        email: "",
        age: "",
        password: "",
    });

    const [passwordRules, setPasswordRules] = useState({
        length: false,
        upper: false,
        lower: false,
        number: false,
        special: false,
    });

    const handlePasswordChange = (e) => {
        const { value } = e.target;
        setFormData({ ...formData, password: value });
        setPasswordRules(checkPasswordRules(value));
    };


    const [errors, setErrors] = useState({});
    const [success, setSuccess] = useState("");

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors({});
        setSuccess("");

        if(!isEmail(formData.email)){
            setErrors({ email: "Invalid email format" });
            return;
        }


        try {
            const response = await
            axios.post(
                "http://localhost:8080/api/users/signUp",
                formData
            );
            setSuccess(`User ${response.data.username} registered successfully!`);
            setFormData({ username: "", email: "", age: "", password: "" });
        } catch (err) {
            if (err.response && err.response.data) {
                setErrors(err.response.data);
            } else {
                setErrors({ general: "Something went wrong" });
            }
        }
    };

    return (
        <div className="bg-cyan-700 rounded-md shadow-2xl" style={{ maxWidth: "550px", margin: "auto", padding: "20px" }}>
            <h1 className="pb-2">Sign Up</h1>
            {errors.general && <p style={{ color: "red" }}>{errors.general}</p>}
            {success && <p style={{ color: "green" }}>{success}</p>}
            <form onSubmit={handleSubmit} className="max-w-md mx-auto  bg-cyan-700 rounded-md m-3 p-3 "
                  style={{width: "500px"}}
            >

                <div className="text-left relative z-0 w-full mb-5 group ">
                    <input type="email" name="email" id="email"
                           className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-slate-400 dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer"
                           placeholder=" "
                           value={formData.email}
                           onChange={handleChange}
                           required/>
                    <label htmlFor="email"
                           className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-amber-50 peer-focus:dark:text-amber-50 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">Email
                        address</label>
                    {errors.email && <p style={{color: "red"}}>{errors.email}</p>}
                </div>

                <div className="text-left relative z-0 w-full mb-5 group">
                    <input type="text" name="username" id="username"
                           className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-slate-400 dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer"
                           placeholder=" "
                           value={formData.username}
                           onChange={handleChange}
                           required/>
                    <label htmlFor="username"
                           className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-amber-50 peer-focus:dark:text-amber-50 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">Username</label>
                    {errors.username && <p style={{color: "red"}}>{errors.username}</p>}
                </div>

                <div className="text-left relative z-0 w-full mb-5 group">
                    <input type="number" name="age" id="age"
                           className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-slate-400 dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer"
                           placeholder=" "
                           value={formData.age}
                           onChange={handleChange}
                           required
                           min={14}
                           max={127}/>
                    <label htmlFor="age"
                           className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-amber-50 peer-focus:dark:text-amber-50 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                        Age</label>
                    {errors.age && <p style={{color: "red"}}>{errors.age}</p>}
                </div>

                <div className="text-left relative z-0 w-full mb-5 group">
                    <input type="password" name="password" id="password"
                           className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-slate-400 dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer"
                           placeholder=" " required
                           value={formData.password}
                           onChange={handlePasswordChange}
                           minLength={8}/>
                    <label htmlFor="password"
                           className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 peer-focus:text-amber-50 peer-focus:dark:text-amber-50 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">Password</label>
                    {errors.password && <p style={{color: "red"}}>{errors.password}</p>}
                </div>

                {/* Rules Display */}
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

                <button type="submiy"
                        className="text-white bg-gradient-to-r from-cyan-500 to-blue-500 hover:bg-gradient-to-bl focus:ring-4 focus:outline-none focus:ring-cyan-300 dark:focus:ring-cyan-800 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2">
                    Sign Up
                </button>
            </form>
        </div>
    );
};

export default SignUpForm;
