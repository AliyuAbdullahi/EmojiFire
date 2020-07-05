package com.fire.repository

import com.fire.models.EmojiPhrase
import com.fire.models.EmojiPhraseTable
import com.fire.models.User
import com.fire.models.UserTable
import com.fire.routes.hash
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class EmojiPhraseRepository : IRepository {

    override suspend fun add(emojiValue: String, phraseValue: String, userIdValue: String) {
        transaction {
            EmojiPhraseTable.insert {
                it[emoji] = emojiValue
                it[phrase] = phraseValue
                it[user] = userIdValue
            }
        }
    }

    override suspend fun getPhrase(id: Int): EmojiPhrase? = dbQuery {
        EmojiPhraseTable.select {
            (EmojiPhraseTable.id eq id)
        }.mapNotNull { toEmojiPhrase(it) }.singleOrNull()
    }

    override suspend fun getPhrase(id: String): EmojiPhrase? = getPhrase(id.toInt())

    override suspend fun getPhrases(): List<EmojiPhrase> = dbQuery {
        EmojiPhraseTable.selectAll().map { toEmojiPhrase(it) }
    }

    override suspend fun removePhrase(id: String): Boolean = removePhrase(id.toInt())

    override suspend fun removePhrase(id: Int): Boolean {
        if (getPhrase(id) == null) {
            throw IllegalArgumentException("No phrase with id $id")
        }

        return dbQuery {
            EmojiPhraseTable.deleteWhere { EmojiPhraseTable.id eq id } > 0
        }
    }

    override suspend fun clear() {
        EmojiPhraseTable.deleteAll()
        UserTable.deleteAll()
    }

    @KtorExperimentalAPI
    override suspend fun createUser(emailValue: String, displayNameValue: String, passwordValue: String) {

        if (getUserByEmail(emailValue) != null) {
            throw IllegalArgumentException("User Already exist")
        }
        val theSalt: String = "$emailValue$displayNameValue${hash(passwordValue)}"
        val uuid = UUID.nameUUIDFromBytes(theSalt.toByteArray()).toString()

        transaction {
            UserTable.insert {
                it[email] = emailValue
                it[passwordHash] = hash(passwordValue)
                it[displayName] = displayNameValue
                it[id] = uuid
            }
        }
    }

    @KtorExperimentalAPI
    override suspend fun getUserByEmailAndPassword(emailValue: String, passwordValue: String): User? = dbQuery {
        UserTable.select { UserTable.email eq emailValue and UserTable.passwordHash.eq(hash(passwordValue)) }
            .mapNotNull { toUser(it) }.singleOrNull()
    }

    override suspend fun getUserById(idValue: String): User? = dbQuery {
        UserTable.select { UserTable.id eq idValue }.mapNotNull { toUser(it) }.singleOrNull()
    }

    override suspend fun getUserByEmail(emailValue: String): User? = dbQuery {
        UserTable.select { UserTable.email.eq(emailValue) }.mapNotNull { toUser(it) }.singleOrNull()
    }

    private fun toEmojiPhrase(row: ResultRow): EmojiPhrase = EmojiPhrase(
        id = row[EmojiPhraseTable.id].value,
        emoji = row[EmojiPhraseTable.emoji],
        phrase = row[EmojiPhraseTable.phrase],
        userId = row[EmojiPhraseTable.user]
    )

    private fun toUser(row: ResultRow): User = User(
        userId = row[UserTable.id],
        displayName = row[UserTable.displayName],
        userPassword = row[UserTable.passwordHash],
        email = row[UserTable.email]
    )

}