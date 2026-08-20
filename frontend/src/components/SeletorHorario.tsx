interface Props {
  data: string; // yyyy-mm-dd
  horarioSelecionado: string | null;
  onSelecionar: (horaISO: string) => void;
}

const SLOTS_HORAS = [8, 9, 10, 11, 12, 13, 14, 15, 16, 17];

export default function SeletorHorario({ data, horarioSelecionado, onSelecionar }: Props) {
  return (
    <div className="grid grid-cols-5 gap-2">
      {SLOTS_HORAS.map((hora) => {
        const iso = `${data}T${String(hora).padStart(2, "0")}:00:00`;
        const selecionado = horarioSelecionado === iso;

        return (
          <button
            key={hora}
            type="button"
            onClick={() => onSelecionar(iso)}
            className={`rounded-md py-2 text-sm font-medium border transition ${
              selecionado
                ? "bg-brand text-white border-brand"
                : "bg-white text-gray-700 border-gray-300 hover:border-brand"
            }`}
          >
            {String(hora).padStart(2, "0")}:00
          </button>
        );
      })}
    </div>
  );
}
