package app.pulse.core.data.utils

open class RingBuffer<T>(val size: Int, private val init: (index: Int) -> T) : Iterable<T> {
    @Suppress("UNCHECKED_CAST")
    private val list = MutableList<Any?>(size) { i -> init(i) } as MutableList<T>

    @get:Synchronized
    @set:Synchronized
    private var index = 0

    @Synchronized
    operator fun get(index: Int) = list.getOrNull(index)
    @Synchronized
    operator fun set(index: Int, value: T) { if (index in list.indices) list[index] = value }
    @Synchronized
    operator fun plusAssign(element: T) {
        list[index++ % size] = element
    }

    @Synchronized
    override fun iterator() = list.toList().iterator()

    @Synchronized
    fun clear() = list.indices.forEach {
        list[it] = init(it)
    }

    @Synchronized
    @Suppress("UNCHECKED_CAST")
    fun removeIf(predicate: (T) -> Boolean) {
        val raw = list as MutableList<Any?>
        list.indices.forEach { i ->
            if (raw[i] != null && predicate(list[i]!!)) raw[i] = null
        }
    }
}
