package com.kodexerp.backend.subscription.repository

import com.kodexerp.backend.subscription.entity.Plan
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PlanRepository : MongoRepository<Plan, UUID> {
    fun findByIsActiveTrueOrderByPriceAsc(): List<Plan>
    fun findByIdAndIsActiveTrue(id: UUID): Plan?
}
