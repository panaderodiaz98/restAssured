package authorizationTokenTests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class LoginUserWithTokenTest {
    //    URL API https://dummyjson.com/docs/
    private String token;
    @BeforeClass
    public void setUp(){
//        Seteamos base uri
        baseURI = "https://dummyjson.com";
    }

    @Test(priority = 1, groups = {"regresion","token","dummyjson"})
    @Owner("Martin Diaz")
    @Epic("Regresion")
    @Feature("Token tests")
    @Description("Validamos la generacion del token a partir de un usuario valido adjuntado en el body")
    @Link("https://dummyjson.com/docs/auth#auth-me")
    public void loginAndGetToken(){
//        Llamo metodo auxiliar para obtener user y almaceno
        String[] user = obtengoUser(5);
        String username = user[0];
        String password = user[1];
//        Formo body del request
        String body = "{\"username\": \""+username+"\"," +
                " \"password\": \""+password+"\"," +
                " \"expiresInMins\": 30" +
                "  }";
//        Obtengo token
        Response response = given()
                .contentType("application/json")
                .when()
                .body(body)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().response();
        token = response.jsonPath().getString("accessToken");
//        Assert validando que no sea nulo el token
        Assert.assertNotEquals(token,"null","El token es igual a nulo");
    }

    @Test(priority = 2, groups = {"regresion","token","dummyjson"})
    @Owner("Martin Diaz")
    @Epic("Regresion")
    @Feature("Token tests")
    @Description("Validamos la generacion del refresh token a partir de un token previamente solicitado")
    @Link("https://dummyjson.com/docs/auth#auth-me")
    public void validateRefreshToken(){
//        Generamos body
        String body = "{\"refreshToken\": \""+token+"\", \"expireInMins\": 30}";
        Response response = given()
                .when()
                .contentType("application/json")
                .when()
                .body(body)
                .post("/auth/refresh")
                .then()
                .statusCode(200)
                .extract().response();
//        Almacenamos refresh token
        String refreshToken = response.jsonPath().getString("refreshToken");
//        Validacion nuevo refresh token generado correctamente
        Assert.assertNotEquals(refreshToken,token,"Refresh token no se ha generado");
    }
//    Metodo para obtener usuarios segun documentacion
//    https://dummyjson.com/docs/auth
    private String[] obtengoUser(Integer id){
        String[] user = new String[2];
//        Almaceno response
        Response response = given()
                .contentType("application/json")
                .when()
                .get("https://dummyjson.com/users/" + id.toString())
                .then()
                .statusCode(200)
                .extract().response();
//        Obtengo username y password
        user[0] = response.jsonPath().getString("username");
        user[1] = response.jsonPath().getString("password");
        System.out.println(user[0]);
        System.out.println(user[1]);
        return user;
    }
}
