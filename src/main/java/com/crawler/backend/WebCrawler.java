package com.crawler.backend;

import com.crawler.backend.data.SearchResult;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {
    private static final int TIMEOUT = 5000; // Timeout para requisições HTTP

    public static void startCrawl(String searchId, String keyword) {
        String baseUrl = System.getenv("BASE_URL");
        Set<String> visited = new HashSet<>();

        SearchResult searchResult = CrawlerAPI.searches.get(searchId);
        if (searchResult == null) return;

        crawl(baseUrl, baseUrl, keyword.toLowerCase(), visited, searchResult);
        searchResult.markAsDone();
    }

    public static void crawl(String baseUrl, String url, String keyword, Set<String> visited, SearchResult searchResult) {
        if (visited.contains(url) || !url.startsWith(baseUrl)) {
            return;
        }

        visited.add(url);
        try {
            String content = fetchHtml(url);

            if (content.toLowerCase().contains(keyword)) {
                searchResult.addUrl(url);
            }

            Set<String> links = extractLinks(baseUrl, content);
            for (String nextUrl : links) {
                crawl(baseUrl, nextUrl, keyword, visited, searchResult);
            }
        } catch (Exception e) {
            System.err.println("Erro ao acessar " + url + ": " + e.getMessage());
        }
    }

    private static String fetchHtml(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder html = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line).append("\n");
            }
            return html.toString();
        }
    }

    private static Set<String> extractLinks(String baseUrl, String html) {
        Set<String> links = new HashSet<>();
        Pattern pattern = Pattern.compile("<a\\s+[^>]*href=[\"']([^\"'#]+)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String link = matcher.group(1);
            if (!link.startsWith("http")) {
                link = baseUrl + (link.startsWith("/") ? link : "/" + link);
            }
            links.add(link);
        }

        return links;
    }
}