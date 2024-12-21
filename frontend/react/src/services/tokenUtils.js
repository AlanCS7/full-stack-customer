import jwtDecode from "jwt-decode";

export function decodeToken(token) {
  try {
    return jwtDecode(token);
  } catch (error) {
    console.error("Token inválido:", error);
    return null;
  }
}

export function isTokenValid(token) {
  try {
    const decoded = jwtDecode(token);
    const currentTime = Date.now() / 1000;
    return decoded.exp > currentTime;
  } catch (error) {
    console.error("Erro ao verificar validade do token:", error);
    return false;
  }
}
