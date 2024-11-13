import { ChakraProvider, createStandaloneToast } from "@chakra-ui/react";

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.jsx";
import "./index.css";

const { ToastContainer } = createStandaloneToast();

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <ChakraProvider>
      <App />
      <ToastContainer />
    </ChakraProvider>
  </StrictMode>
);
