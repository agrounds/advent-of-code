package com.groundsfam.advent

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


// usage:
//   val map: MutableMap<String, String> by mapWithPutDefault { "some default using $it" }
//   val someValue = map.getValue("some-key")  <- default value generated and stored at this point
fun <K, V> mapWithPutDefault(default: (K) -> V): ReadWriteProperty<Any?, MutableMap<K, V>> =
    object : ReadWriteProperty<Any?, MutableMap<K, V>> {
        private var map: MutableMap<K, V> = with(mutableMapOf<K, V>()) {
            withDefault { key -> getOrPut(key) { default(key) } }
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): MutableMap<K, V> = map

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableMap<K, V>) {
            this.map = value
        }
    }
