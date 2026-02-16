import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class Dz13UserAgentTest {

    private static final List<String> listUserAgentError = new ArrayList<>();

    @ParameterizedTest(name = "UserAgent: {0}")
    @CsvSource({
            // userAgent, expectedDevice, expectedBrowser, expectedPlatform
            "'Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30', Android, 'No', Mobile",
            "'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 Version/14.0 Mobile/15E148 Safari/604.1', iOS, 'Chrome', Mobile",
            "'Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', Unknown, Unknown, Googlebot",
            "'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0', No, Chrome, Web",
            "'Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1', iPhone, No, Mobile"
    })
    public void testUserAgent(String userAgent, String expectedDevice, String expectedBrowser, String expectedPlatform) {

        JsonPath response = given()
                .header("User-Agent", userAgent)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .then()
                .statusCode(200)
                .extract().jsonPath();

        String actualDevice = response.getString("device");
        String actualBrowser = response.getString("browser");
        String actualPlatform = response.getString("platform");


        if (!expectedDevice.equals(actualDevice)) {
            listUserAgentError.add(userAgent + " - device: ожидалось '" + expectedDevice + "', получено '" + actualDevice + "'");
        }
        if (!expectedBrowser.equals(actualBrowser)) {
            listUserAgentError.add(userAgent + " - browser: ожидалось '" + expectedBrowser + "', получено '" + actualBrowser + "'");
        }
        if (!expectedPlatform.equals(actualPlatform)) {
            listUserAgentError.add(userAgent + " - platform: ожидалось '" + expectedPlatform + "', получено '" + actualPlatform + "'");
        }
    }

    @AfterAll
    static void printErrors() {
        System.out.println("Проблемные User-Agent (" + listUserAgentError.size() + "):");
        listUserAgentError.forEach(System.out::println);
    }
}
