import { createContext, useContext, useEffect, useState } from "react";
import {
  getCustomerByEmail,
  login as performLogin,
} from "../../services/client";
import { decodeToken, isTokenValid } from "../../services/tokenUtils";

const getToken = () => localStorage.getItem("access_token");
const setToken = (token) => localStorage.setItem("access_token", token);
const removeToken = () => localStorage.removeItem("access_token");

const AuthContext = createContext({});

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);

  const loadUserFromToken = async () => {
    const token = getToken();
    if (token && isTokenValid(token)) {
      try {
        const decoded = decodeToken(token);
        const response = await getCustomerByEmail(decoded.sub);
        setUser({ name: response.data.name, roles: response.data.roles });
      } catch (error) {
        console.error("Error loading user from token:", error);
        logout();
      }
    }
  };

  useEffect(() => {
    loadUserFromToken();
  }, []);

  const login = async (credentials) => {
    try {
      const response = await performLogin(credentials);
      const token = response.data.token;
      setToken(token);
      await loadUserFromToken();
      return response;
    } catch (error) {
      console.error("Login error:", error);
      throw error;
    }
  };

  const logout = () => {
    removeToken();
    setUser(null);
  };

  const isCustomerAuthenticated = () => {
    const token = getToken();
    if (!token || !isTokenValid(token)) {
      logout();
      return false;
    }
    return true;
  };

  return (
    <AuthContext.Provider
      value={{
        login,
        logout,
        user,
        isCustomerAuthenticated,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
