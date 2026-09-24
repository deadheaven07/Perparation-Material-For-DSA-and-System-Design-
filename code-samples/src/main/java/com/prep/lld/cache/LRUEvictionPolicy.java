package com.prep.lld.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Least Recently Used (LRU) eviction policy implementing an O(1) Doubly Linked List.
 *
 * @param <K> Key type
 */
public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {

    private static class Node<K> {
        final K key;
        Node<K> prev;
        Node<K> next;

        Node(K key) {
            this.key = key;
        }
    }

    private final Map<K, Node<K>> nodeMap = new HashMap<>();
    private final Node<K> head;
    private final Node<K> tail;

    public LRUEvictionPolicy() {
        this.head = new Node<>(null);
        this.tail = new Node<>(null);
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public synchronized void recordAccess(K key) {
        Node<K> node = nodeMap.get(key);
        if (node != null) {
            detach(node);
            attachToHead(node);
        }
    }

    @Override
    public synchronized void recordInsertion(K key) {
        Node<K> existing = nodeMap.get(key);
        if (existing != null) {
            detach(existing);
            attachToHead(existing);
        } else {
            Node<K> newNode = new Node<>(key);
            nodeMap.put(key, newNode);
            attachToHead(newNode);
        }
    }

    @Override
    public synchronized void recordDeletion(K key) {
        Node<K> node = nodeMap.remove(key);
        if (node != null) {
            detach(node);
        }
    }

    @Override
    public synchronized Optional<K> evictKey() {
        if (tail.prev == head) {
            return Optional.empty();
        }
        Node<K> lruNode = tail.prev;
        detach(lruNode);
        nodeMap.remove(lruNode.key);
        return Optional.of(lruNode.key);
    }

    @Override
    public synchronized void clear() {
        nodeMap.clear();
        head.next = tail;
        tail.prev = head;
    }

    private void attachToHead(Node<K> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void detach(Node<K> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = null;
        node.next = null;
    }
}
