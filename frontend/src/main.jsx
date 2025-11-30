import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import LogIn from "./pages/Auth/LogIn.jsx";
import Home from "./pages/Dashboard/Home.jsx";
import RouteDiscovery from "./pages/Dashboard/RouteDiscovery.jsx";
import Profile from "./pages/Dashboard/Profile.jsx";
import ResetPassword from "./pages/Auth/ResetPassword.jsx";
import ForgotPassword from "./pages/Auth/ForgotPassword.jsx";
import SignUpForm from "@/pages/Auth/SignUp.jsx";
import Layout from "@/Layout.jsx";
import { MantineProvider } from "@mantine/core";
import "@mantine/core/styles.css";
import "@mantine/carousel/styles.css";
import "./index.css";
import "./App.css";
import {AuthProvider} from "@/context/AuthContext.jsx";
import EditProfile from "@/pages/Auth/EditProfile.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
    <MantineProvider withGlobalStyles withNormalizeCSS>
        <React.StrictMode>
            <AuthProvider>
                <BrowserRouter>
                    <Routes>
                        <Route path="/signup" element={<SignUpForm />} />
                        <Route path="/login" element={<LogIn />} />
                        <Route path="/forgot-password" element={<ForgotPassword />} />
                        <Route path="/reset-password" element={<ResetPassword />} />
                        <Route element={<Layout />}>
                            <Route path="/" element={<Home />} />
                            <Route path="/routeDiscovery" element={<RouteDiscovery />} />
                            <Route path="/profile" element={<Profile />} />
                            <Route path="/profile/:userId" element={<Profile />} />

                            <Route path="/edit-profile" element={<EditProfile />} />
                        </Route>


                    </Routes>
                </BrowserRouter>
            </AuthProvider>
        </React.StrictMode>
    </MantineProvider>
);
