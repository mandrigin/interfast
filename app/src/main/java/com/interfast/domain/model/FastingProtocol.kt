package com.interfast.domain.model

/**
 * Represents a fasting protocol with defined fasting and eating windows.
 */
data class FastingProtocol(
    val id: String,
    val name: String,
    val fastingHours: Int,
    val eatingHours: Int,
    val description: String,
    val isCustom: Boolean = false
) {
    val totalHours: Int get() = fastingHours + eatingHours

    companion object {
        val PROTOCOL_16_8 = FastingProtocol(
            id = "16_8",
            name = "16:8",
            fastingHours = 16,
            eatingHours = 8,
            description = "Standard intermittent fasting"
        )

        val PROTOCOL_18_6 = FastingProtocol(
            id = "18_6",
            name = "18:6",
            fastingHours = 18,
            eatingHours = 6,
            description = "Extended daily fast"
        )

        val PROTOCOL_20_4 = FastingProtocol(
            id = "20_4",
            name = "20:4",
            fastingHours = 20,
            eatingHours = 4,
            description = "Warrior diet"
        )

        val PROTOCOL_23_1 = FastingProtocol(
            id = "23_1",
            name = "23:1",
            fastingHours = 23,
            eatingHours = 1,
            description = "OMAD - One Meal A Day"
        )

        val DEFAULT_PROTOCOLS = listOf(
            PROTOCOL_16_8,
            PROTOCOL_18_6,
            PROTOCOL_20_4,
            PROTOCOL_23_1
        )

        fun custom(fastingHours: Int, eatingHours: Int): FastingProtocol {
            return FastingProtocol(
                id = "custom_${fastingHours}_${eatingHours}",
                name = "$fastingHours:$eatingHours",
                fastingHours = fastingHours,
                eatingHours = eatingHours,
                description = "Custom protocol",
                isCustom = true
            )
        }
    }
}
