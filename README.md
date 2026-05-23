# Testes Automatizados — Carrinhos API

Testes automatizados de API REST para o módulo de **Carrinhos** da plataforma [ServeRest](https://serverest.dev), cobrindo validação de status code, teste de contrato (JSON Schema) e relatórios visuais com Allure.

---

## Stack

| Tecnologia | Versão | Função |
|---|---|---|
| Java | 17 | Linguagem |
| Maven | 3.x | Build e gerenciamento de dependências |
| JUnit Jupiter | 5.8.2 | Framework de testes |
| RestAssured | 5.4.0 | Client HTTP para testes de API |
| JSON Schema Validator | 5.4.0 | Validação de contrato (schema) |
| JavaFaker | 1.0.2 | Geração de dados dinâmicos |
| Allure JUnit5 | 2.27.0 | Relatório de testes |
| Allure RestAssured | 2.27.0 | Captura automática de request/response no relatório |
| AspectJ Weaver | 1.9.21 | Suporte a anotações do Allure |

---

## Estrutura do Projeto

```
testes_automatizados2/
├── pom.xml
└── src/
    ├── main/java/
    │   └── Main.java
    └── test/
        ├── java/
        │   ├── dto/
        │   │   ├── LoginDTO.java              # Payload de login
        │   │   ├── UsuarioReqDTO.java         # Payload de criação de usuário
        │   │   ├── ProdutoDTO.java            # Payload de produto
        │   │   ├── CarrinhoProdutoItemDTO.java # Item de produto dentro do carrinho
        │   │   └── CarrinhoReqDTO.java        # Payload de criação de carrinho
        │   └── serverrest/
        │       ├── BaseTest.java              # Configuração base e helpers HTTP
        │       └── test/carrinho/
        │           └── CarrinhoTest.java      # Suíte de testes do carrinho
        └── resources/
            ├── allure.properties              # Configuração do diretório de resultados
            └── schemas/
                ├── carrinhos-schema.json      # Contrato: GET /carrinhos
                ├── carrinho-schema.json       # Contrato: GET /carrinhos/{id}
                └── carrinho-criado-schema.json # Contrato: POST /carrinhos
```

---

## Pré-requisitos

- **Java 17+** instalado e no PATH
- **Maven 3.6+** instalado e no PATH
- Conexão com a internet (API pública: `https://serverest.dev`)

Verificar instalação:
```bash
java -version
mvn -version
```

---

## Como Executar

### Rodar os testes
```bash
mvn test
```

Os resultados são gerados em `target/allure-results/`.

### Abrir relatório Allure no browser (requer [Allure CLI](https://allurereport.org/docs/install/))
```bash
mvn allure:serve
```

### Gerar relatório estático
```bash
mvn allure:report
```
O relatório é gerado em `target/allure-report/index.html`.

---

## Cobertura de Testes

### GET /carrinhos

| Teste | Tipo | Status Esperado |
|---|---|---|
| Listar todos os carrinhos | Status Code | 200 |
| Validar contrato da listagem | JSON Schema | 200 |

### GET /carrinhos/{id}

| Teste | Tipo | Status Esperado |
|---|---|---|
| Buscar carrinho por ID válido | Status Code + Body | 200 |
| Validar contrato do carrinho por ID | JSON Schema | 200 |
| Buscar carrinho com ID inexistente | Status Code | 400 |

### POST /carrinhos

| Teste | Tipo | Status Esperado |
|---|---|---|
| Cadastrar carrinho com sucesso | Status Code + Body | 201 |
| Validar contrato ao criar carrinho | JSON Schema | 201 |
| Criar carrinho sem token | Status Code | 401 |
| Criar carrinho com produto inexistente | Status Code | 400 |
| Criar segundo carrinho para o mesmo usuário | Status Code | 400 |

### DELETE /carrinhos/concluir-compra

| Teste | Tipo | Status Esperado |
|---|---|---|
| Concluir compra com sucesso | Status Code + Body | 200 |
| Concluir compra sem token | Status Code | 401 |

### DELETE /carrinhos/cancelar-compra

| Teste | Tipo | Status Esperado |
|---|---|---|
| Cancelar compra com sucesso | Status Code + Body | 200 |
| Cancelar compra sem token | Status Code | 401 |

**Total: 14 testes**

---

## Schemas de Contrato

Os schemas estão em `src/test/resources/schemas/` e seguem o padrão **JSON Schema Draft-06**.

### carrinhos-schema.json — resposta de GET /carrinhos
```json
{
  "quantidade": integer,
  "carrinhos": [
    {
      "_id": "string",
      "produtos": [
        { "idProduto": "string", "quantidade": integer, "precoUnitario": integer }
      ],
      "precoTotal": integer,
      "quantidadeTotal": integer,
      "idUsuario": "string"
    }
  ]
}
```

### carrinho-schema.json — resposta de GET /carrinhos/{id}
```json
{
  "_id": "string",
  "produtos": [
    { "idProduto": "string", "quantidade": integer, "precoUnitario": integer }
  ],
  "precoTotal": integer,
  "quantidadeTotal": integer,
  "idUsuario": "string"
}
```

### carrinho-criado-schema.json — resposta de POST /carrinhos
```json
{
  "message": "string",
  "_id": "string"
}
```

---

## Allure Report

O Allure captura automaticamente cada request e response via `AllureRestAssured`. Os testes são organizados em:

- **Epic**: ServeRest API
- **Feature**: Carrinhos
- **Story**: Listar Carrinhos / Buscar Carrinho por ID / Cadastrar Carrinho / Concluir Compra / Cancelar Compra

Níveis de severidade usados: `BLOCKER`, `CRITICAL`, `NORMAL`.

---

## Variáveis de Ambiente

Nenhuma variável de ambiente é necessária. A base URL `https://serverest.dev` está configurada diretamente em `BaseTest.java`.

---

## Referências

- [ServeRest — Swagger](https://serverest.dev/swagger.json?lang=pt-BR)
- [RestAssured Docs](https://rest-assured.io)
- [Allure Framework](https://allurereport.org)
- [JSON Schema](https://json-schema.org)
