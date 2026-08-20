# Sistema de Agendamento — Test-Drive & Revisão

Aplicação full stack para uma concessionária: o cliente agenda test-drives ou
revisões, e a equipe interna acompanha a agenda do dia em um painel administrativo.

Projetado como peça de portfólio para vagas de **Desenvolvedor Full Stack Júnior**,
cobrindo front-end, back-end, banco de dados, autenticação e um diferencial de IA.

## Stack

**Back-end**
- Java 17 + Spring Boot 3 (Web, Data JPA, Security, Validation)
- PostgreSQL + Flyway (versionamento de schema)
- Autenticação stateless com JWT (jjwt)

**Front-end**
- React 18 + TypeScript + Vite
- Tailwind CSS
- React Router + Axios (com interceptor de token)

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

## Próximos passos (evolução sugerida)

- Testes automatizados (JUnit no back-end, Testing Library no front) —
  reaproveitando sua experiência com QA
- Deploy: back-end em Railway/Render, front-end na Vercel
- Cancelamento de agendamento pelo cliente
- Notificação por e-mail/WhatsApp ao confirmar o agendamento
