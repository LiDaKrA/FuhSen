package de.ddb.next

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin

import org.junit.*

import de.ddb.common.constants.Type


@TestMixin(IntegrationTestMixin)
class SavedSearchServiceIntegrationTests {

    def savedSearchService

    /** The userId is refreshed in setUp() for every test method*/
    def userId = null

    /**
     * Is called before every test method.
     * Creates a new userId for the test.
     */
    void setUp() {
        println "--------------------------------------------------------------------"
        println "Setup tests"
        userId = UUID.randomUUID() as String
        logStats()
    }

    /**
     * Is called after every test method.
     * Cleanup user content created by a test method
     */
    void tearDown() {
        println "Cleanup tests"

        def results = savedSearchService.findSavedSearchByUserId(userId)
        println "Saved user searches after test: " + results.size()

        savedSearchService.deleteSavedSearchesByUserId(userId)

        logStats()
        println "--------------------------------------------------------------------"
    }


    def logStats() {
        println "userId " + userId
        println "Index has " + savedSearchService.getSavedSearchesCount() + " searches"
    }

    @Test
    void shouldSavedUserSearch() {
        log.info "should saved user search"

        def queryString = 'query=Goethe&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'

        def savedSearchId = savedSearchService.saveSearch(userId, queryString, Type.CULTURAL_ITEM)
        log.info "id: ${savedSearchId}"
        assert savedSearchId  != null
    }

    @Test
    void shouldSavedUserSearchWithTitleAndDescription() {
        log.info "should saved user search"

        def queryString = 'query=Goethe&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'

        def savedSearchId = savedSearchService.saveSearch(userId, queryString, 'Goethe Related',
            'All things related to Goethe', Type.CULTURAL_ITEM)
        log.info "id: ${savedSearchId}"
        assert savedSearchId  != null
    }

    @Test
    void shouldFindAllSavedSearchesByUserId() {
        log.info "should find all saved searches by user ID"

        def queryStringForGoethe = 'query=Goethe&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def goetheSavedSearchId = savedSearchService.saveSearch(userId, queryStringForGoethe , 'Goethe Related',
            'All things related to Goethe', Type.CULTURAL_ITEM)
        assert goetheSavedSearchId != null

        def queryStringForMozart = 'query=mozart&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def mozartSavedSearchId = savedSearchService.saveSearch(userId, queryStringForMozart , 'Mozart Related',
            Type.CULTURAL_ITEM)
        assert mozartSavedSearchId != null

        def results = savedSearchService.findSavedSearchByUserId(userId)
        assert results.size() == 2
    }

    @Test
    void shouldDeleteSavedSearches() {
        log.info "should delete saved search by IDs"

        def queryStringForGoethe = 'query=Goethe&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def goetheSavedSearchId = savedSearchService.saveSearch(userId, queryStringForGoethe ,
            'Goethe Related', 'All things related to Goethe', Type.CULTURAL_ITEM)
        assert goetheSavedSearchId != null

        def queryStringForMozart = 'query=mozart&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def mozartSavedSearchId = savedSearchService.saveSearch(userId, queryStringForMozart , 'Mozart Related',
            Type.CULTURAL_ITEM)
        assert mozartSavedSearchId != null


        def results = savedSearchService.findSavedSearchByUserId(userId)
        assert results.size() == 2

        savedSearchService.deleteSavedSearch(
                [
                    goetheSavedSearchId,
                    mozartSavedSearchId
                ])

        assert savedSearchService.findSavedSearchByUserId(userId).size() == 0
    }

    @Test
    void shouldDeleteSavedSearchesByUserId() {
        def queryStringForGoethe = 'query=Goethe&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def goetheSavedSearchId = savedSearchService.saveSearch(userId, queryStringForGoethe , 'Goethe Related',
            'All things related to Goethe', Type.CULTURAL_ITEM)
        assert goetheSavedSearchId != null

        def queryStringForMozart = 'query=mozart&facetValues[]=keywords_fct%3DFotos&facetValues[]=type_fct%3Dmediatype_002&facetValues[]=sector_fct%3Dsec_05&facetValues[]=begin_time%3D[*+TO+693961]&facetValues[]=end_time%3D[547509+TO+*]'
        def mozartSavedSearchId = savedSearchService.saveSearch(userId, queryStringForMozart , 'Mozart Related',
            Type.CULTURAL_ITEM)
        assert mozartSavedSearchId != null


        def results = savedSearchService.findSavedSearchByUserId(userId)
        assert results.size() == 2

        savedSearchService.deleteSavedSearchesByUserId(userId)

        assert savedSearchService.findSavedSearchByUserId(userId).size() == 0
    }

    // TODO: shouldUpdateSavedSearch
}
