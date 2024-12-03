package idempotenciaTests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;

public class IdempotenciaTest {
    private String accessToken;
    private String clientName = "Martin Diaz";
    private String orderId;

    @BeforeClass
    public void generateOrder(){
        RestAssured.baseURI = "https://simple-books-api.glitch.me";
//        Genero token con metodos auxiliares
        accessToken = generateToken();
        System.out.println(accessToken);
//        Genero orden luego de generar token con metodos auxiliares
        orderId = order();
        System.out.println(orderId);
    }
    @Test(priority = 1, groups = {"regresion","idempotencia","simplebooks"})
    @Owner("Martin Diaz")
    @Epic("Idempotencia")
    @Feature("SimpleBooksApi")
    @Description("Validamos que el request haga update del customer name correctamente dos veces seguidas")
    @Link("https://github.com/vdespa/introduction-to-postman-course/blob/main/simple-books-api.md")
    public void updateOrder(){
//        Creo body para crear updates
        String firstNameUpdate = "Rocio Bustamante";
        String secondNameUpdate = "Ignacio Bustamante";
        String firstBodyUpdate = "{\n \"customerName\": \""+firstNameUpdate+"\"}";
        String secondBodyUpdate = "{\n \"customerName\": \""+secondNameUpdate+"\"}";
//        Almaceno response para validar update 1
                given()
                .contentType("application/json")
                .header("Authorization","Bearer " + accessToken)
                .when()
                .body(firstBodyUpdate)
                .when()
                .patch("/orders/"+orderId)
                .then()
                .statusCode(204);
        Response response = given()
                .contentType("application/json")
                .header("Authorization","Bearer " + accessToken)
                .when()
                .body(firstBodyUpdate)
                .when()
                .get("/orders/"+orderId)
                .then()
                .statusCode(200)
                .extract().response();
//        Valido cambio name 1
        String newClientName = response.jsonPath().getString("customerName");
        Assert.assertTrue(newClientName.equals(firstNameUpdate));
        Assert.assertTrue(!newClientName.equals(clientName));
//        Almaceno response para validar update 2
            given()
                .contentType("application/json")
                .header("Authorization","Bearer " + accessToken)
                .when()
                .body(secondBodyUpdate)
                .when()
                .patch("/orders/"+orderId)
                .then()
                .statusCode(204)
                .extract().response();
        response = given()
                .contentType("application/json")
                .header("Authorization","Bearer " + accessToken)
                .when()
                .body(firstBodyUpdate)
                .when()
                .get("/orders/"+orderId)
                .then()
                .statusCode(200)
                .extract().response();
//        Valido cambio name 2
        newClientName = response.jsonPath().getString("customerName");
        Assert.assertTrue(newClientName.equals(secondNameUpdate));
        Assert.assertTrue(!newClientName.equals(firstNameUpdate));

    }
    @Test(priority = 2, groups = {"regresion","idempotencia","simplebooks"})
    @Owner("Martin Diaz")
    @Epic("Idempotencia")
    @Feature("SimpleBooksApi")
    @Description("Eliminamos la order generada y validamos que intentando por segunda vez nos arroje un 404 confirmando que no existe mas esa order")
    @Link("https://github.com/vdespa/introduction-to-postman-course/blob/main/simple-books-api.md")
    public void deleteOrder(){
//        Delete order
        given()
                .contentType("application/json")
                .when()
                .header("Authorization","Bearer "+accessToken)
                .when()
                .delete("/orders/"+orderId)
                .then()
                .statusCode(204);
//        Intentamos deletear misma orden nuevamente
        given()
                .contentType("application/json")
                .when()
                .header("Authorization","Bearer "+accessToken)
                .when()
                .delete("/orders/"+orderId)
                .then()
                .statusCode(404);
    }

//    Metodo auxiliar genero token para books API
    private String generateToken(){
        //        Creo body para crear user
        String body = "{\n \"clientName\": \""+clientName+"\", \"clientEmail\": \""+usuarioRandom()+"\"\n}";
        Response response = given()
                .contentType("application/json")
                .when()
                .body(body)
                .when()
                .post("/api-clients/")
                .then()
                .statusCode(201)
                .extract().response();
//         Retorno token
       return response.jsonPath().getString("accessToken");
    }
//    Metodo auxiliar para generar datos por cada ejecucion
    private String usuarioRandom(){
        Random random = new Random();  // creo objeto random
        int randomNumber = random.nextInt(1000);
        return "DiazMartin" + randomNumber + "@gmail.com";
    }
//    Metodo auxiliar para generar orden
    private String order(){
        //        Creo body para crear orden
        String body = "{\n" +
                "  \"bookId\": 1,\n" +
                "  \"customerName\": \""+ clientName +"\"\n" +
                "}";
//        Request
        Response response = given()
                .contentType("application/json")
                .when()
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .post("/orders")
                .then()
                .statusCode(201)
                .extract().response();
//        Assert de creacion
        Boolean created = response.jsonPath().getBoolean("created");
        Assert.assertTrue(created);
//        Almaceno order ID
        return response.jsonPath().getString("orderId");
    }

}
