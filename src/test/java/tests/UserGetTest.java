package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth() {
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        System.out.println(responseUserData.asString());
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Test
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String,String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password","1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        System.out.println("Полученные данные авторизации:");
        System.out.println(responseGetAuth.asString());

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        System.out.println("Полученные данные пользователя:");
        System.out.println(responseUserData.asString());

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    public void testGetUserDetailsAuthAsDifferentUser() {
        Map<String,String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password","1234");

        String userEndpoint = "https://playground.learnqa.ru/api/user/login";
        Response responseGetAuth = apiCoreRequests.makePostRequest(userEndpoint, authData);

        System.out.println("Полученные данные авторизации:");
        System.out.println(responseGetAuth.asString());

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");


        String userEndpointTwo = "https://playground.learnqa.ru/api/user/1";
        Response responseUserData = apiCoreRequests.makeGetRequest(userEndpointTwo, header, cookie);

        System.out.println("Полученные данные пользователя:");
        System.out.println(responseUserData.asString());

        // Проверяем, что видим ТОЛЬКО username чужого пользователя
        String[] visibleFields = {"username"};
        Assertions.assertJsonHasFields(responseUserData, visibleFields);

        String[] hiddenFields = {"firstName", "lastName", "email"};
        for (String field : hiddenFields) {
            Assertions.assertJsonHasNotField(responseUserData, field);
        }
    }

}
