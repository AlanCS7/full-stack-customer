import { createContext, useContext, useState, useEffect } from "react";
import { login as performLogin } from "../../services/client";
import { decodeToken, isTokenValid } from "../../services/tokenUtils";

const AuthContext = createContext({});

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);

  const loadUserFromToken = () => {
    const token = localStorage.getItem("access_token");
    if (token && isTokenValid(token)) {
      const decoded = decodeToken(token);
      setUser({ email: decoded.sub });
    } else {
      setUser(null);
    }
  };

  useEffect(() => {
    loadUserFromToken();
  }, []);

  const login = async (credentials) => {
    return new Promise((resolve, reject) => {
      performLogin(credentials)
        .then((response) => {
          const token = response.data.token;
          localStorage.setItem("access_token", token);
          loadUserFromToken();
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  };

  const logout = () => {
    localStorage.removeItem("access_token");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ login, logout, user }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
