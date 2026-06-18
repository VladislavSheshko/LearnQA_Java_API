import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldTest {

    @Test
    public void testHelloWorldTest(){
        System.out.println("Hello from Vladislav Sheshko");
    }


    @Test
    public void testGetRequestTest(){
        Map<String, String> params = new HashMap<>();
        params.put("name", "Vladislav");

        Response response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
        // Проверка Content-Type вместо вывода в консоль
        assertEquals("application/json", response.getHeader("Content-Type"));
        //System.out.println(response.getHeader("Content-Type"));
    }

    @Test
    public void testRestAssured(){
        Map<String, String> params = new HashMap<>();
        params.put("name", "Vladislav");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String name = response.get("answer");
        System.out.println(name);
    }
}
