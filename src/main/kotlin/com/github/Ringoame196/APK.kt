package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.InventoryView
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class APK {
    fun add(player: org.bukkit.entity.Player, itemName: String, plugin: Plugin) {
        val apkName = itemName.replace("[アプリケーション]", "")
        val playerData = Data.DataManager.playerDataMap.getOrPut(player.uniqueId) { PlayerData() }.smartphone
        if ((playerData?.size ?: 0) > 12) {
            Player().errorMessage(player, "[スマートフォン]容量が足りませんでした")
            return
        }
        if (playerData?.contains(apkName) == true) {
            Player().errorMessage(player, "[スマートフォン]同アプリをインストールすることはできません")
            return
        }
        Yml().addToList(plugin, "smartphone", player.uniqueId.toString(), apkName)
        val playerItem = player.inventory.itemInMainHand.clone()
        playerItem.amount = playerItem.amount - 1
        player.inventory.setItemInMainHand(playerItem)
        player.sendMessage("${ChatColor.AQUA}[スマートフォン]${apkName}${ChatColor.AQUA}のインストール開始…")
        var t = 0
        object : BukkitRunnable() {
            override fun run() {
                t ++
                player.playSound(player, Sound.BLOCK_LAVA_POP, 1f, 1f)
                if (t == 10) {
                    Data.DataManager.playerDataMap.getOrPut(player.uniqueId) { PlayerData() }.smartphone?.add(apkName)
                    player.sendMessage("${ChatColor.YELLOW}[スマートフォン]${apkName}${ChatColor.YELLOW}のインストール完了")
                    player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒間隔 (20 ticks) でタスクを実行
    }
    fun remove(player: org.bukkit.entity.Player, itemName: String, customModelData: Int, plugin: Plugin) {
        player.inventory.addItem(Item().make(Material.GREEN_CONCRETE, "[アプリケーション]$itemName", "", customModelData))
        Data.DataManager.playerDataMap.getOrPut(player.uniqueId) { PlayerData() }.smartphone?.remove(itemName)
        Yml().removeFromList(plugin, "smartphone", player.uniqueId.toString(), itemName)
        player.sendMessage("${ChatColor.RED}[スマートフォン]${itemName}${ChatColor.RED}をアンインストールしました")
        player.playSound(player, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f)
    }
    fun sortGUIOpen(player: org.bukkit.entity.Player) {
        val gui = Bukkit.createInventory(null, 18, "${ChatColor.BLUE}スマートフォン(並び替え)")
        player.openInventory(gui)
        val apkList = Data.DataManager.playerDataMap.getOrPut(player.uniqueId) { PlayerData() }.smartphone ?: return
        for (apk in apkList) {
            gui.addItem(Item().make(Material.GREEN_CONCRETE, "[アプリケーション]$apk", null, Smartphone().giveCustomModel(apk)))
        }
    }
    fun setSort(player: HumanEntity, gui: InventoryView, plugin: Plugin) {
        val apkList = mutableListOf<String>()
        var c = 1
        for (apk in gui.topInventory) {
            if (apk == null) { continue }
            if (c <= 12) {
                apkList.add(apk.itemMeta?.displayName?.replace("[アプリケーション]", "") ?: continue)
            } else {
                player.inventory.addItem(apk)
            }
            c ++
        }
        if (Data.DataManager.playerDataMap.getOrPut(player.uniqueId) { PlayerData() }.smartphone == apkList) {
            return
        }
        Data.DataManager.playerDataMap.getOrPut(player.uniqueId) { PlayerData() }.smartphone = apkList
        Yml().setList(plugin, "smartphone", player.uniqueId.toString(), apkList)
    }
}
