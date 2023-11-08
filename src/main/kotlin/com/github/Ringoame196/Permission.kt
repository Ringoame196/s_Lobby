package com.github.Ringoame196

import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class Permission {
    fun add(player: Player, plugin: Plugin, permission: String) {
        val permissions = player.addAttachment(plugin) // "plugin" はプラグインのインスタンスを指します
        permissions.setPermission(permission, true)
        player.recalculatePermissions()
    }
}
