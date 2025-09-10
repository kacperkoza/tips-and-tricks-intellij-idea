package com.presentation.intellij.tips.offer.infrastracture.api

import com.presentation.intellij.tips.offer.*
import com.presentation.intellij.tips.offer.search.SearchResult
import com.presentation.intellij.tips.offer.search.SortBy
import com.presentation.intellij.tips.offer.validation.ValidationResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/offers")
class OffersEndpointV2(
    private val offersService: OffersService
) {

    @GetMapping
    fun getOffers(
        @RequestParam("limit", defaultValue = "20") limit: Int,
        @RequestParam("offset", defaultValue = "0") offset: Int
    ): Offers {
        val offers = offersService.getOffers(limit, offset)
        return Offers(offers)
    }

    @GetMapping("/search")
    fun searchOffers(
        @RequestParam("query", required = false) query: String?,
        @RequestParam("minPrice", required = false) minPrice: BigDecimal?,
        @RequestParam("maxPrice", required = false) maxPrice: BigDecimal?,
        @RequestParam("category", required = false) category: OfferCategory?,
        @RequestParam("status", required = false) status: OfferStatus?,
        @RequestParam("sortBy", defaultValue = "CREATED_DESC") sortBy: SortBy,
        @RequestParam("limit", defaultValue = "20") limit: Int,
        @RequestParam("offset", defaultValue = "0") offset: Int
    ): SearchResult {
        return offersService.searchOffers(
            query = query,
            minPrice = minPrice,
            maxPrice = maxPrice,
            category = category,
            status = status,
            sortBy = sortBy,
            limit = limit,
            offset = offset
        )
    }

    @GetMapping("/category/{category}")
    fun getOffersByCategory(
        @PathVariable category: OfferCategory,
        @RequestParam("limit", defaultValue = "20") limit: Int,
        @RequestParam("offset", defaultValue = "0") offset: Int
    ): ResponseEntity<List<Offer>> {
        val offers = offersService.getOffersByCategory(category, limit, offset)
        return ResponseEntity.ok(offers)
    }

    @GetMapping("/active")
    fun getActiveOffers(
        @RequestParam("limit", defaultValue = "20") limit: Int,
        @RequestParam("offset", defaultValue = "0") offset: Int
    ): ResponseEntity<List<Offer>> {
        val offers = offersService.getActiveOffers(limit, offset)
        return ResponseEntity.ok(offers)
    }

    @GetMapping("/price-range")
    fun getOffersByPriceRange(
        @RequestParam minPrice: BigDecimal,
        @RequestParam maxPrice: BigDecimal,
        @RequestParam("limit", defaultValue = "20") limit: Int,
        @RequestParam("offset", defaultValue = "0") offset: Int
    ): ResponseEntity<List<Offer>> {
        val offers = offersService.getOffersByPriceRange(minPrice, maxPrice, limit, offset)
        return ResponseEntity.ok(offers)
    }

    @PostMapping("/users/{userId}")
    fun addOffer(
        @PathVariable userId: String,
        @RequestBody offer: Offer
    ): ResponseEntity<ValidationResult> {
        return try {
            val validationResult = offersService.add(offer, userId, generateRequestId())
            if (validationResult.isValid) {
                ResponseEntity.status(HttpStatus.CREATED).body(validationResult)
            } else {
                ResponseEntity.badRequest().body(validationResult)
            }
        } catch (ex: IncorrectAccountStatusException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ValidationResult(false, emptyList(), emptyList()))
        }
    }

    @PutMapping("/{offerId}/status")
    fun updateOfferStatus(
        @PathVariable offerId: Long,
        @RequestParam newStatus: OfferStatus,
        @RequestParam accountId: String
    ): ResponseEntity<Offer> {
        return try {
            val updatedOffer = offersService.updateOfferStatus(offerId, newStatus, accountId)
            ResponseEntity.ok(updatedOffer)
        } catch (ex: OfferNotFoundException) {
            ResponseEntity.notFound().build()
        } catch (ex: UnauthorizedOfferModificationException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } catch (ex: OfferValidationException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/{offerId}/activate")
    fun activateOffer(
        @PathVariable offerId: Long,
        @RequestParam accountId: String
    ): ResponseEntity<Offer> {
        return updateOfferStatus(offerId, OfferStatus.ACTIVE, accountId)
    }

    @PostMapping("/{offerId}/suspend")
    fun suspendOffer(
        @PathVariable offerId: Long,
        @RequestParam accountId: String
    ): ResponseEntity<Offer> {
        return updateOfferStatus(offerId, OfferStatus.SUSPENDED, accountId)
    }

    @PostMapping("/{offerId}/expire")
    fun expireOffer(
        @PathVariable offerId: Long,
        @RequestParam accountId: String
    ): ResponseEntity<Offer> {
        return updateOfferStatus(offerId, OfferStatus.EXPIRED, accountId)
    }

    private fun generateRequestId(): String = java.util.UUID.randomUUID().toString()
}
