import React, { useState } from "react";
import { Eye, EyeOff } from "lucide-react";

const ShowPassword = ({ name, value, onChange, label = "Password", error }) => {
    const [showPassword, setShowPassword] = useState(false);

    const togglePassword = () => setShowPassword((prev) => !prev);

    return (
        <div className="text-left relative z-0 w-full mb-5    group">
            <input
                type={showPassword ? "text" : "password"}
                name={name}
                id={name}
                className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-slate-400 dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer"
                placeholder=" "
                value={value}
                onChange={onChange}
                minLength={8}
                required
            />
            <span
                className="absolute right-2 top-2.5 cursor-pointer text-gray-500"
                onClick={togglePassword}
            >
        {showPassword ? (
            <Eye className="text-gray-500" size={22} />
        ) : (
            <EyeOff className="text-gray-500" size={22} />
        )}
      </span>
            <label
                htmlFor={name}
                className="peer-focus:font-medium absolute text-sm text-gray-800 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-amber-50 peer-focus:dark:text-amber-50 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
            >
                {label}
            </label>
            {error && <p style={{ color: "red" }}>{error}</p>}
        </div>
    );
};

export default ShowPassword;
