package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class Trade {
    fun open(player: Player, partner: Player) {
        val gui = Bukkit.createInventory(null, 18, "${ChatColor.DARK_GREEN}トレード")
        for (i in 0 until gui.size) {
            gui.setItem(i, Item().make(Material.BARRIER, "${ChatColor.RED}選択不可", null, null))
        }
        gui.setItem(0, Item().make(Material.PLAYER_HEAD, player.name, null, null))
        gui.setItem(9, Item().make(Material.PLAYER_HEAD, partner.name, null, null))
        gui.setItem(1, Item().make(Material.AIR, " ", null, null))
        gui.setItem(10, Item().make(Material.AIR, " ", null, null))
        gui.setItem(3, Item().make(Material.EMERALD, "${ChatColor.GREEN}交換", null, null))
        gui.setItem(12, Item().make(Material.EMERALD, "${ChatColor.GREEN}交換", null, null))
        player.openInventory(gui)
        partner.openInventory(gui)
    }
    fun exchange(gui: InventoryView) {
        if (gui.getItem(3) != gui.getItem(12)) { return }
        val player1 = Bukkit.getPlayer(gui.getItem(0)?.itemMeta?.displayName ?: return) ?: return
        val player2 = Bukkit.getPlayer(gui.getItem(9)?.itemMeta?.displayName ?: return) ?: return
        player1.inventory.addItem(gui.getItem(10) ?: Item().make(Material.AIR, " ", null, null))
        player2.inventory.addItem(gui.getItem(1) ?: Item().make(Material.AIR, " ", null, null))
        gui.topInventory.clear()
        player1.closeInventory()
        player2.closeInventory()
        player1.playSound(player1, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        player2.playSound(player1, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
    fun close(gui: InventoryView, player: Player, slot: Int) {
        gui.setItem(slot, Item().make(Material.REDSTONE_BLOCK, "${ChatColor.RED}オフライン", null, null))
        gui.setItem(slot + 3, Item().make(Material.REDSTONE_BLOCK, "${ChatColor.RED}オフライン", null, null))
        player.inventory.addItem(gui.getItem(slot + 1) ?: return)
        gui.setItem(slot + 1, Item().make(Material.AIR, " ", null, null))
    }
    fun click(gui: InventoryView, player: Player, name: String, item: ItemStack, slot: Int) {
        if (name == "${ChatColor.GREEN}交換") {
            gui.setItem(slot + 3, Item().make(Material.EMERALD_BLOCK, "${ChatColor.YELLOW}了承", null, null))
            Trade().exchange(gui)
            return
        }
        if (gui.getItem(slot + 3)!!.itemMeta?.displayName != "${ChatColor.GREEN}交換") { return }
        val guiItem = gui.getItem(slot + 1)?.clone() ?: Item().make(Material.AIR, "", null, null)
        gui.setItem(slot + 1, item.clone())
        player.inventory.addItem(guiItem)
    }
}
