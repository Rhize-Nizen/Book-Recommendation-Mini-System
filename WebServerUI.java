import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import models.Book;
import structures.Trie;
import structures.InvertedIndex;
import engine.RecommenderEngine;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebServerUI {
    private static final Map<String, Book> catalog = new HashMap<>();
    private static final Trie trie = new Trie();
    private static final InvertedIndex invertedIndex = new InvertedIndex();
    private static final RecommenderEngine recommender = new RecommenderEngine();

    public static void main(String[] args) throws IOException {
        loadSampleData();

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // API Endpoints
        server.createContext("/", new StaticHTMLHandler());
        server.createContext("/api/catalog", new CatalogHandler());
        server.createContext("/api/search/prefix", new PrefixSearchHandler());
        server.createContext("/api/search/tags", new TagSearchHandler());
        server.createContext("/api/recommend", new RecommendHandler());

        server.setExecutor(null);
        System.out.println("==========================================================");
        System.out.println("  BOOK RECOMMENDATION SYSTEM - WEB SERVER STARTED        ");
        System.out.println("  Open your browser and go to: http://localhost:" + port);
        System.out.println("==========================================================");
        server.start();
    }

    private static void loadSampleData() {
        List<Book> books = Arrays.asList(
                new Book("B1", "Data Structures in Java", "Robert Lafore", Set.of("dsa", "java", "coding", "software")),
                new Book("B2", "Algorithm Design Manual", "Steven Skiena", Set.of("algorithms", "dsa", "math", "interviewing")),
                new Book("B3", "Java Concurrency in Practice", "Brian Goetz", Set.of("java", "multithreading", "coding", "performance")),
                new Book("B4", "Introduction to Algorithms (CLRS)", "Thomas H. Cormen", Set.of("algorithms", "dsa", "math", "academic")),
                new Book("B5", "Clean Code", "Robert C. Martin", Set.of("coding", "software", "best-practices", "refactoring")),
                new Book("B6", "Design Patterns", "Erich Gamma", Set.of("software", "architecture", "java", "design")),
                new Book("B7", "Effective Java", "Joshua Bloch", Set.of("java", "coding", "best-practices", "programming")),
                new Book("B8", "The Pragmatic Programmer", "Andrew Hunt", Set.of("software", "career", "coding", "productivity")),
                new Book("B9", "Code Complete", "Steve McConnell", Set.of("software", "engineering", "coding", "testing")),
                new Book("B10", "Grokking Algorithms", "Aditya Bhargava", Set.of("algorithms", "dsa", "beginner", "visual")),
                new Book("B11", "Designing Data-Intensive Applications", "Martin Kleppmann", Set.of("systems", "databases", "distributed", "architecture")),
                new Book("B12", "Refactoring", "Martin Fowler", Set.of("coding", "refactoring", "java", "design")),
                new Book("B13", "Head First Design Patterns", "Eric Freeman", Set.of("design", "java", "oop", "visual")),
                new Book("B14", "Python Crash Course", "Eric Matthes", Set.of("python", "coding", "beginner", "projects")),
                new Book("B15", "Fluent Python", "Luciano Ramalho", Set.of("python", "coding", "advanced", "oop")),
                new Book("B16", "Compilers: Principles, Techniques, and Tools", "Alfred Aho", Set.of("compilers", "theory", "cs", "academic")),
                new Book("B17", "Operating System Concepts", "Abraham Silberschatz", Set.of("os", "systems", "cs", "concurrency")),
                new Book("B18", "Computer Networking: A Top-Down Approach", "James Kurose", Set.of("networking", "protocols", "internet", "cs")),
                new Book("B19", "Modern Operating Systems", "Andrew S. Tanenbaum", Set.of("os", "systems", "concurrency", "hardware")),
                new Book("B20", "Artificial Intelligence: A Modern Approach", "Stuart Russell", Set.of("ai", "machine-learning", "algorithms", "cs")),
                new Book("B21", "Pattern Recognition and Machine Learning", "Christopher Bishop", Set.of("ai", "machine-learning", "math", "statistics")),
                new Book("B22", "Deep Learning", "Ian Goodfellow", Set.of("ai", "deep-learning", "math", "neural-networks")),
                new Book("B23", "Hands-On Machine Learning", "Aurélien Géron", Set.of("ai", "python", "machine-learning", "scikit-learn")),
                new Book("B24", "Database System Concepts", "Abraham Silberschatz", Set.of("databases", "sql", "systems", "cs")),
                new Book("B25", "High Performance MySQL", "Silvia Botros", Set.of("databases", "sql", "performance", "systems")),
                new Book("B26", "Structure and Interpretation of Computer Programs", "Harold Abelson", Set.of("cs", "theory", "lisp", "programming")),
                new Book("B27", "The Art of Computer Programming", "Donald Knuth", Set.of("algorithms", "math", "cs", "advanced")),
                new Book("B28", "Cracking the Coding Interview", "Gayle Laakmann McDowell", Set.of("interviewing", "dsa", "coding", "career")),
                new Book("B29", "System Design Interview", "Alex Xu", Set.of("systems", "architecture", "interviewing", "distributed")),
                new Book("B30", "Clean Architecture", "Robert C. Martin", Set.of("architecture", "software", "design", "oop")),
                new Book("B31", "Domain-Driven Design", "Eric Evans", Set.of("architecture", "software", "design", "domain")),
                new Book("B32", "Continuous Delivery", "Jez Humble", Set.of("devops", "ci-cd", "testing", "software")),
                new Book("B33", "The DevOps Handbook", "Gene Kim", Set.of("devops", "management", "software", "ci-cd")),
                new Book("B34", "Site Reliability Engineering", "Betsy Beyer", Set.of("devops", "systems", "google", "cloud")),
                new Book("B35", "Kubernetes Up and Running", "Kelsey Hightower", Set.of("devops", "cloud", "kubernetes", "containers")),
                new Book("B36", "Docker Deep Dive", "Nigel Poulton", Set.of("devops", "docker", "containers", "cloud")),
                new Book("B37", "Rust Programming Language", "Steve Klabnik", Set.of("rust", "systems", "coding", "memory")),
                new Book("B38", "Programming in Lua", "Roberto Ierusalimschy", Set.of("lua", "scripting", "gamedev", "coding")),
                new Book("B39", "Game Programming Patterns", "Robert Nystrom", Set.of("gamedev", "design", "patterns", "c++")),
                new Book("B40", "Real-Time Rendering", "Tomas Akenine-Möller", Set.of("gamedev", "graphics", "3d", "math")),


                new Book("B41", "A Brief History of Time", "Stephen Hawking", Set.of("physics", "astronomy", "cosmology", "science")),
                new Book("B42", "The Selfish Gene", "Richard Dawkins", Set.of("biology", "evolution", "genetics", "science")),
                new Book("B43", "Cosmos", "Carl Sagan", Set.of("astronomy", "science", "space", "physics")),
                new Book("B44", "Sapiens: A Brief History of Humankind", "Yuval Noah Harari", Set.of("history", "anthropology", "science", "civilization")),
                new Book("B45", "Homo Deus", "Yuval Noah Harari", Set.of("future", "history", "technology", "philosophy")),
                new Book("B46", "Thinking, Fast and Slow", "Daniel Kahneman", Set.of("psychology", "decision-making", "science", "mind")),
                new Book("B47", "The Elegant Universe", "Brian Greene", Set.of("physics", "string-theory", "relativity", "space")),
                new Book("B48", "Gödel, Escher, Bach", "Douglas Hofstadter", Set.of("math", "logic", "consciousness", "philosophy")),
                new Book("B49", "The Gene: An Intimate History", "Siddhartha Mukherjee", Set.of("biology", "genetics", "medicine", "science")),
                new Book("B50", "The Emperor of All Maladies", "Siddhartha Mukherjee", Set.of("medicine", "history", "science", "biology")),
                new Book("B51", "What Is Life?", "Erwin Schrödinger", Set.of("physics", "biology", "science", "philosophy")),
                new Book("B52", "Feynman Lectures on Physics", "Richard Feynman", Set.of("physics", "mechanics", "quantum", "math")),
                new Book("B53", "Surely You're Joking, Mr. Feynman!", "Richard Feynman", Set.of("physics", "autobiography", "humor", "science")),
                new Book("B54", "Linear Algebra Done Right", "Sheldon Axler", Set.of("math", "linear-algebra", "academic", "vector-spaces")),
                new Book("B55", "Calculus", "Michael Spivak", Set.of("math", "calculus", "analysis", "academic")),
                new Book("B56", "Principles of Mathematical Analysis", "Walter Rudin", Set.of("math", "analysis", "academic", "advanced")),
                new Book("B57", "Probability Theory: The Logic of Science", "E.T. Jaynes", Set.of("math", "probability", "statistics", "bayesian")),
                new Book("B58", "Astrophysics for People in a Hurry", "Neil deGrasse Tyson", Set.of("astronomy", "physics", "space", "science")),
                new Book("B59", "The Order of Time", "Carlo Rovelli", Set.of("physics", "time", "quantum", "relativity")),
                new Book("B60", "Seven Brief Lessons on Physics", "Carlo Rovelli", Set.of("physics", "science", "universe", "beginner")),
                new Book("B61", "Guns, Germs, and Steel", "Jared Diamond", Set.of("history", "anthropology", "geography", "science")),
                new Book("B62", "Collapse", "Jared Diamond", Set.of("history", "environment", "societies", "ecology")),
                new Book("B63", "The Structure of Scientific Revolutions", "Thomas S. Kuhn", Set.of("philosophy", "science", "history", "paradigm")),
                new Book("B64", "The Blind Watchmaker", "Richard Dawkins", Set.of("biology", "evolution", "genetics", "science")),
                new Book("B65", "The Man Who Knew Infinity", "Robert Kanigel", Set.of("math", "biography", "history", "ramanujan")),
                new Book("B66", "Fermat's Enigma", "Simon Singh", Set.of("math", "history", "cryptography", "fermat")),
                new Book("B67", "The Code Book", "Simon Singh", Set.of("cryptography", "history", "math", "security")),
                new Book("B68", "Immune", "Philipp Dettmer", Set.of("biology", "medicine", "health", "visual")),
                new Book("B69", "Entangled Life", "Merlin Sheldrake", Set.of("biology", "fungi", "nature", "science")),
                new Book("B70", "The Body: A Guide for Occupants", "Bill Bryson", Set.of("biology", "medicine", "health", "human-body")),
                new Book("B71", "A Short History of Nearly Everything", "Bill Bryson", Set.of("science", "history", "humor", "general")),
                new Book("B72", "The Origin of Species", "Charles Darwin", Set.of("biology", "evolution", "history", "classic")),
                new Book("B73", "Quantum Mechanics and Path Integrals", "Richard Feynman", Set.of("physics", "quantum", "math", "advanced")),
                new Book("B74", "The Character of Physical Law", "Richard Feynman", Set.of("physics", "philosophy", "science", "laws")),
                new Book("B75", "The Fabric of the Cosmos", "Brian Greene", Set.of("physics", "space", "time", "quantum")),


                new Book("B76", "Atomic Habits", "James Clear", Set.of("productivity", "habits", "self-help", "psychology")),
                new Book("B77", "Deep Work", "Cal Newport", Set.of("productivity", "focus", "career", "work")),
                new Book("B78", "Digital Minimalism", "Cal Newport", Set.of("productivity", "technology", "focus", "mindfulness")),
                new Book("B79", "So Good They Can't Ignore You", "Cal Newport", Set.of("career", "productivity", "skills", "work")),
                new Book("B80", "The Psychology of Money", "Morgan Housel", Set.of("finance", "investing", "psychology", "wealth")),
                new Book("B81", "Rich Dad Poor Dad", "Robert Kiyosaki", Set.of("finance", "investing", "money", "mindset")),
                new Book("B82", "The Intelligent Investor", "Benjamin Graham", Set.of("finance", "investing", "stocks", "value")),
                new Book("B83", "Zero to One", "Peter Thiel", Set.of("business", "startups", "entrepreneurship", "technology")),
                new Book("B84", "The Lean Startup", "Eric Ries", Set.of("business", "startups", "entrepreneurship", "management")),
                new Book("B85", "Good to Great", "Jim Collins", Set.of("business", "leadership", "management", "strategy")),
                new Book("B86", "Built to Last", "Jim Collins", Set.of("business", "leadership", "strategy", "companies")),
                new Book("B87", "Principles: Life and Work", "Ray Dalio", Set.of("business", "management", "leadership", "mindset")),
                new Book("B88", "The Personal MBA", "Josh Kaufman", Set.of("business", "management", "marketing", "finance")),
                new Book("B89", "Hooked: How to Build Habit-Forming Products", "Nir Eyal", Set.of("product", "design", "psychology", "business")),
                new Book("B90", "Inspired: How to Create Tech Products", "Marty Cagan", Set.of("product", "management", "tech", "software")),
                new Book("B91", "Measure What Matters", "John Doerr", Set.of("management", "okrs", "business", "leadership")),
                new Book("B92", "The Hard Thing About Hard Things", "Ben Horowitz", Set.of("startups", "business", "leadership", "management")),
                new Book("B93", "Shoe Dog", "Phil Knight", Set.of("business", "biography", "nike", "entrepreneurship")),
                new Book("B94", "Steve Jobs", "Walter Isaacson", Set.of("biography", "tech", "business", "apple")),
                new Book("B95", "Elon Musk", "Walter Isaacson", Set.of("biography", "tech", "business", "spacex")),
                new Book("B96", "The Everything Store", "Brad Stone", Set.of("business", "tech", "amazon", "biography")),
                new Book("B97", "Influence: The Psychology of Persuasion", "Robert Cialdini", Set.of("psychology", "marketing", "persuasion", "business")),
                new Book("B98", "Never Split the Difference", "Chris Voss", Set.of("negotiation", "psychology", "business", "communication")),
                new Book("B99", "Crucial Conversations", "Kerry Patterson", Set.of("communication", "leadership", "business", "relationships")),
                new Book("B100", "How to Win Friends and Influence People", "Dale Carnegie", Set.of("communication", "relationships", "self-help", "business")),
                new Book("B101", "The 7 Habits of Highly Effective People", "Stephen Covey", Set.of("productivity", "leadership", "habits", "self-help")),
                new Book("B102", "Essentialism: The Disciplined Pursuit of Less", "Greg McKeown", Set.of("productivity", "focus", "time-management", "lifestyle")),
                new Book("B103", "The 4-Hour Workweek", "Timothy Ferriss", Set.of("lifestyle", "business", "productivity", "remote")),
                new Book("B104", "Getting Things Done (GTD)", "David Allen", Set.of("productivity", "organization", "workflow", "time-management")),
                new Book("B105", "Make Time", "Jake Knapp", Set.of("productivity", "focus", "time-management", "habits")),
                new Book("B106", "Extreme Ownership", "Jocko Willink", Set.of("leadership", "management", "military", "mindset")),
                new Book("B107", "Start with Why", "Simon Sinek", Set.of("leadership", "business", "marketing", "motivation")),
                new Book("B108", "Leaders Eat Last", "Simon Sinek", Set.of("leadership", "management", "teamwork", "culture")),
                new Book("B109", "Drive: The Surprising Truth About Motivation", "Daniel H. Pink", Set.of("psychology", "motivation", "business", "leadership")),
                new Book("B110", "To Sell Is Human", "Daniel H. Pink", Set.of("sales", "marketing", "business", "communication")),
                new Book("B111", "Rework", "Jason Fried", Set.of("business", "startups", "productivity", "remote")),
                new Book("B112", "Company of One", "Paul Jarvis", Set.of("business", "entrepreneurship", "lifestyle", "solo")),
                new Book("B113", "Financial Freedom", "Grant Sabatier", Set.of("finance", "investing", "money", "independence")),
                new Book("B114", "The Millionaire Fastlane", "MJ DeMarco", Set.of("finance", "business", "wealth", "entrepreneurship")),
                new Book("B115", "Your Money or Your Life", "Vicki Robin", Set.of("finance", "budgeting", "lifestyle", "independence")),


                new Book("B116", "1984", "George Orwell", Set.of("fiction", "dystopia", "classic", "politics")),
                new Book("B117", "Animal Farm", "George Orwell", Set.of("fiction", "satire", "politics", "classic")),
                new Book("B118", "Brave New World", "Aldous Huxley", Set.of("fiction", "dystopia", "sci-fi", "classic")),
                new Book("B119", "Fahrenheit 451", "Ray Bradbury", Set.of("fiction", "dystopia", "sci-fi", "classic")),
                new Book("B120", "Dune", "Frank Herbert", Set.of("sci-fi", "fiction", "epic", "space")),
                new Book("B121", "Dune Messiah", "Frank Herbert", Set.of("sci-fi", "fiction", "space", "sequel")),
                new Book("B122", "Foundation", "Isaac Asimov", Set.of("sci-fi", "space", "empire", "classic")),
                new Book("B123", "Foundation and Empire", "Isaac Asimov", Set.of("sci-fi", "space", "classic", "empire")),
                new Book("B124", "I, Robot", "Isaac Asimov", Set.of("sci-fi", "robots", "ai", "classic")),
                new Book("B125", "Neuromancer", "William Gibson", Set.of("sci-fi", "cyberpunk", "tech", "classic")),
                new Book("B126", "Snow Crash", "Neal Stephenson", Set.of("sci-fi", "cyberpunk", "metaverse", "tech")),
                new Book("B127", "The Hobbit", "J.R.R. Tolkien", Set.of("fantasy", "adventure", "classic", "epic")),
                new Book("B128", "The Fellowship of the Ring", "J.R.R. Tolkien", Set.of("fantasy", "epic", "adventure", "magic")),
                new Book("B129", "The Two Towers", "J.R.R. Tolkien", Set.of("fantasy", "epic", "war", "sequel")),
                new Book("B130", "The Return of the King", "J.R.R. Tolkien", Set.of("fantasy", "epic", "climax", "magic")),
                new Book("B131", "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", Set.of("fantasy", "magic", "adventure", "ya")),
                new Book("B132", "Harry Potter and the Chamber of Secrets", "J.K. Rowling", Set.of("fantasy", "magic", "mystery", "ya")),
                new Book("B133", "Harry Potter and the Prisoner of Azkaban", "J.K. Rowling", Set.of("fantasy", "magic", "adventure", "ya")),
                new Book("B134", "The Name of the Wind", "Patrick Rothfuss", Set.of("fantasy", "magic", "epic", "music")),
                new Book("B135", "The Way of Kings", "Brandon Sanderson", Set.of("fantasy", "epic", "magic", "stormlight")),
                new Book("B136", "Mistborn: The Final Empire", "Brandon Sanderson", Set.of("fantasy", "magic", "heist", "action")),
                new Book("B137", "A Game of Thrones", "George R.R. Martin", Set.of("fantasy", "epic", "politics", "dragons")),
                new Book("B138", "A Clash of Kings", "George R.R. Martin", Set.of("fantasy", "war", "politics", "epic")),
                new Book("B139", "To Kill a Mockingbird", "Harper Lee", Set.of("fiction", "classic", "justice", "drama")),
                new Book("B140", "The Great Gatsby", "F. Scott Fitzgerald", Set.of("fiction", "classic", "drama", "1920s")),
                new Book("B141", "Catcher in the Rye", "J.D. Salinger", Set.of("fiction", "classic", "coming-of-age", "drama")),
                new Book("B142", "Pride and Prejudice", "Jane Austen", Set.of("fiction", "romance", "classic", "drama")),
                new Book("B143", "Crime and Punishment", "Fyodor Dostoevsky", Set.of("fiction", "philosophy", "psychology", "classic")),
                new Book("B144", "The Brothers Karamazov", "Fyodor Dostoevsky", Set.of("fiction", "philosophy", "religion", "classic")),
                new Book("B145", "The Metamorphosis", "Franz Kafka", Set.of("fiction", "classic", "surreal", "existential")),
                new Book("B146", "The Trial", "Franz Kafka", Set.of("fiction", "classic", "law", "surreal")),
                new Book("B147", "The Stranger", "Albert Camus", Set.of("fiction", "philosophy", "existentialism", "classic")),
                new Book("B148", "The Plague", "Albert Camus", Set.of("fiction", "philosophy", "crisis", "classic")),
                new Book("B149", "Project Hail Mary", "Andy Weir", Set.of("sci-fi", "space", "science", "survival")),
                new Book("B150", "The Martian", "Andy Weir", Set.of("sci-fi", "space", "survival", "science")),
                new Book("B151", "Three-Body Problem", "Cixin Liu", Set.of("sci-fi", "aliens", "physics", "epic")),
                new Book("B152", "The Dark Forest", "Cixin Liu", Set.of("sci-fi", "space", "theory", "sequel")),
                new Book("B153", "Death's End", "Cixin Liu", Set.of("sci-fi", "space", "universe", "climax")),
                new Book("B154", "Ender's Game", "Orson Scott Card", Set.of("sci-fi", "war", "military", "space")),
                new Book("B155", "Do Androids Dream of Electric Sheep?", "Philip K. Dick", Set.of("sci-fi", "cyberpunk", "robots", "classic")),
                new Book("B156", "Ubik", "Philip K. Dick", Set.of("sci-fi", "surreal", "mind", "classic")),
                new Book("B157", "Slaughterhouse-Five", "Kurt Vonnegut", Set.of("fiction", "war", "sci-fi", "satire")),
                new Book("B158", "Cat's Cradle", "Kurt Vonnegut", Set.of("fiction", "satire", "sci-fi", "religion")),
                new Book("B159", "The Count of Monte Cristo", "Alexandre Dumas", Set.of("fiction", "classic", "revenge", "adventure")),
                new Book("B160", "Les Misérables", "Victor Hugo", Set.of("fiction", "classic", "history", "drama")),


                new Book("B161", "Meditations", "Marcus Aurelius", Set.of("philosophy", "stoicism", "mindset", "classic")),
                new Book("B162", "Letters from a Stoic", "Seneca", Set.of("philosophy", "stoicism", "life", "wisdom")),
                new Book("B163", "Discourses and Enchiridion", "Epictetus", Set.of("philosophy", "stoicism", "virtue", "mind")),
                new Book("B164", "The Daily Stoic", "Ryan Holiday", Set.of("philosophy", "stoicism", "daily", "self-help")),
                new Book("B165", "The Obstacle Is the Way", "Ryan Holiday", Set.of("philosophy", "stoicism", "resilience", "mindset")),
                new Book("B166", "Ego Is the Enemy", "Ryan Holiday", Set.of("philosophy", "mindset", "success", "leadership")),
                new Book("B167", "Stillness Is the Key", "Ryan Holiday", Set.of("philosophy", "mindfulness", "focus", "stoicism")),
                new Book("B168", "Beyond Good and Evil", "Friedrich Nietzsche", Set.of("philosophy", "ethics", "classic", "existentialism")),
                new Book("B169", "Thus Spoke Zarathustra", "Friedrich Nietzsche", Set.of("philosophy", "existentialism", "morality", "classic")),
                new Book("B170", "The Republic", "Plato", Set.of("philosophy", "politics", "justice", "classic")),
                new Book("B171", "Nicomachean Ethics", "Aristotle", Set.of("philosophy", "ethics", "virtue", "classic")),
                new Book("B172", "Man's Search for Meaning", "Viktor E. Frankl", Set.of("psychology", "philosophy", "meaning", "holocaust")),
                new Book("B173", "The Art of War", "Sun Tzu", Set.of("strategy", "military", "philosophy", "leadership")),
                new Book("B174", "The Prince", "Niccolò Machiavelli", Set.of("politics", "strategy", "philosophy", "leadership")),
                new Book("B175", "The Laws of Human Nature", "Robert Greene", Set.of("psychology", "human-nature", "power", "mindset")),
                new Book("B176", "The 48 Laws of Power", "Robert Greene", Set.of("power", "strategy", "psychology", "business")),
                new Book("B177", "The Art of Seduction", "Robert Greene", Set.of("psychology", "persuasion", "relationships", "strategy")),
                new Book("B178", "Outliers: The Story of Success", "Malcolm Gladwell", Set.of("psychology", "success", "culture", "stories")),
                new Book("B179", "Blink: The Power of Thinking Without Thinking", "Malcolm Gladwell", Set.of("psychology", "intuition", "decision-making", "mind")),
                new Book("B180", "The Tipping Point", "Malcolm Gladwell", Set.of("sociology", "trends", "marketing", "psychology")),
                new Book("B181", "Quiet: The Power of Introverts", "Susan Cain", Set.of("psychology", "introversion", "personality", "work")),
                new Book("B182", "Grit: The Power of Passion and Perseverance", "Angela Duckworth", Set.of("psychology", "success", "passion", "mindset")),
                new Book("B183", "Mindset: The New Psychology of Success", "Carol S. Dweck", Set.of("psychology", "mindset", "growth", "learning")),
                new Book("B184", "Flow: The Psychology of Optimal Experience", "Mihaly Csikszentmihalyi", Set.of("psychology", "focus", "happiness", "performance")),
                new Book("B185", "Stolen Focus", "Johann Hari", Set.of("psychology", "focus", "technology", "attention")),
                new Book("B186", "The Anxious Generation", "Jonathan Haidt", Set.of("psychology", "technology", "youth", "society")),
                new Book("B187", "The Righteous Mind", "Jonathan Haidt", Set.of("psychology", "politics", "morality", "culture")),
                new Book("B188", "The Coddling of the American Mind", "Jonathan Haidt", Set.of("sociology", "psychology", "culture", "education")),
                new Book("B189", "Factfulness", "Hans Rosling", Set.of("statistics", "world", "progress", "data")),
                new Book("B190", "Enlightenment Now", "Steven Pinker", Set.of("history", "science", "progress", "optimism")),
                new Book("B191", "The Better Angels of Our Nature", "Steven Pinker", Set.of("history", "psychology", "violence", "progress")),
                new Book("B192", "Why We Sleep", "Matthew Walker", Set.of("health", "science", "sleep", "biology")),
                new Book("B193", "Breath: The New Science of a Lost Art", "James Nestor", Set.of("health", "science", "breathing", "wellness")),
                new Book("B194", "Lifespan: Why We Age—and Why We Don't Have To", "David A. Sinclair", Set.of("biology", "longevity", "genetics", "science")),
                new Book("B195", "Outlive: The Science and Art of Longevity", "Peter Attia", Set.of("health", "medicine", "longevity", "science")),
                new Book("B196", "The Silk Roads", "Peter Frankopan", Set.of("history", "world", "trade", "geography")),
                new Book("B197", "The Dawn of Everything", "David Graeber", Set.of("history", "anthropology", "civilization", "society")),
                new Book("B198", "Debt: The First 5,000 Years", "David Graeber", Set.of("history", "economics", "anthropology", "debt")),
                new Book("B199", "Capital in the Twenty-First Century", "Thomas Piketty", Set.of("economics", "inequality", "finance", "history")),
                new Book("B200", "The Wealth of Nations", "Adam Smith", Set.of("economics", "classic", "history", "business"))
        );

        for (Book b : books) {
            catalog.put(b.getId(), b);
            trie.insert(b.getTitle(), b);
            invertedIndex.addBook(b);
        }
    }

    static class StaticHTMLHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = getHTMLContent();
            byte[] response = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    static class CatalogHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, booksToJson(catalog.values()));
        }
    }

    static class PrefixSearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getQuery());
            String prefix = query.getOrDefault("prefix", "");
            List<Book> results = trie.searchPrefix(prefix);
            sendJsonResponse(exchange, booksToJson(results));
        }
    }

    static class TagSearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getQuery());
            String tagsParam = query.getOrDefault("tags", "");
            Set<String> tags = new HashSet<>();
            for (String t : tagsParam.split(",")) {
                if (!t.trim().isEmpty()) tags.add(t.trim());
            }
            Set<Book> results = invertedIndex.searchByTags(tags);
            sendJsonResponse(exchange, booksToJson(results));
        }
    }

    static class RecommendHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getQuery());
            String bookId = query.getOrDefault("id", "").toUpperCase();
            int k = Integer.parseInt(query.getOrDefault("k", "3"));

            Book target = catalog.get(bookId);
            if (target == null) {
                sendJsonResponse(exchange, "[]");
                return;
            }

            List<RecommenderEngine.Recommendation> recs = 
                recommender.getTopKRecommendations(target, new ArrayList<>(catalog.values()), k);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < recs.size(); i++) {
                json.append(recs.get(i).toJson());
                if (i < recs.size() - 1) json.append(",");
            }
            json.append("]");

            sendJsonResponse(exchange, json.toString());
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    private static String booksToJson(Collection<Book> books) {
        StringBuilder json = new StringBuilder("[");
        int i = 0;
        for (Book b : books) {
            json.append(b.toJson());
            if (++i < books.size()) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], java.net.URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
            } else if (entry.length == 1) {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    private static String getHTMLContent() {
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Book Recommendation Engine (DSA)</title>
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap" rel="stylesheet">
            <style>
                :root {
                    --bg-gradient: linear-gradient(135deg, #0f172a 0%, #1e1b4b 100%);
                    --glass-bg: rgba(255, 255, 255, 0.05);
                    --glass-border: rgba(255, 255, 255, 0.12);
                    --accent: #6366f1;
                    --accent-hover: #4f46e5;
                    --text-main: #f8fafc;
                    --text-muted: #94a3b8;
                }
                * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Inter', sans-serif; }
                body { background: var(--bg-gradient); color: var(--text-main); min-height: 100vh; padding: 2rem; }
                .container { max-width: 1100px; margin: 0 auto; }
                header { text-align: center; margin-bottom: 2.5rem; }
                header h1 { font-size: 2.2rem; font-weight: 700; margin-bottom: 0.5rem; background: linear-gradient(to right, #818cf8, #c084fc); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
                header p { color: var(--text-muted); font-size: 0.95rem; }
                
                .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.5rem; margin-bottom: 2rem; }
                .card { background: var(--glass-bg); border: 1px solid var(--glass-border); backdrop-filter: blur(12px); border-radius: 16px; padding: 1.5rem; transition: transform 0.2s, border-color 0.2s; }
                .card:hover { border-color: rgba(99, 102, 241, 0.4); transform: translateY(-2px); }
                .card h3 { font-size: 1.1rem; margin-bottom: 1rem; color: #a5b4fc; display: flex; align-items: center; justify-content: space-between; }
                .badge { font-size: 0.75rem; padding: 0.2rem 0.6rem; border-radius: 20px; background: rgba(99, 102, 241, 0.2); color: #c7d2fe; }

                .form-group { margin-bottom: 1rem; }
                label { display: block; font-size: 0.85rem; color: var(--text-muted); margin-bottom: 0.4rem; }
                input { width: 100%; padding: 0.75rem 1rem; border-radius: 8px; border: 1px solid var(--glass-border); background: rgba(15, 23, 42, 0.6); color: #fff; font-size: 0.9rem; outline: none; transition: border-color 0.2s; }
                input:focus { border-color: var(--accent); }
                
                button { width: 100%; padding: 0.75rem; border-radius: 8px; border: none; background: var(--accent); color: white; font-weight: 600; cursor: pointer; transition: background 0.2s; }
                button:hover { background: var(--accent-hover); }

                .results-container { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: 16px; padding: 1.5rem; }
                .results-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; padding-bottom: 0.8rem; border-bottom: 1px solid var(--glass-border); }
                .results-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1rem; }
                
                .book-card { background: rgba(15, 23, 42, 0.5); border: 1px solid var(--glass-border); border-radius: 12px; padding: 1rem; }
                .book-card h4 { font-size: 1rem; color: #f1f5f9; margin-bottom: 0.3rem; }
                .book-card p { font-size: 0.85rem; color: var(--text-muted); margin-bottom: 0.6rem; }
                .tag-list { display: flex; flex-wrap: wrap; gap: 0.3rem; }
                .tag { font-size: 0.7rem; background: rgba(255, 255, 255, 0.08); padding: 0.15rem 0.5rem; border-radius: 4px; color: #cbd5e1; }
                .sim-score { font-size: 0.8rem; color: #34d399; font-weight: 600; margin-top: 0.4rem; }
            </style>
        </head>
        <body>
            <div class="container">
                <header>
                    <h1>Book Recommendation Engine</h1>
                    <p>Powered by Java Data Structures (Trie • Inverted Index • Jaccard Graph • Max-Heap)</p>
                </header>

                <div class="grid">
                    <!-- Trie Prefix Search -->
                    <div class="card">
                        <h3>Prefix Title Search <span class="badge">O(L) Trie</span></h3>
                        <div class="form-group">
                            <label>Book Title Prefix</label>
                            <input type="text" id="prefixInput" placeholder="e.g., Alg, Java, Clean" onkeyup="searchPrefix()">
                        </div>
                    </div>

                    <!-- Inverted Index Tag Search -->
                    <div class="card">
                        <h3>Multi-Tag Filter <span class="badge">O(1) Hash Map</span></h3>
                        <div class="form-group">
                            <label>Comma Separated Tags</label>
                            <input type="text" id="tagsInput" placeholder="e.g., dsa, java, math">
                        </div>
                        <button onclick="searchTags()">Filter Tags</button>
                    </div>

                    <!-- Recommender Engine -->
                    <div class="card">
                        <h3>Recommendations <span class="badge">Max-Heap Top-K</span></h3>
                        <div style="display: flex; gap: 0.5rem;">
                            <div class="form-group" style="flex: 2;">
                                <label>Target Book ID</label>
                                <input type="text" id="recIdInput" placeholder="e.g., B1, B2">
                            </div>
                            <div class="form-group" style="flex: 1;">
                                <label>Top-K</label>
                                <input type="number" id="recKInput" value="3" min="1">
                            </div>
                        </div>
                        <button onclick="getRecommendations()">Get Top Recommendations</button>
                    </div>
                </div>

                <div class="results-container">
                    <div class="results-header">
                        <h3 id="resultsTitle">Catalog Overview</h3>
                        <button style="width: auto; padding: 0.4rem 1rem;" onclick="loadCatalog()">Reset / Show All</button>
                    </div>
                    <div class="results-list" id="resultsList"></div>
                </div>
            </div>

            <script>
                window.onload = loadCatalog;

                function renderBooks(books, isRec = false) {
                    const list = document.getElementById('resultsList');
                    list.innerHTML = '';

                    if (!books || books.length === 0) {
                        list.innerHTML = '<p style="color: var(--text-muted); grid-column: 1/-1;">No matching books found.</p>';
                        return;
                    }

                    books.forEach(item => {
                        const book = isRec ? item.book : item;
                        const score = isRec ? item.similarityScore : null;

                        const card = document.createElement('div');
                        card.className = 'book-card';
                        card.innerHTML = `
                            <h4>[${book.id}] ${book.title}</h4>
                            <p>Author: ${book.author}</p>
                            <div class="tag-list">
                                ${book.tags.map(t => `<span class="tag">#${t}</span>`).join('')}
                            </div>
                            ${score !== null ? `<div class="sim-score">Similarity: ${(score * 100).toFixed(0)}%</div>` : ''}
                        `;
                        list.appendChild(card);
                    });
                }

                function loadCatalog() {
                    document.getElementById('resultsTitle').innerText = 'Complete Catalog';
                    fetch('/api/catalog')
                        .then(res => res.json())
                        .then(data => renderBooks(data));
                }

                function searchPrefix() {
                    const val = document.getElementById('prefixInput').value.trim();
                    if (!val) { loadCatalog(); return; }
                    document.getElementById('resultsTitle').innerText = `Prefix Results for "${val}"`;
                    fetch(`/api/search/prefix?prefix=${encodeURIComponent(val)}`)
                        .then(res => res.json())
                        .then(data => renderBooks(data));
                }

                function searchTags() {
                    const val = document.getElementById('tagsInput').value.trim();
                    if (!val) { loadCatalog(); return; }
                    document.getElementById('resultsTitle').innerText = `Matching Tags: ${val}`;
                    fetch(`/api/search/tags?tags=${encodeURIComponent(val)}`)
                        .then(res => res.json())
                        .then(data => renderBooks(data));
                }

                function getRecommendations() {
                    const id = document.getElementById('recIdInput').value.trim();
                    const k = document.getElementById('recKInput').value.trim();
                    if (!id) return;
                    document.getElementById('resultsTitle').innerText = `Recommendations for Book [${id.toUpperCase()}]`;
                    fetch(`/api/recommend?id=${encodeURIComponent(id)}&k=${k}`)
                        .then(res => res.json())
                        .then(data => renderBooks(data, true));
                }
            </script>
        </body>
        </html>
        """;
    }
}