# Desafio Backend - Requisitos

## Configuração do Ambiente Local

### Pré-requisitos
- Java 17
- PostgreSQL 15+
- Redis
- Maven ou IntelliJ IDEA

### Configuração do Banco de Dados
1. Certifique-se de que o PostgreSQL está rodando na porta 5432
2. Execute o script `create_database.sql` para criar o banco de dados:
   ```bash
   psql -U postgres -f create_database.sql
   ```

### Configuração da Aplicação
1. Configure as variáveis de ambiente no arquivo `application-dev.properties`
2. O Flyway irá criar e atualizar automaticamente as tabelas na primeira execução

### Executando a Aplicação
1. No IntelliJ IDEA:
   - Importe o projeto como um projeto Maven
   - Aguarde o download das dependências
   - Configure o perfil ativo como "dev" nas configurações de execução
   - Execute a classe `ProductApplication`

2. Via linha de comando:
   ```bash
   ./mvnw spring-boot:run -Dspring.profiles.active=dev
   ```

### Acessando a Aplicação
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/api/swagger-ui.html

### Usuário Admin Padrão
- Email: contato@simplesdental.com
- Senha: KMbT%5wT*R!46i@@YHqx

## 1. Validações

Você deve ajustar as entidades (model e sql) de acordo com as regras abaixo: 

- `Product.name` é obrigatório, não pode ser vazio e deve ter no máximo 100 caracteres.
- `Product.description` é opcional e pode ter no máximo 255 caracteres.
- `Product.price` é obrigatório deve ser > 0.
- `Product.status` é obrigatório.
- `Product.category` é obrigatório.
- `Category.name` deve ter no máximo 100 caracteres.
- `Category.description` é opcional e pode ter no máximo 255 caracteres.

## 2. Otimização de Performance
- Analisar consultas para identificar possíveis gargalos.
- Utilizar índices e restrições de unicidade quando necessário.
- Implementar paginação nos endpoints para garantir a escala conforme o volume de dados crescer.
- Utilizar cache com `Redis` para o endpoint `/auth/context`, garantindo que a invalidação seja feita em caso de alteração dos dados.

## 3. Logging
- Registrar logs em arquivos utilizando um formato estruturado (ex.: JSON).
- Implementar níveis de log: DEBUG, INFO, WARNING, ERROR, CRITICAL.
- Utilizar logging assíncrono.
- Definir estratégias de retenção e compressão dos logs.

## 4. Refatoração
- Atualizar a entidade `Product`:
  - Alterar o atributo `code` para o tipo inteiro.
- Versionamento da API:
  - Manter o endpoint atual (v1) em `/api/products` com os códigos iniciados por `PROD-`.
  - Criar uma nova versão (v2) em `/api/v2/products` onde `code` é inteiro.

## 5. Integração com Swagger
- Documentar todos os endpoints com:
  - Descrições detalhadas.
  - Exemplos de JSON para requisições e respostas.
  - Listagem de códigos HTTP e mensagens de erro.

## 6. Autenticação e Gerenciamento de Usuários
- Criar a tabela `users` com as colunas:
  - `id` (chave primária com incremento automático)
  - `name` (obrigatório)
  - `email` (obrigatório, único e com formato válido)
  - `password` (obrigatório)
  - `role` (obrigatório e com valores permitidos: `admin` ou `user`)
- Inserir um usuário admin inicial:
  - Email: `contato@simplesdental.com`
  - Password: `KMbT%5wT*R!46i@@YHqx`
- Endpoints:
  - `POST /auth/login` - Realiza login.
  - `POST /auth/register` - Registra novos usuários (se permitido).
  - `GET /auth/context` - Retorna `id`, `email` e `role` do usuário autenticado.
  - `PUT /users/password` - Atualiza a senha do usuário autenticado.

## 7. Permissões e Controle de Acesso
- Usuários com `role` admin podem criar, alterar, consultar e excluir produtos, categorias e outros usuários.
- Usuários com `role` user podem:
  - Consultar produtos e categorias.
  - Atualizar apenas sua própria senha.
  - Não acessar ou alterar dados de outros usuários.

## 8. Testes
- Desenvolver testes unitários para os módulos de autenticação, autorização e operações CRUD.

## Perfis da Aplicação

A aplicação possui dois perfis principais:

### Perfil de Desenvolvimento (dev)

```properties
spring.profiles.active=dev
```

Características:
- Logs mais detalhados (nível DEBUG)
- Swagger UI habilitado
- Estatísticas do Hibernate ativadas
- Validação do Flyway desativada para facilitar o desenvolvimento
- Todos os endpoints do Actuator expostos

### Perfil de Produção (prod)

```properties
spring.profiles.active=prod
```

Características:
- Logs mais enxutos (nível INFO)
- Swagger UI desabilitado
- Estatísticas do Hibernate desativadas
- Validação rigorosa do Flyway
- Apenas endpoints essenciais do Actuator expostos
- Secrets via variáveis de ambiente

## Variáveis de Ambiente

Copie o arquivo `.env.example` para `.env` e configure as variáveis:

```bash
cp .env.example .env
```

### Variáveis Essenciais para Produção

```properties
# Database
DB_HOST=seu_host
DB_PORT=5432
DB_NAME=simplesdental
DB_USER=seu_usuario
DB_PASSWORD=sua_senha

# Redis
REDIS_HOST=seu_redis_host
REDIS_PORT=6379
REDIS_PASSWORD=sua_senha_redis

# JWT
JWT_SECRET_KEY=sua_chave_secreta
JWT_EXPIRATION=86400000
```

### Executando com Docker

Desenvolvimento:
```bash
docker-compose up -d
```

Produção:
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

---

# Perguntas

1. **Se tivesse a oportunidade de criar o projeto do zero ou refatorar o projeto atual, qual arquitetura você utilizaria e por quê?**
2. **Qual é a melhor estratégia para garantir a escalabilidade do código mantendo o projeto organizado?**  
3. **Quais estratégias poderiam ser utilizadas para implementar multitenancy no projeto?**
4. **Como garantir a resiliência e alta disponibilidade da API durante picos de tráfego e falhas de componentes?**
5. **Quais práticas de segurança essenciais você implementaria para prevenir vulnerabilidades como injeção de SQL e XSS?**
5. **Qual a abordagem mais eficaz para estruturar o tratamento de exceções de negócio, garantindo um fluxo contínuo desde sua ocorrência até o retorno da API?**
5. **Considerando uma aplicação composta por múltiplos serviços, quais componentes você considera essenciais para assegurar sua robustez e eficiência?**
6. **Como você estruturaria uma pipeline de CI/CD para automação de testes e deploy, assegurando entregas contínuas e confiáveis?**

Obs: Forneça apenas respostas textuais; não é necessário implementar as perguntas acima.

# Respostas

1. **Se tivesse a oportunidade de criar o projeto do zero ou refatorar o projeto atual, qual arquitetura você utilizaria e por quê?**

Adotaria uma arquitetura baseada em microsserviços com:
- Domain-Driven Design (DDD) para organização em bounded contexts
- Clean Architecture para separação clara de responsabilidades
- Event-Driven com Apache Kafka para comunicação assíncrona
- API Gateway (Spring Cloud Gateway) para roteamento e segurança
- Service Discovery (Eureka) para registro e descoberta
- Circuit Breaker (Resilience4j) para tolerância a falhas

Esta arquitetura ofereceria melhor escalabilidade, manutenibilidade e resiliência.

2. **Qual é a melhor estratégia para garantir a escalabilidade do código mantendo o projeto organizado?**

- Modularização em componentes coesos e independentes
- Princípios SOLID rigorosamente aplicados
- Feature Flags para controle gradual de funcionalidades
- Testes automatizados com alta cobertura
- Documentação como código
- Padrões de projeto adequados
- Convenções de código consistentes

3. **Quais estratégias poderiam ser utilizadas para implementar multitenancy no projeto?**

- Database per Tenant: banco dedicado por cliente
- Schema per Tenant: schemas separados no mesmo banco
- Discriminator Column: tenant_id em todas as tabelas
- Hibernate Filter para filtro automático
- JWT com identificação do tenant
- Cache isolado por tenant
- Contexto de segurança por tenant

4. **Como garantir a resiliência e alta disponibilidade da API durante picos de tráfego e falhas de componentes?**

- Auto Scaling baseado em métricas
- Load Balancing entre instâncias
- Circuit Breaker para prevenir falhas em cascata
- Rate Limiting por cliente
- Redis para cache distribuído
- Retry Policies para operações falhas
- Monitoramento com Prometheus + Grafana
- Health Checks dos componentes

5. **Quais práticas de segurança essenciais você implementaria para prevenir vulnerabilidades como injeção de SQL e XSS?**

- Prepared Statements contra SQL Injection
- Input Validation rigorosa
- Output Encoding
- Content Security Policy
- HTTPS em toda comunicação
- JWT com JWE (tokens criptografados)
- Audit Logging de ações sensíveis
- Secrets Management seguro
- Security Headers HTTP

6. **Qual a abordagem mais eficaz para estruturar o tratamento de exceções de negócio?**

- Hierarquia clara de exceções
- Global Exception Handler centralizado
- Formato padronizado de respostas de erro
- Contexto completo nos logs
- Gerenciamento automático de transações
- Códigos de erro padronizados
- Validações contextuais com grupos

7. **Componentes essenciais para uma aplicação com múltiplos serviços?**

- API Gateway como ponto único de entrada
- Service Registry para descoberta
- Config Server centralizado
- Message Broker para comunicação assíncrona
- Distributed Tracing para rastreamento
- Stack de monitoramento completa
- Agregação centralizada de logs
- Pipeline CI/CD automatizada

8. **Como estruturar uma pipeline de CI/CD?**

Stages:
1. Build e Testes Unitários
2. Análise de Código (SonarQube)
3. Testes de Integração
4. Build de Imagem Docker
5. Deploy em Homologação
6. Testes E2E
7. Deploy em Produção

Ferramentas:
- GitHub Actions/Jenkins
- SonarQube
- Docker Registry
- Kubernetes
- ArgoCD
- Terraform

Práticas:
- Feature Branches
- Pull Request Reviews
- Semantic Versioning
- Blue/Green Deployment
- Canary Releases

## 9. Setup Local do Projeto

### 9.1 Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

- Java JDK 17 ou superior
- PostgreSQL 12 ou superior
- Redis 6 ou superior
- Git
- IDE de sua preferência (recomendamos IntelliJ IDEA ou VS Code)

### 9.2 Configuração do Banco de Dados

1. Crie um banco de dados PostgreSQL:
```sql
CREATE DATABASE simplesdental;
```

2. O usuário padrão é `postgres` com senha `2599`. Se você precisar usar credenciais diferentes, atualize o arquivo `application-dev.properties`.

### 9.3 Configuração do Redis

1. Instale o Redis:
   - Windows: Baixe o Redis através do [GitHub](https://github.com/microsoftarchive/redis/releases)
   - Linux: `sudo apt-get install redis-server`
   - macOS: `brew install redis`

2. Inicie o servidor Redis:
   - Windows: Execute o redis-server.exe
   - Linux/macOS: `redis-server`

### 9.4 Clonando e Executando o Projeto

1. Clone o repositório:
```bash
git clone <url-do-repositorio>
cd desafio-backend
```

2. Execute o projeto:
```bash
./mvnw spring-boot:run
```

3. O servidor iniciará em `http://localhost:8080/api`

### 9.5 Testando a API

1. Acesse a documentação Swagger:
```
http://localhost:8080/api/swagger-ui.html
```

2. Use o usuário admin padrão para fazer login:
```json
{
  "email": "contato@simplesdental.com",
  "password": "KMbT%5wT*R!46i@@YHqx"
}
```

3. Copie o token JWT retornado e use-o no botão "Authorize" do Swagger para testar os outros endpoints.

### 9.6 Problemas Comuns

1. **Erro de conexão com PostgreSQL**:
   - Verifique se o serviço está rodando
   - Confirme as credenciais em `application-dev.properties`
   - Certifique-se que o banco `simplesdental` foi criado

2. **Erro de conexão com Redis**:
   - Verifique se o servidor Redis está rodando
   - Confirme se a porta 6379 está disponível
   - Teste a conexão com `redis-cli ping`

3. **Erro de porta em uso**:
   - Verifique se a porta 8080 está livre
   - Altere a porta em `application-dev.properties` se necessário

### 9.7 Executando os Testes

```bash
./mvnw test
```

Para ver o relatório de cobertura:
```bash
./mvnw verify
```
O relatório será gerado em: `target/site/jacoco/index.html`
