package com.presentation.intellij.tips.offer.infrastracture.repository

import com.presentation.intellij.tips.offer.infrastracture.api.Offer

interface OffersRepository {

    fun getOffers(): List<Offer>

    fun addOffer(accountId: String, offer: Offer)
}