package com.github.Ringoame196

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player

class Player {
    fun errorMessage(player: Player, message: String) {
        player.sendMessage("${ChatColor.RED}$message")
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
    }
    fun sendActionBar(player: Player, message: String) {
        val actionBarMessage = ChatColor.translateAlternateColorCodes('&', message)
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(actionBarMessage))
    }
    fun getPlayersInRadius(center: Location, radius: Double): List<Player>? {
        val playersInRadius = mutableListOf<Player>()

        for (player in center.world?.players ?: return null) {
            val playerLocation = player.location
            val distance = center.distance(playerLocation)

            if (distance <= radius) {
                // 半径内にいるプレイヤーをリストに追加
                playersInRadius.add(player)
            }
        }

        return playersInRadius
    }
}
