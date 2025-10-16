import { Navigate, Outlet } from "react-router-dom";
import Cookies from "js-cookie";

const PrivateRoutes = () => {
    const token = Cookies.get("auth");
    return token ? <Outlet /> : <Navigate to="/login" />;
};

export default PrivateRoutes;
