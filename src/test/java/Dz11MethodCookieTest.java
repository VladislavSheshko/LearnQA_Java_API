import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class Dz11MethodCookieTest {

    @Test
    public void testMethodCookie(){

        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

       Map<String, String> cookies = response.getCookies(); //Сохранили куки
        assertEquals(200, response.statusCode(), "Unexpected status code");  //Проверяем код ответа
        assertTrue(cookies.containsKey("HomeWork"), "Response doesn't have 'HomeWork' cookie");  //Проверяем наличие ключа HomeWork в полученном куки



    }
}
