import { Navigate, Outlet } from 'react-router-dom'

const PrivateRoutes = () => {
    const isAuthenticated = localStorage.getItem("token"); // or use context/state

    return isAuthenticated ? children : <Navigate to="/login" />;

}

export default PrivateRoutes;