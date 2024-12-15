package goforthegold.model;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A generic hash table implementation.
 */
public class HashTable<K, V> {
    private ArrayList<LinkedList<Entry<K, V>>> buckets;
    private int size;
    private static final double LOAD_FACTOR = 0.75;

    public HashTable() {
        buckets = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            buckets.add(new LinkedList<>());
        }
        size = 0;
    }

    public void put(K key, V value) {
        int index = getIndex(key);
        for (Entry<K, V> entry : buckets.get(index)) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }
        buckets.get(index).add(new Entry<>(key, value));
        size++;

        if ((double) size / buckets.size() > LOAD_FACTOR) {
            resize();
        }
    }

    public V get(K key) {
        int index = getIndex(key);
        for (Entry<K, V> entry : buckets.get(index)) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    public void remove(K key) {
        int index = getIndex(key);
        buckets.get(index).removeIf(entry -> entry.key.equals(key));
        size--;
    }

    private int getIndex(K key) {
        return Math.abs(key.hashCode()) % buckets.size();
    }

    private void resize() {
        ArrayList<LinkedList<Entry<K, V>>> oldBuckets = buckets;
        buckets = new ArrayList<>();
        for (int i = 0; i < oldBuckets.size() * 2; i++) {
            buckets.add(new LinkedList<>());
        }
        size = 0;
        for (LinkedList<Entry<K, V>> bucket : oldBuckets) {
            for (Entry<K, V> entry : bucket) {
                put(entry.key, entry.value);
            }
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}