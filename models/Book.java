package models;

import java.util.HashSet;
import java.util.Set;

public class Book {
    private String id;
    private String title;
    private String author;
    private Set<String> tags;

    public Book(String id, String title, String author, Set<String> tags) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.tags = new HashSet<>();
        for (String tag : tags) {
            this.tags.add(tag.toLowerCase().trim());
        }
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Set<String> getTags() { return tags; }

    // Helper to format JSON response for HTTP endpoints
    public String toJson() {
        StringBuilder tagsJson = new StringBuilder("[");
        int i = 0;
        for (String t : tags) {
            tagsJson.append("\"").append(t).append("\"");
            if (++i < tags.size()) tagsJson.append(",");
        }
        tagsJson.append("]");

        return String.format(
            "{\"id\":\"%s\", \"title\":\"%s\", \"author\":\"%s\", \"tags\":%s}",
            id, escapeJson(title), escapeJson(author), tagsJson.toString()
        );
    }

    private String escapeJson(String input) {
        return input.replace("\"", "\\\"");
    }
}