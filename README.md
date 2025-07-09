# 🧠 LRU Cache Visualizer (Java Swing GUI)

An interactive Java Swing-based simulation of the **Least Recently Used (LRU) Cache** algorithm. This educational tool lets users perform cache operations (`PUT`, `GET`, `CLEAR`), visually observe cache behavior in real time, and track performance with live statistics such as **hits**, **misses**, and **hit rate**.

---

## 📌 Features

✅ Graphical representation of the cache  
✅ Real-time visualization of MRU → LRU order  
✅ `PUT` operation to insert/update key-value pairs  
✅ `GET` operation to retrieve and mark most recently used items  
✅ `CLEAR CACHE` button to reset the cache  
✅ Operation log to show all cache actions and outcomes  
✅ Statistics display for:
- Number of cache **hits**
- Number of **misses**
- **Hit rate (%)**  
✅ Informational panel to explain LRU Cache, its use cases, and working  
✅ Easy-to-use UI with clean layout and color coding

---

## 🎯 Purpose

This tool is designed to **help students, educators, and interview candidates**:
- Understand how LRU Cache works internally
- Visualize the real-time order of usage
- Learn the data structure combination (HashMap + Doubly Linked List)
- See how cache eviction and access policies affect performance

---

## 🏗️ Technologies Used

- Java 8+  
- Java Swing (GUI components, event handling)  
- Object-Oriented Programming  
- Custom rendering using `Graphics2D`  

---

## 🚀 How to Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/lru-cache-visualizer.git
