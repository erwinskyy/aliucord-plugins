package com.github.erwinskyy

import android.content.Context
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.utils.RxUtils.subscribe
import rx.Subscription

@Suppress("unused")
@AliucordPlugin
class FavEmojiFirst : Plugin() {

    @Volatile
    private var cachedFavoriteIds: Set<String>? = null  // null = use snapshot fallback

    private var subscription: Subscription? = null

    override fun start(context: Context) {

        val autocompleteVmClass    = Class.forName("com.discord.widgets.chat.input.autocomplete.AutocompleteViewModel")
        val emojiAutocompletable   = Class.forName("com.discord.widgets.chat.input.autocomplete.EmojiAutocompletable")
        val autocompleteStateClass = Class.forName($$"com.discord.widgets.chat.input.autocomplete.AutocompleteViewState$Autocomplete")
        val storeStreamClass       = Class.forName("com.discord.stores.StoreStream")
        val companionClass         = Class.forName($$"com.discord.stores.StoreStream$Companion")
        val storeMediaFavClass     = Class.forName("com.discord.stores.StoreMediaFavorites")

        val emojiField = emojiAutocompletable
            .getDeclaredField("emoji")
            .also { it.isAccessible = true }

        val autocompletablesField = autocompleteStateClass
            .getDeclaredField("autocompletables")
            .also { it.isAccessible = true }

        val companionInstance = storeStreamClass
            .getDeclaredField("Companion")
            .also { it.isAccessible = true }
            .get(null)!!

        val storeStream = companionClass
            .getDeclaredMethod("getCollector")
            .also { it.isAccessible = true }
            .invoke(companionInstance)!!

        val mediaFavoritesStore = storeStreamClass
            .getDeclaredField("mediaFavorites")
            .also { it.isAccessible = true }
            .get(storeStream)!!

        val getFavoritesDefault = storeMediaFavClass
            .getDeclaredMethod(
                $$"getFavorites$default",
                storeMediaFavClass,
                MutableSet::class.java,
                Int::class.javaPrimitiveType,
                Any::class.java
            ).also { it.isAccessible = true }

        val getAutocompleteViewState = autocompleteVmClass.getDeclaredMethod(
            "getAutocompleteViewState",
            String::class.java,
            List::class.java,
            List::class.java,
            Boolean::class.javaPrimitiveType
        ).also { it.isAccessible = true }

        val getUniqueId = emojiField.type.methods
            .first { it.name == "getUniqueId" }

        // Reflects emojiUniqueId off a native Favorite subclass (e.g. FavCustomEmoji).
        // Only used in the snapshot path (no Frecents).
        fun extractNativeId(fav: Any): String? =
            fav.javaClass
                .declaredFields
                .firstOrNull { f -> f.name == "emojiUniqueId" }
                ?.also { f -> f.isAccessible = true }
                ?.get(fav)?.toString()

        // ── If Frecents is installed, subscribe to its settings observable ────
        // observeSettings() may block on a network call the first time it's invoked,
        // so we do the whole setup on a background thread to keep start() fast.

        val pluginManagerClass = Class.forName("com.aliucord.PluginManager")
        @Suppress("UNCHECKED_CAST")
        val plugins = pluginManagerClass
            .getDeclaredField("plugins")
            .also { it.isAccessible = true }
            .get(null) as? Map<String, Plugin>

        val frecentsPlugin = plugins?.get("Frecents")

        if (frecentsPlugin != null) {
            Utils.threadPool.execute {
                val frecencySettingsManager = frecentsPlugin.javaClass
                    .declaredFields
                    .firstOrNull { it.name == "frecencySettings" }
                    ?.also { it.isAccessible = true }
                    ?.get(frecentsPlugin)

                val observeSettingsMethod = frecencySettingsManager?.javaClass
                    ?.declaredMethods
                    ?.firstOrNull { it.name == "observeSettings" }
                    ?.also { it.isAccessible = true }

                @Suppress("UNCHECKED_CAST")
                val settingsObservable = observeSettingsMethod
                    ?.invoke(frecencySettingsManager) as? rx.Observable<Any>

                if (settingsObservable != null) {
                    subscription = settingsObservable.subscribe {
                        val favoriteEmojis = this.javaClass
                            .declaredFields
                            .firstOrNull { f -> f.name == "favorite_emojis" }
                            ?.also { f -> f.isAccessible = true }
                            ?.get(this)

                        @Suppress("UNCHECKED_CAST")
                        val emojisList = favoriteEmojis?.javaClass
                            ?.declaredFields
                            ?.firstOrNull { f -> f.name == "emojis" }
                            ?.also { f -> f.isAccessible = true }
                            ?.get(favoriteEmojis) as? List<String>

                        cachedFavoriteIds = emojisList?.toSet() ?: emptySet()
                    }
                }
            }
        }

        // ── Hook ──────────────────────────────────────────────────────────────

        patcher.patch(getAutocompleteViewState, Hook { param ->

            val result = param.result ?: return@Hook
            if (!autocompleteStateClass.isInstance(result)) return@Hook

            @Suppress("UNCHECKED_CAST")
            val autocompletables = autocompletablesField.get(result) as? List<Any> ?: return@Hook
            if (autocompletables.isEmpty()) return@Hook

            // Check only the first element rather than iterating the entire list
            if (!emojiAutocompletable.isInstance(autocompletables.first())) return@Hook

            // Only sort after at least one character has been typed after the colon
            if (((param.args[0] as? String)?.length ?: 0) <= 1) return@Hook

            // Use Frecents cache if available; otherwise read the native snapshot.
            // The native snapshot result is cached on first non-empty read so we
            // don't pay the reflection cost on every keystroke.
            val favoriteIds: Set<String> = cachedFavoriteIds ?: run {
                @Suppress("UNCHECKED_CAST")
                val snap = getFavoritesDefault
                    .invoke(null, mediaFavoritesStore, null, 1, null) as? Set<Any>
                    ?: return@Hook
                val ids = snap.mapNotNull { fav -> extractNativeId(fav) }.toSet()
                if (ids.isNotEmpty()) cachedFavoriteIds = ids  // cache for subsequent keystrokes
                ids
            }

            if (favoriteIds.isEmpty()) return@Hook

            val sorted = autocompletables.sortedWith { a, b ->
                val idA = emojiField.get(a)
                    ?.let { runCatching { getUniqueId.invoke(it) as? String }.getOrNull() }
                val idB = emojiField.get(b)
                    ?.let { runCatching { getUniqueId.invoke(it) as? String }.getOrNull() }
                when {
                    favoriteIds.contains(idA) && !favoriteIds.contains(idB) -> -1
                    !favoriteIds.contains(idA) && favoriteIds.contains(idB) -> 1
                    else -> 0
                }
            }

            autocompletablesField.set(result, sorted)
        })
    }

    override fun stop(context: Context) {
        subscription?.unsubscribe()
        subscription = null
        patcher.unpatchAll()
    }
}
