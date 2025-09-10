package com.presentation.intellij.tips.offer.infrastracture.repository

import com.presentation.intellij.tips.offer.Offer
import com.presentation.intellij.tips.offer.OfferCategory
import com.presentation.intellij.tips.offer.OfferStatus
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDateTime

@Repository
class InMemoryOffersRepository : OffersRepository {

    private val offers = mutableMapOf<String, Offer>()
    private val offersById = mutableMapOf<Long, Offer>()

    override fun addOffer(accountId: String, offer: Offer) {
        offers[accountId] = offer
        offersById[offer.id] = offer
    }

    override fun getOffers(): List<Offer> {
        return if (offers.isEmpty()) {
            val mockOffers = getMockOffers()
            // Add mock offers to our internal storage for consistency
            mockOffers.forEach { offer ->
                offersById[offer.id] = offer
            }
            mockOffers
        } else {
            offers.values.toList()
        }
    }

    override fun getOfferById(offerId: Long): Offer? {
        // If we don't have any offers yet, initialize with mock data
        if (offersById.isEmpty()) {
            getOffers() // This will populate offersById with mock data
        }
        return offersById[offerId]
    }

    override fun updateOffer(offer: Offer): Offer {
        offersById[offer.id] = offer
        // Also update in the accountId-based map if it exists
        offers.entries.find { it.value.id == offer.id }?.let { entry ->
            offers[entry.key] = offer
        }
        return offer
    }

    private fun getMockOffers(): List<Offer> = listOf(
        Offer(
            id = 1L,
            title = "Gaming Laptop",
            description = "High-performance gaming laptop with RTX graphics card",
            price = BigDecimal("1299.99"),
            category = OfferCategory.ELECTRONICS,
            status = OfferStatus.ACTIVE,
            imageUrl = URI("https://example.com/laptop.jpg"),
            sellerId = "seller1",
            createdAt = LocalDateTime.now().minusDays(5),
            tags = setOf("gaming", "laptop", "rtx", "warranty")
        ),
        Offer(
            id = 2L,
            title = "Designer Jeans",
            description = "Premium denim jeans in excellent condition",
            price = BigDecimal("89.99"),
            category = OfferCategory.FASHION,
            status = OfferStatus.ACTIVE,
            imageUrl = URI("https://example.com/jeans.jpg"),
            sellerId = "seller2",
            createdAt = LocalDateTime.now().minusDays(3),
            tags = setOf("jeans", "designer", "size32", "condition-excellent")
        ),
        Offer(
            id = 3L,
            title = "Cookbook Collection",
            description = "Set of 5 professional cooking books",
            price = BigDecimal("45.50"),
            category = OfferCategory.BOOKS,
            status = OfferStatus.ACTIVE,
            imageUrl = URI("https://example.com/books.jpg"),
            sellerId = "seller3",
            createdAt = LocalDateTime.now().minusDays(1),
            tags = setOf("cookbook", "cooking", "professional", "collection")
        ),
        Offer(
            id = 4L,
            title = "Bicycle - Draft",
            description = "Mountain bike, needs final photos",
            price = BigDecimal("250.00"),
            category = OfferCategory.SPORTS,
            status = OfferStatus.DRAFT,
            imageUrl = URI("https://example.com/bike.jpg"),
            sellerId = "seller4",
            createdAt = LocalDateTime.now().minusHours(2),
            tags = setOf("bicycle", "mountain", "sports")
        )
    ).also { print("some message") }
}