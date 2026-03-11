package com.github.erwinskyy

import android.content.Context
import android.os.Bundle
import android.view.View
import com.aliucord.Utils.createCheckedSetting
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting

class Settings(private val settings: SettingsAPI) : BottomSheet() {
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val ctx = view.context
        addCheckedSetting(
            ctx,
            "Enable long-press to jump to top",
            "Long-press on the channel title bar to jump to the first message",
            "longPressEnabled"
        )
    }

    private fun addCheckedSetting(ctx: Context, title: String, subtitle: String, key: String) {
        val cs = createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, subtitle)
        cs.isChecked = settings.getBool(key, true)
        cs.setOnCheckedListener { checked: Boolean? -> settings.setBool(key, checked ?: true) }
        addView(cs)
    }
}
