package com.github.Ringoame196

import java.util.UUID

class Data {
    object DataManager {
        var cheatWebhook: String? = null
        val playerDataMap: MutableMap<UUID, PlayerData> = mutableMapOf()
    }
}
