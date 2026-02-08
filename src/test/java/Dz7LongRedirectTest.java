import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Dz7LongRedirectTest {

    @Test
    public void testDz7LongRedirect(){

        //Переменная для отслеживания текущего URL
        String currentUrl = "https://playground.learnqa.ru/api/long_redirect";
        int statusCode;
        Response response;

        do {
            response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(currentUrl)  // Используем текущий URL
                    .andReturn();

            statusCode = response.getStatusCode();
            String locationHeader = response.getHeader("Location");

            System.out.println("Status: " + statusCode + ", URL: " + currentUrl);
            System.out.println("Location header: " + locationHeader);
            //Пустую строку добавил для более читаемого выводы
            System.out.println();

            // Сохраняем ссылку из редиректа для следующей итерации
            currentUrl = locationHeader;

        } while (statusCode != 200);

        System.out.println("Финальный URL (200 OK): " + currentUrl);

    }
}


/*
Первая итеррация привела меня к бесконечному циклу,
понимал, что так будет, тк в каждой итерации ходил по одному и тому же URL,
но как подставлять значение из редиректа для след запроса не мог понять,
пошел в гугл

  int statusCode;
        Response response;

        do {
            response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get("https://playground.learnqa.ru/api/long_redirect")
                    .andReturn();

            statusCode = response.getStatusCode();
            System.out.println("Status code: " + statusCode);

            String locationHeader = response.getHeader("Location");
            System.out.println(locationHeader);
        } while (statusCode != 200);
 */