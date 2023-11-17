package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class Database {
    fun incrementPlayerUUID(player: Player, databaseName: String, tableName: String) {
        // データベース接続
        val connection: Connection?
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://${Data.DataManager.databaseHost}:${Data.DataManager.databasePort}/$databaseName",
                Data.DataManager.databaseUsername,
                Data.DataManager.databasePassword
            )
        } catch (e: SQLException) {
            Bukkit.broadcastMessage(e.message.toString())
            return
        }

        // プレイヤーのUUIDで検索
        val selectQuery = "SELECT * FROM $tableName WHERE uuid = ?;"
        val selectStatement = connection.prepareStatement(selectQuery)
        selectStatement.setString(1, player.uniqueId.toString())
        val resultSet = selectStatement.executeQuery()

        if (resultSet.next()) {
            // データが存在する場合、更新クエリを実行
            val updateQuery = "UPDATE $tableName SET point = point + 1 WHERE uuid = ?;"
            val updateStatement = connection.prepareStatement(updateQuery)
            updateStatement.setString(1, player.uniqueId.toString())

            val rowsUpdated = updateStatement.executeUpdate()
            if (rowsUpdated > 0) {
                println("UUIDが更新されました。")
            } else {
                println("該当するデータが見つかりませんでした。")
            }
            updateStatement.close()
        } else {
            // データが存在しない場合、新しく作成
            val insertQuery = "INSERT INTO $tableName (uuid, point) VALUES (?, 1);"
            val insertStatement = connection.prepareStatement(insertQuery)
            insertStatement.setString(1, player.uniqueId.toString())

            val rowsInserted = insertStatement.executeUpdate()
            if (rowsInserted > 0) {
                println("新しいデータが作成されました。")
            } else {
                println("新しいデータの作成に失敗しました。")
            }
            insertStatement.close()
        }

        // 後処理
        selectStatement.close()
        connection.close()
    }
}
