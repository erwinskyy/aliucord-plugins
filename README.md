# Aliucord Plugins

A repository of custom plugins for [Aliucord](https://github.com/Aliucord).

## Available Plugins

### FavEmojiFirst

Sorts your favorited emojis to the top of the autocomplete picker when typing `:`.

**Current Version:** 1.0.0

**Features:**

* Favorited emojis appear at the top of autocomplete results after typing the first character following `:`.
* Works with native Aliucord favorites out of the box.
* Optionally integrates with [Frecents](https://github.com/zt64/aliucord-plugins) to sync and use your desktop Discord favorites on mobile.

> **Note:** If you use [Frecents](https://github.com/zt64/aliucord-plugins), install it alongside this plugin and your desktop-synced favorites will automatically be used instead of Aliucord's native ones.

---

### JumpToTop

Jump to the first message in any channel or DM via long-press or slash command.

**Current Version:** 1.0.0

**Features:**

* Long-press the channel title bar to jump to the first message in any server channel, DM, or thread.
* Use `/jumptotop` to do the same from the slash command menu.
* Long-press can be toggled on or off from the plugin settings.

---

## Installation

1. **Download the `.zip` file** for the plugin you want from the links below.
2. On your Android device, open your file manager and navigate to your internal storage.
3. Locate the `Aliucord/plugins` directory.
4. Move the downloaded `.zip` file directly into the `plugins` folder. **(Do not extract the .zip file.)**
5. Restart the Discord application if it was already running.
6. Navigate to **Settings** > **Plugins** in Discord to confirm the plugin is enabled.

| Plugin | Download |
|--------|----------|
| FavEmojiFirst | [FavEmojiFirst.zip](https://github.com/erwinskyy/aliucord-plugins/raw/builds/FavEmojiFirst.zip) |
| JumpToTop | [JumpToTop.zip](https://github.com/erwinskyy/aliucord-plugins/raw/builds/JumpToTop.zip) |

## Building from Source

To compile a plugin locally, ensure you have JDK 21 and a standard Gradle environment configured.

```
git clone https://github.com/erwinskyy/aliucord-plugins.git
cd aliucord-plugins
./gradlew :<PluginName>:make
```

The compiled plugin will be generated as a `.zip` file in `plugins/<PluginName>/build/outputs/`.

## Credits

* **FavEmojiFirst** is inspired by the [favEmojiFirst](https://github.com/Vendicated/Vencord/tree/main/src/plugins/favEmojiFirst) plugin from [Vencord](https://github.com/Vendicated/Vencord) by Vendicated.
* **JumpToTop** is inspired by the [JumpToTop](https://github.com/Huderon/BetterDiscordPlugins/tree/main) BetterDiscord plugin by Huderon, and the [FirstMessage](https://github.com/rushiiMachine/aliucord-plugins) Aliucord plugin by rushii.

## License

All plugins are licensed under the [Open Software License 3.0](LICENSE).
