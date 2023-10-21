package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class LandPurchase {
    fun make(player: Player, sign: Sign) {
        if (!player.isOp) { return }
        sign.setLine(0, "${ChatColor.YELLOW}[土地販売]")
        sign.setLine(1, "${ChatColor.GREEN}${sign.getLine(1)}円")
        sign.update()
    }
    fun buyGUI(player: Player, sign: Sign) {
        if (WorldGuard().getOwnerOfRegion(sign.location)?.size() != 0) {
            Player().errorMessage(player, "この土地は既に買われています")
            return
        }
        val money = sign.getLine(1).replace("${ChatColor.GREEN}", "").replace("円", "").toInt()
        val name = WorldGuard().getName(sign.location) ?: return
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
            }
        }
    }
}
