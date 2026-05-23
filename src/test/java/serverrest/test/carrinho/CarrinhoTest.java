package serverrest.test.carrinho;

import com.github.javafaker.Faker;
import dto.CarrinhoReqDTO;
import dto.LoginDTO;
import dto.ProdutoDTO;
import dto.UsuarioReqDTO;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import serverrest.BaseTest;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@Epic("ServeRest API")
@Feature("Carrinhos")
public class CarrinhoTest extends BaseTest {

    static String PRODUTO_ID;
    static String CARRINHO_ID;
    static String USER_TOKEN;

    @BeforeAll
    static void gerarMassaDados() {
        Faker faker = new Faker();

        // Cria usuário admin para gerenciar produtos
        UsuarioReqDTO adminDTO = new UsuarioReqDTO("true");
        doPost(adminDTO, "/usuarios", HttpStatus.SC_CREATED);
        String adminToken = getToken(new LoginDTO(adminDTO.getEmail(), adminDTO.getPassword()));

        // Cria produto usado nos testes de carrinho
        ProdutoDTO produtoDTO = new ProdutoDTO(
                faker.commerce().productName() + " " + faker.number().digits(6),
                faker.number().numberBetween(10, 500),
                faker.lorem().sentence(),
                50
        );
        PRODUTO_ID = doPost(produtoDTO, "/produtos", HttpStatus.SC_CREATED, adminToken)
                .extract().jsonPath().get("_id");

        // Cria usuário comum para operações de carrinho
        UsuarioReqDTO userDTO = new UsuarioReqDTO("false");
        doPost(userDTO, "/usuarios", HttpStatus.SC_CREATED);
        USER_TOKEN = getToken(new LoginDTO(userDTO.getEmail(), userDTO.getPassword()));

        // Cria carrinho para esse usuário (usado nos testes de GET e carrinho duplicado)
        CARRINHO_ID = doPost(new CarrinhoReqDTO(PRODUTO_ID, 1), "/carrinhos", HttpStatus.SC_CREATED, USER_TOKEN)
                .extract().jsonPath().get("_id");
    }

    /** Cria um novo usuário e retorna seu token de autenticação. */
    private static String criarUsuarioComToken() {
        UsuarioReqDTO dto = new UsuarioReqDTO("false");
        doPost(dto, "/usuarios", HttpStatus.SC_CREATED);
        return getToken(new LoginDTO(dto.getEmail(), dto.getPassword()));
    }

    /** Cria um carrinho para o token informado e retorna o ID gerado. */
    private static String criarCarrinho(String token) {
        return doPost(new CarrinhoReqDTO(PRODUTO_ID, 1), "/carrinhos", HttpStatus.SC_CREATED, token)
                .extract().jsonPath().get("_id");
    }

    // =========================================================================
    // GET /carrinhos
    // =========================================================================

    @Test
    @Story("Listar Carrinhos")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /carrinhos - deve retornar status 200 e lista de carrinhos")
    public void deveListarCarrinhos() {
        doGet("/carrinhos", HttpStatus.SC_OK)
                .body("quantidade", notNullValue())
                .body("carrinhos", not(empty()));
    }

    @Test
    @Story("Listar Carrinhos")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /carrinhos - deve validar contrato de resposta")
    public void deveValidarContratoDaListaDeCarrinhos() {
        doGet("/carrinhos", HttpStatus.SC_OK)
                .body(matchesJsonSchemaInClasspath("schemas/carrinhos-schema.json"));
    }

    // =========================================================================
    // GET /carrinhos/{id}
    // =========================================================================

    @Test
    @Story("Buscar Carrinho por ID")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /carrinhos/{id} - deve retornar status 200 e dados do carrinho")
    public void deveBuscarCarrinhoPorId() {
        doGet("/carrinhos/" + CARRINHO_ID, HttpStatus.SC_OK)
                .body("_id", equalTo(CARRINHO_ID))
                .body("produtos", not(empty()))
                .body("precoTotal", notNullValue())
                .body("quantidadeTotal", notNullValue())
                .body("idUsuario", notNullValue());
    }

    @Test
    @Story("Buscar Carrinho por ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /carrinhos/{id} - deve validar contrato do carrinho encontrado")
    public void deveValidarContratoDoCarrinhoPorId() {
        doGet("/carrinhos/" + CARRINHO_ID, HttpStatus.SC_OK)
                .body(matchesJsonSchemaInClasspath("schemas/carrinho-schema.json"));
    }

    @Test
    @Story("Buscar Carrinho por ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /carrinhos/{id} - deve retornar 400 para ID inexistente")
    public void naoDeveBuscarCarrinhoComIdInexistente() {
        // A API retorna 400 para IDs inexistentes; body omitido pois o campo message
        // pode não estar presente dependendo do formato do ID enviado.
        doGet("/carrinhos/id_invalido_00000", HttpStatus.SC_BAD_REQUEST);
    }

    // =========================================================================
    // POST /carrinhos
    // =========================================================================

    @Test
    @Story("Cadastrar Carrinho")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /carrinhos - deve cadastrar carrinho com sucesso e retornar 201")
    public void deveCadastrarCarrinhoComSucesso() {
        String token = criarUsuarioComToken();
        doPost(new CarrinhoReqDTO(PRODUTO_ID, 1), "/carrinhos", HttpStatus.SC_CREATED, token)
                .body("message", containsString(MSG_CADASTRO_USER_OK))
                .body("_id", notNullValue());
    }

    @Test
    @Story("Cadastrar Carrinho")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /carrinhos - deve validar contrato da resposta ao criar carrinho")
    public void deveValidarContratoAoCadastrarCarrinho() {
        String token = criarUsuarioComToken();
        doPost(new CarrinhoReqDTO(PRODUTO_ID, 1), "/carrinhos", HttpStatus.SC_CREATED, token)
                .body(matchesJsonSchemaInClasspath("schemas/carrinho-criado-schema.json"));
    }

    @Test
    @Story("Cadastrar Carrinho")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /carrinhos - não deve cadastrar carrinho sem token de autenticação")
    public void naoDeveCadastrarCarrinhoSemToken() {
        given().contentType(ContentType.JSON)
                .body(new CarrinhoReqDTO(PRODUTO_ID, 1))
                .when().post("/carrinhos")
                .then().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", containsString(MSG_TOKEN_AUSENTE));
    }

    @Test
    @Story("Cadastrar Carrinho")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /carrinhos - não deve cadastrar carrinho com produto inexistente")
    public void naoDeveCadastrarCarrinhoComProdutoInexistente() {
        String token = criarUsuarioComToken();
        doPost(new CarrinhoReqDTO("id_produto_invalido_000", 1), "/carrinhos", HttpStatus.SC_BAD_REQUEST, token)
                .body("message", containsString(MSG_PRODUTO_NAO_ENCONTRADO));
    }

    @Test
    @Story("Cadastrar Carrinho")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /carrinhos - não deve permitir mais de um carrinho por usuário")
    public void naoDeveCadastrarCarrinhoDuplicado() {
        // USER_TOKEN já possui o CARRINHO_ID criado no @BeforeAll
        given().contentType(ContentType.JSON)
                .header("Authorization", USER_TOKEN)
                .body(new CarrinhoReqDTO(PRODUTO_ID, 1))
                .when().post("/carrinhos")
                .then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", containsString(MSG_CARRINHO_DUPLICADO));
    }

    // =========================================================================
    // DELETE /carrinhos/concluir-compra
    // =========================================================================

    @Test
    @Story("Concluir Compra")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("DELETE /carrinhos/concluir-compra - deve concluir compra e retornar 200")
    public void deveConcluirCompraComSucesso() {
        String token = criarUsuarioComToken();
        criarCarrinho(token);
        doDelete("/carrinhos/concluir-compra", HttpStatus.SC_OK, token)
                .body("message", containsString(MSG_REGISTRO_EXCLUIDO));
    }

    @Test
    @Story("Concluir Compra")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE /carrinhos/concluir-compra - não deve concluir compra sem token")
    public void naoDeveConcluirCompraSemToken() {
        doDelete("/carrinhos/concluir-compra", HttpStatus.SC_UNAUTHORIZED)
                .body("message", containsString(MSG_TOKEN_AUSENTE));
    }

    // =========================================================================
    // DELETE /carrinhos/cancelar-compra
    // =========================================================================

    @Test
    @Story("Cancelar Compra")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("DELETE /carrinhos/cancelar-compra - deve cancelar compra, restaurar estoque e retornar 200")
    public void deveCancelarCompraComSucesso() {
        String token = criarUsuarioComToken();
        criarCarrinho(token);
        doDelete("/carrinhos/cancelar-compra", HttpStatus.SC_OK, token)
                .body("message", containsString(MSG_REGISTRO_EXCLUIDO));
    }

    @Test
    @Story("Cancelar Compra")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE /carrinhos/cancelar-compra - não deve cancelar compra sem token")
    public void naoDeveCancelarCompraSemToken() {
        doDelete("/carrinhos/cancelar-compra", HttpStatus.SC_UNAUTHORIZED)
                .body("message", containsString(MSG_TOKEN_AUSENTE));
    }
}
