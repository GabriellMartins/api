package com.kodexerp.backend.admin.config

import com.kodexerp.backend.admin.service.PlanService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val planService: PlanService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        planService.initializeDefaultPlans()
    }
}
