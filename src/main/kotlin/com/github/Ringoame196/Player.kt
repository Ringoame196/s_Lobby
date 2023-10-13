package com.github.Ringoame196

import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player

class Player {
    fun errorMessage(player: Player, message: String) {
        player.sendMessage("${ChatColor.RED}$message")
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
    }
}
