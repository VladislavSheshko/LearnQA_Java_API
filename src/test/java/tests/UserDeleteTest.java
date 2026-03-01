package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.HashMap;
import java.util.Map;
import io.qameta.allure.*;

@Epic("User API")
@Feature("DELETE /api/user/{id} - User Deletion")
@Story("User deletion scenarios")
@Severity(SeverityLevel.CRITICAL)

public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("DELETE /api/user/2 - защищённый тестовый пользователь → 400")
    @Description("Попытка удалить системного пользователя ID=2 (vinkotov@example.com) → " +
            "400 'do not delete test users 1,2,3,4,5'")
    @Story("Protected test users")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @Tag("security")
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

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");
    }

    @Test
    @DisplayName("DELETE /api/user/{id} - успешное удаление своего пользователя → 200 + GET 404")
    @Description("Создание → логин → DELETE → GET проверка (404 'User not found'). " +
            "Полное подтверждение удаления")
    @Story("Successful deletion")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("smoke")
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

        Assertions.assertResponseCodeEquals(response, 200);
        Assertions.assertResponseTextEquals(response, "{\"success\":\"!\"}");

        Response responseUser = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid")
        );

        Assertions.assertResponseCodeEquals(responseUser, 404);
        Assertions.assertResponseTextEquals(responseUser, "User not found");
    }

    @Test
    @DisplayName("DELETE /api/user/{id} - чужой пользователь НЕ МОЖЕТ удалить → 400")
    @Description("vinkotov@example.com (ID=2) пытается удалить созданного пользователя → " +
            "400 'do not delete test users'")
    @Story("Role-Based Access Control restrictions")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("security")
    @Tag("RoleBasedAccessControl")
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

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");
    }
}
