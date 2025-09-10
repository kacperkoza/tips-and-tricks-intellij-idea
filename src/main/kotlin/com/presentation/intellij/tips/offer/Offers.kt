package com.presentation.intellij.tips.offer

import java.math.BigDecimal
import java.net.URI
import java.time.LocalDateTime

data class Offers(
    val offers: List<Offer>,
)

enum class OfferStatus {
    DRAFT,      // Offer created but not published
    ACTIVE,     // Offer is live and available
    SUSPENDED,  // Offer temporarily disabled
    EXPIRED     // Offer has expired or been deactivated
}

enum class OfferCategory {
    ELECTRONICS, FASHION, HOME, BOOKS, SPORTS, AUTOMOTIVE, OTHER
}

data class Offer(
    val id: Long,
    val title: String,
    val description: String,
    val price: BigDecimal,
    val category: OfferCategory,
    val status: OfferStatus = OfferStatus.DRAFT,
    val imageUrl: URI,
    val sellerId: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null,
    val tags: Set<String> = emptySet(),
    val viewCount: Long = 0
) {
    fun isActive(): Boolean = status == OfferStatus.ACTIVE && !isExpired()

    fun isExpired(): Boolean = expiresAt?.isBefore(LocalDateTime.now()) ?: false

    fun canBeModified(): Boolean = status in listOf(OfferStatus.DRAFT, OfferStatus.SUSPENDED)

    fun activate(): Offer = copy(
        status = OfferStatus.ACTIVE,
        updatedAt = LocalDateTime.now()
    )

    fun suspend(): Offer = copy(
        status = OfferStatus.SUSPENDED,
        updatedAt = LocalDateTime.now()
    )

    fun expire(): Offer = copy(
        status = OfferStatus.EXPIRED,
        updatedAt = LocalDateTime.now()
    )
}
