package testingforest

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class CaseSpec extends Specification implements DomainUnitTest<Case> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}
