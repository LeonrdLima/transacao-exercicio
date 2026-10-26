package br.com.ada.lms.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class TransactionResourceTest {

    private Integer criarTransacao() {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "Compra no supermercado",
                          "tipo": "Saída",
                          "categoria": "Alimentação",
                          "valor": -150.75,
                          "data": "2026-10-20"
                        }
                        """)
                .when().post("/transacoes")
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    @Test
    void postRetorna201ComLocation() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "Salário",
                          "tipo": "Entrada",
                          "categoria": "Renda",
                          "valor": 5000,
                          "data": "2026-10-01"
                        }
                        """)
                .when().post("/transacoes")
                .then()
                .statusCode(201)
                .header("Location", containsString("/transacoes/"))
                .body("id", notNullValue())
                .body("descricao", equalTo("Salário"));
    }

    @Test
    void getPorIdInexistenteRetorna404() {
        given()
                .when().get("/transacoes/9999")
                .then()
                .statusCode(404)
                .body("erro", containsString("9999"));
    }

    @Test
    void putAtualizaTodosOsCampos() {
        Integer id = criarTransacao();

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "descricao": "Compra atualizada",
                          "tipo": "Saída",
                          "categoria": "Casa",
                          "valor": -175.90,
                          "data": "2026-10-21"
                        }
                        """)
                .when().put("/transacoes/" + id)
                .then()
                .statusCode(200)
                .body("descricao", equalTo("Compra atualizada"))
                .body("categoria", equalTo("Casa"));
    }

    @Test
    void deleteRetorna204EDepois404() {
        Integer id = criarTransacao();

        given().when().delete("/transacoes/" + id).then().statusCode(204);
        given().when().get("/transacoes/" + id).then().statusCode(404);
        given().when().delete("/transacoes/" + id).then().statusCode(404);
    }
}
