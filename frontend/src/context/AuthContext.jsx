import React, { createContext, useState, useEffect, useContext } from "react";
import { jwtDecode } from "jwt-decode";
import { loginApi, logoutApi, refreshApi } from "@/api/authApi.jsx";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [accessToken, setAccessToken] = useState(() =>
        localStorage.getItem("accessToken") || null
    );
    const [loading, setLoading] = useState(true);

    const setUserFromToken = (token) => {
        try {
            const decoded = jwtDecode(token);
            setUser({
                id: decoded.id,
                username: decoded.username || decoded.sub,
                role: decoded.role || "user",
            });
        } catch (err) {
            console.error("Failed to decode token", err);
            setUser(null);
        }
    };

    const refreshTokens = async (isInitial = false) => {
        try {
            const res = await refreshApi();
            const { accessToken: newAccess } = res.data;

            if (newAccess) {
                setAccessToken(newAccess);
                setUserFromToken(newAccess);
                localStorage.setItem("accessToken", newAccess);
            }
        } catch (err) {
            console.warn("No refresh token or refresh failed.");

            if (!isInitial && localStorage.getItem("accessToken")) {
                await logout();
            }
        }
    };

    useEffect(() => {
        const stored = localStorage.getItem("accessToken") || sessionStorage.getItem("accessToken");
        if (stored) {
            setAccessToken(stored);
            setUserFromToken(stored);
            setLoading(false);
        } else {
            refreshTokens(true).finally(() => setLoading(false));
        }
    }, []);

    useEffect(() => {
        if (!accessToken || loading) return;

        const decoded = jwtDecode(accessToken);
        const exp = decoded.exp * 1000;
        const timeout = exp - Date.now() - 60_000; // refresh 1 min early

        if (timeout > 0) {
            const id = setTimeout(() => refreshTokens(), timeout);
            return () => clearTimeout(id);
        }
    }, [accessToken, loading]);

    const login = async (username, password, rememberMe) => {
        const res = await loginApi({ username, password, rememberMe });
        const { accessToken: token } = res.data;
        setAccessToken(token);
        setUserFromToken(token);


        localStorage.setItem("accessToken", token);


        return token;
    };

    const logout = async () => {
        try {
            await logoutApi();
        } catch (err) {
            console.error("Logout failed (ignored):", err);
        }
        localStorage.removeItem("accessToken");
        setAccessToken(null);
        setUser(null);
        window.location.href = "/login";
    };

    const isAuthenticated = !!user;

    if (loading) return <div>Loading...</div>;

    return (
        <AuthContext.Provider value={{ user, isAuthenticated, login, logout, accessToken }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
