package com.presentation.intellij.tips.offer.infrastracture.repository

import com.presentation.intellij.tips.offer.Offer

interface OffersRepository {

    fun addOffer(accountId: String, offer: Offer)

    fun getOffers(): List<Offer>

    fun getOfferById(offerId: Long): Offer?

    fun updateOffer(offer: Offer): Offer

}