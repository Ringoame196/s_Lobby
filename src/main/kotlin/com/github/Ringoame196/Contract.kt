package com.github.Ringoame196

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Contract {
    fun request(player: Player, message: String) {
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta as BookMeta
        val money = message.replace("!契約 ", "").toInt()
        if (money == 0) { return }
        meta.setDisplayName("${ChatColor.YELLOW}契約書[契約待ち]")
        val bookMessage = meta.getPage(1)
            .replace("甲方：[プレイヤー名]\nUUID：[UUID]", "甲方：${player.name}\nUUID：${player.uniqueId}")
            .replace("取引金額：[値段]", "取引金額：${money}円")
        meta.setPage(1, bookMessage)
        item.setItemMeta(meta)
        player.inventory.setItemInMainHand(item)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
    fun contract(player: Player, message: String) {
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta as BookMeta
        val money = message.replace("!契約 ", "")
        val bookMessage = meta.getPage(1)
        val priceIndex = bookMessage.indexOf("取引金額：")
        val priceMessage = bookMessage.substring(priceIndex + "取引金額：".length).replace("円", "")
        if (money != priceMessage) {
            Player().errorMessage(player, "金額が違います")
            return
        }
        if (Economy().get(player)!! < money.toInt()) {
            Player().errorMessage(player, "お金が足りません")
            return
        }
        Economy().remove(player, money.toInt(), false)
        val currentDate = LocalDate.now()

        // 日付を指定したフォーマットで文字列として取得
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(dateFormatter)
        meta.setDisplayName("${ChatColor.RED}契約本@${money}円契約")
        val setBookMessage = meta.getPage(1)
            .replace("乙方：[プレイヤー名]\nUUID：[UUID]", "乙方：${player.name}\nUUID：${player.uniqueId}")
            .replace("契約日：[日付]", "契約日：$formattedDate")
        meta.setPage(1, setBookMessage)
        item.setItemMeta(meta)
        player.inventory.setItemInMainHand(item)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
    fun returnMoney(player: Player) {
        val item = player.inventory.itemInMainHand
        val bookMessage = item.itemMeta as BookMeta
        if (!bookMessage.getPage(1).contains("UUID：${player.uniqueId}")) {
            return
        }
        val money = item.itemMeta?.displayName?.replace("${ChatColor.RED}契約本@", "")?.replace("円契約", "")?.toInt()
        Economy().add(player, money ?: return, false)
        player.inventory.setItemInMainHand(ItemStack(Material.AIR))
    }
    fun copyBlock(item: ItemStack, player: Player): ItemStack {
        val meta = item.itemMeta as BookMeta
        val currentDate = LocalDate.now()

        // 日付を指定したフォーマットで文字列として取得
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(dateFormatter)
        meta.setPage(
            1,
            "${ChatColor.DARK_RED}STOP COPYING\n\n" +
                "『契約書の複製は、青りんごサーバーの規約により禁止されています。』\n\n\n" +
                "プレイヤー名:${player.name}\n" +
                "日にち:$formattedDate"
        )
        item.setItemMeta(meta)
        return item
    }
}
