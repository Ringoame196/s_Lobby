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
    fun add(player: Player, amount: Int) {
        val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java)
        val economy = rsp?.provider
        economy!!.depositPlayer(player, amount.toDouble())
        player.sendMessage("${ChatColor.AQUA}+${amount}円")
    }
    fun remove(player: Player, amount: Int) {
        val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java)
        val economy = rsp?.provider
        economy?.withdrawPlayer(player, amount.toDouble())
        player.sendMessage("${ChatColor.RED}-${amount}円")
    }
}
