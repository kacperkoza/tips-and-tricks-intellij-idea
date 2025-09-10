package com.presentation.intellij.tips.offer.infrastracture.api.dto

import com.presentation.intellij.tips.offer.validation.ValidationError
import com.presentation.intellij.tips.offer.validation.ValidationResult
import com.presentation.intellij.tips.offer.validation.ValidationWarning

data class ValidationResultDto(
    val isValid: Boolean,
    val errors: List<ValidationErrorDto>,
    val warnings: List<ValidationWarningDto> = emptyList()
) {
    fun getErrorMessages(): List<String> = errors.map { it.message }
    fun getWarningMessages(): List<String> = warnings.map { it.message }
}

enum class ValidationErrorDto(val message: String) {
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

enum class ValidationWarningDto(val message: String) {
    HIGH_PRICE_WARNING("This is a high-priced item that may require additional verification"),
    SHORT_DESCRIPTION_WARNING("Consider adding more details to improve buyer confidence"),
    NO_TAGS_WARNING("Adding relevant tags will help buyers find your offer"),
    EXPIRES_SOON_WARNING("This offer expires within 7 days")
}

// Extension functions for mapping
fun ValidationResult.toDto(): ValidationResultDto = ValidationResultDto(
    isValid = this.isValid,
    errors = this.errors.map { it.toDto() },
    warnings = this.warnings.map { it.toDto() }
)

fun ValidationError.toDto(): ValidationErrorDto = when (this) {
    ValidationError.TITLE_EMPTY -> ValidationErrorDto.TITLE_EMPTY
    ValidationError.TITLE_TOO_SHORT -> ValidationErrorDto.TITLE_TOO_SHORT
    ValidationError.TITLE_TOO_LONG -> ValidationErrorDto.TITLE_TOO_LONG
    ValidationError.TITLE_CONTAINS_PROFANITY -> ValidationErrorDto.TITLE_CONTAINS_PROFANITY
    ValidationError.DESCRIPTION_EMPTY -> ValidationErrorDto.DESCRIPTION_EMPTY
    ValidationError.DESCRIPTION_TOO_SHORT -> ValidationErrorDto.DESCRIPTION_TOO_SHORT
    ValidationError.DESCRIPTION_TOO_LONG -> ValidationErrorDto.DESCRIPTION_TOO_LONG
    ValidationError.DESCRIPTION_CONTAINS_PROFANITY -> ValidationErrorDto.DESCRIPTION_CONTAINS_PROFANITY
    ValidationError.PRICE_INVALID -> ValidationErrorDto.PRICE_INVALID
    ValidationError.PRICE_TOO_HIGH -> ValidationErrorDto.PRICE_TOO_HIGH
    ValidationError.PRICE_TOO_LOW -> ValidationErrorDto.PRICE_TOO_LOW
    ValidationError.SELLER_ID_EMPTY -> ValidationErrorDto.SELLER_ID_EMPTY
    ValidationError.EXPIRATION_TOO_SOON -> ValidationErrorDto.EXPIRATION_TOO_SOON
    ValidationError.EXPIRATION_TOO_FAR -> ValidationErrorDto.EXPIRATION_TOO_FAR
    ValidationError.ACTIVE_OFFER_NEEDS_EXPIRATION -> ValidationErrorDto.ACTIVE_OFFER_NEEDS_EXPIRATION
    ValidationError.EXPIRED_OFFER_FUTURE_EXPIRATION -> ValidationErrorDto.EXPIRED_OFFER_FUTURE_EXPIRATION
    ValidationError.CANNOT_ACTIVATE_EXPIRED_OFFER -> ValidationErrorDto.CANNOT_ACTIVATE_EXPIRED_OFFER
    ValidationError.TOO_MANY_TAGS -> ValidationErrorDto.TOO_MANY_TAGS
    ValidationError.TAG_TOO_LONG -> ValidationErrorDto.TAG_TOO_LONG
    ValidationError.ELECTRONICS_PRICE_SUSPICIOUS -> ValidationErrorDto.ELECTRONICS_PRICE_SUSPICIOUS
    ValidationError.ELECTRONICS_MISSING_REQUIRED_TAGS -> ValidationErrorDto.ELECTRONICS_MISSING_REQUIRED_TAGS
    ValidationError.AUTOMOTIVE_PRICE_SUSPICIOUS -> ValidationErrorDto.AUTOMOTIVE_PRICE_SUSPICIOUS
    ValidationError.AUTOMOTIVE_MISSING_REQUIRED_TAGS -> ValidationErrorDto.AUTOMOTIVE_MISSING_REQUIRED_TAGS
    ValidationError.FASHION_MISSING_REQUIRED_TAGS -> ValidationErrorDto.FASHION_MISSING_REQUIRED_TAGS
    ValidationError.BOOKS_PRICE_SUSPICIOUS -> ValidationErrorDto.BOOKS_PRICE_SUSPICIOUS
}

fun ValidationWarning.toDto(): ValidationWarningDto = when (this) {
    ValidationWarning.HIGH_PRICE_WARNING -> ValidationWarningDto.HIGH_PRICE_WARNING
    ValidationWarning.SHORT_DESCRIPTION_WARNING -> ValidationWarningDto.SHORT_DESCRIPTION_WARNING
    ValidationWarning.NO_TAGS_WARNING -> ValidationWarningDto.NO_TAGS_WARNING
    ValidationWarning.EXPIRES_SOON_WARNING -> ValidationWarningDto.EXPIRES_SOON_WARNING
}
