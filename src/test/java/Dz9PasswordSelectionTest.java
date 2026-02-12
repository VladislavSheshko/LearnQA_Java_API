import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

public class Dz9PasswordSelectionTest {

    boolean returnStart = true;

    @Test
    public void testDz9PasswordSelection(){
        String[] passwordsToTry = {"password", "123456",
                "123456789", "qwerty", "password", "1234567",
                "12345678", "12345", "iloveyou", "111111",
                "123123", "abc123", "qwerty123", "1q2w3e4r",
                "admin", "qwertyuiop", "654321", "555555",
                "lovely", "7777777", "welcome", "888888",
                "princess", "dragon", "password1", "123qwe"};
        String correctLogin = "super_admin";
        //Шаг первый: передаем данные для авторизации и сохраняем полученный куки в переменную для 2 запроса

        do {
            for (String password : passwordsToTry) {
                Map<String, String> data = new HashMap<>();
                data.put("login", correctLogin);
                data.put("password", password);

                Response responseForGetCookie = RestAssured
                        .given()
                        .body(data)
                        .when()
                        .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                        .andReturn();

                String responseCookie = responseForGetCookie.getCookie("auth_cookie");
                String passwordCurrent = responseForGetCookie.jsonPath().get("password");

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
                    System.out.println("Пароль: " + passwordCurrent + ", Авторизация: " + checkCookie);
                    returnStart = false;  // ← Останавливаем do-while
                    break;  // ← Выходим из for
                }
                else
                    returnStart = true;
            }
        } while (returnStart);
    }
}
