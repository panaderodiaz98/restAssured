package serializacionDeserializacionTests.deserializar;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class DeserializarTest {
    private int id;

    @BeforeMethod
    public void setUp(){
        RestAssured.baseURI = "https://rickandmortyapi.com/api";
        id = 2;
    }
    @Test(groups = {"regresion","Rick and morty api","deserializar"})
    @Owner("Martin Diaz")
    @Epic("Deserializar test")
    @Feature("Rick and morty api")
    @Description("Obtenemos mediante un get un personaje X mediante su ID y deserializamos sus atributos en una clase")
    @Link("https://rickandmortyapi.com/documentation/#character-schema")
    public void characterDeserializar(){
//        Generamos el response del tipo clase Character
        CharacterRickAndMorty response = given()
                .contentType("application/json")
                .when()
                .get("/character/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(CharacterRickAndMorty.class);
//        Validamos los datos obtenidos
        Assert.assertTrue(!response.getEpisode().isEmpty());
        Assert.assertTrue(response.getId() == id);

        System.out.println(response.getId());
    }
}
