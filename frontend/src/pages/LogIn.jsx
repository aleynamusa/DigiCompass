import React, {useState} from "react";
import axios from "axios"
import {Link, useNavigate} from "react-router-dom";

const LogIn = () => {
    const [loginData, setLoginData] = useState({
        username: "",
        email: "",// not sure if I am gonna use email or password decide later and refactor the backend
        password:"",
    });

    const navigate = useNavigate();

    const[errors, setErrors] = useState({});
    const[success, setSuccess] = useState("");

    const handleChange = (e) => {
        setLoginData({...loginData, [e.target.name] : e.target.value});
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors({});
        setSuccess("");

        try{
            const response = await axios.post(
                "http://localhost:8080/api/users/logIn",
                loginData
            );

            if (response.data === true) {
                setSuccess("Login successful!");
                navigate('/');
            } else {
                setErrors({ general: "Invalid username or password" });
            }
        }
        catch (err){
            if (err.response && err.response.data) {
                setErrors(err.response.data);
            } else {
                setErrors({ general: "Something went wrong" });
            }
        }
    };

    return(
        <div style={{ maxWidth: "400px", margin: "auto", padding: "20px" }}>
            <h2>Log In</h2>
            {errors.general && <p style={{ color: "red" }}>{errors.general}</p>}
            {success && <p style={{ color: "green" }}>{success}</p>}
            <form onSubmit={handleSubmit} className="max-w-md mx-auto" style={{width: "250px"}}
            >

                <div className="text-left relative z-0 w-full mb-5 group">
                    <input type="text" name="username" id="username"
                           className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-gray-800 dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                           placeholder=" "
                           value={loginData.username}
                           onChange={handleChange}
                           required/>
                    <label htmlFor="username"
                           className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">Username</label>
                    {errors.username && <p style={{color: "red"}}>{errors.username}</p>}
                </div>

                <div className="text-left relative z-0 w-full mb-5 group">
                    <input type="password" name="password" id="password"
                           className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-gray-800 dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                           placeholder=" " required
                           value={loginData.password}
                           onChange={handleChange}
                           minLength={8}/>
                    <label htmlFor="password"
                           className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">Password</label>
                    {errors.password && <p style={{color: "red"}}>{errors.password}</p>}
                </div>

                <Link to="/signup">Don't have an account?</Link>

                <button type="submit" >Log In</button>
            </form>
        </div>
    );

};

export default LogIn;