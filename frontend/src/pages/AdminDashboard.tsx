import { useEffect, useState } from "react";
import { api } from "../api/client";
import type { Agendamento } from "../types";

function inicioFimDoDia(data: string) {
  return {
    inicio: `${data}T00:00:00`,
    fim: `${data}T23:59:59`,
  };
}

export default function AdminDashboard() {
  const [data, setData] = useState(new Date().toISOString().slice(0, 10));
  const [agendamentos, setAgendamentos] = useState<Agendamento[]>([]);
  const [carregando, setCarregando] = useState(false);

  useEffect(() => {
    carregar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [data]);

  function carregar() {
    setCarregando(true);
    const { inicio, fim } = inicioFimDoDia(data);
    api
      .get<Agendamento[]>("/agendamentos", { params: { inicio, fim } })
      .then((r) => setAgendamentos(r.data))
      .finally(() => setCarregando(false));
  }

  return (
    <div className="max-w-4xl mx-auto p-6 space-y-6">
      <header className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-brand">Painel — Agenda do dia</h1>
        <input
          type="date"
          className="border rounded-md px-3 py-2"
          value={data}
          onChange={(e) => setData(e.target.value)}
        />
      </header>

      {carregando && <p className="text-sm text-gray-500">Carregando...</p>}

      <div className="bg-white rounded-xl shadow-sm divide-y">
        {agendamentos.length === 0 && !carregando && (
          <p className="p-4 text-sm text-gray-500">Nenhum agendamento para esta data.</p>
        )}

        {agendamentos
          .sort((a, b) => a.horario.localeCompare(b.horario))
          .map((a) => (
            <div key={a.id} className="p-4 flex items-center justify-between text-sm">
              <div>
                <p className="font-medium">
                  {new Date(a.horario).toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" })}
                  {"  —  "}
                  {a.clienteNome}
                </p>
                <p className="text-gray-500">
                  {a.veiculoModelo} • {a.tipoServico === "TEST_DRIVE" ? "Test-Drive" : "Revisão"}
                </p>
              </div>
              <span className="text-xs font-semibold uppercase text-brand">{a.status}</span>
            </div>
          ))}
      </div>
    </div>
  );
}
