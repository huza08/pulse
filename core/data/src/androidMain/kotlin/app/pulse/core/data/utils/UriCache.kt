package app.pulse.core.data.utils

import android.net.Uri

class UriCache<Key : Any, Meta>(size: Int = 16) {
    private val buffer = RingBuffer<CachedUri<Key, Meta>?>(size) { null }

    data class CachedUri<Key, Meta> internal constructor(
        val key: Key,
        val meta: Meta,
        val uri: Uri,
        // epoch millis when this URL expires. Null = unknown. //
        val expiresAt: Long? = null
    )

    operator fun get(key: Key) = buffer.find { it != null && it.key == key }

    fun push(
        key: Key,
        meta: Meta,
        uri: Uri
    ) {
        buffer += CachedUri(key, meta, uri, uri.parseExpiryMs())
    }

    fun remove(key: Key) = buffer.removeIf { it?.key == key }

    fun clear() = buffer.clear()

    /// Return entries expiring within [refreshMarginMs]. //
    fun expiringSoon(): List<CachedUri<Key, Meta>> {
        val now = System.currentTimeMillis()
        val threshold = now + REFRESH_MARGIN_MS
        return buffer.filterNotNull().filter { it.expiresAt != null && it.expiresAt < threshold }
    }

    // Replace an entry with the same key. //
    fun update(key: Key, meta: Meta, uri: Uri) {
        remove(key)
        push(key, meta, uri)
    }

    private fun Uri.parseExpiryMs(): Long? {
        val expire = getQueryParameter("expire")?.toLongOrNull() ?: return null
        return expire * 1000L
    }

    companion object {
        private const val REFRESH_MARGIN_MS = 5 * 60 * 1000L
    }
}
