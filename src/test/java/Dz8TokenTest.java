import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Dz8TokenTest {

    @Test
    public void testDz8Token() {

        //Шаг первый: создание задачи и сохранение токена в переменную
        JsonPath jsonPath = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String token = jsonPath.get("token");           // Извлекаем token
        int seconds = jsonPath.getInt("seconds"); // Извлекаем seconds

        System.out.println("Token: " + token);
        System.out.println("Seconds: " + seconds);
        System.out.println();

        //Шаг второй: проверка статуса задачи созданной в первом шаге
        Response responseForStatus = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();

        String status = responseForStatus.jsonPath().get("status");
        if(status.equals("Job is NOT ready")){
            System.out.println("Задача еще не готова");
            System.out.println();
        }
        else
            System.out.println("Задача готова");

        //Шаг три: ожидаем время когда задача исполнится, ждем (seconds + 1) секунд
        int waitTimeMs = (seconds + 1) * 1000;
        try {
            Thread.sleep(waitTimeMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Ожидание завершено");
        System.out.println();

        // Шаг четыре: выполняем вызов и проверяем значение поля status и наличие поля result

        Response responseForStatusAndResult = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();

        JsonPath jsonPathTwo = responseForStatusAndResult.jsonPath();
        String statusCompleted = jsonPathTwo.get("status");
        String result = jsonPathTwo.get("result");

        if(statusCompleted.equals("Job is ready")){
            System.out.println("Задача готова");
        }
        else
            System.out.println("Задача еще не готова");

        if(result != null){
            System.out.println("Поле result есть в ответе");
        }
    }
}
