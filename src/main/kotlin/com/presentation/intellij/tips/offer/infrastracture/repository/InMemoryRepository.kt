package com.presentation.intellij.tips.offer.infrastracture.repository

import com.presentation.intellij.tips.offer.infrastracture.api.Offer
import org.springframework.stereotype.Repository
import java.net.URI

@Repository
class InMemoryRepository : OffersRepository {

    private val offers = mutableMapOf<Long, Offer>()

    override fun getOffers(): List<Offer> {
        return if (offers.isEmpty()) {
            getMockOffers()
        } else {
            offers.values.toList()
        }
    }

    override fun addOffer(accountId: String, offer: Offer) {
        offers[offer.id] = offer
    }

    private fun getMockOffers(): List<Offer> {
        return listOf(
            Offer(1L, "test", URI("https://google.pl")),
            Offer(2L, "test", URI("https://google.pl")),
            Offer(3L, "test", URI("https://google.pl")),
            Offer(4L, "test", URI("https://google.pl")),
            Offer(5L, "test", URI("https://google.pl")),
            Offer(6L, "test", URI("https://google.pl")),
            Offer(7L, "test", URI("https://google.pl")),
            Offer(8L, "test", URI("https://google.pl")),
        )

    }
}