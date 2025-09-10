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

    def "exception on invalid limit"() {
        when:
        offersService.getOffers(-1, null)

        then:
        thrown(InvalidPaginationException)
    }

    @Unroll
    def "should throw exception when adding offer on not allowed account status"() {
        given:
        accountStatusClient.getAccountStatus("user-id") >> accountStatus

        when:
        offersService.add(offer(), "user-id", "any-request-id")

        then:
        thrown(IncorrectAccountStatusException)

        where:
        accountStatus << [AccountStatus.BLOCKED, AccountStatus.TO_ACTIVATE]
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
                description,
                price,
                category,
                OfferStatus.ACTIVE,
                URI.create("https://example.com/image.jpg"),
                "seller-" + id,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                [] as Set<String>,
                0L
        )
    }

    private static Offer offer() {
        return new Offer(
                0L,                                    // id
                "Test Offer Title",                    // title
                "Test offer description",              // description
                new BigDecimal("99.99"),              // price
                OfferCategory.ELECTRONICS,             // category
                OfferStatus.DRAFT,                     // status (default)
                URI.create("https://google.com"),      // imageUrl
                "seller-123",                          // sellerId
                LocalDateTime.now(),                   // createdAt (default)
                LocalDateTime.now(),                   // updatedAt (default)
                null,                                  // expiresAt (default)
                [] as Set<String>,                     // tags (default)
                0L                                     // viewCount (default)
        )
    }
}
