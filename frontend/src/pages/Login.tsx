import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/client";
import type { TokenResponse } from "../types";

export default function Login() {
  const [modo, setModo] = useState<"login" | "cadastro">("login");
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [telefone, setTelefone] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(false);

  const navigate = useNavigate();

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setErro(null);
    setCarregando(true);

    try {
      const endpoint = modo === "login" ? "/auth/login" : "/auth/registrar";
      const payload =
        modo === "login" ? { email, senha } : { nome, email, senha, telefone };

      const { data } = await api.post<TokenResponse>(endpoint, payload);

      localStorage.setItem("token", data.token);
      localStorage.setItem("nome", data.nome);
      localStorage.setItem("perfil", data.perfil);

      navigate(data.perfil === "ADMIN" ? "/admin" : "/agendar");
    } catch (err: any) {
      setErro(err.response?.data?.erro ?? "Não foi possível concluir. Tente novamente.");
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-sm bg-white shadow-md rounded-xl p-8 space-y-4"
      >
        <h1 className="text-2xl font-bold text-brand">
          {modo === "login" ? "Entrar" : "Criar conta"}
        </h1>

        {modo === "cadastro" && (
          <>
            <input
              className="w-full border rounded-md px-3 py-2"
              placeholder="Nome completo"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              required
            />
            <input
              className="w-full border rounded-md px-3 py-2"
              placeholder="Telefone"
              value={telefone}
              onChange={(e) => setTelefone(e.target.value)}
              required
            />
          </>
        )}

        <input
          type="email"
          className="w-full border rounded-md px-3 py-2"
          placeholder="E-mail"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          className="w-full border rounded-md px-3 py-2"
          placeholder="Senha"
          value={senha}
          onChange={(e) => setSenha(e.target.value)}
          required
        />

        {erro && <p className="text-red-600 text-sm">{erro}</p>}

        <button
          type="submit"
          disabled={carregando}
          className="w-full bg-brand hover:bg-brand-dark text-white rounded-md py-2 font-semibold disabled:opacity-60"
        >
          {carregando ? "Aguarde..." : modo === "login" ? "Entrar" : "Cadastrar"}
        </button>

        <button
          type="button"
          className="w-full text-sm text-gray-500 underline"
          onClick={() => setModo(modo === "login" ? "cadastro" : "login")}
        >
          {modo === "login" ? "Não tem conta? Cadastre-se" : "Já tem conta? Entrar"}
        </button>
      </form>
    </div>
  );
}
