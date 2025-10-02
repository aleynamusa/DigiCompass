import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import App from "./App";
import LogIn from "./pages/LogIn.jsx";
import "./index.css";
import Home from "./pages/Home.jsx";
import PrivateRoutes from "./PrivateRoutes.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/signup" element={<App />} />
                <Route path="/login" element={<LogIn />} />
                <Route element={<PrivateRoutes/>}>
                    <Route path='/' element={<Home/>} />
                </Route>
            </Routes>
        </BrowserRouter>
    </React.StrictMode>
);
