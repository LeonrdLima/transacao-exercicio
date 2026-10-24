# API de Transações Financeiras (Quarkus + REST)

Projeto didático de uma API REST para gerenciamento de **transações
financeiras**, desenvolvida com Quarkus. A API permite listar,
consultar, criar, atualizar e remover transações, com os dados
persistidos em PostgreSQL.

## Sobre a API

A API trabalha com uma única entidade principal: `Transacao`.

Cada transação possui os seguintes atributos:

  -----------------------------------------------------------------------
  Atributo                   Tipo            Descrição
  -------------------------- --------------- ----------------------------
  `id`                       `Long`          Identificador gerado pelo
                                             banco de dados.

  `descricao`                `String`        Descrição da transação.

  `tipo`                     `String`        Tipo da transação, por
                                             exemplo, receita ou despesa,
                                             conforme os valores adotados
                                             pelo projeto.

  `categoria`                `String`        Categoria da transação.

  `valor`                    `double`        Valor financeiro da
                                             transação.

  `data`                     `LocalDate`     Data da transação,
                                             representada como
                                             `AAAA-MM-DD` no JSON.
  -----------------------------------------------------------------------

Na entidade JPA, os campos `descricao`, `tipo`, `categoria`, `valor` e
`data` estão anotados com `@Column(nullable = false)`. As validações da
entrada também dependem das regras definidas em `TransacaoRequest`.

## Tecnologias e componentes

-   **Java** --- linguagem de programação.
-   **Quarkus** --- framework da aplicação.
-   **Jakarta REST (JAX-RS)** --- disponibilização dos endpoints HTTP.
-   **Jakarta Persistence (JPA) / Hibernate** --- mapeamento da entidade
    para o banco de dados.
-   **PostgreSQL** --- banco de dados relacional.
-   **Docker** --- execução do PostgreSQL em container, conforme a
    configuração do projeto.
-   **JSON** --- formato de entrada e saída da API.

Componentes identificados no código:

-   `modelo/Transacao`: entidade JPA que representa uma transação.
-   `rest/TransacaoResource`: define as rotas HTTP.
-   `servico/TransacaoService`: concentra as operações chamadas pelo
    recurso.
-   `rest/dto/TransacaoRequest`: objeto usado para receber os dados
    enviados nas requisições.

## Pré-requisito: Docker

Se o projeto utiliza o `docker-compose.yml` para executar o PostgreSQL,
instale e inicie o Docker antes de subir o banco.

-   Windows/macOS: [Docker
    Desktop](https://docs.docker.com/get-started/get-docker/).
-   Linux: [instalação do Docker
    Engine](https://docs.docker.com/engine/install/).

Confira se o Docker está funcionando:

``` bash
docker --version
docker compose version
docker run --rm hello-world
```

## Como rodar

Na pasta do projeto, se os arquivos de configuração mantiverem o fluxo
original com Docker Compose:

``` bash
# 1. Inicia o PostgreSQL em segundo plano
docker compose up -d

# 2. Inicia a aplicação em modo de desenvolvimento
./mvnw quarkus:dev

# 3. Executa os testes automatizados
./mvnw test
```

No Windows, pode ser necessário usar `mvnw.cmd` no lugar de `./mvnw`,
conforme o terminal utilizado.

### Endereços úteis

-   API: `http://localhost:8080/transacoes`
-   Swagger UI: `http://localhost:8080/q/swagger-ui`
-   Dev UI: `http://localhost:8080/q/dev-ui`

Os endereços de Swagger UI e Dev UI dependem de essas extensões estarem
habilitadas no projeto.

## O banco de dados

A entidade `Transacao` é mapeada pelo JPA e representa os dados
persistidos no banco. O identificador usa
`@GeneratedValue(strategy = GenerationType.IDENTITY)`, permitindo que o
banco gere o ID.

Os parâmetros efetivos de conexão --- host, porta, nome do banco,
usuário e senha --- são definidos nos arquivos de configuração, como
`docker-compose.yml` e `application.properties`. Confira esses arquivos
antes de usar os comandos abaixo.

### Comandos úteis do Docker

``` bash
docker compose ps
docker compose logs -f
docker compose stop
docker compose down
```

`docker compose down -v` também remove os volumes associados e pode
apagar os dados persistidos. Use-o somente se quiser remover esses
dados.

Se o projeto usar o nome de container, banco e usuário `lms` da
configuração original, o acesso ao PostgreSQL pode ser feito com:

``` bash
docker exec -it lms-postgres psql -U lms -d lms
```

Dentro do `psql`, use `\dt` para listar as tabelas e `\q` para sair. O
nome do container e as credenciais devem ser confirmados no
`docker-compose.yml`.

## Endpoints

O caminho-base do recurso é `/transacoes`. Os métodos disponíveis na
classe `TransacaoResource` são:

  -----------------------------------------------------------------------------
  Método          Caminho              O que faz             Sucesso
  --------------- -------------------- --------------------- ------------------
  `GET`           `/transacoes`        Lista todas as        `200 OK`
                                       transações.           

  `GET`           `/transacoes/{id}`   Busca uma transação   `200 OK`
                                       pelo ID.              

  `POST`          `/transacoes`        Cria uma transação.   `201 Created` +
                                                             cabeçalho
                                                             `Location`

  `PUT`           `/transacoes/{id}`   Atualiza uma          `200 OK`
                                       transação existente.  

`DELETE`        `/transacoes/{id}`   Remove uma transação. `204 No Content`

### 1. Listar transações

``` http
GET /transacoes
```

Retorna uma lista de transações em JSON.

### 2. Buscar uma transação por ID

``` http
GET /transacoes/1
```

Substitua `1` pelo identificador desejado. Se a transação for
encontrada, a resposta contém o objeto correspondente. O comportamento
para um ID inexistente depende do tratamento de exceções implementado na
aplicação.

### 3. Criar uma transação

``` http
POST /transacoes
Content-Type: application/json
```

Corpo da requisição:

``` json
{
  "descricao": "Compra no supermercado",
  "tipo": "Despesa",
  "categoria": "Alimentação",
  "valor": 150.75,
  "data": "2026-10-20"
}
```

O exemplo não envia `id`, pois ele é gerado pelo banco de dados. Quando
a criação é concluída, o recurso retorna `201 Created`, o objeto criado
no corpo da resposta e o cabeçalho `Location` apontando para a nova
transação.

### 4. Atualizar uma transação

``` http
PUT /transacoes/1
Content-Type: application/json
```

Corpo da requisição:

``` json
{
  "descricao": "Compra no supermercado",
  "tipo": "Despesa",
  "categoria": "Alimentação",
  "valor": 175.90,
  "data": "2026-10-21"
}
```

O ID é informado no caminho e os dados atualizados são enviados no
corpo. Envie todos os campos esperados pelo `TransacaoRequest`, pois o
endpoint disponível é `PUT` e não `PATCH`.

### 5. Excluir uma transação

``` http
DELETE /transacoes/1
```

Substitua `1` pelo ID da transação. Em caso de sucesso, a API retorna
`204 No Content`, sem corpo na resposta.

### Exemplo de resposta JSON

``` json
{
  "id": 1,
  "descricao": "Compra no supermercado",
  "tipo": "Despesa",
  "categoria": "Alimentação",
  "valor": 150.75,
  "data": "2026-10-20"
}
```

Os valores são apenas ilustrativos.

## Códigos de status HTTP

  --------------------------------------------------------------------------
  Código                         Significado
  ------------------------------ -------------------------------------------
  `200 OK`                       Consulta ou atualização concluída com
                                 sucesso.

  `201 Created`                  Transação criada; o cabeçalho `Location`
                                 aponta para o recurso criado.

  `204 No Content`               Transação removida com sucesso, sem corpo
                                 na resposta.

  `400 Bad Request`              Requisição inválida ou falha de validação,
                                 conforme as regras configuradas.

  `404 Not Found`                Transação não encontrada, se esse caso for
                                 tratado pela camada de serviço e pelo
                                 mapeamento de exceções.

  `409 Conflict`                 Possível conflito de negócio, somente se
                                 houver regra correspondente implementada.

  `415 Unsupported Media Type`   Tipo de conteúdo não suportado; o recurso
                                 espera JSON.
  --------------------------------------------------------------------------

Os códigos de erro efetivamente retornados dependem das validações,
exceções e mapeadores configurados no restante do projeto.

## Estrutura do código

Com base nos arquivos fornecidos, o fluxo principal é:

``` text
TransacaoResource
       |
       v
TransacaoService
       |
       v
Persistência / banco de dados
```

-   **Resource:** recebe as requisições HTTP e define os endpoints.
-   **Service:** executa as operações solicitadas pelo resource.
-   **Entidade `Transacao`:** representa os dados da transação e o
    mapeamento JPA.
-   **DTO `TransacaoRequest`:** representa os dados recebidos nas
    requisições.

Os nomes e a organização de outros pacotes, como repositórios, exceções
e validadores, devem ser confirmados na estrutura atual do projeto.

## Observações

-   A API consome e produz `application/json`.
-   A data usa `LocalDate` e é representada nos exemplos como
    `AAAA-MM-DD`.
-   O `id` é gerado pelo banco de dados.
-   A classe `TransacaoResource` disponibiliza `GET`, `POST`, `PUT` e
    `DELETE`; não há endpoint `PATCH` nessa classe.
-   As instruções exatas de inicialização do banco e as credenciais
    dependem dos arquivos de configuração do projeto.
