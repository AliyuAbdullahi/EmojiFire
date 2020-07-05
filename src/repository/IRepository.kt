package com.fire.repository

import com.fire.models.EmojiPhrase
import com.fire.models.User

interface IRepository {
    suspend fun add(emoji: String, phrase: String, userId: String)
    suspend fun getPhrase(id: Int): EmojiPhrase?
    suspend fun getPhrase(id: String): EmojiPhrase?
    suspend fun getPhrases(): List<EmojiPhrase>
    suspend fun removePhrase(id: String): Boolean
    suspend fun removePhrase(id: Int): Boolean
    suspend fun clear()

    suspend fun createUser(email: String, displayName: String, password: String)
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?
    suspend fun getUserById(id: String): User?
}