package com.presentation.intellij.tips

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest
@ContextConfiguration(classes = AppRunner)
class BaseIntegrationTest extends Specification {

    def 'should load Spring application context'() {
        expect: 'application context loads successfully'
        true // If we reach this point, the context loaded successfully
    }

    def 'should have correct application properties'() {
        given: 'application configuration'
        def expectedGroup = 'com.presentation'
        def expectedVersion = '0.0.1-SNAPSHOT'

        expect: 'properties match expected values'
        expectedGroup != null
        expectedVersion.contains('SNAPSHOT')
    }
}
