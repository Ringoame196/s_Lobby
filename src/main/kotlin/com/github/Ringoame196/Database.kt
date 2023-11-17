package com.github.Ringoame196

import java.sql.Connection
import java.sql.DriverManager

class Database {
    private val host = Data.DataManager.databaseHost
    private val port = Data.DataManager.databasePort
    private val userName = Data.DataManager.databaseUsername
    private val password = Data.DataManager.databasePassword

    private fun connection(databaseName: String): Connection? {
        return DriverManager.getConnection(
            "jdbc:mysql://$host:$port/$databaseName",
            userName,
            password
        )
    }
    private fun make(player: String, newPoint: Int, databaseName: String, tableName: String) {
        val insertStatement = connection(databaseName)?.prepareStatement(insertQuery(tableName))
        insertStatement?.setString(1, player)
        insertStatement?.setInt(2, newPoint)

        insertStatement?.executeUpdate()
        insertStatement?.close()
    }
    private fun selectQuery(tableName: String): String {
        return "SELECT * FROM $tableName WHERE uuid = ?;"
    }
    private fun updateQuery(tableName: String): String {
        return "UPDATE $tableName SET point = ? WHERE uuid = ?;"
    }
    private fun insertQuery(tableName: String): String {
        return "INSERT INTO $tableName (uuid, point) VALUES (?, ?);"
    }
    fun getInt(player: String, databaseName: String, tableName: String, point: String): Int {
        var getPoint = 0
        val connection = connection(databaseName)
        val selectStatement = connection?.prepareStatement(selectQuery(tableName))
        selectStatement?.setString(1, player)
        val resultSet = selectStatement?.executeQuery()

        if (resultSet?.next() == true) {
            getPoint = resultSet.getInt(point)
        }

        // 後処理
        selectStatement?.close()
        connection?.close()

        return getPoint
    }
    fun setPlayerPoint(player: String, databaseName: String, tableName: String, newPoint: Int) {
        // データベース接続
        val connection = connection(databaseName) ?: return

        // プレイヤーのUUIDで検索
        val selectStatement = connection.prepareStatement(selectQuery(tableName))
        selectStatement.setString(1, player)
        val resultSet = selectStatement.executeQuery()

        if (resultSet.next()) {
            // データが存在する場合、更新クエリを実行
            val updateStatement = connection.prepareStatement(updateQuery(tableName))
            updateStatement.setInt(1, newPoint)
            updateStatement.setString(2, player)

            updateStatement.executeUpdate()
            updateStatement.close()
        } else {
            make(player, newPoint, databaseName, tableName)
        }

        // 後処理
        selectStatement.close()
        connection.close()
    }
}
