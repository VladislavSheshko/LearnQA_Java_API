package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import io.qameta.allure.*;
import java.util.HashMap;
import java.util.Map;

@Epic("User API")
@Feature("POST /api/user/ - Registration")
@Story("Registration validation scenarios")
@Severity(SeverityLevel.BLOCKER)

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String userEndpoint = "/api/user/";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://playground.learnqa.ru";
    }

    @Test
    @DisplayName("POST /api/user/ - дублированный email → 400")
    @Description("Проверяет уникальность email. Уже существующий → 400 'already exists'")
    @Story("Duplicate email validation")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    public void  testCreateUserWithExistingEmail() {
        String email = "vladsheshko@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);


        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");

    }

    @Test
    @DisplayName("POST /api/user/ - успешная регистрация → 200 + ID")
    @Description("Регистрация с валидными данными → 200 OK с полем 'id'")
    @Story("Happy path registration")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("smoke")
    public void  testCreateUserSuccessfully() {
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @DisplayName("POST /api/user/ - невалидный email (без @) → 400")
    @Description("Email без символа '@' → 400 'Invalid email format'")
    @Story("Email format validation")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("regression")
    public void testCreateUserWithInvalidEmailNoAt() {
        Map<String, String> userData = DataGenerator.getRegistrationData(Map.of("email", "vlad_Invalid-email.com"));

        Response response = apiCoreRequests.makePostRequest(userEndpoint, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Invalid email format");
    }

    @ParameterizedTest
    @DisplayName("POST /api/user/ - отсутствует обязательное поле: {0}")
    @Description("Проверяет 5 обязательных полей: email, password, username, firstName, lastName. " +
            "Каждое отсутствие → 400 + текст ошибки")
    @Story("Required fields validation")
    @Severity(SeverityLevel.CRITICAL)
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserMissingRequiredField(String missingField) {
        System.out.println("Тестируем отсутствие поля: " + missingField);

        Map<String, String> userData = DataGenerator.getRegistrationDataWithoutField(missingField);

        Response response = apiCoreRequests.makePostRequest(userEndpoint, userData);

        // Выводим код и текст ответа после каждой итерации
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response: " + response.asString());
        System.out.println("---");

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextContains(response, "The following required params are missed");
        Assertions.assertResponseTextContains(response, missingField);
    }

    @Test
    @DisplayName("POST /api/user/ - firstName слишком короткое (1 символ) → 400")
    @Description("firstName длиной 1 символ → 400 'too short'")
    @Story("Field length validation")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithTooShortName() {
        Map<String, String> userData = DataGenerator.getRegistrationData(Map.of("firstName", "A"));

        Response response = apiCoreRequests.makePostRequest(userEndpoint, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too short");
    }

    @Test
    @DisplayName("POST /api/user/ - firstName слишком длинное (251 символов) → 400")
    @Description("firstName > 250 символов → 400 'too long'")
    @Story("Field length validation")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithTooLongName() {
        String longName = "A".repeat(251);
        Map<String, String> userData = DataGenerator.getRegistrationData(Map.of("firstName", longName));

        Response response = apiCoreRequests.makePostRequest(userEndpoint, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too long");
    }
}
