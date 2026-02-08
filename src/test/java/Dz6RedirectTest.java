import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


public class Dz6RedirectTest {

    @Test
    public void testDz6Redirect(){
        
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);

        /*
        Выводит список заголовков, где можно увидеть расположение адреса редиректа

        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);
        */
    }
}





