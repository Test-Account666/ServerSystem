package me.testaccount666.serversystem.utils

class BiDirectionalHashMap<T, V> {
    private val _keyValueMap = HashMap<T, V>()
    private val _valueKeyMap = HashMap<V, T>()

    fun containsKey(key: T) = key in _keyValueMap

    fun containsValue(value: V) = value in _valueKeyMap

    fun getValue(key: T) = _keyValueMap[key]

    fun getKey(value: V) = _valueKeyMap[value]

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

    fun keySet() = _keyValueMap.keys.toSet()

    fun valueSet() = _valueKeyMap.keys.toSet()

    fun size() = _keyValueMap.size

    fun clear() {
        _keyValueMap.clear()
        _valueKeyMap.clear()
    }
}
