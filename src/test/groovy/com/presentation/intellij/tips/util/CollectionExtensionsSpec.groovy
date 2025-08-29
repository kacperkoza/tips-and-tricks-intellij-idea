package com.presentation.intellij.tips.util

import spock.lang.Specification
import spock.lang.Unroll

class CollectionExtensionsSpec extends Specification {

    def 'should drop elements when offset is not null'() {
        given: 'a list of numbers'
        def numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

        when: 'we drop first 3 elements'
        def result = numbers.dropIfNotNull(3)

        then: 'first 3 elements are removed'
        result == [4, 5, 6, 7, 8, 9, 10]
    }

    def 'should return original list when offset is null'() {
        given: 'a list of numbers'
        def numbers = [1, 2, 3, 4, 5]

        when: 'we drop with null offset'
        def result = numbers.dropIfNotNull(null)

        then: 'original list is returned'
        result == [1, 2, 3, 4, 5]
    }

    def 'should take elements when limit is not null'() {
        given: 'a list of numbers'
        def numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

        when: 'we take first 4 elements'
        def result = numbers.takeIfNotNull(4)

        then: 'only first 4 elements are returned'
        result == [1, 2, 3, 4]
    }

    def 'should return original list when limit is null'() {
        given: 'a list of numbers'
        def numbers = [1, 2, 3, 4, 5]

        when: 'we take with null limit'
        def result = numbers.takeIfNotNull(null)

        then: 'original list is returned'
        result == [1, 2, 3, 4, 5]
    }

    @Unroll
    def 'should paginate correctly: offset=#offset, limit=#limit'() {
        given: 'a list of numbers 1 to 10'
        def numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

        when: 'we apply pagination'
        def result = numbers.paginate(offset, limit)

        then: 'correct subset is returned'
        result == expected

        where: 'test data combinations'
        offset | limit | expected
        null   | null  | [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        2      | null  | [3, 4, 5, 6, 7, 8, 9, 10]
        null   | 5     | [1, 2, 3, 4, 5]
        3      | 4     | [4, 5, 6, 7]
        0      | 3     | [1, 2, 3]
        5      | 2     | [6, 7]
    }

    def 'should handle edge cases gracefully'() {
        given: 'an empty list'
        def emptyList = []

        expect: 'extension methods handle empty lists'
        emptyList.dropIfNotNull(5) == []
        emptyList.takeIfNotNull(5) == []
        emptyList.paginate(2, 3) == []

        and: 'large offsets/limits are handled'
        [1, 2, 3].dropIfNotNull(10) == []
        [1, 2, 3].takeIfNotNull(10) == [1, 2, 3]
        [1, 2, 3].paginate(10, 5) == []
    }
}
