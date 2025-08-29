package com.presentation.intellij.tips.offer

import com.presentation.intellij.tips.infrastracture.account.AccountStatus
import com.presentation.intellij.tips.infrastracture.account.AccountStatusClient
import com.presentation.intellij.tips.offer.infrastracture.api.Offer
import com.presentation.intellij.tips.offer.infrastracture.repository.InMemoryRepository
import com.presentation.intellij.tips.offer.infrastracture.repository.OffersRepository
import spock.lang.Specification
import spock.lang.Unroll


class OffersServiceTest extends Specification {

    OffersRepository offersRepository = new InMemoryRepository()
    AccountStatusClient accountStatusClient = Stub(AccountStatusClient)

    OffersService offersService = new OffersService(offersRepository, accountStatusClient)

    @Unroll
    def 'should return offers according to limit and offset'() {
        when:
        List<Offer> offers = offersService.getOffers(limit, null)

        then:
        offers.collect { it.id as int } == expectedOfferIds

        where:
        limit | expectedOfferIds
        null  | [1, 2, 3, 4, 5, 6, 7, 8]
        1     | [1]
        3     | [1, 2, 3]
        5     | [1, 2, 3, 4, 5]
        10    | [1, 2, 3, 4, 5, 6, 7, 8]
    }

    def 'exception on invalid limit'() {
        when:
        offersService.getOffers(-1, null)

        then:
        thrown(InvalidPaginationException)
    }

    def 'should throw exception when adding offer on not allowed account status'() {
        given:
        accountStatusClient.getAccountStatus('user-id') >> accountStatus

        when:
        offersService.add(offer(), 'user-id')

        then:
        thrown(IncorrectAccountStatusException)


        where:
        accountStatus << [AccountStatus.BLOCKED, AccountStatus.TO_ACTIVATE]
    }

    private static Offer offer() {
        return new Offer(0L, '', URI.create('https://google.com'))
    }
}
