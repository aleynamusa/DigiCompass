import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Dashboard/Home.jsx";
import RouteDiscovery from "./pages/Dashboard/RouteDiscovery.jsx";
import Profile from "./pages/Dashboard/Profile.jsx";
import ResetPassword from "./pages/Auth/ResetPassword.jsx";
import ForgotPassword from "./pages/Auth/ForgotPassword.jsx";
import Layout from "@/Layout.jsx";
import { MantineProvider } from "@mantine/core";
import "@mantine/core/styles.css";
import "@mantine/carousel/styles.css";
import "./index.css";
import "./App.css";
import {AuthProvider} from "@/context/AuthContext.jsx";
import EditProfile from "@/pages/Auth/EditProfile.jsx";
import '@mantine/dropzone/styles.css';
import WeatherForecast from "@/pages/Dashboard/WeatherForecast.jsx";
import {TripPlanning} from "@/pages/Dashboard/TripPlanning.jsx";
import '@mantine/dates/styles.css';
import SignUpForm from "@/pages/Auth/SignUp.jsx";
import LogIn from "@/pages/Auth/LogIn.jsx";
import {ModalsProvider} from "@mantine/modals";
import ProtectedRoute from "@/ProtectedRoutes.jsx";


ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <MantineProvider>
            <ModalsProvider>
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
                                <Route path="/edit-profile" element={<EditProfile />} />
                                <Route path="/weather" element={<WeatherForecast />} />
                                <Route path="/tripPlanning" element={<TripPlanning />} />
                            </Route>
                            <Route element={<ProtectedRoute />}>
                                <Route element={<Layout />}>
                                    <Route path="/profile/:userId" element={<Profile />} />
                                    <Route path="/profile" element={<Profile />} />
                                </Route>
                            </Route>
                        </Routes>
                    </BrowserRouter>
                </AuthProvider>
            </ModalsProvider>
        </MantineProvider>
    </React.StrictMode>
);
