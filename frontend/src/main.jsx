import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import LogIn from "./pages/LogIn.jsx";
import Home from "./pages/Home.jsx";
import PrivateRoutes from "./PrivateRoutes.jsx";
import RouteDiscovery from "./pages/RouteDiscovery.jsx";
import ResetPassword from "./pages/ResetPassword.jsx";
import ForgotPassword from "./pages/ForgotPassword.jsx";
import SignUpForm from "@/pages/SignUp.jsx";
import Layout from "@/Layout.jsx";
import { MantineProvider } from "@mantine/core";
import "@mantine/core/styles.css";
import "@mantine/carousel/styles.css";
import "./index.css";
import "./App.css";

ReactDOM.createRoot(document.getElementById("root")).render(
    <MantineProvider withGlobalStyles withNormalizeCSS>
        <React.StrictMode>
            <BrowserRouter>
                <Routes>
                    {/* Public routes */}
                    <Route path="/signup" element={<SignUpForm />} />
                    <Route path="/login" element={<LogIn />} />
                    <Route path="/forgot-password" element={<ForgotPassword />} />
                    <Route path="/reset-password" element={<ResetPassword />} />
                    <Route element={<Layout />}>
                        <Route path="/" element={<Home />} />
                        <Route path="/routeDiscovery" element={<RouteDiscovery />} />
                        {/* Add more pages here */}
                    </Route>

                    {/*<Route element={<PrivateRoutes />}>*/}
                    {/*    */}
                    {/*</Route>*/}
                </Routes>
            </BrowserRouter>
        </React.StrictMode>
    </MantineProvider>
);
