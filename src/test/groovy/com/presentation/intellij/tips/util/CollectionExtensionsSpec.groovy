package com.presentation.intellij.tips.util

import spock.lang.Specification

class CollectionExtensionsSpec extends Specification {

    def 'dropIfNotNull should drop elements when offset is not null'() {
        given:
        def list = [1, 2, 3, 4, 5]

        when:
        def result = list.dropIfNotNull(2)

        then:
        result == [3, 4, 5]
    }

    def 'dropIfNotNull should return original list when offset is null'() {
        given:
        def list = [1, 2, 3, 4, 5]

        when:
        def result = list.dropIfNotNull(null)

        then:
        result == [1, 2, 3, 4, 5]
    }

    def 'dropIfNotNull should return empty list when offset is greater than list size'() {
        given:
        def list = [1, 2, 3]

        when:
        def result = list.dropIfNotNull(5)

        then:
        result == []
    }

    def 'takeIfNotNull should take elements when limit is not null'() {
        given:
        def list = [1, 2, 3, 4, 5]

        when:
        def result = list.takeIfNotNull(3)

        then:
        result == [1, 2, 3]
    }

    def 'takeIfNotNull should return original list when limit is null'() {
        given:
        def list = [1, 2, 3, 4, 5]

        when:
        def result = list.takeIfNotNull(null)

        then:
        result == [1, 2, 3, 4, 5]
    }

    def 'takeIfNotNull should return original list when limit is greater than list size'() {
        given:
        def list = [1, 2, 3]

        when:
        def result = list.takeIfNotNull(5)

        then:
        result == [1, 2, 3]
    }

    def 'should chain dropIfNotNull and takeIfNotNull for pagination'() {
        given:
        def list = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

        when:
        def result = list.dropIfNotNull(offset).takeIfNotNull(limit)

        then:
        result == expected

        where:
        offset | limit | expected
        2      | 3     | [3, 4, 5]
        null   | 3     | [1, 2, 3]
        2      | null  | [3, 4, 5, 6, 7, 8, 9, 10]
        null   | null  | [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        0      | 5     | [1, 2, 3, 4, 5]
        5      | 2     | [6, 7]
    }
}
