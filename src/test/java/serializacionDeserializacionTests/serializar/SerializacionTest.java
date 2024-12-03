package serializacionDeserializacionTests.serializar;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;

public class SerializacionTest {
    @BeforeMethod
    public void setUp(){
        RestAssured.baseURI = "https://simple-books-api.glitch.me";
    }
    @Test(groups = {"regresion","serializar","simplebooks"})
    @Owner("Martin Diaz")
    @Epic("Serializar test")
    @Feature("SimpleBooksApi")
    @Description("Generamos un token de acceso pasandole mediante un body generado por clase serializada con sus atributos")
    @Link("https://github.com/vdespa/introduction-to-postman-course/blob/main/simple-books-api.md")
    public void getToken(){
//        Creo objeto del tipo clase "GetTokenBody"
        GetTokenBody getTokenBody = new GetTokenBody("Martin Diaz",generateEmail());
//        Genero request
        Response response = given()
                .contentType("application/json")
                .when()
                .body(getTokenBody)
                .when()
                .post("/api-clients/")
                .then()
                .statusCode(201)
                .extract().response();
//        Almaceno token
        String token = response.jsonPath().getString("accessToken");
//        Valido que no sea vacio
        Assert.assertTrue(!token.isEmpty(),"Token generado con error");
    }

    private String generateEmail(){
        Random random = new Random();
        int numberRandom = random.nextInt(10000);
        return "userToken" + numberRandom + "@gmail.com";
    }
}
