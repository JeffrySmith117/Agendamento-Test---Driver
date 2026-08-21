CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    perfil VARCHAR(20) NOT NULL DEFAULT 'CLIENTE'
);

CREATE TABLE veiculos (
    id BIGSERIAL PRIMARY KEY,
    modelo VARCHAR(100) NOT NULL,
    versao VARCHAR(100) NOT NULL,
    placa VARCHAR(20),
    disponivel_para_test_drive BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE agendamentos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES usuarios(id),
    veiculo_id BIGINT NOT NULL REFERENCES veiculos(id),
    horario TIMESTAMP NOT NULL,
    tipo_servico VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADO',
    observacoes VARCHAR(500),
    UNIQUE (veiculo_id, horario)
);

-- alguns veiculos de exemplo para testar a aplicacao localmente
INSERT INTO veiculos (modelo, versao, placa, disponivel_para_test_drive) VALUES
    ('Honda City', 'EX CVT', NULL, TRUE),
    ('Honda HR-V', 'Touring', NULL, TRUE),
    ('Honda Civic', 'Sport', NULL, TRUE);
