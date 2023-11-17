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
        val insertQuery = "INSERT INTO $tableName (uuid, point) VALUES (?, ?);"
        val insertStatement = connection(databaseName)?.prepareStatement(insertQuery)
        insertStatement?.setString(1, player)
        insertStatement?.setInt(2, newPoint)

        insertStatement?.executeUpdate()
        insertStatement?.close()
    }
    fun getInt(player: String, databaseName: String, tableName: String, point: String): Int {
        var getPoint = 0
        val connection = connection(databaseName)
        val selectQuery = "SELECT * FROM $tableName WHERE uuid = ?;"
        val selectStatement = connection?.prepareStatement(selectQuery)
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
        val selectQuery = "SELECT * FROM $tableName WHERE uuid = ?;"
        val selectStatement = connection.prepareStatement(selectQuery)
        selectStatement.setString(1, player)
        val resultSet = selectStatement.executeQuery()

        if (resultSet.next()) {
            // データが存在する場合、更新クエリを実行
            val updateQuery = "UPDATE $tableName SET point = ? WHERE uuid = ?;"
            val updateStatement = connection.prepareStatement(updateQuery)
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
