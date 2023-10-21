package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Smartphone {
    fun open(player: Player) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(1, Item().make(Material.ENDER_CHEST, "${ChatColor.YELLOW}エンダーチェスト", null, null))
        gui.setItem(3, Item().make(Material.BOOK, "${ChatColor.GREEN}テレポート", null, null))
        gui.setItem(5, Item().make(Material.TRIPWIRE_HOOK, "${ChatColor.RED}アイテム保護", null, null))
        gui.setItem(7, Item().make(Material.SLIME_BALL, "${ChatColor.GREEN}プレイヤー評価", null, null))
        gui.setItem(8, Item().make(Material.COMMAND_BLOCK, "${ChatColor.YELLOW}OP用", null, null))
        gui.setItem(10, Item().make(Material.REDSTONE_BLOCK, "${ChatColor.RED}未設定", null, null))
        gui.setItem(12, Item().make(Material.REDSTONE_BLOCK, "${ChatColor.RED}未設定", null, null))
        gui.setItem(14, Item().make(Material.REDSTONE_BLOCK, "${ChatColor.RED}未設定", null, null))
        gui.setItem(16, Item().make(Material.REDSTONE_BLOCK, "${ChatColor.RED}未設定", null, null))
        gui.setItem(21, Item().make(Material.GOLD_INGOT, "${ChatColor.GOLD}所持金", "${Economy().get(player)?.toInt()}円", null))
        gui.setItem(22, Item().make(Material.GOLD_BLOCK, "${ChatColor.GREEN}所持金変換", null, null))
        player.openInventory(gui)
    }
    fun clickItem(player: Player, item: ItemStack) {
        val itemName = item.itemMeta?.displayName
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        when (itemName) {
            "${ChatColor.YELLOW}エンダーチェスト" -> player.openInventory(player.enderChest)
            "${ChatColor.GREEN}所持金変換" -> conversion(player)
            "${ChatColor.RED}アイテム保護" -> ItemProtection().open(player)
            "${ChatColor.GREEN}テレポート" -> tpGUI(player)
            "${ChatColor.GOLD}ロビー" -> player.teleport(Bukkit.getWorld("world")?.spawnLocation ?: return)
            "${ChatColor.GREEN}生活ワールド" -> player.teleport(Bukkit.getWorld("Home")?.spawnLocation ?: return)
            "${ChatColor.AQUA}資源ワールド" -> player.teleport(Bukkit.getWorld("Survival")?.spawnLocation ?: return)
            "${ChatColor.YELLOW}ショップ" -> player.teleport(Bukkit.getWorld("shop")?.spawnLocation ?: return)
            "${ChatColor.YELLOW}ダンジョン" -> player.teleport(Bukkit.getWorld("world")?.spawnLocation ?: return)
            "${ChatColor.RED}イベント" -> player.teleport(Bukkit.getWorld("world")?.spawnLocation ?: return)
            "${ChatColor.YELLOW}OP用" -> op(player)
            "${ChatColor.GREEN}プレイヤー評価" -> Evaluation().display(player)
        }
        if (item.type == Material.EMERALD && (item.itemMeta?.customModelData ?: return) >= 1) {
            if ((item.itemMeta?.customModelData ?: return) > 4) { return }
            val money = itemName?.replace("${ChatColor.GREEN}", "")?.replace("円", "")?.toInt()
            moneyItem(player, money ?: return, item)
        }
    }
    fun opClick(item: ItemStack, plugin: Plugin) {
        when (item.itemMeta?.displayName) {
            "${ChatColor.RED}ショップ保護リセット" -> {
                val list = Yml().getList(plugin, "conservationLand", "protectedName") ?: return
                for (name in list) {
                    WorldGuard().resetOwner(name, Bukkit.getWorld("shop") ?: return)
                    Yml().removeFromList(plugin, "conservationLand", "protectedName", name)
                }
                Bukkit.broadcastMessage("${ChatColor.RED}[ショップ] ショップの購入土地がリセットされました")
            }
        }
    }
    private fun tpGUI(player: Player) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(1, Item().make(Material.CHEST, "${ChatColor.GOLD}ロビー", null, null))
        gui.setItem(3, Item().make(Material.GRASS_BLOCK, "${ChatColor.GREEN}生活ワールド", null, null))
        gui.setItem(5, Item().make(Material.DIAMOND_PICKAXE, "${ChatColor.AQUA}資源ワールド", null, null))
        gui.setItem(7, Item().make(Material.QUARTZ_BLOCK, "${ChatColor.YELLOW}ショップ", null, null))
        gui.setItem(19, Item().make(Material.OBSIDIAN, "${ChatColor.YELLOW}ダンジョン", null, null))
        gui.setItem(21, Item().make(Material.BEDROCK, "${ChatColor.RED}イベント", null, null))
        player.openInventory(gui)
    }
    private fun op(player: Player) {
        if (!player.isOp) { return }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.YELLOW}OP用")
        gui.setItem(0, Item().make(Material.WOODEN_AXE, "${ChatColor.RED}ショップ保護リセット", null, null))
        player.openInventory(gui)
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
            Economy().remove(player, money, false)
        }
    }
}
