import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const ProtectedRoute = ({ children }) => {
  const { isCustomerAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isCustomerAuthenticated()) {
      logout();
      navigate("/");
    }
  }, [isCustomerAuthenticated, logout, navigate]);

  return isCustomerAuthenticated() ? children : "";
};

export default ProtectedRoute;
