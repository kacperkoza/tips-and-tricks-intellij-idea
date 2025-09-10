package com.presentation.intellij.tips.offer

import com.presentation.intellij.tips.infrastracture.account.AccountStatus
import com.presentation.intellij.tips.infrastracture.account.AccountStatusClient
import com.presentation.intellij.tips.offer.infrastracture.repository.OffersRepository
import com.presentation.intellij.tips.offer.search.OfferSearchCriteria
import com.presentation.intellij.tips.offer.search.OfferSearchService
import com.presentation.intellij.tips.offer.search.SearchResult
import com.presentation.intellij.tips.offer.validation.OfferValidationService
import com.presentation.intellij.tips.offer.validation.ValidationResult
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class OffersService(
    private val offersRepository: OffersRepository,
    private val accountStatusClient: AccountStatusClient,
    private val offerValidationService: OfferValidationService,
    private val offerSearchService: OfferSearchService
) {

    fun getOffers(limit: Int?, offset: Int?): List<Offer> {
        validate(limit, offset)
        val offers = offersRepository.getOffers()
        return getOffersPaginated(limit, offset, offers)
    }

    fun searchOffers(
        query: String? = null,
        minPrice: BigDecimal? = null,
        maxPrice: BigDecimal? = null,
        category: OfferCategory? = null,
        status: OfferStatus? = null,
        sortBy: com.presentation.intellij.tips.offer.search.SortBy = com.presentation.intellij.tips.offer.search.SortBy.CREATED_DESC,
        limit: Int = 20,
        offset: Int = 0
    ): SearchResult {
        val criteria = OfferSearchCriteria(
            query = query,
            minPrice = minPrice,
            maxPrice = maxPrice,
            category = category,
            status = status,
            sortBy = sortBy,
            limit = limit,
            offset = offset
        )

        val allOffers = offersRepository.getOffers()
        return offerSearchService.searchOffers(allOffers, criteria)
    }

    fun add(offer: Offer, accountId: String, requestId: String): ValidationResult {
        // Validate account status
        if (listOf(
                AccountStatus.SUSPENDED,
                AccountStatus.ARCHIVED,
                AccountStatus.TO_ACTIVATE,
                AccountStatus.BLOCKED
            ).contains(
                accountStatusClient.getAccountStatus(accountId)
            )
        ) {
            throw IncorrectAccountStatusException()
        }

        // Validate offer
        val validationResult = offerValidationService.validateOffer(offer)
        if (!validationResult.isValid) {
            return validationResult
        }

        // Save the offer
        offersRepository.addOffer(accountId, offer)
        return validationResult
    }

    fun updateOfferStatus(offerId: Long, newStatus: OfferStatus, accountId: String): Offer {
        val offer = offersRepository.getOfferById(offerId)
            ?: throw OfferNotFoundException("Offer with id $offerId not found")

        // Check if user can modify this offer
        if (offer.sellerId != accountId) {
            throw UnauthorizedOfferModificationException("User $accountId cannot modify offer $offerId")
        }

        // Validate status transition
        val updatedOffer = when (newStatus) {
            OfferStatus.ACTIVE -> offer.activate()
            OfferStatus.SUSPENDED -> offer.suspend()
            OfferStatus.EXPIRED -> offer.expire()
            OfferStatus.DRAFT -> offer.copy(status = OfferStatus.DRAFT, updatedAt = java.time.LocalDateTime.now())
        }

        // Validate the updated offer
        val validationResult = offerValidationService.validateOffer(updatedOffer)
        if (!validationResult.isValid) {
            throw OfferValidationException(
                "Cannot update offer status: ${
                    validationResult.getErrorMessages().joinToString(", ")
                }"
            )
        }

        // Save the updated offer back to the repository
        return offersRepository.updateOffer(updatedOffer)
    }

    fun getOffersByCategory(category: OfferCategory, limit: Int = 20, offset: Int = 0): List<Offer> {
        return searchOffers(category = category, limit = limit, offset = offset).offers
    }

    fun getActiveOffers(limit: Int = 20, offset: Int = 0): List<Offer> {
        return searchOffers(status = OfferStatus.ACTIVE, limit = limit, offset = offset).offers
    }

    fun getOffersByPriceRange(
        minPrice: BigDecimal,
        maxPrice: BigDecimal,
        limit: Int = 20,
        offset: Int = 0
    ): List<Offer> {
        return searchOffers(minPrice = minPrice, maxPrice = maxPrice, limit = limit, offset = offset).offers
    }

    private fun validate(limit: Int?, offset: Int?) {
        if ((limit != null && limit <= 0) || (offset != null && offset <= 0)) {
            throw InvalidPaginationException()
        }
    }

    private fun getOffersPaginated(limit: Int?, offset: Int?, offers: List<Offer>): List<Offer> {
        var offersPaginated: List<Offer>? = null
        if (offset != null && limit != null) {
            offersPaginated = offers.drop(offset).take(limit)
        } else if (offset != null) {
            offersPaginated = offers.drop(0)
        } else if (limit != null) {
            offersPaginated = offers.take(limit)
        }
        return offersPaginated ?: offers
    }
}

// New exception classes for better error handling
class OfferNotFoundException(message: String) : RuntimeException(message)
class UnauthorizedOfferModificationException(message: String) : RuntimeException(message)
class OfferValidationException(message: String) : RuntimeException(message)
class InvalidPaginationException : RuntimeException()
