package authorizationTokenTests.simpleBooksApi;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.*;

public class SimpleBooksTokenTests {
    private String accessToken;
    private String clientName;
    private List<Integer> ids;
    private List<String> libros;
    private String orderId;
//    Link documentacion https://github.com/vdespa/introduction-to-postman-course/blob/main/simple-books-api.md


    @BeforeMethod
    public void setUp(){
        //    Setup para base uri
        baseURI = "https://simple-books-api.glitch.me";
        clientName = "Martin Diaz";
    }


//    Create user para obtener token de validacion usuario
    @Test(priority = 1, groups = {"regresion","token","simplebooks"})
    @Owner("Martin Diaz")
    @Epic("Token tests")
    @Feature("SimpleBooksApi")
    @Description("Solicitamos token de acceso mediante la creacion de un usuario")
    @Link("https://github.com/vdespa/introduction-to-postman-course/blob/main/simple-books-api.md")
    public void createUser(){
//        Creo body para crear user
        String body = "{\n \"clientName\": \""+clientName+"\", \"clientEmail\": \""+generoUsuarioRandom()+"\"\n}";
         Response response = given()
                    .contentType("application/json")
                    .when()
                    .body(body)
                    .when()
                    .post("/api-clients/")
                    .then()
                    .statusCode(201)
                    .extract().response();
//         Almaceno token
        accessToken = response.jsonPath().getString("accessToken");
//        Assert token no sea en blanco
        Assert.assertNotEquals(accessToken," ", "No se genero token de acceso");
    }
    @Test(priority = 2, groups = {"regresion","token","simplebooks"})
    @Owner("Martin Diaz")
    @Epic("Token tests")
    @Feature("SimpleBooksApi")
    @Description("Consultamos por listados de libros disponibles")
    @Link("https://github.com/vdespa/introduction-to-postman-course/blob/main/simple-books-api.md")
    public void getListOfBooks(){
        Response response = given()
                .contentType("application/json")
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .extract().response();

        ids = response.jsonPath().getList("id");
        libros = response.jsonPath().getList("name");

        Assert.assertTrue(!ids.isEmpty());
        Assert.assertTrue(!libros.isEmpty());
    }
    @Test(priority = 3, groups = {"regresion","token","simplebooks"})
    @Owner("Martin Diaz")
    @Epic("Token tests")
    @Feature("SimpleBooksApi")
    @Description("Creamos una nueva orden de libros con el listado consultado anteriormente")
    @Link("https://github.com/vdespa/introduction-to-postman-course/blob/main/simple-books-api.md")
    public void submitNewOrder(){
//        Creo body para crear orden
        String body = "{\n" +
                "  \"bookId\": "+ ids.getFirst() +",\n" +
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
        orderId = response.jsonPath().getString("orderId");
        Assert.assertTrue(!orderId.isEmpty());
    }
    @Test(priority = 4, groups = {"regresion","token","simplebooks"})
    @Owner("Martin Diaz")
    @Epic("Token tests")
    @Feature("SimpleBooksApi")
    @Description("Cambiamos el nombre del cliente mediante un update al order id de la orden creada anteriormente")
    @Link("https://github.com/vdespa/introduction-to-postman-course/blob/main/simple-books-api.md")
    public void updateOrderCreated(){
//        Body update
        String newName = "Lucas Llorente";
        String body = "{\n" +
                "  \"customerName\": \""+newName+"\"\n" +
                "}";
//        Request update
        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .body(body)
                .patch("/orders/" + orderId)
                .then()
                .statusCode(204);
//        Request vuelvo a traer orderID para ver si hizo el update
        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/orders/" + orderId)
                .then()
                .statusCode(200)
                .extract().response();
//        Almaceno datos para verificar update
        String newCustomerName = response.jsonPath().getString("customerName");
        String idNewName = response.jsonPath().getString("id");
//        Assert para verificar update!
        Assert.assertNotEquals(newCustomerName, clientName, "No se updateo correctamente");
        Assert.assertEquals(idNewName,orderId,"El id de pedido no es el correcto");
    }
//    Metodo auxiliar para generar datos por cada ejecucion
    public String generoUsuarioRandom(){
        Random random = new Random();  // creo objeto random
        int randomNumber = random.nextInt(1000);
        return "martinDiaz" + randomNumber + "@gmail.com";
    }
}
