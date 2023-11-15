package com.github.Ringoame196

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.Plugin
import java.util.UUID

class LandPurchase {
    fun make(player: Player, sign: Sign) {
        if (!player.isOp) { return }
        sign.setLine(0, "${ChatColor.YELLOW}[土地販売]")
        sign.setLine(1, "${ChatColor.GREEN}${sign.getLine(1)}円")
        sign.update()
    }
    private fun ownerGUI(player: Player, name: String, money: Int) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}$name@土地設定")
        gui.setItem(3, Item().make(Material.GREEN_WOOL, "${ChatColor.GREEN}メンバー追加", null, null))
        gui.setItem(5, Item().make(Material.RED_WOOL, "${ChatColor.RED}メンバー削除", null, null))
        if (player.world.name == "shop" && Scoreboard().getValue("protectionContract", name) == 1) {
            gui.setItem(8, Item().make(Material.GOLD_INGOT, "${ChatColor.YELLOW}前払い", "${(money * 1.5).toInt()}円", null))
        }
        player.openInventory(gui)
    }
    fun addMemberGUI(player: Player, name: String) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}$name@メンバー追加")
        var i = 0
        for (worldPlayer in player.world.players) {
            gui.addItem(playerHead(worldPlayer))
            i++
            if (i >= gui.size) { continue }
        }
        player.openInventory(gui)
    }
    fun removeMemberGUI(player: Player, name: String) {
        val gui = Bukkit.createInventory(null, 27, "${ChatColor.RED}$name@メンバー削除")
        var i = 0
        val members = WorldGuard().getMember(player.world, name)?.playerDomain?.uniqueIds
        for (worldPlayer in members?.toList() ?: return) {
            gui.addItem(Item().make(Material.PLAYER_HEAD, worldPlayer.toString(), Bukkit.getPlayer(worldPlayer as UUID)?.name ?: worldPlayer.toString(), null))
            i++
            if (i >= gui.size) { continue }
        }
        player.openInventory(gui)
    }
    fun advancePayment(player: Player, name: String, money: Int) {
        if (money > Economy().get(player)!!) {
            Player().errorMessage(player, "お金が足りません")
        } else {
            Economy().remove(player, money, true)
            Scoreboard().set("protectionContract", name, 2)
            player.sendMessage("${ChatColor.AQUA}前払いしました")
        }
        player.closeInventory()
    }
    private fun playerHead(target: Player): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as SkullMeta
        meta.setDisplayName(target.name)
        meta.setOwningPlayer(target)
        item.setItemMeta(meta)
        return item
    }
    fun buyGUI(player: Player, sign: Sign) {
        val money = sign.getLine(1).replace("${ChatColor.GREEN}", "").replace("円", "").toInt()
        val name = WorldGuard().getName(sign.location) ?: return
        if (WorldGuard().getOwnerOfRegion(sign.location)?.size() != 0) {
            if (WorldGuard().getOwnerOfRegion(sign.location)?.contains(player.uniqueId) == true) {
                ownerGUI(player, name, money)
            } else {
                Player().errorMessage(player, "この土地は既に買われています")
            }
            return
        }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}$name@土地購入")
        gui.setItem(4, Item().make(Material.EMERALD, "${ChatColor.GREEN}購入", "${money}円", null))
        player.openInventory(gui)
    }
    fun buy(player: Player, item: ItemStack, guiName: String, plugin: Plugin) {
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        when (item.itemMeta?.displayName) {
            "${ChatColor.GREEN}購入" -> {
                val money = item.itemMeta?.lore?.get(0)?.replace("円", "")?.toInt() ?: return
                if (money > Economy().get(player)!!) {
                    Player().errorMessage(player, "お金が足りません")
                    return
                }
                val name = guiName.replace("${ChatColor.BLUE}", "").replace("@土地購入", "")
                if (WorldGuard().getOwner(player.world, name) != "") {
                    Player().errorMessage(player, "この土地は既に買われています")
                    return
                }
                WorldGuard().addOwnerToRegion(name, player)
                player.closeInventory()
                player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
                Economy().remove(player, money, true)
                if (player.world.name != "shop") { return }
                Yml().addToList(plugin, "conservationLand", "protectedName", name)
                Scoreboard().set("protectionContract", name, 1)
            }
        }
    }
    fun price(player: Player): Int {
        val session = WorldEdit.getInstance().sessionManager[BukkitAdapter.adapt(player)]

        try {
            val region: Region = session.getSelection(BukkitAdapter.adapt(player.world))
            if (region is CuboidRegion) {
                val cuboidRegion = region

                // x軸およびz軸の長さを計算
                val xLength =
                    Math.abs(cuboidRegion.maximumPoint.blockX - cuboidRegion.minimumPoint.blockX) + 1
                val zLength =
                    Math.abs(cuboidRegion.maximumPoint.blockZ - cuboidRegion.minimumPoint.blockZ) + 1

                // x軸およびz軸上のブロック数を計算
                val blockCount = xLength * zLength
                return blockCount * if (blockCount <= 256) { 10 } else { 100 }
            }
        } catch (e: IncompleteRegionException) {
            e.printStackTrace()
        }
        return 0
    }
    fun doesRegionContainProtection(player: Player): Boolean {
        val session = WorldEdit.getInstance().sessionManager[BukkitAdapter.adapt(player)]

        try {
            val region: Region = session.getSelection(BukkitAdapter.adapt(player.world))
            if (region is CuboidRegion) {
                val minPoint = region.minimumPoint
                val maxPoint = region.maximumPoint

                // 選択範囲内のx座標とz座標を取得するリスト
                val xzCoordinates = mutableListOf<Pair<Int, Int>>()

                // 選択範囲内のすべてのx座標とz座標を取得
                for (x in minPoint.blockX until maxPoint.blockX + 1) {
                    for (z in minPoint.blockZ until maxPoint.blockZ + 1) {
                        xzCoordinates.add(Pair(x, z))
                    }
                }

                // 取得したx座標とz座標のリストを使って処理を行います
                for ((x, z) in xzCoordinates) {
                    if (WorldGuard().getName(player.world.getBlockAt(x, 0, z).location) == null) { continue }
                    return true
                }
                return false
            }
        } catch (e: IncompleteRegionException) {
            e.printStackTrace()
        }
        return false
    }
    fun listRegionsInWorld(player: Player) {
        val regionManager = com.sk89q.worldguard.WorldGuard.getInstance().platform.regionContainer.get(BukkitAdapter.adapt(player.world))
        val regions: MutableMap<String, ProtectedRegion>? = regionManager?.regions ?: return

        player.sendMessage("${ChatColor.YELLOW}---あなたの所持保護一覧---")
        var c = 1
        for ((regionName, region) in regions ?: return) {
            if (!region.owners.contains(player.uniqueId)) { continue }
            player.sendMessage("${ChatColor.AQUA}$c,$regionName")
            c++
        }
    }
}
