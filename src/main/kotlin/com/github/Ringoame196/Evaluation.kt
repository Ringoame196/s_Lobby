package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class Evaluation {
    fun display(player: Player) {
        val gui = Bukkit.createInventory(null, 18, "${ChatColor.BLUE}プレイヤー評価")
        var i = 0
        for (target in Player().getPlayersInRadius(player.location, 10.0) ?: return) {
            gui.addItem(playerHead(target))
            if (i == 18) { continue }
            i ++
        }
        player.openInventory(gui)
    }
    fun voidGUI(player: Player, target: ItemStack) {
        if (Scoreboard().getValue("evaluationVote", player.name) != 0) {
            Player().errorMessage(player, "評価は1日1回です")
            return
        }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}プレイヤー評価")
        gui.setItem(2, target)
        gui.setItem(4, Item().make(Material.STONE_BUTTON, "${ChatColor.GREEN}高評価", null, null))
        gui.setItem(6, Item().make(Material.STONE_BUTTON, "${ChatColor.RED}低評価", null, null))
        player.openInventory(gui)
    }
    fun void(target: ItemStack, button: String, player: Player) {
        val targetUUID = target.itemMeta?.lore?.get(1) ?: return
        val evaluation = Database().getInt(targetUUID, "aoringoserver", "playerpoint", "point")
        Scoreboard().set("evaluationVote", player.name, 1)
        Database().setPlayerPoint(
            targetUUID, "aoringoserver", "playerpoint",
            when (button) {
                "${ChatColor.GREEN}高評価" -> evaluation + 1
                "${ChatColor.RED}低評価" -> evaluation - 1
                else -> return
            }
        )
        player.closeInventory()
        player.sendMessage("${ChatColor.YELLOW}プレイヤー評価しました")
    }
    private fun playerHead(target: Player): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as SkullMeta
        val evaluation = Database().getInt(target.uniqueId.toString(), "aoringoserver", "playerpoint", "point")
        meta.setDisplayName(target.name)
        meta.setOwningPlayer(target)
        meta.lore = mutableListOf("評価:$evaluation", target.uniqueId.toString())
        item.setItemMeta(meta)
        return item
    }
}
