import { useState } from "react";

export function usePasswordValidation() {
    const [password, setPassword] = useState("");
    const [confirm, setConfirm] = useState("");
    const [passwordMatch, setPasswordMatch] = useState(true);

    const [passwordRules, setPasswordRules] = useState({
        length: false,
        upper: false,
        lower: false,
        number: false,
        special: false,
    });

    const checkPasswordRules = (password) => ({
        length: password.length >= 8,
        upper: /[A-Z]/.test(password),
        lower: /[a-z]/.test(password),
        number: /[0-9]/.test(password),
        special: /[#?!@$%^&*-]/.test(password),
    });

    const handlePasswordChange = (value) => {
        setPassword(value);
        setPasswordRules(checkPasswordRules(value));
    };

    const handleConfirmChange = (value) => {
        setConfirm(value);
        setPasswordMatch(value === password);
    };

    return {
        password,
        confirm,
        passwordMatch,
        passwordRules,
        handlePasswordChange,
        handleConfirmChange,
    };
}
