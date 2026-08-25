# Sistema de Agendamento - Test-Drive & Revisao

[![CI](https://github.com/JeffrySmith117/Agendamento-Test---Driver/actions/workflows/ci.yml/badge.svg)](https://github.com/JeffrySmith117/Agendamento-Test---Driver/actions/workflows/ci.yml)

Aplicacao full stack para uma concessionaria: o cliente agenda test-drives ou
revisoes, e a equipe interna acompanha a agenda do dia em um painel administrativo.
Catalogo com a linha real de carros e motos Honda.

**Demo ao vivo:** https://agendamento-test-driver.vercel.app
> A API roda em um plano gratuito (Render) e "dorme" apos inatividade - a
> primeira requisicao do dia pode levar ate ~1 minuto para responder. As
> seguintes sao instantaneas.

Projetado como peca de portfolio para vagas de **Desenvolvedor Full Stack Junior**,
cobrindo front-end, back-end, banco de dados, autenticacao e um diferencial de IA.

## Stack

**Back-end**
- Java 17 + Spring Boot 3 (Web, Data JPA, Security, Validation)
- PostgreSQL + Flyway (versionamento de schema)
- Autenticacao stateless com JWT (jjwt)
- JUnit 5 + Mockito + AssertJ (testes unitarios das regras de negocio)

**Front-end**
- React 18 + TypeScript + Vite
- Tailwind CSS
- React Router + Axios (com interceptor de token)

**DevOps**
- Docker + Docker Compose (ambiente local com um comando)
- GitHub Actions (CI: build e testes a cada push)
- Deploy: back-end no Render, front-end na Vercel

## Funcionalidades

- Cadastro/login de cliente (JWT)
- Listagem de veiculos disponiveis para test-drive
- Agendamento de test-drive ou revisao, com validacao de horario comercial
  e regra de negocio que impede overbooking do mesmo veiculo/horario
- **Sugestao inteligente de horario**: se o horario pedido estiver ocupado,
  o backend sugere o proximo slot livre (ponto de extensao pronto para
  plugar um modelo de recomendacao/LLM no futuro - ex. via API Claude)
- Painel administrativo com a agenda do dia


## Seguranca

- Autenticacao stateless via JWT, com senhas hasheadas (BCrypt)
- Controle de acesso por role (RBAC): endpoints administrativos exigem
  role ADMIN, verificado via @PreAuthorize no nivel de metodo
- Durante uma revisao de seguranca, foi identificada e corrigida uma falha
  de controle de acesso (OWASP Top 10 - A01:2021) no endpoint de listagem
  de agendamentos por periodo, que nao restringia o acesso apenas a
  administradores
- CORS restrito a origens explicitamente permitidas via variavel de ambiente
## Metodologia - organizado em sprints

| Sprint | Entrega |
|---|---|
| 1 | Modelagem de entidades, banco (Flyway) e autenticacao JWT |
| 2 | CRUD de agendamento + regras de negocio (horario comercial, anti-overbooking) |
| 3 | Front-end: telas de login, agendamento e painel admin |
| 4 | Sugestao inteligente de horario + testes + documentacao |

## Como rodar

### Opcao A - Docker (recomendado, um comando so)
```bash
docker compose up --build
```
Isso sobe Postgres, back-end e front-end juntos:
- Front-end: `http://localhost:5173`
- API: `http://localhost:8080`
- Postgres: `localhost:5432` (usuario/senha `postgres`/`postgres`)

### Opcao B - rodando localmente sem Docker

**Back-end**
```bash
createdb agendamento_db
cd backend
./mvnw spring-boot:run
```

**Front-end**
```bash
cd frontend
npm install
npm run dev
```

## Testes

```bash
cd backend
./mvnw test
```
Cobre as regras de negocio de `AgendamentoService`: horario comercial, bloqueio
de domingos, anti-overbooking e a logica de sugestao de horario alternativo.

## Proximos passos (evolucao sugerida)

- Cancelamento de agendamento pelo cliente
- Notificacao por e-mail/WhatsApp ao confirmar o agendamento
- Testes de integracao do front-end (Testing Library)