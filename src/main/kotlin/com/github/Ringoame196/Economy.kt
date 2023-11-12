package com.github.Ringoame196

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class Economy {
    fun get(player: Player): Double? {
        val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java)
        val economy = rsp?.provider
        return economy?.getBalance(player)
    }
    fun getUnei(player: Player) {
        val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java)
        val economy = rsp?.provider
        val price = economy?.getBalance("akamaruXkun")?.toInt() ?: return
        player.sendMessage("${ChatColor.YELLOW}[運営のお金]" + if (price >= 1000) { "${ChatColor.AQUA}" } else { "${ChatColor.DARK_RED}" } + price + "円")
    }
    fun add(player: Player, amount: Int, unei: Boolean) {
        val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java)
        val economy = rsp?.provider
        if (unei) {
            if (economy?.getBalance("akamaruXkun")!! < amount.toDouble()) {
                Player().errorMessage(player, "決済処理が正常に処理されませんでした(error1)")
                player.sendMessage("${ChatColor.GREEN}そのため手形発行されました(運営に渡してください)")
                player.inventory.addItem(Item().make(Material.PAPER, "${ChatColor.GREEN}運営手形[公式]", "値段:${amount}円", 11))
                return
            }
            economy.withdrawPlayer("akamaruXkun", amount.toDouble())
        }
        economy!!.depositPlayer(player, amount.toDouble())
        Player().sendActionBar(player, "${ChatColor.AQUA}+${amount}円")
    }
    fun remove(player: Player, amount: Int, unei: Boolean) {
        val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java)
        val economy = rsp?.provider
        economy?.withdrawPlayer(player, amount.toDouble())
        Player().sendActionBar(player, "${ChatColor.RED}-${amount}円")
        if (unei) {
            economy?.depositPlayer("akamaruXkun", amount.toDouble())
        }
    }
}
