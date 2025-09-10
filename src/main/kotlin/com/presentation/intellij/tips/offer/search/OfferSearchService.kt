package com.presentation.intellij.tips.offer.search

import com.presentation.intellij.tips.offer.Offer
import org.springframework.stereotype.Service

@Service
class OfferSearchService {

    fun searchOffers(offers: List<Offer>, criteria: OfferSearchCriteria): SearchResult {
        var filteredOffers = offers

        // Apply filters
        filteredOffers = applyFilters(filteredOffers, criteria)

        val totalCount = filteredOffers.size.toLong()

        // Apply sorting
        filteredOffers = applySorting(filteredOffers, criteria.sortBy)

        // Apply pagination
        val paginatedOffers = applyPagination(filteredOffers, criteria.limit, criteria.offset)

        return SearchResult(
            offers = paginatedOffers,
            totalCount = totalCount,
            hasNext = criteria.offset + criteria.limit < totalCount,
            hasPrevious = criteria.offset > 0
        )
    }

    private fun applyFilters(offers: List<Offer>, criteria: OfferSearchCriteria): List<Offer> {
        return offers.filter { offer ->
            // Query filter (searches in title, description, and tags)
            val queryMatch = criteria.query?.let { query ->
                val searchTerm = query.lowercase()
                offer.title.lowercase().contains(searchTerm) ||
                offer.description.lowercase().contains(searchTerm) ||
                offer.tags.any { tag -> tag.lowercase().contains(searchTerm) }
            } ?: true

            // Price range filter
            val priceMatch = (criteria.minPrice?.let { offer.price >= it } ?: true) &&
                           (criteria.maxPrice?.let { offer.price <= it } ?: true)

            // Category filter
            val categoryMatch = criteria.category?.let { offer.category == it } ?: true

            // Status filter
            val statusMatch = criteria.status?.let { offer.status == it } ?: true

            // Seller filter
            val sellerMatch = criteria.sellerId?.let { offer.sellerId == it } ?: true

            // Tags filter (any of the provided tags must match)
            val tagsMatch = criteria.tags?.let { searchTags ->
                searchTags.any { searchTag ->
                    offer.tags.any { offerTag ->
                        offerTag.lowercase().contains(searchTag.lowercase())
                    }
                }
            } ?: true

            queryMatch && priceMatch && categoryMatch && statusMatch && sellerMatch && tagsMatch
        }
    }

    private fun applySorting(offers: List<Offer>, sortBy: SortBy): List<Offer> {
        return when (sortBy) {
            SortBy.PRICE_ASC -> offers.sortedBy { it.price }
            SortBy.PRICE_DESC -> offers.sortedByDescending { it.price }
            SortBy.CREATED_ASC -> offers.sortedBy { it.createdAt }
            SortBy.CREATED_DESC -> offers.sortedByDescending { it.createdAt }
            SortBy.TITLE_ASC -> offers.sortedBy { it.title.lowercase() }
            SortBy.TITLE_DESC -> offers.sortedByDescending { it.title.lowercase() }
            SortBy.POPULARITY -> offers.sortedByDescending { it.viewCount }
        }
    }

    private fun applyPagination(offers: List<Offer>, limit: Int, offset: Int): List<Offer> {
        return offers.drop(offset).take(limit)
    }
}
