package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player

class Resource {
    fun openTpGUI(player: Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.GREEN}資源テレポート")
        gui.setItem(1, Item().make(Material.CHEST, "${ChatColor.GOLD}ロビー", null, null))
        gui.setItem(4, Item().make(Material.GRASS_BLOCK, "${ChatColor.GREEN}オーバーワールド", null, null))
        gui.setItem(7, Item().make(Material.NETHERRACK, "${ChatColor.RED}ネザー", null, null))
        player.openInventory(gui)
        player.playSound(player, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
    }
    fun guiClick(player: Player, itemName: String) {
        when (itemName) {
            "${ChatColor.GOLD}ロビー" -> worldTP(player, "world")
            "${ChatColor.GREEN}オーバーワールド" -> worldTP(player, "Survival")
            "${ChatColor.RED}ネザー" -> worldTP(player, "Nether")
        }
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
    }
    private fun worldTP(player: Player, worldName: String) {
        val world = Bukkit.getWorld(worldName)?.spawnLocation
        player.teleport(world ?: return)
    }
}
