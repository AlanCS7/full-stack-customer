import { createContext, useContext, useState, useEffect } from "react";
import { login as performLogin } from "../../services/client";

const AuthContext = createContext({});

const AuthProvider = ({ children }) => {
  const login = async (credentials) => {
    return new Promise((resolve, reject) => {
      performLogin(credentials)
        .then((response) => {
          const token = response.data.token;
          localStorage.setItem("access_token", token);
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  };

  return (
    <AuthContext.Provider value={{ login }}>{children}</AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

export default AuthProvider;
