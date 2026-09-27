#  Tag-Based Library Search & Recommendation System

An intelligent library management and book recommendation engine built using custom Data Structures and Algorithms. The system enables instant prefix-based book search, multi-tag filtering via inverted indexing, and graph-based book recommendations ranked using priority queues.

---

##  Features

- ** Prefix Auto-Complete & Search:** Lightning-fast title and author lookup using a Trie data structure.
- ** Multi-Tag Filtering:** Instant tag-based filtering using an Inverted Index and optimized set intersections.
- ** Tag-Based Recommendation Engine:** Graph-based book similarity matching calculated via Jaccard Similarity.
- ** Top-K Recommendation Ranking:** Efficient ranking using a Max-Heap / Priority Queue.
- ** Interactive API & UI:** Built with FastAPI and an interactive web interface for real-time search and recommendations.

---

##  Tech Stack & DSA Architecture

| Component | Framework / Tool | Data Structure / Algorithm Applied |
| :--- | :--- | :--- |
| **Search Engine** | Python | **Trie (Prefix Tree)** — $O(L)$ prefix matching |
| **Tag Indexing** | Python | **Inverted Index (Hash Maps & Sets)** — $O(1)$ tag lookup |
| **Recommender System** | Python | **Graph Structure + Jaccard Similarity** |
| **Ranking Engine** | Python | **Max-Heap (Priority Queue)** — $O(N \log K)$ Top-K retrieval |
| **Backend API** | FastAPI | RESTful Endpoints |
| **Frontend** | HTML/CSS/JS (or React) | Dynamic UI & Performance Metrics |

---

##  Project Structure

```text
├── backend/
│   ├── data/
│   │   └── books.json          # Dataset of books with tag metadata
│   ├── structures/
│   │   ├── trie.py             # Custom Trie for prefix search
│   │   ├── inverted_index.py   # Inverted Index for multi-tag filtering
│   │   └── graph.py            # Graph and Similarity metrics
│   ├── main.py                 # FastAPI application and routes
│   └── requirements.txt
├── frontend/                   # User interface files
└── README.md