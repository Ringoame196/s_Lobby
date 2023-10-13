package com.github.Ringoame196

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Smartphone {
    fun open(player: Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(0, Item().make(Material.GOLD_INGOT, "${ChatColor.GOLD}所持金", "${Economy().get(player)?.toInt()}円", null))
        gui.setItem(1, Item().make(Material.GOLD_BLOCK, "${ChatColor.GREEN}所持金変換", null, null))
        gui.setItem(2, Item().make(Material.ENDER_CHEST, "${ChatColor.YELLOW}エンダーチェスト", null, null))
        gui.setItem(3, Item().make(Material.BOOK, "${ChatColor.GREEN}テレポート", null, null))
        player.openInventory(gui)
    }
    fun clickItem(player: Player, item: ItemStack) {
        val itemName = item.itemMeta?.displayName
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        when (itemName) {
            "${ChatColor.YELLOW}エンダーチェスト" -> player.openInventory(player.enderChest)
            "${ChatColor.GREEN}所持金変換" -> conversion(player)
        }
        if (item.type == Material.EMERALD && (item.itemMeta?.customModelData ?: return) >= 1) {
            if ((item.itemMeta?.customModelData ?: return) > 4) { return }
            val money = itemName?.replace("${ChatColor.GREEN}", "")?.replace("円", "")?.toInt()
            moneyItem(player, money ?: return, item)
        }
    }
    private fun conversion(player: Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(1, Item().make(Material.EMERALD, "${ChatColor.GREEN}100円", null, 1))
        gui.setItem(3, Item().make(Material.EMERALD, "${ChatColor.GREEN}1000円", null, 2))
        gui.setItem(5, Item().make(Material.EMERALD, "${ChatColor.GREEN}10000円", null, 3))
        gui.setItem(7, Item().make(Material.EMERALD, "${ChatColor.GREEN}100000円", null, 4))
        player.openInventory(gui)
    }
    private fun moneyItem(player: Player, money: Int, item: ItemStack) {
        if ((Economy().get(player) ?: return) < money) {
            Player().errorMessage(player, "お金が足りません")
        } else {
            val giveItem = item.clone()
            giveItem.amount = 1
            player.inventory.addItem(giveItem)
            Economy().remove(player, money)
        }
    }
}
