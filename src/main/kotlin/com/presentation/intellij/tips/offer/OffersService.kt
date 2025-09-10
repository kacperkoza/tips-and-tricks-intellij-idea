package com.presentation.intellij.tips.offer

import com.presentation.intellij.tips.infrastracture.account.AccountStatus
import com.presentation.intellij.tips.infrastracture.account.AccountStatusClient
import com.presentation.intellij.tips.offer.infrastracture.repository.OffersRepository
import org.springframework.stereotype.Component

@Component
class OffersService(
    private val offersRepository: OffersRepository,
    private val accountStatusClient: AccountStatusClient
) {

    fun getOffers(limit: Int?, offset: Int?): List<Offer> {
        validate(limit, offset)
        val offers = offersRepository.getOffers()
        return getOffersPaginated(limit, offset, offers)
    }

    fun add(offer: Offer, accountId: String, requestId: String) {
        if (listOf(AccountStatus.TO_ACTIVATE, AccountStatus.BLOCKED).contains(
                accountStatusClient.getAccountStatus(
                    accountId
                )
            )
        ) {
            throw IncorrectAccountStatusException()
        }
        offersRepository.addOffer(accountId, offer)
    }

    private fun validate(limit: Int?, offset: Int?) {
        if ((limit != null && limit <= 0) || (offset != null && offset <= 0)) {
            throw InvalidPaginationExceptionTwo()
        }
    }

    private fun getOffersPaginated(limit: Int?, offset: Int?, offers: List<Offer>): List<Offer> {
        var offersPaginated: List<Offer> = emptyList()
        if (offset != null && limit != null) {
            offersPaginated = offers.drop(offset).take(limit)
        } else if (offset != null) {
            offers.drop(offset)
        } else if (limit != null) {
            offers.take(limit)
        }
        return offersPaginated
    }

}

class InvalidPaginationExceptionTwo : RuntimeException()
