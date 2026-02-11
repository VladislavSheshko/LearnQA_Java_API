import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class Dz9PasswordSelectionTest {

    boolean returnStart = false;
    //Шаг первый: передаем данные для авторизации и сохраняем полученный куки в переменную для 2 запроса
    @Test
    public void testDz9PasswordSelection(){

        do {

        Map<String, String> data = new HashMap<>();
        data.put("login", "super_admin");
        data.put("password", "welcome");

        Response responseForGetCookie = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                .andReturn();

        String responseCookie = responseForGetCookie.getCookie("auth_cookie");
        String password = responseForGetCookie.jsonPath().get("password");

        Map<String, String> cookies = new HashMap<>();
        cookies.put("auth_cookie", responseCookie);

        //Шаг два: проверка корректности авторизации
        Response responseForCheckCookie = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                .andReturn();

        String checkCookie = responseForCheckCookie.asString();

            if(checkCookie.equals("You are authorized")){
                System.out.println("Пароль: " + password + ", Авторизация: " + checkCookie);
            }
            else
                returnStart = true;
        } while (returnStart);
    }
}
