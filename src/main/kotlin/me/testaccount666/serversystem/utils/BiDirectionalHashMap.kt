package me.testaccount666.serversystem.utils

import java.util.*

class BiDirectionalHashMap<T, V> {
    private val _keyValueMap: MutableMap<T, V> = HashMap()
    private val _valueKeyMap: MutableMap<V, T> = HashMap()

    fun containsKey(key: T): Boolean = key in _keyValueMap

    fun containsValue(value: V): Boolean = value in _valueKeyMap

    fun getValue(key: T): Optional<V> = Optional.ofNullable(_keyValueMap[key]).map { obj: V -> obj }

    fun getKey(value: V): Optional<T> = Optional.ofNullable(_valueKeyMap[value]).map { obj: T -> obj }

    fun put(key: T, value: V) {
        _keyValueMap[key] = value
        _valueKeyMap[value] = key
    }

    fun removeByKey(key: T) {
        if (!_keyValueMap.containsKey(key)) return
        _valueKeyMap.remove(_keyValueMap[key])
        _keyValueMap.remove(key)
    }

    fun removeByValue(value: V) {
        if (!_valueKeyMap.containsKey(value)) return
        _keyValueMap.remove(_valueKeyMap[value])
        _valueKeyMap.remove(value)
    }

    fun keySet(): MutableSet<T> = _keyValueMap.keys

    fun valueSet(): MutableSet<V> = _valueKeyMap.keys

    fun size(): Int = _keyValueMap.size

    fun clear() {
        _keyValueMap.clear()
        _valueKeyMap.clear()
    }
}
