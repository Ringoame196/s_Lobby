package com.github.Ringoame196

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

class Yml {
    fun makePluginFolder(plugin: Plugin) {
        val dataFolder = plugin.dataFolder
        dataFolder.mkdirs()
    }
    fun addToList(plugin: Plugin, fileName: String, key: String, item: String) {
        val filePath = File(plugin.dataFolder, "$fileName.yml")
        val yamlConfiguration = YamlConfiguration.loadConfiguration(filePath)

        // 既存のリストを読み込むか新しいリストを作成
        val currentList = yamlConfiguration.getStringList(key) ?: mutableListOf()
        currentList.add(item)

        // リストを設定
        yamlConfiguration.set(key, currentList)

        try {
            yamlConfiguration.save(filePath)
            println("Item '$item' added to the list in $fileName.yml with key: $key")
        } catch (e: IOException) {
            println("Error while saving data: ${e.message}")
        }
    }
    fun getList(plugin: Plugin, fileName: String, key: String): List<String>? {
        val filePath = File(plugin.dataFolder, "$fileName.yml")
        val yamlConfiguration = YamlConfiguration.loadConfiguration(filePath)

        // 指定されたキーのリストを取得
        return yamlConfiguration.getStringList(key)
    }
    fun removeFromList(plugin: Plugin, fileName: String, key: String, item: String) {
        val filePath = File(plugin.dataFolder, "$fileName.yml")
        val yamlConfiguration = YamlConfiguration.loadConfiguration(filePath)

        // 既存のリストを読み込むか新しいリストを作成
        val currentList = yamlConfiguration.getStringList(key) ?: mutableListOf()

        // リストから指定されたアイテムを削除
        currentList.remove(item)

        // 更新後のリストを設定
        yamlConfiguration.set(key, currentList)

        try {
            yamlConfiguration.save(filePath)
            println("Item '$item' removed from the list in $fileName.yml with key: $key")
        } catch (e: IOException) {
            println("Error while saving data: ${e.message}")
        }
    }
    fun setList(plugin: Plugin, fileName: String, key: String, list: MutableList<String>) {
        val filePath = File(plugin.dataFolder, "$fileName.yml")
        val yamlConfiguration = YamlConfiguration.loadConfiguration(filePath)

        yamlConfiguration.set(key, list)

        try {
            yamlConfiguration.save(filePath)
            println("Item list removed from the list in $fileName.yml with key: $key")
        } catch (e: IOException) {
            println("Error while saving data: ${e.message}")
        }
    }
}
