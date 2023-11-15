package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory

class GUI {
    fun make(title: String, size: Int): Inventory {
        return Bukkit.createInventory(null, size, title)
    }
}
