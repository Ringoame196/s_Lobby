package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

class ResourcePack {
    fun save(plugin: Plugin, key: String, text: String) {
        val filePath = File(plugin.dataFolder, "/resourcePack.yml")
        val yamlConfiguration = YamlConfiguration.loadConfiguration(filePath)

        // 既存のデータを上書き
        yamlConfiguration.set(key, text)

        try {
            yamlConfiguration.save(filePath)
        } catch (e: IOException) {
            println("Error while saving data: ${e.message}")
        }
    }
    fun update(plugin: Plugin) {
        val path = "${plugin.dataFolder}/resourcePack.yml"
        val configPath = File(path)
        if (!configPath.exists()) { return }
        val url = YamlConfiguration.loadConfiguration(File(path)).getString("URL")
        val gas = YamlConfiguration.loadConfiguration(File(path)).getString("GAS_URL") ?: return

        val newURL = Web().get(gas).toString()
        if (url == newURL) { return }
        save(plugin, "URL", newURL)
        GameData.DataManager.resourcePack = newURL
        Bukkit.broadcastMessage("${ChatColor.YELLOW}[青りんごサーバー]リソースパックが更新されました")
        Bukkit.broadcastMessage("${ChatColor.YELLOW}[青りんごサーバー]次回参加時に適応されます")
    }
    fun adaptation(player: Player) {
        val resourcePack = GameData.DataManager.resourcePack ?: return
        player.setResourcePack(resourcePack)
    }
}
