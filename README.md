# Sistema de Agendamento — Test-Drive & Revisão

[![CI](https://github.com/JeffrySmith117/Agendamento-Test---Driver/actions/workflows/ci.yml/badge.svg)](https://github.com/JeffrySmith117/Agendamento-Test---Driver/actions/workflows/ci.yml)

Aplicação full stack para uma concessionária: o cliente agenda test-drives ou
revisões, e a equipe interna acompanha a agenda do dia em um painel administrativo.
Catálogo com a linha real de carros e motos Honda.

**🔗 Demo ao vivo:** https://agendamento-test-driver.vercel.app
> A API roda em um plano gratuito (Render) e "dorme" após inatividade — a
> primeira requisição do dia pode levar até ~1 minuto para responder. As
> seguintes são instantâneas.

Projetado como peça de portfólio para vagas de **Desenvolvedor Full Stack Júnior**,
cobrindo front-end, back-end, banco de dados, autenticação e um diferencial de IA.

## Stack

**Back-end**
- Java 17 + Spring Boot 3 (Web, Data JPA, Security, Validation)
- PostgreSQL + Flyway (versionamento de schema)
- Autenticação stateless com JWT (jjwt)
- JUnit 5 + Mockito + AssertJ (testes unitários das regras de negócio)

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
- Listagem de veículos disponíveis para test-drive
- Agendamento de test-drive ou revisão, com validação de horário comercial
  e regra de negócio que impede overbooking do mesmo veículo/horário
- **Sugestão inteligente de horário**: se o horário pedido estiver ocupado,
  o backend sugere o próximo slot livre (ponto de extensão pronto para
  plugar um modelo de recomendação/LLM no futuro — ex. via API Claude)
- Painel administrativo com a agenda do dia

## Metodologia — organizado em sprints

| Sprint | Entrega |
|---|---|
| 1 | Modelagem de entidades, banco (Flyway) e autenticação JWT |
| 2 | CRUD de agendamento + regras de negócio (horário comercial, anti-overbooking) |
| 3 | Front-end: telas de login, agendamento e painel admin |
| 4 | Sugestão inteligente de horário + testes + documentação |

## Como rodar

### Opção A — Docker (recomendado, um comando só)
```bash
docker compose up --build
```
Isso sobe Postgres, back-end e front-end juntos:
- Front-end: `http://localhost:5173`
- API: `http://localhost:8080`
- Postgres: `localhost:5432` (usuário/senha `postgres`/`postgres`)

O `docker-compose.yml` já cuida da ordem de inicialização (o backend só sobe
depois que o Postgres está saudável) e passa as credenciais via variáveis de
ambiente — não precisa editar nada.

### Opção B — rodando localmente sem Docker

**Back-end**
```bash
# criar o banco (Postgres precisa estar instalado e rodando)
createdb agendamento_db

cd backend
./mvnw spring-boot:run
```
A API sobe em `http://localhost:8080`. O `mvnw` (Maven Wrapper) baixa a versão
certa do Maven automaticamente na primeira execução — não precisa ter o Maven
instalado.

**Front-end**
```bash
cd frontend
npm install
npm run dev
```
A aplicação sobe em `http://localhost:5173`.

## Testes

```bash
cd backend
./mvnw test
```
Cobre as regras de negócio de `AgendamentoService`: horário comercial, bloqueio
de domingos, anti-overbooking e a lógica de sugestão de horário alternativo.

## Próximos passos (evolução sugerida)

- Cancelamento de agendamento pelo cliente
- Notificação por e-mail/WhatsApp ao confirmar o agendamento
- Testes de integração do front-end (Testing Library)