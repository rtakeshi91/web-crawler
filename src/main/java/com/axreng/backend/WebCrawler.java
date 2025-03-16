package com.axreng.backend;

import com.axreng.backend.data.SearchResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebCrawler {
    private static final ConcurrentHashMap<String, SearchResult> searches = CrawlerAPI.searches;

    public static void startCrawl(String searchId, String keyword) {
        String baseUrl = System.getenv("BASE_URL");
        Set<String> visited = new HashSet<>();

        com.axreng.backend.data.SearchResult searchResult = searches.get(searchId);
        if (searchResult == null) return;

        crawl(baseUrl, baseUrl, keyword.toLowerCase(), visited, searchResult);
        searchResult.markAsDone(); // Finaliza a busca
    }

    public static void crawl(String baseUrl, String url, String keyword, Set<String> visited, com.axreng.backend.data.SearchResult searchResult) {
        if (visited.contains(url) || !url.startsWith(baseUrl)) {
            return;
        }

        visited.add(url);
        try {
            Document doc = Jsoup.connect(url).get();
            String content = doc.text().toLowerCase();

            if (content.contains(keyword)) {
                searchResult.addUrl(url); // Adiciona URL encontrada
            }

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                crawl(baseUrl, nextUrl, keyword, visited, searchResult);
            }
        } catch (Exception e) {
            System.err.println("Erro ao acessar " + url);
        }
    }
}

