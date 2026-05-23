package serverrest;

import com.github.javafaker.Faker;
import dto.LoginDTO;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

public class BaseTest {

    public static String TOKEN;

    public static final String MSG_CADASTRO_USER_OK    = "Cadastro realizado com sucesso";
    public static final String MSG_EMAIL_DUPLICADO      = "Este email já está sendo usado";
    public static final String MSG_LOGIN_OK             = "Login realizado com sucesso";
    public static final String MSG_LOGIN_NOK            = "Email e/ou senha inválidos";
    public static final String MSG_CARRINHO_NAO_ENCONTRADO = "Carrinho não encontrado";
    public static final String MSG_CARRINHO_DUPLICADO   = "Não é permitido ter mais de 1 carrinho";
    public static final String MSG_PRODUTO_NAO_ENCONTRADO = "Produto não encontrado";
    public static final String MSG_REGISTRO_EXCLUIDO    = "Registro excluído com sucesso";
    public static final String MSG_TOKEN_AUSENTE        = "Token de acesso ausente";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://serverest.dev";
        RestAssured.filters(new AllureRestAssured());
    }

    public static RequestSpecification baseRequest() {
        return given().contentType(ContentType.JSON);
    }

    public static ValidatableResponse doGet(String path, int statusCode) {
        return baseRequest()
                .when()
                .get(path)
                .then()
                .statusCode(statusCode);
    }

    public static ValidatableResponse doGet(String path, int statusCode, String token) {
        return baseRequest()
                .header("Authorization", token)
                .when()
                .get(path)
                .then()
                .statusCode(statusCode);
    }

    public static ValidatableResponse doPost(Object body, String path, int statusCode) {
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path)
                .then()
                .statusCode(statusCode);
    }

    public static ValidatableResponse doPost(Object body, String path, int statusCode, String token) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(body)
                .when()
                .post(path)
                .then()
                .statusCode(statusCode);
    }

    public static ValidatableResponse doDelete(String path, int statusCode) {
        return baseRequest()
                .when()
                .delete(path)
                .then()
                .statusCode(statusCode);
    }

    public static ValidatableResponse doDelete(String path, int statusCode, String token) {
        return baseRequest()
                .header("Authorization", token)
                .when()
                .delete(path)
                .then()
                .statusCode(statusCode);
    }

    public static String getToken(LoginDTO loginDTO) {
        return doPost(loginDTO, "/login", HttpStatus.SC_OK)
                .extract().jsonPath().get("authorization");
    }

    public Faker getFaker() {
        return new Faker();
    }
}
