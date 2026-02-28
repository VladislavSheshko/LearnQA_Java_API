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

public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testEditJustCreatedTest() {
    //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        String editUrl = "https://playground.learnqa.ru/api/user/" + userId;
        Response responseEditUser = apiCoreRequests.makePutRequest(
                editUrl,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData
        );

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        System.out.println(responseUserData.asString());

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    public void testEditNotAuth() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreate = RestAssured
                .given().body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
        String userId = responseCreate.getString("id");

        Map<String, String> editData = Map.of("firstName", "New Name");
        Response response = RestAssured
                .given().body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        System.out.println(response.asString());
        Assertions.assertResponseCodeEquals(response, 400);
    }

    @Test
    public void testEditDifferentUser() {
        // Создаем пользователя
        Map<String, String> testUserData = DataGenerator.getRegistrationData();
        JsonPath responseCreateTestUser = RestAssured
                .given()
                .body(testUserData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
        String testUserId = responseCreateTestUser.getString("id");
        System.out.println(testUserId);
        // Логинимся другим пользователем
        Map<String, String> authData = Map.of(
                "email", "vinkotov@example.com",
                "password", "1234"
        );
        Response responseAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login", authData
        );

        Map<String, String> editData = Map.of("firstName", "Hacked");
        Response response = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + testUserId,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid"),
                editData
        );
        System.out.println(response.asString());
        Assertions.assertResponseCodeEquals(response, 400);
    }

    @Test
    public void testEditInvalidEmail() {
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

        // Пытаемся заменить email на некорректный
        Map<String, String> editData = Map.of("email", "invalid-email.com");
        Response response = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid"),
                editData
        );

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "{\"error\":\"Invalid email format\"}");
    }

    @Test
    public void testEditTooShortFirstName() {
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

        // Короткое имя
        Map<String, String> editData = Map.of("firstName", "A");
        Response response = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid"),
                editData
        );

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "{\"error\":\"The value for field `firstName` is too short\"}");
    }
}
