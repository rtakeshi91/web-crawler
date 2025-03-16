import com.axreng.backend.data.SearchResult;
import com.axreng.backend.WebCrawler;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WebCrawlerTest {
    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testCrawl_ShouldFindKeywordInMockPage() throws IOException {
        // Simulando uma resposta HTTP com HTML contendo "security"
        String mockHtml = "<html><body>Security is important!</body></html>";
        mockWebServer.enqueue(new MockResponse().setBody(mockHtml).setResponseCode(200));

        // Criando a URL da página mock
        String testUrl = mockWebServer.url("/test-page").toString();
        String keyword = "security";

        Set<String> visited = new HashSet<>();
        SearchResult searchResult = new SearchResult("test123");

        // Chamando o crawler na URL mock
        WebCrawler.crawl(testUrl, testUrl, keyword.toLowerCase(), visited, searchResult);

        // Verificando se a URL foi encontrada
        assertFalse(searchResult.getUrls().isEmpty(), "A URL deveria ser encontrada");
    }

    @Test
    public void testCrawl_ShouldIgnorePagesWithoutKeyword() {
        String mockHtml = "<html><body>This is a test page.</body></html>";
        String keyword = "security";

        Set<String> visited = new HashSet<>();
        SearchResult searchResult = new SearchResult("test456");

        WebCrawler.crawl("http://example.com", "http://example.com", keyword.toLowerCase(), visited, searchResult);

        assertTrue(searchResult.getUrls().isEmpty(), "Any URL should be registered!");
    }
}

