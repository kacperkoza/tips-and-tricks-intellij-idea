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

    init {
        val sampleOffers = listOf(
            Offer(
                id = 1L,
                title = "MacBook Pro 16-inch",
                description = "Like new MacBook Pro with M2 chip, 16GB RAM, 512GB SSD",
                price = BigDecimal("2499.99"),
                category = OfferCategory.ELECTRONICS,
                status = OfferStatus.ACTIVE,
                imageUrl = URI("https://example.com/images/macbook.jpg"),
                sellerId = "user001",
                tags = setOf("laptop", "apple", "professional")
            ),
            Offer(
                id = 2L,
                title = "Nike Air Max 270",
                description = "Brand new Nike Air Max 270 sneakers, size 10, never worn",
                price = BigDecimal("149.99"),
                category = OfferCategory.SPORTS,
                status = OfferStatus.ACTIVE,
                imageUrl = URI("https://example.com/images/nike-air-max.jpg"),
                sellerId = "user002",
                tags = setOf("shoes", "nike", "sports", "new")
            ),
            Offer(
                id = 3L,
                title = "Coffee Table - Oak Wood",
                description = "Beautiful handcrafted oak coffee table, perfect for living room",
                price = BigDecimal("299.00"),
                category = OfferCategory.HOME,
                status = OfferStatus.ACTIVE,
                imageUrl = URI("https://example.com/images/coffee-table.jpg"),
                sellerId = "user003",
                tags = setOf("furniture", "wood", "handcrafted")
            ),
            Offer(
                id = 4L,
                title = "iPhone 14 Pro",
                description = "iPhone 14 Pro 128GB, Space Black, excellent condition with case",
                price = BigDecimal("899.99"),
                category = OfferCategory.ELECTRONICS,
                status = OfferStatus.SUSPENDED,
                imageUrl = URI("https://example.com/images/iphone14.jpg"),
                sellerId = "user001",
                tags = setOf("smartphone", "apple", "mobile")
            ),
            Offer(
                id = 5L,
                title = "The Art of Clean Code",
                description = "Programming book in excellent condition, clean code principles",
                price = BigDecimal("39.99"),
                category = OfferCategory.BOOKS,
                status = OfferStatus.ACTIVE,
                imageUrl = URI("https://example.com/images/clean-code-book.jpg"),
                sellerId = "user004",
                tags = setOf("programming", "education", "software")
            ),
            Offer(
                id = 6L,
                title = "Vintage Leather Jacket",
                description = "Genuine leather jacket from the 80s, size M, great vintage condition",
                price = BigDecimal("199.50"),
                category = OfferCategory.FASHION,
                status = OfferStatus.ACTIVE,
                imageUrl = URI("https://example.com/images/leather-jacket.jpg"),
                sellerId = "user005",
                tags = setOf("vintage", "leather", "jacket", "80s")
            ),
            Offer(
                id = 7L,
                title = "BMW 3 Series Spare Parts",
                description = "Original BMW spare parts for 3 Series E90, various components available",
                price = BigDecimal("150.00"),
                category = OfferCategory.AUTOMOTIVE,
                status = OfferStatus.DRAFT,
                imageUrl = URI("https://example.com/images/bmw-parts.jpg"),
                sellerId = "user006",
                tags = setOf("bmw", "spare-parts", "automotive", "e90")
            ),
            Offer(
                id = 8L,
                title = "Gaming Headset RGB",
                description = "High-quality gaming headset with RGB lighting and surround sound",
                price = BigDecimal("79.99"),
                category = OfferCategory.ELECTRONICS,
                status = OfferStatus.ACTIVE,
                imageUrl = URI("https://example.com/images/gaming-headset.jpg"),
                sellerId = "user007",
                tags = setOf("gaming", "headset", "rgb", "audio")
            ),
            Offer(
                id = 9L,
                title = "Designer Handbag",
                description = "Authentic designer handbag, barely used, comes with authenticity certificate",
                price = BigDecimal("450.00"),
                category = OfferCategory.FASHION,
                status = OfferStatus.EXPIRED,
                imageUrl = URI("https://example.com/images/designer-handbag.jpg"),
                sellerId = "user008",
                tags = setOf("designer", "handbag", "luxury", "authentic"),
                expiresAt = LocalDateTime.now().minusDays(5)
            ),
            Offer(
                id = 10L,
                title = "Mountain Bike Trek",
                description = "Trek mountain bike, 21 speeds, perfect for trail riding and adventures",
                price = BigDecimal("650.00"),
                category = OfferCategory.SPORTS,
                status = OfferStatus.ACTIVE,
                imageUrl = URI("https://example.com/images/mountain-bike.jpg"),
                sellerId = "user009",
                tags = setOf("bike", "mountain", "trek", "cycling", "outdoor")
            )
        )

        sampleOffers.forEach { offer ->
            addOffer(offer.sellerId, offer)
        }
    }

    override fun addOffer(accountId: String, offer: Offer) {
        offers[accountId] = offer
        offersById[offer.id] = offer
    }

    override fun getOffers(): List<Offer> {
        return offers.values.toList()
    }

    override fun getOfferById(offerId: Long): Offer? {
        return offersById[offerId]
    }

    override fun updateOffer(offer: Offer): Offer {
        offersById[offer.id] = offer
        offers.entries.find { it.value.id == offer.id }?.let { entry ->
            offers[entry.key] = offer
        }
        return offer
    }
}