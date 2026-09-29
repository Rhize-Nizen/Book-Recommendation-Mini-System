# Book Recommendation System & Search Engine

An in-memory Java search engine and book recommendation system that uses fundamental computer science data structures—including a **Trie**, **Inverted Index**, and **Max-Heap Priority Queue**—to deliver instant title autocomplete, multi-tag filtering, and Jaccard-similarity-based recommendations.

---

## Key Features

* **Instant Title Autocomplete**: Prefix-based search powered by a custom Trie.
* **Multi-Tag Boolean Search**: Fast tag filtering utilizing an Inverted Index with set intersection (`AND` logic).
* **Jaccard Similarity Recommendations**: Ranked recommendations generated via tag overlap ratios and extracted using a Max-Heap.
* **Lightweight REST API**: Embedded HTTP server serving JSON endpoints without external framework dependencies.
* **Multi-Indexed Architecture**: Single memory footprint with concurrent index mapping ($O(1)$ ID lookups, $O(L)$ prefix searches, and fast set operations).

---

## Data Structures & Algorithmic Breakdown

### 1. Trie (`structures/Trie.java`)
* **Purpose**: Title prefix search and autocomplete.
* **Implementation**: A tree structure where each `TrieNode` contains a `Map<Character, TrieNode>` for dynamic branching and a `List<Book>` storing books whose titles end at that node.
* **Traversal**: Uses Depth-First Search (DFS) recursion (`collectAllBooks`) to gather all matching titles under a prefix branch.
* **Complexity**: Search time is $O(L + N + K)$, where $L$ is prefix length, $N$ is subtree nodes visited, and $K$ is matching books collected.

### 2. Inverted Index (`structures/InvertedIndex.java`)
* **Purpose**: Multi-tag filtering.
* **Implementation**: A `Map<String, Set<Book>>` mapping normalized tag keywords to `HashSet` collections of matching books.
* **Search Logic**: Evaluates multi-tag queries using set intersection (`Set.retainAll()`), ensuring returned books possess **all** queried tags.
* **Complexity**: Lookup and intersection take $O(T \times M)$ time, where $T$ is the query tag count and $M$ is the average books per tag set.

### 3. Recommender Engine (`engine/RecommenderEngine.java`)
* **Purpose**: Content-based recommendation system.
* **Similarity Metric**: Calculates the Jaccard Similarity Coefficient across tag sets:
  
  $$J(A, B) = \frac{\vert{}A \cap B\vert{}}{\vert{}A \cup B\vert{}}$$

* **Ranking**: Pushes candidate recommendations with non-zero similarity scores into a Max-Heap (`PriorityQueue` with a descending comparator) and extracts the top $K$ items.
* **Complexity**: $O(N \cdot T + N \log N)$ time, where $N$ is total books and $T$ is average tag set size.

---

## Project Structure

```text
├── models/
│   └── Book.java                  # Model class with normalized tag sets & JSON exporter
├── structures/
│   ├── Trie.java                  # Prefix tree for title search
│   └── InvertedIndex.java         # Tag-to-book map for filtering
├── engine/
│   └── RecommenderEngine.java     # Jaccard calculator & PriorityQueue Max-Heap ranker
└── WebServerUI.java               # Server entry point, sample dataset, & HTTP route handlers