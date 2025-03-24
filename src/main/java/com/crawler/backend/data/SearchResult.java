package com.crawler.backend.data;

import java.util.HashSet;
import java.util.Set;

public class SearchResult {
    private final String id;
    private String status;
    private final Set<String> urls;

    public SearchResult(String id) {
        this.id = id;
        this.status = "active"; // Começa como ativa
        this.urls = new HashSet<>();
    }

    public synchronized void addUrl(String url) {
        this.urls.add(url);
    }

    public synchronized void markAsDone() {
        this.status = "done";
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Set<String> getUrls() {
        return urls;
    }
}

