package com.presentation.intellij.tips.offer.infrastracture.repository

import com.presentation.intellij.tips.offer.Offer
import org.springframework.stereotype.Repository
import java.net.URI
import java.util.*

@Repository
class InMemoryOffersRepository : OffersRepository {

    private val offers = HashMap<String, Offer>()


    override fun addOffer(accountId: String, offer: Offer) {
        offers[accountId] = offer
    }

    override fun getOffers(): List<Offer> {
        return if (offers.isEmpty()) {
            getMockOffers()
        } else {
            offers.values.toList()
        }
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