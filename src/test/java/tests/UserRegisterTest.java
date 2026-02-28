package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String userEndpoint = "/api/user/";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://playground.learnqa.ru";
    }

    @Test
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
    public void testCreateUserWithInvalidEmailNoAt() {
        Map<String, String> userData = DataGenerator.getRegistrationData(Map.of("email", "vlad_Invalid-email.com"));

        Response response = apiCoreRequests.makePostRequest(userEndpoint, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Invalid email format");
    }

    @ParameterizedTest
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
    public void testCreateUserWithTooShortName() {
        Map<String, String> userData = DataGenerator.getRegistrationData(Map.of("firstName", "A"));

        Response response = apiCoreRequests.makePostRequest(userEndpoint, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too short");
    }

    @Test
    public void testCreateUserWithTooLongName() {
        String longName = "A".repeat(251);
        Map<String, String> userData = DataGenerator.getRegistrationData(Map.of("firstName", longName));

        Response response = apiCoreRequests.makePostRequest(userEndpoint, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too long");
    }
}
