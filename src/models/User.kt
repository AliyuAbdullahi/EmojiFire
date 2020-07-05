package com.fire.models

import io.ktor.auth.Principal
import org.jetbrains.exposed.sql.Table
import java.io.Serializable

data class User(val displayName: String, val userId: String, val userPassword: String, val email: String): Serializable, Principal

object UserTable: Table() {
    val id  = varchar("id", 128).primaryKey()
    val email = varchar("email", 128).uniqueIndex()
    val displayName = varchar("display_name", 256)
    val passwordHash = varchar("password_hash", 64)
}