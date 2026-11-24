package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authors")
data class AuthorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val literaryTitle: String,
    val era: String,
    val bio: String,
    val accentColorHex: Long,
    val emblemSymbol: String,
    val sectionOrder: Int
)
