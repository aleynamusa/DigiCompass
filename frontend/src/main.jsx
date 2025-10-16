import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import LogIn from "./pages/LogIn.jsx";
import "./index.css";
import Home from "./pages/Home.jsx";
import PrivateRoutes from "./PrivateRoutes.jsx";
import RoutePopUp from "./components/route_popup.jsx";
import RouteDiscovery from "./pages/RouteDiscovery.jsx";
import ResetPassword from "./pages/ResetPassword.jsx";
import ForgotPassword from "./pages/ForgotPassword.jsx";
import SignUpForm from "@/pages/SignUp.jsx";
import './index.css';
import './App.css'


ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/signup" element={<SignUpForm />} />
                <Route path="/login" element={<LogIn />} />
                <Route element={<PrivateRoutes/>}>
                    <Route path='/' element={<Home/>} />
                </Route>
                <Route path="/route" element={<RoutePopUp/>}></Route>
                <Route path="/routeDiscovery" element={<RouteDiscovery/>}></Route>
                <Route path="/forgot-password" element={<ForgotPassword />} />
                <Route path="/reset-password" element={<ResetPassword />} />
            </Routes>
        </BrowserRouter>
    </React.StrictMode>
);
