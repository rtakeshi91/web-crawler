package com.crawler.backend;

import static spark.Spark.*;

import com.crawler.backend.data.RequestData;
import com.crawler.backend.data.ResponseId;
import com.crawler.backend.data.SearchResult;

import com.google.gson.Gson;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;

public class CrawlerAPI {
    static final ConcurrentHashMap<String, SearchResult> searches = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(
            Integer.parseInt(System.getenv().getOrDefault("THREAD_POOL_SIZE", "10"))
    );
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        port(4567);

        post("/crawl", (req, res) -> {
            RequestData requestData = gson.fromJson(req.body(), RequestData.class);
            if (requestData.keyword.length() < 4 || requestData.keyword.length() > 32) {
                res.status(400);
                return "A palavra-chave deve ter entre 4 e 32 caracteres.";
            }

            String searchId = UUID.randomUUID().toString().substring(0, 8);
            SearchResult searchResult = new SearchResult(searchId);
            searches.put(searchId, searchResult);

            executor.submit(() -> WebCrawler.startCrawl(searchId, requestData.keyword));

            res.type("application/json");
            return gson.toJson(new ResponseId(searchId));
        });

        get("/crawl/:id", (req, res) -> {
            String searchId = req.params("id");
            SearchResult result = searches.get(searchId);

            if (result == null) {
                res.status(404);
                return "ID não encontrado.";
            }

            res.type("application/json");
            return gson.toJson(result);
        });
    }
}

