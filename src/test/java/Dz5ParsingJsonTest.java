import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


public class Dz5ParsingJsonTest {
    
        @Test
        public void testParsingJson(){
            JsonPath response = RestAssured
                    .given()
                    .get("https://playground.learnqa.ru/api/get_json_homework")
                    .jsonPath();

            // Выводим полный JSON для изучения
            System.out.println("Полный JSON ответ:");
            System.out.println(response.prettyPrint());

            // Извлекаем текст второго сообщения (индекс [1])
            String secondMessage = response.get("messages[1].message");
            System.out.println("Текст второго сообщения: " + secondMessage);

    }
}
