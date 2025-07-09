import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LRUCacheDemo2 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("LRU Cache Visualizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("LRU Visualizer", new CachePanel());
            tabbedPane.addTab("Info", new InfoPanel());

            frame.setContentPane(tabbedPane);
            frame.setVisible(true);
        });
    }
}

// ================= CACHE PANEL =================
class CachePanel extends JPanel {
    private final int CACHE_CAPACITY = 5;
    private LRUCache<Integer, String> cache;
    private JTextField keyField, valueField;
    private JButton putButton, getButton, clearButton;
    private JLabel statusLabel, cacheSizeLabel, statsLabel;
    private JTextArea operationLog;
    private CacheDiagram diagram;

    private int hits = 0;
    private int misses = 0;

    public CachePanel() {
        setLayout(new BorderLayout());
        cache = new LRUCache<>(CACHE_CAPACITY);

        // Input Panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(new Color(220, 220, 220));

        keyField = new JTextField(5);
        valueField = new JTextField(10);
        putButton = new JButton("PUT");
        getButton = new JButton("GET");
        clearButton = new JButton("CLEAR CACHE");

        styleButton(putButton, new Color(70, 130, 180));
        styleButton(getButton, new Color(46, 139, 87));
        styleButton(clearButton, new Color(178, 34, 34));

        inputPanel.add(new JLabel("Key:"));
        inputPanel.add(keyField);
        inputPanel.add(new JLabel("Value:"));
        inputPanel.add(valueField);
        inputPanel.add(putButton);
        inputPanel.add(getButton);
        inputPanel.add(clearButton);

        // Diagram
        diagram = new CacheDiagram();
        JScrollPane diagramScroll = new JScrollPane(diagram);

        // Log
        operationLog = new JTextArea(6, 50);
        operationLog.setEditable(false);
        operationLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(operationLog);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Operation Log"));

        // Bottom Status Panel
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        cacheSizeLabel = new JLabel("Cache Size: 0 / " + CACHE_CAPACITY, SwingConstants.CENTER);
        cacheSizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        statsLabel = new JLabel("Hits: 0 | Misses: 0 | Hit Rate: 0.00%", SwingConstants.CENTER);
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        bottomPanel.add(statusLabel);
        bottomPanel.add(cacheSizeLabel);
        bottomPanel.add(statsLabel);

        // Layout
        add(inputPanel, BorderLayout.NORTH);
        add(diagram, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        putButton.addActionListener(e -> {
            try {
                int key = Integer.parseInt(keyField.getText());
                String value = valueField.getText();

                if (value.isEmpty()) {
                    statusLabel.setText("Value cannot be empty for PUT operation.");
                    return;
                }

                boolean updated = cache.contains(key);
                cache.put(key, value);
                updateStatus("PUT", key, value, updated ? "Updated existing key." : "Inserted new key.");
                updateDiagram();
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid key. Please enter an integer.");
            }
        });

        getButton.addActionListener(e -> {
            try {
                int key = Integer.parseInt(keyField.getText());
                String val = cache.get(key);
                if (val != null) {
                    hits++;
                    updateStatus("GET", key, val, "Key found. Moved to MRU.");
                } else {
                    misses++;
                    statusLabel.setText("Key " + key + " not found.");
                    log("GET " + key + " ➜ Miss (not found)");
                }
                updateStats();
                updateDiagram();
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid key. Please enter an integer.");
            }
        });

        clearButton.addActionListener(e -> {
            cache.clear();
            hits = 0;
            misses = 0;
            updateStats();
            updateStatus("CLEAR", -1, "", "All items cleared.");
            updateDiagram();
        });
    }

    private void updateStatus(String op, int key, String val, String extra) {
        String msg = switch (op) {
            case "PUT" -> "PUT [" + key + ":" + val + "] ➜ " + extra;
            case "GET" -> "GET [" + key + "] ➜ Found: " + val;
            case "CLEAR" -> "Cache cleared.";
            default -> "";
        };
        statusLabel.setText(msg);
        log(msg);
        cacheSizeLabel.setText("Cache Size: " + cache.size() + " / " + CACHE_CAPACITY);
    }

    private void updateStats() {
        double hitRate = (hits + misses) == 0 ? 0.0 : (hits * 100.0) / (hits + misses);
        statsLabel.setText("Hits: " + hits + " | Misses: " + misses + " | Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }

    private void log(String msg) {
        operationLog.append(msg + "\n");
    }

    private void updateDiagram() {
        diagram.repaint();
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    class CacheDiagram extends JPanel {
        public CacheDiagram() {
            setPreferredSize(new Dimension(700, 200));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int boxW = 100, boxH = 60, gap = 10;
            int startX = (getWidth() - (CACHE_CAPACITY * (boxW + gap))) / 2;
            int y = 60;

            for (int i = 0; i < CACHE_CAPACITY; i++) {
                int x = startX + i * (boxW + gap);
                g2d.setColor(new Color(230, 230, 230));
                g2d.fillRect(x, y, boxW, boxH);
                g2d.setColor(Color.GRAY);
                g2d.drawRect(x, y, boxW, boxH);
                g2d.drawString("Slot " + (i + 1), x + 5, y + 15);
            }

            LRUCache<Integer, String>.Node<Integer, String> current = cache.list.head;
            int pos = 0;
            while (current != null && pos < CACHE_CAPACITY) {
                int x = startX + pos * (boxW + gap);
                g2d.setColor(new Color(100, 149, 237));
                g2d.fillRect(x + 2, y + 2, boxW - 4, boxH - 4);
                g2d.setColor(Color.WHITE);
                g2d.drawString(current.key + " : " + current.value, x + 10, y + 35);

                if (pos == 0) {
                    g2d.setColor(Color.GREEN);
                    g2d.drawString("MRU", x + 5, y + 50);
                } else if (pos == cache.size() - 1) {
                    g2d.setColor(Color.RED);
                    g2d.drawString("LRU", x + 5, y + 50);
                }

                if (pos < cache.size() - 1) {
                    int arrowX = x + boxW;
                    g2d.setColor(Color.BLACK);
                    g2d.drawLine(arrowX, y + boxH / 2, arrowX + 10, y + boxH / 2);
                    g2d.drawLine(arrowX + 10, y + boxH / 2, arrowX + 5, y + boxH / 2 - 5);
                    g2d.drawLine(arrowX + 10, y + boxH / 2, arrowX + 5, y + boxH / 2 + 5);
                }

                pos++;
                current = current.next;
            }
        }
    }
}

// ================= INFO PANEL =================
class InfoPanel extends JPanel {
    public InfoPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoArea.setText("""
            Q) What is an LRU Cache?
            Ans: LRU stands for Least Recently Used. It's a caching strategy that removes the least recently accessed item when the cache reaches its maximum capacity.


            Q) Key Concepts:
            • It maintains the most recently accessed data at the front (MRU).
            • The least recently accessed item is evicted when capacity is exceeded (LRU).
            • Typically implemented with a HashMap + Doubly Linked List for O(1) operations.


            Q) Why use LRU?
            Ans: • LRU is useful when memory is limited and only the most relevant data should be kept.
                 •  It's widely used in CPU caches, browsers, and memory managers.


            Q) Time Complexity:
            Ans: • get(key) – O(1)
                 • put(key, value) – O(1)


            Q) How it Works in This Visualizer:
            Ans: • Insert key-value pairs using PUT.
                 • Retrieve a key using GET to update its usage position.
                 • When capacity is full, the least used item is evicted.
                 • CLEAR removes all items.
                 • Real-time logs and diagram show internal state.
            """);

        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("About LRU Cache"));
        add(scrollPane, BorderLayout.CENTER);
    }
}

// ================= LRU CACHE LOGIC =================
class LRUCache<K, V> {
    final int capacity;
    final Map<K, Node<K, V>> cache;
    final DoublyLinkedList<K, V> list;
    private int size;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.list = new DoublyLinkedList<>();
        this.size = 0;
    }

    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    public V get(K key) {
        if (!cache.containsKey(key)) return null;
        Node<K, V> node = cache.get(key);
        list.moveToFront(node);
        return node.value;
    }

    public void put(K key, V value) {
        if (cache.containsKey(key)) {
            Node<K, V> node = cache.get(key);
            node.value = value;
            list.moveToFront(node);
            return;
        }

        if (size == capacity) {
            Node<K, V> lru = list.removeFromTail();
            cache.remove(lru.key);
            size--;
        }

        Node<K, V> node = new Node<>(key, value);
        list.addToFront(node);
        cache.put(key, node);
        size++;
    }

    public void clear() {
        list.head = null;
        list.tail = null;
        cache.clear();
        size = 0;
    }

    public int size() {
        return size;
    }

    class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev, next;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    class DoublyLinkedList<K, V> {
        Node<K, V> head, tail;

        void addToFront(Node<K, V> node) {
            if (head == null) {
                head = tail = node;
            } else {
                node.next = head;
                head.prev = node;
                head = node;
            }
        }

        void moveToFront(Node<K, V> node) {
            if (node == head) return;

            if (node.prev != null) node.prev.next = node.next;
            if (node.next != null) node.next.prev = node.prev;

            if (node == tail) tail = node.prev;

            node.prev = null;
            node.next = head;
            head.prev = node;
            head = node;

            if (tail == null) tail = head;
        }

        Node<K, V> removeFromTail() {
            if (tail == null) return null;
            Node<K, V> removed = tail;
            if (head == tail) {
                head = tail = null;
            } else {
                tail = tail.prev;
                tail.next = null;
            }
            return removed;
        }
    }
}
