package com.fire.models

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
import java.io.Serializable

data class EmojiPhrase(var id: Int, val emoji: String, val phrase: String, val userId: String): Serializable

object EmojiPhraseTable: IntIdTable() {
    val emoji: Column<String> = varchar("emoji", 255)
    val phrase: Column<String> = varchar("phrase", 255)
    val user: Column<String> = varchar("user_id", 128).index()
}