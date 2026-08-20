import axios from "axios";

export const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

// injeta o token JWT salvo no login em toda requisição autenticada
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// se o token expirar/for inválido, desloga e manda pra tela de login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);
