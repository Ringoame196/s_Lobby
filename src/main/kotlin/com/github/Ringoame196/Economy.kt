package com.github.Ringoame196

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class Economy {
    private val uneiName = "akamaruXkun"
    private val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java)
    private val economy = rsp?.provider
    fun get(player: String): Double {
        return economy?.getBalance(player) ?: 0.0
    }
    fun getUnei(): Double {
        return get(uneiName) ?: 0.0
    }
    private fun giveBill(player: Player?, amount: Int) {
        Player().errorMessage(player ?: return, "決済処理が正常に処理されませんでした(error1)")
        player.sendMessage("${ChatColor.GREEN}そのため手形発行されました(運営に渡してください)")
        player.inventory.addItem(Item().make(Material.PAPER, "${ChatColor.GREEN}運営手形[公式]", "値段:${amount}円", 11))
    }
    fun add(player: String, amount: Int, unei: Boolean) {
        if (unei) {
            if (getUnei() < amount.toDouble()) {
                giveBill(Bukkit.getPlayer(player), amount)
            } else {
                remove(uneiName, amount, false)
            }
        }
        economy?.depositPlayer(player, amount.toDouble())
        Player().sendActionBar(Bukkit.getPlayer(player) ?: return, "${ChatColor.AQUA}+${amount}円")
    }
    fun remove(player: String, amount: Int, unei: Boolean) {
        economy?.withdrawPlayer(player, amount.toDouble())
        if (unei) {
            add(uneiName, amount, false)
        }
        Player().sendActionBar(Bukkit.getPlayer(player) ?: return, "${ChatColor.RED}-${amount}円")
    }
}
