/*
 * Copyright (C) 2014 FIZ Karlsruhe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ddb.common

import groovy.json.*

import org.codehaus.groovy.grails.web.util.WebUtils

import de.ddb.common.ApiResponse.HttpStatus
import de.ddb.common.constants.FacetEnum
import de.ddb.common.constants.RoleFacetEnum
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.exception.EntityNotFoundException

/**
 * Service class for all entity related methods
 *
 * @author boz
 */
class EntityService {

    //Autowire the grails application bean
    def grailsApplication
    def configurationService
    def cultureGraphService

    def transactional=false

    def doFacetSearch(def offset, def rows, RoleFacetEnum roleFacetEnum, def entityForename, def entitySurname, def entityId) {
        def facetSearch = [:]

        def searchQuery = []

        def searchUrlParameter = []

        def gndUrl = CultureGraphService.GND_URI_PREFIX

        def normdataFacetValue = gndUrl + entityId + roleFacetEnum.getHierarchicalName()

        def normdataQuery = FacetEnum.AFFILIATE_FCT_ROLE_NORMDATA.getName() + ":(\"" + normdataFacetValue + "\")"

        searchQuery = [
            (SearchParamEnum.ROWS.getName()): rows,
            (SearchParamEnum.OFFSET.getName()): offset,
            (SearchParamEnum.QUERY.getName()): normdataQuery]


        //These parameters are for the frontend to create a search link which is not limited to 4 documents...
        searchUrlParameter = [
            (SearchParamEnum.QUERY.getName()): normdataQuery
        ]

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)

        if(!apiResponse.isOk()){
            def message = "doFacetSearch(): Search response contained error"
            log.error message
            throw new RuntimeException(message)
        }

        def jsonSearchResult = apiResponse.getResponse()

        facetSearch["items"] = jsonSearchResult?.results?.docs
        facetSearch["resultCount"] = jsonSearchResult?.numberOfResults
        facetSearch["searchUrlParameter"] = searchUrlParameter

        return facetSearch
    }

    /**
     * Performs a search request on the backend.
     *
     * @param query the name of the entity
     * @param offset the search offset
     * @param rows the number of search results
     *
     * @return the serach result
     */
    def doEntitySearch(def query) {
        def searchPreview = [:]

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl() ,'/entities', false, query)
        if(!apiResponse.isOk()){
            //Provisional DDBNEXT-1716
            //To get a empty result
            apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl() ,'/entities', false, ['query': '..1.2.2.3..'])
            if(!apiResponse.isOk()){
                def message = "doEntitySearch(): Search response contained error"
                log.error message
                throw new RuntimeException(message)
            }
        }

        def jsonSearchResult = apiResponse.getResponse()
        searchPreview["entity"] = jsonSearchResult.results
        searchPreview["totalResults"] = jsonSearchResult.numberOfResults
        return searchPreview
    }


    /**
     * Performs a search request on the backend.
     *
     * @param query the name of the entity
     * @param offset the search offset
     * @param rows the number of search results
     *
     * @return the serach result
     */
    def doItemSearch(def query, def offset, def rows, def jsonGraph) {

        def searchQuery = buildSearchQuery(jsonGraph, offset, rows)
        searchQuery[(FacetEnum.TYPE_FCT.getName())].add("mediatype_001")
        searchQuery[(FacetEnum.TYPE_FCT.getName())].add("mediatype_002")
        searchQuery[(FacetEnum.TYPE_FCT.getName())].add("mediatype_005")

        def searchPreview = [:]

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)
        if(!apiResponse.isOk()){
            def message = "doItemSearch(): Search response contained error"
            log.error message
            throw new RuntimeException(message)
        }

        def jsonSearchResult = apiResponse.getResponse()

        searchPreview["items"] = jsonSearchResult.results?.docs
        searchPreview["resultCount"] = jsonSearchResult.numberOfResults

        return searchPreview
    }

    /**
     * Get the detailed information for the given entity id from the entity service
     *
     * @param entityId the entity id
     *
     * @return detailed information about this entity
     */
    def Map getEntityDetails(String entityId) {
        def ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), "entities", false,
                [(SearchParamEnum.QUERY.getName()) :
                    SearchParamEnum.ID.getName() + ":\"" + CultureGraphService.GND_URI_PREFIX + entityId + "\""])

        def response = apiResponse.getResponse()
        if (apiResponse.isOk()) {
            if (response.numberOfResults == 1) {
                return response.results[0].docs[0]
            }
            else if (response.numberOfResults == 0) {
                throw new EntityNotFoundException()
            }
            else {
                throw new RuntimeException("number of results should be 1 but is " + response.numberOfResults)
            }
        }
        else if (apiResponse.status == HttpStatus.HTTP_404) {
            throw new EntityNotFoundException()
        }
        else {
            log.error "getEntityDetails(): Entitiy response returned error " + apiResponse.status
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
    }

    /**
     * Gets the number of results for a given query and facet type
     *
     * @param searchString the search query
     * @param facetType the facet type
     *
     * @return the number of results for a given query and facet type
     */
    def getResultCountsForFacetType(def searchString, def facetType, def offset, def rows, def jsonGraph) {
        def searchQuery = buildSearchQuery(jsonGraph, offset, rows)
        searchQuery[(FacetEnum.TYPE_FCT.getName())].add(facetType)

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, searchQuery)

        if(!apiResponse.isOk()){
            def message = "getResultCountsForFacetType(): Search response contained error"
            log.error message
            throw new RuntimeException(message)
        }

        return apiResponse.getResponse().numberOfResults
    }

    def getResultLinkQuery(def offset, def rows, def jsonGraph) {
        def searchQuery = buildSearchQuery(jsonGraph, offset, rows)
        return searchQuery
    }


    private def buildSearchQuery(def jsonGraph, def offset, def rows){
        def forename = jsonGraph.person.forename
        if(jsonGraph.person.prefix != null && !jsonGraph.person.prefix.trim().isEmpty()){
            forename += " "+jsonGraph.person.prefix
        }
        def surname = jsonGraph.person.surname
        def queryName = forename+" "+surname
        def affiliateName = surname+ ", "+forename

        def searchQuery = [:]
        searchQuery[(SearchParamEnum.QUERY.getName())] = queryName
        searchQuery[(SearchParamEnum.FACET.getName())] = []
        searchQuery[(SearchParamEnum.FACET.getName())].add(FacetEnum.AFFILIATE_FCT.getName())
        searchQuery[(SearchParamEnum.FACET.getName())].add(FacetEnum.TYPE_FCT.getName())
        searchQuery[(FacetEnum.AFFILIATE_FCT.getName())] = affiliateName
        searchQuery[(FacetEnum.TYPE_FCT.getName())] = []
        searchQuery[(SearchParamEnum.ROWS.getName())] = rows
        searchQuery[(SearchParamEnum.OFFSET.getName())] = offset
        searchQuery[(SearchParamEnum.SORT.getName())] = SearchParamEnum.SORT_RELEVANCE.getName()

        return searchQuery
    }

    /**
     *
     * Build the list of facets to be rendered in the non javascript version of search results
     *
     * @param urlQuery the urlQuery
     * @return list of all facets filtered
     */
    def buildSubFacets(LinkedHashMap urlQuery, def facetsList){
        def emptyFacets = facetsList.clone()
        def res = []
        //We want only the first 10 facets
        urlQuery["facet.limit"] = 10

        urlQuery[SearchParamEnum.FACET.getName()].each{
            if (it != SearchService.THUMBNAIL_FACET) {
                emptyFacets.remove(it)
                def tmpUrlQuery = urlQuery.clone()
                tmpUrlQuery[SearchParamEnum.ROWS.getName()]=1
                tmpUrlQuery[SearchParamEnum.OFFSET.getName()]=0
                tmpUrlQuery.remove(it)
                def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl() ,'/entities', false, tmpUrlQuery)
                if(!apiResponse.isOk()){
                    log.error "Json: Json file was not found"
                    apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                }
                def jsonResp = apiResponse.getResponse()
                jsonResp.facets.each{ facet->
                    if(facet.field==it){
                        res.add(facet)
                    }
                }
            }
        }
        //fill the remaining empty facets
        emptyFacets.each{
            res.add([field: it, numberOfFacets: 0, facetValues: []])
        }
        return res
    }
}
