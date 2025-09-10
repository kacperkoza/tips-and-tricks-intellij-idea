package com.presentation.intellij.tips.offer.validation

import com.presentation.intellij.tips.offer.Offer
import com.presentation.intellij.tips.offer.OfferCategory
import com.presentation.intellij.tips.offer.OfferStatus
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class OfferValidationService {

    fun validateOffer(offer: Offer): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Basic field validation
        validateBasicFields(offer, errors)

        // Business rules validation
        validateBusinessRules(offer, errors)

        // Category-specific validation
        validateCategorySpecificRules(offer, errors)

        // Status transition validation
        validateStatusTransitions(offer, errors)

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = generateWarnings(offer)
        )
    }

    private fun validateBasicFields(offer: Offer, errors: MutableList<ValidationError>) {
        // Title validation
        when {
            offer.title.isBlank() -> errors.add(ValidationError.TITLE_EMPTY)
            offer.title.length < 5 -> errors.add(ValidationError.TITLE_TOO_SHORT)
            offer.title.length > 100 -> errors.add(ValidationError.TITLE_TOO_LONG)
            containsProfanity(offer.title) -> errors.add(ValidationError.TITLE_CONTAINS_PROFANITY)
        }

        // Description validation
        when {
            offer.description.isBlank() -> errors.add(ValidationError.DESCRIPTION_EMPTY)
            offer.description.length < 20 -> errors.add(ValidationError.DESCRIPTION_TOO_SHORT)
            offer.description.length > 2000 -> errors.add(ValidationError.DESCRIPTION_TOO_LONG)
            containsProfanity(offer.description) -> errors.add(ValidationError.DESCRIPTION_CONTAINS_PROFANITY)
        }

        // Price validation
        when {
            offer.price <= BigDecimal.ZERO -> errors.add(ValidationError.PRICE_INVALID)
            offer.price > BigDecimal("999999.99") -> errors.add(ValidationError.PRICE_TOO_HIGH)
            offer.price < BigDecimal("0.01") -> errors.add(ValidationError.PRICE_TOO_LOW)
        }

        // Seller validation
        if (offer.sellerId.isBlank()) {
            errors.add(ValidationError.SELLER_ID_EMPTY)
        }
    }

    private fun validateBusinessRules(offer: Offer, errors: MutableList<ValidationError>) {
        // Expiration date validation
        offer.expiresAt?.let { expiresAt ->
            if (expiresAt.isBefore(LocalDateTime.now().plusHours(1))) {
                errors.add(ValidationError.EXPIRATION_TOO_SOON)
            }
            if (expiresAt.isAfter(LocalDateTime.now().plusYears(1))) {
                errors.add(ValidationError.EXPIRATION_TOO_FAR)
            }
        }

        // Status-specific validations
        when (offer.status) {
            OfferStatus.ACTIVE -> {
                if (offer.expiresAt == null) {
                    errors.add(ValidationError.ACTIVE_OFFER_NEEDS_EXPIRATION)
                }
            }

            OfferStatus.EXPIRED -> {
                if (offer.expiresAt?.isAfter(LocalDateTime.now()) == true) {
                    errors.add(ValidationError.EXPIRED_OFFER_FUTURE_EXPIRATION)
                }
            }

            else -> { /* No specific validation for DRAFT and SUSPENDED */
            }
        }

        // Tags validation
        if (offer.tags.size > 10) {
            errors.add(ValidationError.TOO_MANY_TAGS)
        }

        offer.tags.forEach { tag ->
            if (tag.length > 50) {
                errors.add(ValidationError.TAG_TOO_LONG)
            }
        }
    }

    private fun validateCategorySpecificRules(offer: Offer, errors: MutableList<ValidationError>) {
        when (offer.category) {
            OfferCategory.ELECTRONICS -> {
                if (offer.price < BigDecimal("10")) {
                    errors.add(ValidationError.ELECTRONICS_PRICE_SUSPICIOUS)
                }
                if (offer.tags.none { it.lowercase() in listOf("warranty", "brand", "model", "condition") }) {
                    errors.add(ValidationError.ELECTRONICS_MISSING_REQUIRED_TAGS)
                }
            }

            OfferCategory.AUTOMOTIVE -> {
                if (offer.price < BigDecimal("500") && !offer.tags.contains("parts")) {
                    errors.add(ValidationError.AUTOMOTIVE_PRICE_SUSPICIOUS)
                }
                if (offer.tags.none { it.lowercase() in listOf("make", "model", "year", "mileage") }) {
                    errors.add(ValidationError.AUTOMOTIVE_MISSING_REQUIRED_TAGS)
                }
            }

            OfferCategory.FASHION -> {
                if (offer.tags.none { it.lowercase() in listOf("size", "condition", "brand") }) {
                    errors.add(ValidationError.FASHION_MISSING_REQUIRED_TAGS)
                }
            }

            OfferCategory.BOOKS -> {
                if (offer.price > BigDecimal("1000")) {
                    errors.add(ValidationError.BOOKS_PRICE_SUSPICIOUS)
                }
            }

            else -> { /* No specific validation for other categories */
            }
        }
    }

    private fun validateStatusTransitions(offer: Offer, errors: MutableList<ValidationError>) {
        // This would typically check against the previous status in a real application
        // For now, we'll validate that certain statuses have required conditions

        if (offer.status == OfferStatus.ACTIVE && offer.isExpired()) {
            errors.add(ValidationError.CANNOT_ACTIVATE_EXPIRED_OFFER)
        }
    }

    private fun generateWarnings(offer: Offer): List<ValidationWarning> {
        val warnings = mutableListOf<ValidationWarning>()

        // Price warnings
        if (offer.price > BigDecimal("10000")) {
            warnings.add(ValidationWarning.HIGH_PRICE_WARNING)
        }

        // Description warnings
        if (offer.description.length < 50) {
            warnings.add(ValidationWarning.SHORT_DESCRIPTION_WARNING)
        }

        // Tags warnings
        if (offer.tags.isEmpty()) {
            warnings.add(ValidationWarning.NO_TAGS_WARNING)
        }

        // Expiration warnings
        offer.expiresAt?.let { expiresAt ->
            if (expiresAt.isBefore(LocalDateTime.now().plusDays(7))) {
                warnings.add(ValidationWarning.EXPIRES_SOON_WARNING)
            }
        }

        return warnings
    }

    private fun containsProfanity(text: String): Boolean {
        val profanityList = listOf("spam", "fake", "scam", "fraud", "cheat")
        return profanityList.any { text.lowercase().contains(it) }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError>,
    val warnings: List<ValidationWarning> = emptyList()
) {
    fun getErrorMessages(): List<String> = errors.map { it.message }
    fun getWarningMessages(): List<String> = warnings.map { it.message }
}

enum class ValidationError(val message: String) {
    // Title errors
    TITLE_EMPTY("Title cannot be empty"),
    TITLE_TOO_SHORT("Title must be at least 5 characters long"),
    TITLE_TOO_LONG("Title cannot exceed 100 characters"),
    TITLE_CONTAINS_PROFANITY("Title contains inappropriate content"),

    // Description errors
    DESCRIPTION_EMPTY("Description cannot be empty"),
    DESCRIPTION_TOO_SHORT("Description must be at least 20 characters long"),
    DESCRIPTION_TOO_LONG("Description cannot exceed 2000 characters"),
    DESCRIPTION_CONTAINS_PROFANITY("Description contains inappropriate content"),

    // Price errors
    PRICE_INVALID("Price must be greater than zero"),
    PRICE_TOO_HIGH("Price cannot exceed 999,999.99"),
    PRICE_TOO_LOW("Price must be at least 0.01"),

    // Seller errors
    SELLER_ID_EMPTY("Seller ID cannot be empty"),

    // Business rule errors
    EXPIRATION_TOO_SOON("Expiration date must be at least 1 hour in the future"),
    EXPIRATION_TOO_FAR("Expiration date cannot be more than 1 year in the future"),
    ACTIVE_OFFER_NEEDS_EXPIRATION("Active offers must have an expiration date"),
    EXPIRED_OFFER_FUTURE_EXPIRATION("Expired offers cannot have future expiration dates"),
    CANNOT_ACTIVATE_EXPIRED_OFFER("Cannot activate an expired offer"),

    // Tags errors
    TOO_MANY_TAGS("Cannot have more than 10 tags"),
    TAG_TOO_LONG("Tags cannot exceed 50 characters"),

    // Category-specific errors
    ELECTRONICS_PRICE_SUSPICIOUS("Electronics items under $10 require manual review"),
    ELECTRONICS_MISSING_REQUIRED_TAGS("Electronics offers should include warranty, brand, model, or condition tags"),
    AUTOMOTIVE_PRICE_SUSPICIOUS("Automotive items under $500 should be marked as parts"),
    AUTOMOTIVE_MISSING_REQUIRED_TAGS("Automotive offers should include make, model, year, or mileage tags"),
    FASHION_MISSING_REQUIRED_TAGS("Fashion offers should include size, condition, or brand tags"),
    BOOKS_PRICE_SUSPICIOUS("Books over $1000 require manual review")
}

enum class ValidationWarning(val message: String) {
    HIGH_PRICE_WARNING("This is a high-priced item that may require additional verification"),
    SHORT_DESCRIPTION_WARNING("Consider adding more details to improve buyer confidence"),
    NO_TAGS_WARNING("Adding relevant tags will help buyers find your offer"),
    EXPIRES_SOON_WARNING("This offer expires within 7 days")
}
