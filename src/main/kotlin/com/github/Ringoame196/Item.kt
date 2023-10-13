package com.github.Ringoame196

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Item {
    fun make(material: Material, name: String, lore: String?, customModelData: Int?): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta?.setDisplayName(name)
        meta?.setCustomModelData(customModelData)
        if (lore != null) {
            meta?.lore = mutableListOf(lore)
        }
        item.setItemMeta(meta)
        return item
    }
    fun smartphone(): ItemStack {
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val meta = item.itemMeta
        meta?.setDisplayName("${ChatColor.YELLOW}スマートフォン")
        meta?.setCustomModelData(1)
        item.setItemMeta(meta)
        return item
    }
}
