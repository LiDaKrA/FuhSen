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

import static groovyx.net.http.ContentType.*
import groovy.json.*
import de.ddb.common.constants.FacetEnum
import de.ddb.common.constants.SearchParamEnum

/**
 * Set of services used in the ApisController for views/search
 *
 * @author ema
 * @author boz
 */

class ApisService {
    def transactional=false

    /**
     * Transform the query parameter Map from the SearchService to a backend compatible query Map  
     * 
     * @param queryParameters The map with the original frontend API query parameter
     * 
     * @return query parameter map for the cortex API
     */
    def getQueryParameters(Map queryParameters){
        def query = [ (SearchParamEnum.QUERY.getName()): queryParameters.query ]

        if(queryParameters[SearchParamEnum.OFFSET.getName()]) {
            query[SearchParamEnum.OFFSET.getName()]= queryParameters[SearchParamEnum.OFFSET.getName()]
        }

        if(queryParameters[SearchParamEnum.ROWS.getName()]) {
            query[SearchParamEnum.ROWS.getName()] = queryParameters[SearchParamEnum.ROWS.getName()]
        }

        if(queryParameters[SearchParamEnum.CALLBACK.getName()]) {
            query[SearchParamEnum.CALLBACK.getName()] = queryParameters[SearchParamEnum.CALLBACK.getName()]
        }

        if(queryParameters[SearchParamEnum.FACET.getName()]) {
            if(queryParameters[SearchParamEnum.FACET.getName()].getClass().isArray()){
                query[SearchParamEnum.FACET.getName()] = []
                queryParameters[SearchParamEnum.FACET.getName()].each {
                    query[SearchParamEnum.FACET.getName()].add(it)
                }
            } else {
                query[SearchParamEnum.FACET.getName()]=queryParameters[SearchParamEnum.FACET.getName()]
            }
        }

        if(queryParameters[SearchParamEnum.MINDOCS.getName()]) {
            query[SearchParamEnum.MINDOCS.getName()] = queryParameters[SearchParamEnum.MINDOCS.getName()]
        }

        if(queryParameters[SearchParamEnum.SORT.getName()]) {
            query[SearchParamEnum.SORT.getName()] = queryParameters[SearchParamEnum.SORT.getName()]
        }

        if(queryParameters[SearchParamEnum.SORT_FIELD.getName()]) {
            query[SearchParamEnum.SORT_FIELD.getName()] = queryParameters[SearchParamEnum.SORT_FIELD.getName()]
        }

        //Evaluates the facetValues from the API request
        FacetEnum.values().each() {
            evaluateFacetParameter(query, queryParameters[it.getName()], it.getName())
        }

        if (queryParameters[SearchService.THUMBNAIL_FACET]) {
            query[SearchService.THUMBNAIL_FACET] = queryParameters[SearchService.THUMBNAIL_FACET]
        }

        if(queryParameters["facet.limit"]){
            query["facet.limit"] = queryParameters["facet.limit"]
        }

        return query
    }

    /**
     * 
     * @param query
     * @param queryParameter
     * @param facetName
     * @return
     */
    private def evaluateFacetParameter(Map query, Object queryParameter, String facetName) {
        if(queryParameter){
            if(queryParameter.getClass().isArray()){
                query[facetName] = []
                queryParameter.each {
                    query[facetName].add(it)
                }
            } else {
                query[facetName]= queryParameter
            }
        }
    }

    /**
     * If a role is an element of an query parameter, than the according root facet value must be removed!
     * 
     * Example for the query parameter affiliate_fct_role:
     * affiliate_fct_role:[Schiller, Friedrich (1759-1805), Schiller, Friedrich (1759-1805)_1_affiliate_fct_subject]]
     * 
     * The parameter contains the role "Schiller, Friedrich (1759-1805)_1_affiliate_fct_subject"
     * and the root facet "Schiller, Friedrich (1759-1805)"
     * 
     * So the root facet must be removed to perform a valid role search!
     * 
     * @param query the query parameter map
     * 
     * @return the filtered query parameter map
     */
    def filterForRoleFacets(Map query) {
        query.each { key, value ->
            //Search for root facets that must be removed
            Set rootFacetsToRemove = []
            if (key.endsWith('_role')) {
                value.each {
                    if (it =~ /_\d+_/) {
                        //Get the literal part of the role, which must be removed from the query parameter
                        rootFacetsToRemove.add(it.split(/_\d+_/)[0])
                    }
                }
            }

            //Remove the root facets from the parameter
            if (rootFacetsToRemove.size()) {
                value.removeAll(rootFacetsToRemove)
            }
        }

        return query
    }

    /**
     * Used in FacetsController gives you back an array containing the following Map: {facet value, localized facet value, count results}
     *
     * @param facets list of facets fetched from the backend
     * @param fctName name of the facet field required
     * @param numberOfElements number of elements to return
     * @return List of Map
     */
    def getSelectedFacetValuesFromOldApi(List facets, String fctName, int numberOfElements, String matcher, Locale locale){
        def res = [type: fctName, values: []]
        def allFacetFilters = configurationService.getFacetsFilter()

        facets.each{
            if(it.field==fctName){
                int max = (numberOfElements != -1 && it.facetValues.size()>numberOfElements)?numberOfElements:it.facetValues.size()
                for(int i=0;i<max;i++){
                    //Check if facet value has to be filtered
                    boolean filterFacet = false
                    for(int k=0; k<allFacetFilters.size(); k++){
                        if(fctName == allFacetFilters[k].facetName && it.facetValues[i].value.toString() == allFacetFilters[k].filter){
                            filterFacet = true
                            break
                        }
                    }

                    if(!filterFacet){
                        if(matcher && this.getI18nFacetValue(fctName, it.facetValues[i].value.toString()).toLowerCase().contains(matcher.toLowerCase())){
                            def localizedValue = this.getI18nFacetValue(fctName, it.facetValues[i].value.toString())
                            def firstIndexMatcher = localizedValue.toLowerCase().indexOf(matcher.toLowerCase())
                            localizedValue = localizedValue.substring(0, firstIndexMatcher)+"<strong>"+localizedValue.substring(firstIndexMatcher,firstIndexMatcher+matcher.size())+"</strong>"+localizedValue.substring(firstIndexMatcher+matcher.size(),localizedValue.size())
                            res.values.add([value: it.facetValues[i].value, localizedValue: localizedValue, count: String.format(locale, "%,d", it.facetValues[i].count.toInteger())])
                        }else if(!matcher)
                            res.values.add([value: it.facetValues[i].value, localizedValue: this.getI18nFacetValue(fctName, it.facetValues[i].value.toString()), count: String.format(locale, "%,d", it.facetValues[i].count.toInteger())])
                    }
                }
            }
        }
        return res
    }
}
