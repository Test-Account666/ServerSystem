package me.testaccount666.serversystem.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BiDirectionalHashMap<T, V> {
    private final Map<T, V> _keyValueMap = new HashMap<>();
    private final Map<V, T> _valueKeyMap = new HashMap<>();

    public boolean containsKey(T key) {
        return _keyValueMap.containsKey(key);
    }

    public boolean containsValue(V value) {
        return _valueKeyMap.containsKey(value);
    }

    public Optional<V> getValue(T key) {
        return Optional.ofNullable(_keyValueMap.get(key));
    }

    public Optional<T> getKey(V value) {
        return Optional.ofNullable(_valueKeyMap.get(value));
    }

    public void put(T key, V value) {
        _keyValueMap.put(key, value);
        _valueKeyMap.put(value, key);
    }

    public void removeByKey(T key) {
        if (!_keyValueMap.containsKey(key)) return;
        _valueKeyMap.remove(_keyValueMap.get(key));
        _keyValueMap.remove(key);
    }

    public void removeByValue(V value) {
        if (!_valueKeyMap.containsKey(value)) return;
        _keyValueMap.remove(_valueKeyMap.get(value));
        _valueKeyMap.remove(value);
    }

    public Set<T> keySet() {
        return _keyValueMap.keySet();
    }

    public Set<V> valueSet() {
        return _valueKeyMap.keySet();
    }

    public int size() {
        return _keyValueMap.size();
    }

    public void clear() {
        _keyValueMap.clear();
        _valueKeyMap.clear();
    }
}
