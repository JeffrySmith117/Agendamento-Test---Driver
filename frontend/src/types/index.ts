export type TipoServico = "TEST_DRIVE" | "REVISAO";
export type StatusAgendamento = "CONFIRMADO" | "CANCELADO" | "CONCLUIDO";

export interface Veiculo {
  id: number;
  modelo: string;
  versao: string;
  disponivelParaTestDrive: boolean;
}

export interface Agendamento {
  id: number;
  clienteNome: string;
  veiculoModelo: string;
  horario: string; // ISO datetime
  tipoServico: TipoServico;
  status: StatusAgendamento;
}

export interface TokenResponse {
  token: string;
  nome: string;
  perfil: "CLIENTE" | "ADMIN";
}

export interface HorarioSugerido {
  horarioSugerido: string;
  motivo: string;
}
