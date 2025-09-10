package com.presentation.intellij.tips.offer.search

import com.presentation.intellij.tips.offer.Offer
import com.presentation.intellij.tips.offer.OfferCategory
import com.presentation.intellij.tips.offer.OfferStatus
import java.math.BigDecimal

data class OfferSearchCriteria(
    val query: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val category: OfferCategory? = null,
    val status: OfferStatus? = null,
    val sellerId: String? = null,
    val tags: Set<String>? = null,
    val sortBy: SortBy = SortBy.CREATED_DESC,
    val limit: Int = 20,
    val offset: Int = 0
)

enum class SortBy {
    PRICE_ASC,
    PRICE_DESC,
    CREATED_ASC,
    CREATED_DESC,
    TITLE_ASC,
    TITLE_DESC,
    POPULARITY // Based on view count
}

data class SearchResult(
    val offers: List<Offer>,
    val totalCount: Long,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
