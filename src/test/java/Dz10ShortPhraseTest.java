import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Dz10ShortPhraseTest {

    @ParameterizedTest
    @ValueSource(strings = {"Vladislav", "Vlad"})
    public void testShortPhrase(String name){
        Map<String, String> queryParams = new HashMap<>();
        if(name.length() > 0){
            queryParams.put("name", name);
        }

        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String answer = response.getString("answer");
        String expectedName = (answer.length() > 15) ? answer : "Error";
        assertEquals(expectedName, answer);
    }
}
