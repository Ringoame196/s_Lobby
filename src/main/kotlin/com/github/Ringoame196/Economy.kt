package com.github.Ringoame196

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class Economy {
    fun get(player: Player): Double? {
        val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java)
        val economy = rsp?.provider
        return economy?.getBalance(player)
    }
    fun add(player: Player, amount: Int, unei: Boolean) {
        val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java)
        val economy = rsp?.provider
        economy!!.depositPlayer(player, amount.toDouble())
        Player().sendActionBar(player, "${ChatColor.AQUA}+${amount}円")
        if (unei) {
            economy.withdrawPlayer("akamaruXkun", amount.toDouble())
        }
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
