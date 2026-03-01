package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testDeleteUserFail() {
        // Логинимся пользователем с ID 2
        Map<String, String> authData = Map.of(
                "email", "vinkotov@example.com",
                "password", "1234"
        );

        Response responseAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login", authData
        );

        Map<String, String> editData = new HashMap<>();
        Response response = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/2",
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid")
        );

        System.out.println(response.asString());
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");
    }

    @Test
    public void testDeleteUserSuccess() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreate = RestAssured
                .given().body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
        String userId = responseCreate.getString("id");

        // Логин
        Map<String, String> authData = Map.of(
                "email", userData.get("email"),
                "password", userData.get("password")
        );
        Response responseAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login", authData
        );

        // Удаляем пользователя которого создали ранее
        Response response = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid")
        );

        System.out.println(response.asString());
        Assertions.assertResponseCodeEquals(response, 200);
        Assertions.assertResponseTextEquals(response, "{\"success\":\"!\"}");

        Response responseUser = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid")
        );

        System.out.println(responseUser.asString());
        Assertions.assertResponseCodeEquals(responseUser, 404);
        Assertions.assertResponseTextEquals(responseUser, "User not found");
    }

    @Test
    public void testDeleteUserForbiddenDifferentUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreate = RestAssured
                .given().body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
        String userId = responseCreate.getString("id");

        // Логинимся пользователем с ID 2
        Map<String, String> authData = Map.of(
                "email", "vinkotov@example.com",
                "password", "1234"
        );

        Response responseAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login", authData
        );

        // Удаляем пользователя которого создали ранее
        Response response = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid")
        );

        System.out.println(response.asString());
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");
    }
}
