package com.presentation.intellij.tips

import spock.lang.Specification
import spock.lang.Unroll

class SimpleSpockSpec extends Specification {

    def 'should demonstrate basic Spock assertions'() {
        given: 'some test data'
        def name = 'IntelliJ Tips'
        def version = '0.0.1-SNAPSHOT'

        when: 'we perform some operations'
        def result = name.toLowerCase()

        then: 'we verify the results'
        result == 'intellij tips'
        name.length() == 13
        version.contains('SNAPSHOT')
    }

    @Unroll
    def 'should verify math operations: #a + #b = #expected'() {
        expect: 'addition works correctly'
        a + b == expected

        where: 'test data combinations'
        a  | b  | expected
        1  | 1  | 2
        2  | 3  | 5
        10 | 5  | 15
        -1 | 1  | 0
    }

    def 'should demonstrate exception handling'() {
        when: 'we divide by zero'
        def result = 10 / 0

        then: 'an exception is thrown'
        thrown(ArithmeticException)
    }

    def 'should verify collection operations'() {
        given: 'a list of technologies'
        def technologies = ['Kotlin', 'Spring Boot', 'Spock', 'Groovy']

        expect: 'collection properties'
        technologies.size() == 4
        technologies.contains('Spock')
        technologies.every { it instanceof String }
        technologies.find { it.startsWith('S') } == 'Spring Boot'
    }

}
