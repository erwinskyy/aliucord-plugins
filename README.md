# Aliucord Plugins

A repository of custom plugins for [Aliucord](https://github.com/Aliucord)

## Available Plugins

### FavEmojiFirst

Sorts your favorited emojis to the top of the autocomplete picker when typing `:`.

**Current Version:** 1.0.0

**Features:**

* Favorited emojis appear at the top of autocomplete results after typing the first character following `:`.
* Works with native Aliucord favorites out of the box.
* Optionally integrates with [Frecents](https://github.com/zt64/aliucord-plugins) to sync and use your desktop Discord favorites on mobile.

## Installation

1. **[Download FavEmojiFirst.zip](https://github.com/erwinskyy/aliucord-plugins/raw/builds/FavEmojiFirst.zip)** directly from the builds branch.
2. On your Android device, open your file manager and navigate to your internal storage.
3. Locate the `Aliucord/plugins` directory.
4. Move the downloaded `FavEmojiFirst.zip` file directly into the `plugins` folder. **(Do not extract the .zip file).**
5. Restart the Discord application (if you had it opened in the background).
6. Navigate to **Settings** > **Plugins** in Discord to confirm FavEmojiFirst is enabled.

> **Note:** If you use [Frecents](https://github.com/zt64/aliucord-plugins), install it alongside this plugin and your desktop-synced favorites will automatically be used instead of Aliucord's native ones.

## Building from Source

To compile this plugin locally, ensure you have JDK 21 and a standard Gradle environment configured.

```
git clone https://github.com/erwinskyy/aliucord-plugins.git
cd aliucord-plugins
./gradlew :FavEmojiFirst:make
```

The compiled plugin will be generated as a `.zip` file located in the `plugins/FavEmojiFirst/build/outputs/` directory.

## Credits

FavEmojiFirst is inspired by the [favEmojiFirst](https://github.com/Vendicated/Vencord/tree/main/src/plugins/favEmojiFirst) plugin from [Vencord](https://github.com/Vendicated/Vencord) by Vendicated.

## License

All plugins are licensed under the [Open Software License 3.0](LICENSE)