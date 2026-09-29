package structures;

import models.Book;
import java.util.*;

public class Trie {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        List<Book> books = new ArrayList<>();
    }

    private final TrieNode root = new TrieNode();

    public void insert(String title, Book book) {
        TrieNode current = root;
        for (char ch : title.toLowerCase().toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }
        current.books.add(book);
    }

    public List<Book> searchPrefix(String prefix) {
        TrieNode current = root;
        for (char ch : prefix.toLowerCase().toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return Collections.emptyList();
            }
            current = current.children.get(ch);
        }

        List<Book> result = new ArrayList<>();
        collectAllBooks(current, result);
        return result;
    }

    private void collectAllBooks(TrieNode node, List<Book> result) {
        result.addAll(node.books);
        for (TrieNode child : node.children.values()) {
            collectAllBooks(child, result);
        }
    }
}