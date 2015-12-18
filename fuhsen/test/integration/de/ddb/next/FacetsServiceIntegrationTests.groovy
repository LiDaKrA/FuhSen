package de.ddb.next

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin

import org.junit.*


@TestMixin(IntegrationTestMixin)
class FacetsServiceIntegrationTests {

    def facetsService

    @Test
    void getAllFacetsTest() {
        def allFacets = facetsService.getAllFacets()

        assert allFacets.size() > 0
    }
}
