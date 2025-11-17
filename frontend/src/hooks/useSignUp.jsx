import {signup, signUpCheck} from "@/api/authApi.jsx";
import debounce from "lodash.debounce";
import dayjs from "dayjs";
import {useEffect, useState} from "react";
import {SignUpModel} from "@/models/authModels.jsx"


export function useSignUp(){
    const [formData, setFormData] = useState(SignUpModel);
    const [availability, setAvailability] = useState({
        username: null,
        email: null,
    });
    const [passwordMatch, setPasswordMatch] = useState(null);

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

    const isOldEnough = (birthDate) => {
        if (!birthDate) return false;
        const age = dayjs().diff(dayjs(birthDate), "year");
        return age >= 14;
    };


    useEffect(() => {
        if (!formData.password || !formData.confirmPassword) {
            setPasswordMatch(null);
        } else {
            setPasswordMatch(formData.password === formData.confirmPassword);
        }
    }, [formData.password, formData.confirmPassword]);


    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors({});
        setSuccess("");

        if (formData.password !== formData.confirmPassword) {
            setErrors({ confirmPassword: "Passwords do not match" });
            return;
        }

        formData.role = 2;
        console.log("Sending data:", JSON.stringify(formData, null, 2));

        try {
            const { confirmPassword: _confirmPassword, ...payload } = formData;

            const response = await signup(payload);
            setSuccess(`User ${response.data.username} registered successfully!`);

            setFormData({
                username: "",
                email: "",
                birthDate: "",
                password: "",
                confirmPassword: "",

            });
            setAvailability({ username: null, email: null });
        } catch (err) {
            if (err.response && err.response.data) {
                setErrors(err.response.data);
            } else {
                setErrors({ general: "Something went wrong" });
            }
        }
    };

    const checkAvailability = debounce(async (field, value) => {
        if (!value) return;

        try {
            const response = await signUpCheck(
                 field, value
            );

            setAvailability((prev) => ({
                ...prev,
                [field]: response.data.available ? "taken" : "available",
            }));
        } catch (error) {
            console.error("Availability check failed:", error);
        }
    }, 500); // wait 0.5s after typing stops, so I dont call the function every time
    return {
        formData,
        setFormData,
        setErrors,
        handleChange,
        handleSubmit,
        handlePasswordChange,
        passwordRules,
        passwordMatch,
        availability,
        errors,
        success,
        isOldEnough,
        isEmail,
        checkAvailability,
    };

}

