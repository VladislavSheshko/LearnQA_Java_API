package lib;

import io.restassured.response.Response;
import java.util.List;
import static org.hamcrest.Matchers.hasKey;

public class Assertions {
    public static void checkJsonByName(Response response, String name, String expectedValue, String userAgent, List<String> errors) {
        response.then().assertThat().body("$", hasKey(name));  // Проверка наличия поля остается

        String actualValue = response.jsonPath().getString(name);

        if (!expectedValue.equals(actualValue)) {
            errors.add(userAgent + " - " + name + ": ожидалось '" + expectedValue + "', получено '" + actualValue + "'");
        }
    }
}
