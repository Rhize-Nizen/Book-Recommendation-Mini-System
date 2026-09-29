package engine;

import models.Book;
import java.util.*;

public class RecommenderEngine {

    public static class Recommendation {
        private final Book book;
        private final double similarityScore;

        public Recommendation(Book book, double similarityScore) {
            this.book = book;
            this.similarityScore = similarityScore;
        }

        public Book getBook() { return book; }
        public double getSimilarityScore() { return similarityScore; }

        public String toJson() {
            return String.format("{\"book\":%s, \"similarityScore\":%.2f}", book.toJson(), similarityScore);
        }
    }

    public double calculateJaccardSimilarity(Book b1, Book b2) {
        Set<String> intersection = new HashSet<>(b1.getTags());
        intersection.retainAll(b2.getTags());

        Set<String> union = new HashSet<>(b1.getTags());
        union.addAll(b2.getTags());

        if (union.isEmpty()) return 0.0;
        return (double) intersection.size() / union.size();
    }

    public List<Recommendation> getTopKRecommendations(Book target, List<Book> allBooks, int k) {
        PriorityQueue<Recommendation> maxHeap = new PriorityQueue<>(
            (a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore())
        );

        for (Book other : allBooks) {
            if (other.getId().equalsIgnoreCase(target.getId())) continue;

            double simScore = calculateJaccardSimilarity(target, other);
            if (simScore > 0.0) {
                maxHeap.offer(new Recommendation(other, simScore));
            }
        }

        List<Recommendation> topK = new ArrayList<>();
        while (!maxHeap.isEmpty() && topK.size() < k) {
            topK.add(maxHeap.poll());
        }

        return topK;
    }
}