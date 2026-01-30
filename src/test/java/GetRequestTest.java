import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


public class GetRequestTest {

    @Test
    public void testGetRequestTest(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text") //Создание Get запроса на наш адрес
                .andReturn();  //Просим вернуть нам результат
        response.prettyPrint();//Распечатываем текст ответа в удобном формате
    }
}