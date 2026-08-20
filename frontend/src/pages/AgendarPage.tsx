import { useEffect, useState } from "react";
import { api } from "../api/client";
import SeletorHorario from "../components/SeletorHorario";
import type { Agendamento, HorarioSugerido, TipoServico, Veiculo } from "../types";

const hoje = new Date().toISOString().slice(0, 10);

export default function AgendarPage() {
  const [veiculos, setVeiculos] = useState<Veiculo[]>([]);
  const [meusAgendamentos, setMeusAgendamentos] = useState<Agendamento[]>([]);

  const [veiculoId, setVeiculoId] = useState<number | null>(null);
  const [data, setData] = useState(hoje);
  const [horario, setHorario] = useState<string | null>(null);
  const [tipoServico, setTipoServico] = useState<TipoServico>("TEST_DRIVE");

  const [sugestao, setSugestao] = useState<HorarioSugerido | null>(null);
  const [mensagem, setMensagem] = useState<{ tipo: "erro" | "sucesso"; texto: string } | null>(null);
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    api.get<Veiculo[]>("/veiculos/disponiveis").then((r) => setVeiculos(r.data));
    carregarMeusAgendamentos();
  }, []);

  function carregarMeusAgendamentos() {
    api.get<Agendamento[]>("/agendamentos/meus").then((r) => setMeusAgendamentos(r.data));
  }

  // "diferencial IA": pede ao backend o horário livre mais próximo da preferência do cliente
  async function buscarSugestao() {
    if (!veiculoId || !horario) return;
    const { data: sugestaoResp } = await api.get<HorarioSugerido>("/agendamentos/sugestao", {
      params: { veiculoId, preferencia: horario },
    });
    setSugestao(sugestaoResp);
  }

  async function handleAgendar() {
    if (!veiculoId || !horario) {
      setMensagem({ tipo: "erro", texto: "Selecione o veículo e o horário." });
      return;
    }

    setEnviando(true);
    setMensagem(null);

    try {
      await api.post("/agendamentos", {
        veiculoId,
        horario,
        tipoServico,
      });
      setMensagem({ tipo: "sucesso", texto: "Agendamento confirmado com sucesso!" });
      setSugestao(null);
      setHorario(null);
      carregarMeusAgendamentos();
    } catch (err: any) {
      setMensagem({ tipo: "erro", texto: err.response?.data?.erro ?? "Erro ao agendar." });
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="max-w-3xl mx-auto p-6 space-y-8">
      <header>
        <h1 className="text-2xl font-bold text-brand">Agendar Test-Drive ou Revisão</h1>
        <p className="text-gray-500 text-sm">Olá, {localStorage.getItem("nome")}!</p>
      </header>

      <section className="bg-white rounded-xl shadow-sm p-6 space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="text-sm font-medium">Tipo de serviço</label>
            <select
              className="w-full border rounded-md px-3 py-2 mt-1"
              value={tipoServico}
              onChange={(e) => setTipoServico(e.target.value as TipoServico)}
            >
              <option value="TEST_DRIVE">Test-Drive</option>
              <option value="REVISAO">Revisão</option>
            </select>
          </div>

          <div>
            <label className="text-sm font-medium">Veículo</label>
            <select
              className="w-full border rounded-md px-3 py-2 mt-1"
              value={veiculoId ?? ""}
              onChange={(e) => setVeiculoId(Number(e.target.value))}
            >
              <option value="" disabled>Selecione...</option>
              {veiculos.map((v) => (
                <option key={v.id} value={v.id}>{v.modelo} {v.versao}</option>
              ))}
            </select>
          </div>
        </div>

        <div>
          <label className="text-sm font-medium">Data</label>
          <input
            type="date"
            className="w-full border rounded-md px-3 py-2 mt-1"
            value={data}
            min={hoje}
            onChange={(e) => { setData(e.target.value); setHorario(null); setSugestao(null); }}
          />
        </div>

        <div>
          <label className="text-sm font-medium block mb-2">Horário</label>
          <SeletorHorario data={data} horarioSelecionado={horario} onSelecionar={setHorario} />
        </div>

        {veiculoId && horario && (
          <button
            type="button"
            onClick={buscarSugestao}
            className="text-sm text-brand underline"
          >
            Verificar disponibilidade / sugerir horário alternativo
          </button>
        )}

        {sugestao && (
          <div className="text-sm bg-amber-50 border border-amber-200 rounded-md p-3">
            Sugestão: <strong>{new Date(sugestao.horarioSugerido).toLocaleString("pt-BR")}</strong>
            <br />
            <span className="text-gray-500">{sugestao.motivo}</span>
          </div>
        )}

        {mensagem && (
          <p className={mensagem.tipo === "erro" ? "text-red-600 text-sm" : "text-green-600 text-sm"}>
            {mensagem.texto}
          </p>
        )}

        <button
          onClick={handleAgendar}
          disabled={enviando}
          className="w-full bg-brand hover:bg-brand-dark text-white rounded-md py-2.5 font-semibold disabled:opacity-60"
        >
          {enviando ? "Agendando..." : "Confirmar agendamento"}
        </button>
      </section>

      <section>
        <h2 className="text-lg font-semibold mb-3">Meus agendamentos</h2>
        <div className="space-y-2">
          {meusAgendamentos.length === 0 && (
            <p className="text-sm text-gray-500">Você ainda não tem agendamentos.</p>
          )}
          {meusAgendamentos.map((a) => (
            <div key={a.id} className="bg-white rounded-lg shadow-sm p-4 flex justify-between text-sm">
              <div>
                <p className="font-medium">{a.veiculoModelo} — {a.tipoServico === "TEST_DRIVE" ? "Test-Drive" : "Revisão"}</p>
                <p className="text-gray-500">{new Date(a.horario).toLocaleString("pt-BR")}</p>
              </div>
              <span className="self-center text-xs font-semibold uppercase text-brand">{a.status}</span>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
