package com.kodexerp.backend.admin.repository

import com.kodexerp.backend.admin.entity.Settings
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SettingsRepository : MongoRepository<Settings, UUID> {
    fun findByKey(key: String): Settings?
}
