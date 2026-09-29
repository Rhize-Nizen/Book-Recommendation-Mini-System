package structures;

import models.Book;
import java.util.*;

public class InvertedIndex {
    private final Map<String, Set<Book>> index = new HashMap<>();

    public void addBook(Book book) {
        for (String tag : book.getTags()) {
            index.putIfAbsent(tag.toLowerCase(), new HashSet<>());
            index.get(tag.toLowerCase()).add(book);
        }
    }

    public Set<Book> searchByTags(Set<String> searchTags) {
        if (searchTags == null || searchTags.isEmpty()) return Collections.emptySet();

        Set<Book> result = null;
        for (String tag : searchTags) {
            Set<Book> matchingBooks = index.getOrDefault(tag.toLowerCase().trim(), Collections.emptySet());
            if (result == null) {
                result = new HashSet<>(matchingBooks);
            } else {
                result.retainAll(matchingBooks);
            }
        }
        return result != null ? result : Collections.emptySet();
    }
}