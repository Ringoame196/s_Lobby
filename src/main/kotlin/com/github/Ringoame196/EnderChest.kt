package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class EnderChest {
    fun open(player: Player, plugin: Plugin) {
        Bukkit.getScheduler().runTask(
            plugin,
            Runnable {
                Bukkit.dispatchCommand(player, "enderchest")
            }
        )
    }
    fun update(player: Player, plugin: Plugin) {
        if (player.hasPermission("enderchest.size.6")) {
            Player().errorMessage(player, "これ以上拡張することはできません")
            return
        } else if (player.hasPermission("enderchest.size.5")) {
            Permission().add(player, plugin, "enderchest.size.6")
            Scoreboard().set("haveEnderChest", player.uniqueId.toString(), 5)
        } else if (player.hasPermission("enderchest.size.4")) {
            Permission().add(player, plugin, "enderchest.size.5")
            Scoreboard().set("haveEnderChest", player.uniqueId.toString(), 4)
        } else if (player.hasPermission("enderchest.size.3")) {
            Permission().add(player, plugin, "enderchest.size.4")
            Scoreboard().set("haveEnderChest", player.uniqueId.toString(), 3)
        } else if (player.hasPermission("enderchest.size.2")) {
            Permission().add(player, plugin, "enderchest.size.3")
            Scoreboard().set("haveEnderChest", player.uniqueId.toString(), 2)
        } else if (player.hasPermission("enderchest.size.1")) {
            Permission().add(player, plugin, "enderchest.size.2")
            Scoreboard().set("haveEnderChest", player.uniqueId.toString(), 1)
        }
        player.sendMessage("${ChatColor.AQUA}エンダーチェスト容量UP")
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
        val item = player.inventory.itemInMainHand
        item.amount = item.amount - 1
        player.inventory.setItemInMainHand(item)
    }
}
