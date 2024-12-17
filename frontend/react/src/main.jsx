import { ChakraProvider, createStandaloneToast } from "@chakra-ui/react";

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import App from "./App.jsx";
import AuthProvider from "./components/context/AuthContext.jsx";
import Login from "./components/login/Login.jsx";
import "./index.css";

const { ToastContainer } = createStandaloneToast();

const router = createBrowserRouter([
  {
    path: "/",
    element: <Login />,
  },
  {
    path: "dashboard",
    element: <App />,
  },
]);

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <ChakraProvider>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
      <ToastContainer />
    </ChakraProvider>
  </StrictMode>
);
