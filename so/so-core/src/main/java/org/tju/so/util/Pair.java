package org.tju.so.util;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Pair<K, V> {

    private K key;

    private V value;

    public Pair() {

    }

    public Pair(K key, V value) {
        setKey(key);
        setValue(value);
    }

    /**
     * @return the key
     */
    public K getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public V getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(V value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "<" + key + ", " + value + ">";
    }

}
