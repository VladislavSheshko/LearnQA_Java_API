import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class Dz12MethodHeaderTest {

    @Test
    public void testMethodHeader(){

        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

       Headers header = response.getHeaders(); //Сохранили заголовки
        assertEquals(200, response.statusCode(), "Unexpected status code");  //Проверяем код ответа
        assertTrue(header.hasHeaderWithName("x-secret-homework-header"), "Response doesn't have 'x-secret-homework-header' header");  //Проверяем наличие конкретного заголовка



    }
}
