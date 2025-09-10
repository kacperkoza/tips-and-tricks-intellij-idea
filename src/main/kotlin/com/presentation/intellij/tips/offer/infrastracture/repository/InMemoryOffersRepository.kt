package com.presentation.intellij.tips.offer.infrastracture.repository

import com.presentation.intellij.tips.offer.Offer
import org.springframework.stereotype.Repository

@Repository
class InMemoryOffersRepository : OffersRepository {

    private val offers = mutableMapOf<String, Offer>()
    private val offersById = mutableMapOf<Long, Offer>()

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