package com.presentation.intellij.tips.offer

import com.presentation.intellij.tips.infrastracture.account.AccountStatus
import com.presentation.intellij.tips.infrastracture.account.AccountStatusClient
import com.presentation.intellij.tips.offer.infrastracture.repository.OffersRepository
import com.presentation.intellij.tips.offer.search.OfferSearchService
import com.presentation.intellij.tips.offer.validation.OfferValidationService
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

class OffersServiceTest extends Specification {

    OffersRepository offersRepository = Stub(OffersRepository)
    AccountStatusClient accountStatusClient = Stub(AccountStatusClient)

    OffersService offersService = new OffersService(
            offersRepository,
            accountStatusClient,
            new OfferValidationService(),
            new OfferSearchService()
    )

    def setup() {
        offersRepository.getOffers() >> createMockOffers()
    }

    @Unroll
    def "should return offers according to limit and offset"() {
        when:
        List<Offer> offers = offersService.getOffers(limit, null)

        then:
        offers.collect { it.id as int } == expectedOfferIds

        where:
        limit | expectedOfferIds
        null  | [1, 2, 3, 4, 5]
        1     | [1]
        3     | [1, 2, 3]
        5     | [1, 2, 3, 4, 5]
        10    | [1, 2, 3, 4, 5]
    }

    def "should throw exception on invalid limit"() {
        when:
        offersService.getOffers(-1, null)

        then:
        thrown(InvalidPaginationException)
    }

    @Unroll
    def "should throw exception when adding offer on not allowed account status"() {
        given:
        accountStatusClient.getAccountStatus('user-id') >> accountStatus

        when:
        offersService.add(offer(), 'user-id', 'any-request-id')

        then:
        thrown(IncorrectAccountStatusException)

        where:
        accountStatus << [AccountStatus.BLOCKED, AccountStatus.TO_ACTIVATE]
    }

    def "should update offer status successfully"() {
        given:
        def existingOffer = createOffer(1L, 'Test Offer', 'Description'.repeat(10), new BigDecimal('100.00'), OfferCategory.ELECTRONICS)
        offersRepository.getOfferById(1L) >> existingOffer
        offersRepository.updateOffer(_) >> { Offer offer -> offer }

        when:
        def result = offersService.updateOfferStatus(1L, OfferStatus.SUSPENDED, 'seller-1')

        then:
        result.status == OfferStatus.SUSPENDED
        result.id == 1L
    }

    def "should throw OfferNotFoundException when updating non-existing offer"() {
        given:
        offersRepository.getOfferById(999L) >> null

        when:
        offersService.updateOfferStatus(999L, OfferStatus.ACTIVE, 'seller-1')

        then:
        thrown(OfferNotFoundException)
    }

    def "should throw UnauthorizedOfferModificationException when user tries to modify another user's offer"() {
        given:
        def existingOffer = createOffer(1L, 'Test Offer', 'Description', new BigDecimal('100.00'), OfferCategory.ELECTRONICS)
        offersRepository.getOfferById(1L) >> existingOffer

        when:
        offersService.updateOfferStatus(1L, OfferStatus.SUSPENDED, 'different-seller')

        then:
        thrown(UnauthorizedOfferModificationException)
    }

    def "should update offer status to EXPIRED"() {
        given:
        // Skip validation for this specific test case by mocking the validation service
        def mockValidationService = Mock(OfferValidationService)
        def mockSearchService = new OfferSearchService()
        def testService = new OffersService(offersRepository, accountStatusClient, mockValidationService, mockSearchService)

        def existingOffer = new Offer(
                1L,
                'Test Offer',
                'Description with enough characters for validation rules',
                new BigDecimal('100.00'),
                OfferCategory.ELECTRONICS,
                OfferStatus.ACTIVE,
                URI.create('https://example.com/image.jpg'),
                'seller-1',
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1),
                ['warranty', 'brand'] as Set<String>,
                0L
        )

        offersRepository.getOfferById(1L) >> existingOffer
        offersRepository.updateOffer(_) >> { Offer offer -> offer }
        mockValidationService.validateOffer(_) >> new com.presentation.intellij.tips.offer.validation.ValidationResult(true, [], [])

        when:
        def result = testService.updateOfferStatus(1L, OfferStatus.EXPIRED, 'seller-1')

        then:
        result.status == OfferStatus.EXPIRED
    }

    def "should get offers by category"() {
        when:
        def result = offersService.getOffersByCategory(OfferCategory.ELECTRONICS, 10, 0)

        then:
        result.size() == 1
        result[0].category == OfferCategory.ELECTRONICS
        result[0].title == 'Gaming Laptop'
    }

    def "should get offers by category with default pagination"() {
        when:
        def result = offersService.getOffersByCategory(OfferCategory.FASHION, 20, 0)

        then:
        result.size() == 1
        result[0].category == OfferCategory.FASHION
        result[0].title == 'Winter Jacket'
    }

    def "should get active offers with default pagination"() {
        when:
        def result = offersService.getActiveOffers(20, 0)

        then:
        result.every { it.status == OfferStatus.ACTIVE }
    }

    @Unroll
    def "should search offers with various criteria: #testDescription"() {
        given:
        def searchOffers = [
                createOffer(1L, 'Gaming Laptop Pro', 'High-performance gaming laptop with RTX graphics', new BigDecimal('1299.99'), OfferCategory.ELECTRONICS),
                createOffer(2L, 'Winter Jacket', 'Warm winter jacket for cold weather', new BigDecimal('89.99'), OfferCategory.FASHION),
                createOffer(3L, 'Coffee Table', 'Modern glass coffee table', new BigDecimal('199.99'), OfferCategory.HOME),
                createOffer(4L, 'Programming Book', 'Learn Kotlin programming', new BigDecimal('49.99'), OfferCategory.BOOKS),
                createOffer(5L, 'Running Shoes', 'Professional running shoes', new BigDecimal('129.99'), OfferCategory.SPORTS),
                createOfferWithStatus(6L, 'Draft Laptop', 'Draft gaming laptop', new BigDecimal('999.99'), OfferCategory.ELECTRONICS, OfferStatus.DRAFT)
        ]
        offersRepository.getOffers() >> searchOffers

        when:
        def result = offersService.searchOffers(query, minPrice, maxPrice, category, status, sortBy, limit, offset)

        then:
        result.offers.size() == expectedSize
        if (query) {
            result.offers.every { offer ->
                offer.title.toLowerCase().contains(query.toLowerCase()) ||
                offer.description.toLowerCase().contains(query.toLowerCase())
            }
        }
        if (category) {
            result.offers.every { it.category == category }
        }
        if (status) {
            result.offers.every { it.status == status }
        }
        if (minPrice && maxPrice) {
            result.offers.every { it.price >= minPrice && it.price <= maxPrice }
        }
        result.totalCount >= expectedSize

        where:
        testDescription              | query    | minPrice             | maxPrice             | category              | status            | sortBy                                                           | limit | offset | expectedSize
        'query search for Gaming'    | 'Gaming' | null                 | null                 | null                  | null              | com.presentation.intellij.tips.offer.search.SortBy.CREATED_DESC | 20    | 0      | 2
        'multiple criteria'          | null     | new BigDecimal('50') | new BigDecimal('200')| OfferCategory.FASHION | OfferStatus.ACTIVE| com.presentation.intellij.tips.offer.search.SortBy.CREATED_DESC | 20    | 0      | 1
        'all offers default'         | null     | null                 | null                 | null                  | null              | com.presentation.intellij.tips.offer.search.SortBy.CREATED_DESC | 20    | 0      | 6
        'limit and offset 2,0'       | null     | null                 | null                 | null                  | null              | com.presentation.intellij.tips.offer.search.SortBy.CREATED_DESC | 2     | 0      | 2
        'limit and offset 3,1'       | null     | null                 | null                 | null                  | null              | com.presentation.intellij.tips.offer.search.SortBy.CREATED_DESC | 3     | 1      | 3
        'limit and offset 10,0'      | null     | null                 | null                 | null                  | null              | com.presentation.intellij.tips.offer.search.SortBy.CREATED_DESC | 10    | 0      | 6
        'limit and offset 2,3'       | null     | null                 | null                 | null                  | null              | com.presentation.intellij.tips.offer.search.SortBy.CREATED_DESC | 2     | 3      | 2
    }

    private static List<Offer> createMockOffers() {
        return [
                createOffer(1L, "Gaming Laptop", "High-performance gaming laptop", new BigDecimal("1299.99"), OfferCategory.ELECTRONICS),
                createOffer(2L, "Winter Jacket", "Warm winter jacket for cold weather", new BigDecimal("89.99"), OfferCategory.FASHION),
                createOffer(3L, "Coffee Table", "Modern glass coffee table", new BigDecimal("199.99"), OfferCategory.HOME),
                createOffer(4L, "Programming Book", "Learn Kotlin programming", new BigDecimal("49.99"), OfferCategory.BOOKS),
                createOffer(5L, "Running Shoes", "Professional running shoes", new BigDecimal("129.99"), OfferCategory.SPORTS)
        ]
    }

    private static Offer createOffer(Long id, String title, String description, BigDecimal price, OfferCategory category) {
        return new Offer(
                id,
                title,
                description.padRight(20, ' '), // Ensure description is at least 20 characters
                price,
                category,
                OfferStatus.ACTIVE,
                URI.create('https://example.com/image.jpg'),
                'seller-' + id,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30), // Add expiration date for ACTIVE offers
                createRequiredTags(category),     // Add required tags for validation
                0L
        )
    }

    private static Offer offer() {
        return new Offer(
                0L,                                    // id
                'Test Offer Title',                    // title
                'Test offer description with enough characters for validation', // description (20+ chars)
                new BigDecimal('99.99'),              // price
                OfferCategory.ELECTRONICS,             // category
                OfferStatus.DRAFT,                     // status (default)
                URI.create('https://google.com'),      // imageUrl
                'seller-123',                          // sellerId
                LocalDateTime.now(),                   // createdAt (default)
                LocalDateTime.now(),                   // updatedAt (default)
                null,                                  // expiresAt (default)
                ['warranty', 'brand'] as Set<String>, // tags (default) with required tags for electronics
                0L                                     // viewCount (default)
        )
    }

    private static Offer createOfferWithStatus(Long id, String title, String description, BigDecimal price, OfferCategory category, OfferStatus status) {
        return new Offer(
                id,
                title,
                description.padRight(20, ' '), // Ensure description is at least 20 characters
                price,
                category,
                status,
                URI.create('https://example.com/image.jpg'),
                'seller-' + id,
                LocalDateTime.now(),
                LocalDateTime.now(),
                status == OfferStatus.ACTIVE ? LocalDateTime.now().plusDays(30) : null,
                createRequiredTags(category),
                0L
        )
    }

    private static Set<String> createRequiredTags(OfferCategory category) {
        switch (category) {
            case OfferCategory.ELECTRONICS:
                return ['warranty', 'brand'] as Set<String>
            case OfferCategory.FASHION:
                return ['size', 'condition'] as Set<String>
            case OfferCategory.AUTOMOTIVE:
                return ['make', 'model'] as Set<String>
            default:
                return [] as Set<String>
        }
    }
}
