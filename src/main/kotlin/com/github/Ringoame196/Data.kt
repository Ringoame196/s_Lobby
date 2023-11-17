package com.github.Ringoame196

import java.util.*

class Data {
    object DataManager {
        var cheatWebhook: String? = null
        val playerDataMap: MutableMap<UUID, PlayerData> = mutableMapOf()
        var databaseHost: String? = null
        var databasePort: Int? = null
        var databaseUsername: String? = null
        var databasePassword: String? = null
    }
}
