package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User API")
@Feature("GET /api/user/{id}")
@Story("Authorization scenarios")
@Severity(SeverityLevel.CRITICAL)

public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("GET /api/user/2 - без авторизации (только username видим)")
    @Description("Проверяет частичный доступ к данным пользователя без авторизации. " +
            "Ожидается: username доступен, firstName/lastName/email скрыты")
    @Feature("Public access")
    @Story("Anonymous user data visibility")
    @Severity(SeverityLevel.BLOCKER)
    @Owner("QA Team")
    public void testGetUserDataNotAuth() {
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        System.out.println(responseUserData.asString());

        String[] visibleFields = {"username"};
        Assertions.assertJsonHasFields(responseUserData, visibleFields);

        String[] hiddenFields = {"firstName", "lastName", "email"};
        for (String field : hiddenFields) {
            Assertions.assertJsonHasNotField(responseUserData, field);
        }
    }

    @Test
    @DisplayName("GET /api/user/2 - авторизован ВЛАДЕЛЕЦ (все поля видны)")
    @Description("Полный доступ к данным пользователя при авторизации владельцем (vinkotov@example.com). " +
            "Ожидается: username, firstName, lastName, email - все поля доступны")
    @Feature("Owner access")
    @Story("Full data visibility for owner")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("QA Team")
    @Tag("smoke")
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String,String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password","1234");

        String userEndpoint = "https://playground.learnqa.ru/api/user/login";
        Response responseGetAuth = apiCoreRequests.makePostRequest(userEndpoint, authData);

        System.out.println("Полученные данные авторизации:");
        System.out.println(responseGetAuth.asString());

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        String userEndpointTwo = "https://playground.learnqa.ru/api/user/2";
        Response responseUserData = apiCoreRequests.makeGetRequest(userEndpointTwo, header, cookie);

        System.out.println("Полученные данные пользователя:");
        System.out.println(responseUserData.asString());

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @DisplayName("GET /api/user/1 - авторизован ЧУЖОЙ пользователь (только username)")
    @Description("Ограниченный доступ к чужим данным при авторизации (vinkotov смотрит user/1). " +
            "Ожидается: только username видим, остальные поля скрыты")
    @Feature("Restricted access")
    @Story("Limited visibility for other users")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
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
