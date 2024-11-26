package schemaTests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class EpisodeSchemaTest {
    private String baseUri = "https://rickandmortyapi.com/api/episode";

    @Test(groups = {"regresion"})
    @Epic("Schema Tests")
    @Feature("Rick and morty api")
    @Description("Validamos el cuerpo JSON de respuesta correspendientes a Episodes segun documentacion")
    @Link("https://rickandmortyapi.com/documentation/#episode-schema")
    public void episodeSchemaTest(){
//    Creamos request del tipo  GET
        given()
                .contentType("application/json")
                .when()
                .baseUri(baseUri)
                .get("/1")
                .then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/episodeSchema.json"));
    }
}
