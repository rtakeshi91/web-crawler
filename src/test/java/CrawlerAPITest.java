import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import com.axreng.backend.CrawlerAPI;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import spark.Spark;

public class CrawlerAPITest {
    private static final Gson gson = new Gson();

    @BeforeAll
    public static void setUp() {
        CrawlerAPI.main(new String[]{}); // Inicia a API
        Spark.awaitInitialization(); // Aguarda inicialização do servidor
    }

    @AfterAll
    public static void tearDown() {
        Spark.stop(); // Encerra a API ao final dos testes
    }

    @Test
    public void testPostCrawl_ValidRequest_ShouldReturnId() {
        String requestBody = "{\"keyword\": \"security\"}";

        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:4567/crawl")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    public void testPostCrawl_InvalidKeyword_ShouldReturn400() {
        String requestBody = "{\"keyword\": \"abc\"}"; // Menos de 4 caracteres

        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:4567/crawl")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGetCrawl_UnknownId_ShouldReturn404() {
        given()
                .when()
                .get("http://localhost:4567/crawl/invalid123")
                .then()
                .statusCode(404);
    }
}
