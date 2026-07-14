package app.pulse.core.data.utils

import android.net.Uri

class UriCache<Key : Any, Meta>(size: Int = 16) {
    private val buffer = RingBuffer<CachedUri<Key, Meta>?>(size) { null }

    data class CachedUri<Key, Meta> internal constructor(
        val key: Key,
        val meta: Meta,
        val uri: Uri
    )

    operator fun get(key: Key) = buffer.find { it != null && it.key == key }

    fun push(
        key: Key,
        meta: Meta,
        uri: Uri
    ) {
        buffer += CachedUri(key, meta, uri)
    }

    fun clear() = buffer.clear()
}
