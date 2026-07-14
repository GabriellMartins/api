package com.kodexerp.backend.admin.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document(collection = "settings")
data class Settings(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Indexed(unique = true)
    val key: String,

    val value: String,

    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
