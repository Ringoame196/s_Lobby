package com.github.Ringoame196

import org.bukkit.ChatColor
import org.bukkit.entity.Player

class TabList {
    fun display(player: Player, title: String) {
        player.playerListHeader = "${ChatColor.AQUA}青りんごサーバー"
        player.playerListFooter = "${ChatColor.YELLOW}" + when (title) {
            "world" -> "ロビーワールド"
            "Survival" -> "資源ワールド"
            "Nether" -> "ネザー"
            else -> title
        }
    }
}
