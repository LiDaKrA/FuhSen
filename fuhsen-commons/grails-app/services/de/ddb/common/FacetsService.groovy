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

import static groovyx.net.http.ContentType.JSON
import net.sf.json.JSONNull

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.web.servlet.support.RequestContextUtils
/**
 * Get faceted search fields and values for facet from the Backend.
 *
 * @author mih
 * @author chh
 */
public class FacetsService {

    def messageSource
    def configurationService
    def searchService

    private static final String ENUM_SEARCH_TYPE = "ENUM"
    private static final String LABEL_SORT_TYPE = "ALPHA_LABEL"
    private static final String FACET_NAME_SUFFIX = "_fct"

    /**
     * Get values for a facet.
     *
     * @param facetName The name of the facet
     * @param allFacetFilters List of all available facet filter mappings
     * @return List of Facet-Values
     */
    public List getFacet(facetName, allFacetFilters) throws IOException {
        def filtersForFacetName = getFiltersForFacetName(facetName, allFacetFilters)
        def res = []
        int i = 0
        log.info "Facet name: ${facetName}"

        ApiResponse responseWrapper = ApiConsumer.getJson(configurationService.getBackendUrl() ,'/search/facets/' + facetName)
        if(responseWrapper.isOk()) {
            def json = responseWrapper.getResponse()
            json.facetValues.each{
                if(filtersForFacetName.isEmpty() || !filtersForFacetName.contains(it.value)){
                    res[i] = it.value
                    i++
                }
            }
            return res
        } else {
            log.warn "Server returned " + responseWrapper.status + " for retrieving values for facet " + facetName
        }
    }

    /**
     * Takes a list of configured facet filter mapping and returns only the filter values for the matching facet name.
     * E.g.: facetName=facet1, allFacetsFilters=[{facetName:facet1, filter:filter1}, {facetName:facet2, filter:filter2}]
     * The returned list would be [filter1]
     * @param facetName The name of the facet
     * @param allFacetFilters List of mappings containing all available facet filter mappings
     * @return A list of filters for the matching facet name
     */
    private List getFiltersForFacetName(facetName, allFacetFilters){
        def filtersForFacetName = []
        for(filter in allFacetFilters) {
            if(filter.facetName != null && filter.facetName.equals(facetName)){
                filtersForFacetName.add(filter.filter)
            }
        }
        return filtersForFacetName
    }

    /**
     * Returns all facets definitions from the backend.
     *
     * TODO Implement a caching mechanism for the facets, because the values will only change from release to release
     *
     * @return a list of all facets in the json format
     */
    public getAllFacets() {
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), '/search/facets/')
        if(!apiResponse.isOk()){
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }

        def resultsItems = apiResponse.getResponse()

        //Convert JSONNull instances to an empty String because of JSONException when calling the render method in a controller
        resultsItems.each{
            it.parent = it.parent instanceof JSONNull ? "" : it.parent
            it.role = it.role instanceof JSONNull ? "" : it.role
            it.sortType = it.sortType instanceof JSONNull ? "" : it.sortType
        }
        return resultsItems
    }

    /**
     * This method converts the "facetValues[]" parameter of a request to an url encoded query String
     *
     * @param reqParameters the requestParameter
     * @return the url encoded query String for facetValues parameter
     */
    def facetValuesToUrlQueryString(GrailsParameterMap reqParameters){
        def res = ""
        def facetValues = searchService.facetValuesRequestParameterToList(reqParameters)

        if(facetValues != null){
            res = facetValuesToUrlQueryString(facetValues)
        }

        return res
    }

    /**
     * This method converts a list of facetValues to an url encoded query String
     *
     * @param facetValues the List of facetValues
     * @return the url encoded query String for facetValues parameter
     */
    def facetValuesToUrlQueryString(List facetValues){
        def res = ""

        facetValues.each{
            res += "&facetValues%5B%5D="+it.encodeAsURL()
        }

        return res
    }


    /**
     * Returns a Map that contains the facet values for all enum based search fields
     *
     * @param facetSearchfields the search fields for which to get the values
     * @param messagePropertyPrefix the i18n prefix
     *
     * @return a Map that contains the facet values for all enum based search fields
     */
    private Map getFacetValues(facetSearchfields, messagePropertyPrefix) {
        def facetValuesMap = [:]
        def allFacetFilters = configurationService.getFacetsFilter()

        for ( facetSearchfield in facetSearchfields ) {
            if (facetSearchfield.searchType.equals(ENUM_SEARCH_TYPE)) {
                def facetValues = null
                def facetDisplayValuesMap = new TreeMap()

                //Special handling for "license_group, as this facet has "
                if (facetSearchfield.name == "license_group") {
                    facetValues = getFacet(facetSearchfield.name , allFacetFilters)
                    for (facetValue in facetValues) {
                        //translate because of sorting
                        facetDisplayValuesMap[facetValue] = getMessage(messagePropertyPrefix + facetSearchfield.name + "_" + facetValue)
                    }
                } else {
                    facetValues = getFacet(facetSearchfield.name + FACET_NAME_SUFFIX, allFacetFilters)
                    for (facetValue in facetValues) {
                        //translate because of sorting
                        facetDisplayValuesMap[facetValue] = getMessage(messagePropertyPrefix + facetSearchfield.name + FACET_NAME_SUFFIX + "_" + facetValue)
                    }
                }

                if (facetSearchfield.sortType != null && facetSearchfield.sortType.equals(LABEL_SORT_TYPE)) {
                    facetDisplayValuesMap = facetDisplayValuesMap.sort {it.value}
                }
                else {
                    facetDisplayValuesMap = facetDisplayValuesMap.sort {it.key}
                }

                facetValuesMap[facetSearchfield.name + FACET_NAME_SUFFIX] = facetDisplayValuesMap
            }
        }
        return facetValuesMap
    }


    /**
     * Returns a new list that contains only that elements from allFacets which match the entries in allowedFacets
     *
     * @param allFacets the original facet list (in JSON format) to filter
     * @param allowedFacets the list with allowed facet names
     *
     * @return a filtered facte list
     */
    List<?> filterOnlyAdvancedSearchFacets(List allFacets, List<String> allowedFacets){
        List<?> filteredFacets = []

        //To stay with the right order we have to iterate over both facet lists.
        allowedFacets.each { itAllowedFacets ->
            allFacets.each { itAllFacets ->
                if(itAllFacets.name == itAllowedFacets){
                    filteredFacets.add(itAllFacets)
                }
            }
        }
        return filteredFacets
    }

    /**
     * get display-value language-dependent.
     *
     * @param name fieldname
     * @return String translated display-value
     */
    private String getMessage(name) {
        def webUtils = WebUtils.retrieveGrailsWebRequest()
        def currentRequest = webUtils.getCurrentRequest()
        Locale locale = RequestContextUtils.getLocale(currentRequest)

        return messageSource.getMessage(name, null, locale)
    }
}
