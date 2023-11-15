package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Smartphone {
    fun open(plugin: Plugin, player: Player) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}スマートフォン")
        val smartphone = mutableListOf(1, 3, 5, 7, 10, 12, 14, 16, 19, 21, 23, 25)
        val playerData = Data.DataManager.playerDataMap.getOrPut(player.uniqueId) { PlayerData() }.smartphone ?: load(plugin, player)
        player.openInventory(gui)
        if (playerData.isNullOrEmpty()) {
            return
        }

        val minSize = minOf(smartphone.size, playerData.size)
        for (i in 0 until minSize) {
            val apkName = playerData[i]
            gui.setItem(smartphone[i], Item().make(Material.GREEN_CONCRETE, apkName, null, giveCustomModel(apkName)))
        }
    }
    fun giveCustomModel(itemName: String): Int {
        return when (itemName) {
            "${ChatColor.YELLOW}エンダーチェスト" -> 1
            "${ChatColor.GREEN}所持金変換" -> 2
            "${ChatColor.RED}アイテム保護" -> 3
            "${ChatColor.GREEN}テレポート" -> 4
            "${ChatColor.GREEN}プレイヤー評価" -> 5
            "${ChatColor.GREEN}土地保護" -> 6
            "${ChatColor.YELLOW}OP用" -> 7
            else -> 0
        }
    }
    private fun load(plugin: Plugin, player: Player): List<String>? {
        Data.DataManager.playerDataMap.getOrPut(player.uniqueId) { PlayerData() }.smartphone =
            Yml().getList(plugin, "smartphone", player.uniqueId.toString()) as MutableList<String>?
        return Yml().getList(plugin, "smartphone", player.uniqueId.toString())
    }
    fun clickItem(player: Player, item: ItemStack, plugin: Plugin, shift: Boolean) {
        val itemName = item.itemMeta?.displayName ?: return
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        if (shift) {
            APK().remove(player, itemName, item.itemMeta?.customModelData ?: 0, plugin)
            open(plugin, player)
            return
        }
        when (itemName) {
            "${ChatColor.YELLOW}エンダーチェスト" -> EnderChest().open(player, plugin)
            "${ChatColor.GREEN}所持金変換" -> conversion(player)
            "${ChatColor.RED}アイテム保護" -> ItemProtection().open(player)
            "${ChatColor.GREEN}テレポート" -> tpGUI(player)
            "${ChatColor.GOLD}ロビー" -> player.teleport(Bukkit.getWorld("world")?.spawnLocation ?: return)
            "${ChatColor.GREEN}生活ワールド" -> player.teleport(Bukkit.getWorld("Home")?.spawnLocation ?: return)
            "${ChatColor.AQUA}資源ワールド" -> player.teleport(Bukkit.getWorld("Survival")?.spawnLocation ?: return)
            "${ChatColor.YELLOW}ショップ" -> player.teleport(Bukkit.getWorld("shop")?.spawnLocation ?: return)
            "${ChatColor.RED}イベント" -> player.teleport(Bukkit.getWorld("event")?.spawnLocation ?: return)
            "${ChatColor.YELLOW}OP用" -> op(player)
            "${ChatColor.GREEN}プレイヤー評価" -> Evaluation().display(player)
            "${ChatColor.GREEN}土地保護" -> wgGUI(player)
            "${ChatColor.YELLOW}アプリ並べ替え" -> APK().sortGUIOpen(player)
        }
        if (item.type == Material.EMERALD && (item.itemMeta?.customModelData ?: return) >= 1) {
            if ((item.itemMeta?.customModelData ?: return) > 4) { return }
            val money = itemName.replace("${ChatColor.GREEN}", "").replace("円", "")?.toInt()
            moneyItem(player, money ?: return, item)
        }
    }
    fun opClick(item: ItemStack, plugin: Plugin, shift: Boolean, player: Player) {
        when (item.itemMeta?.displayName) {
            "${ChatColor.RED}ショップ保護リセット" -> {
                if (!shift) { return }
                val list = Yml().getList(plugin, "conservationLand", "protectedName") ?: return
                for (name in list) {
                    if (Scoreboard().getValue("protectionContract", name) == 2) {
                        Scoreboard().remove("protectionContract", name, 1)
                        continue
                    }
                    WorldGuard().reset(name, Bukkit.getWorld("shop") ?: return)
                    Yml().removeFromList(plugin, "conservationLand", "protectedName", name)
                }
                Bukkit.broadcastMessage("${ChatColor.RED}[ショップ] ショップの購入土地がリセットされました")
            }
            "${ChatColor.YELLOW}リソパ更新" -> ResourcePack().update(plugin)
            "${ChatColor.GREEN}運営ギフトリセット" -> {
                if (!Scoreboard().existence("admingift")) { return }
                Scoreboard().delete("admingift")
                Scoreboard().make("admingift", "admingift")
                Bukkit.broadcastMessage("${ChatColor.YELLOW}[青りんごサーバー] 運営ギフトがリセットされました")
            }
            "${ChatColor.GREEN}テストワールド" -> player.teleport(Bukkit.getWorld("testworld")?.spawnLocation ?: return)
        }
    }
    fun wgClick(item: ItemStack, plugin: Plugin, player: Player, shift: Boolean) {
        if (player.world.name != "Home" && !player.isOp) {
            Player().errorMessage(player, "保護は生活ワールドのみ使用可能です")
            player.closeInventory()
            return
        }
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        when (item.itemMeta?.displayName) {
            "${ChatColor.GOLD}木の斧ゲット" -> player.inventory.addItem(ItemStack(Material.WOODEN_AXE))
            "${ChatColor.AQUA}保護一覧" -> {
                player.closeInventory()
                LandPurchase().listRegionsInWorld(player)
            }
            "${ChatColor.YELLOW}保護作成" -> {
                player.closeInventory()
                player.addScoreboardTag("rg")
                player.sendMessage("${ChatColor.AQUA}[土地保護]保護名を入力してください")
            }
            "${ChatColor.GREEN}情報" -> {
                val gui = player.openInventory.topInventory
                gui.setItem(2, Item().make(Material.MAP, "${ChatColor.YELLOW}保護情報", null, null))
                gui.setItem(4, Item().make(Material.PLAYER_HEAD, "${ChatColor.AQUA}メンバー追加", null, null))
                gui.setItem(6, Item().make(Material.PLAYER_HEAD, "${ChatColor.RED}メンバー削除", null, null))
                gui.setItem(8, Item().make(Material.REDSTONE_BLOCK, "${ChatColor.RED}削除", "${ChatColor.DARK_RED}シフトで実行", null))
            }
            "${ChatColor.YELLOW}保護情報" -> {
                player.closeInventory()
                player.sendMessage("${ChatColor.YELLOW}-----保護情報-----")
                if (WorldGuard().getName(player.location) == null) {
                    player.sendMessage("${ChatColor.RED}保護されていません")
                    return
                }
                player.sendMessage("${ChatColor.GOLD}保護名:${WorldGuard().getName(player.location)}")
                player.sendMessage("${ChatColor.YELLOW}オーナー:" + if (WorldGuard().getOwnerOfRegion(player.location)?.contains(player.uniqueId) == true) { "${ChatColor.GOLD}あなたはオーナーです" } else { "${ChatColor.RED}あなたはオーナーではありません" })
                player.sendMessage("${ChatColor.AQUA}メンバー:" + if (WorldGuard().getMemberOfRegion(player.location)?.contains(player.uniqueId) == true) { "${ChatColor.GOLD}あなたはメンバーです" } else { "${ChatColor.RED}あなたはメンバーではありません" })
            }
            "${ChatColor.AQUA}メンバー追加" -> {
                if (WorldGuard().getOwnerOfRegion(player.location)?.contains(player.uniqueId) != true) {
                    Player().errorMessage(player, "自分の保護土地内で実行してください")
                    return
                }
                LandPurchase().addMemberGUI(player, WorldGuard().getName(player.location) ?: return)
            }
            "${ChatColor.RED}メンバー削除" -> {
                if (WorldGuard().getOwnerOfRegion(player.location)?.contains(player.uniqueId) != true) {
                    Player().errorMessage(player, "自分の保護土地内で実行してください")
                    return
                }
                LandPurchase().removeMemberGUI(player, WorldGuard().getName(player.location) ?: return)
            }
            "${ChatColor.RED}削除" -> {
                if (WorldGuard().getOwnerOfRegion(player.location)?.contains(player.uniqueId) != true) {
                    Player().errorMessage(player, "自分の保護土地内で実行してください")
                    return
                }
                if (!shift) { return }
                WorldGuard().delete(player, WorldGuard().getName(player.location) ?: return)
                player.sendMessage("${ChatColor.RED}保護を削除しました")
            }
        }
    }
    fun protectionGUI(player: Player, name: String) {
        if (LandPurchase().doesRegionContainProtection(player)) {
            Player().errorMessage(player, "保護範囲が含まれています")
            return
        }
        if (WorldGuard().getProtection(player, name)) {
            Player().errorMessage(player, "同じ名前の保護を設定することは不可能です")
            return
        }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}保護設定($name)")
        gui.setItem(4, Item().make(Material.GREEN_WOOL, "${ChatColor.GREEN}作成", "${LandPurchase().price(player)}円", null))
        player.openInventory(gui)
    }
    fun protection(player: Player, item: ItemStack, name: String) {
        val price = item.itemMeta?.lore?.get(0)?.replace("円", "")?.toInt() ?: return
        if ((Economy().get(player) ?: return) < price) {
            Player().errorMessage(player, "お金が足りません")
            return
        }
        player.performCommand("/expand vert")
        player.performCommand("rg claim $name")
        if (WorldGuard().getProtection(player, name)) {
            player.sendMessage("${ChatColor.GREEN}[WG]正常に保護をかけました")
            Economy().remove(player, price, true)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        }
        player.closeInventory()
    }
    private fun tpGUI(player: Player) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}スマートフォン")
        gui.setItem(1, Item().make(Material.CHEST, "${ChatColor.GOLD}ロビー", null, null))
        gui.setItem(3, Item().make(Material.GRASS_BLOCK, "${ChatColor.GREEN}生活ワールド", null, null))
        gui.setItem(5, Item().make(Material.DIAMOND_PICKAXE, "${ChatColor.AQUA}資源ワールド", null, null))
        gui.setItem(7, Item().make(Material.QUARTZ_BLOCK, "${ChatColor.YELLOW}ショップ", null, null))
        gui.setItem(19, Item().make(Material.BEDROCK, "${ChatColor.RED}イベント", null, null))
        player.openInventory(gui)
    }
    private fun op(player: Player) {
        if (!player.isOp) { return }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.YELLOW}OP用")
        gui.setItem(0, Item().make(Material.COMMAND_BLOCK, "${ChatColor.YELLOW}リソパ更新", null, null))
        gui.setItem(2, Item().make(Material.WOODEN_AXE, "${ChatColor.RED}ショップ保護リセット", null, null))
        gui.setItem(4, Item().make(Material.DIAMOND, "${ChatColor.GREEN}運営ギフトリセット", null, null))
        gui.setItem(6, Item().make(Material.CRAFTING_TABLE, "${ChatColor.GREEN}テストワールド", null, null))
        player.openInventory(gui)
    }
    private fun wgGUI(player: Player) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.YELLOW}WorldGuardGUI")
        gui.setItem(2, Item().make(Material.GOLDEN_AXE, "${ChatColor.YELLOW}保護作成", "${LandPurchase().price(player)}円", null))
        gui.setItem(4, Item().make(Material.MAP, "${ChatColor.GREEN}情報", null, null))
        gui.setItem(6, Item().make(Material.CHEST, "${ChatColor.AQUA}保護一覧", null, null))
        gui.setItem(8, Item().make(Material.WOODEN_AXE, "${ChatColor.GOLD}木の斧ゲット", null, null))
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
            Economy().remove(player, money, true)
        }
        player.closeInventory()
    }
}
