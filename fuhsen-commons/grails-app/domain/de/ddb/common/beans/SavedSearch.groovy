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
package de.ddb.common.beans

import groovy.transform.ToString
import de.ddb.common.TimeFacetHelper
import de.ddb.common.constants.FacetEnum
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.constants.Type

@ToString(includeNames=true)

class SavedSearch {
    static SEARCH_PARAMETERS = [(SearchParamEnum.QUERY.getName()): "", (SearchParamEnum.FACETVALUES.getName()): ""]

    String id
    String label
    String queryString
    Type type
    Date creationDate
    final Map<String, Collection<SearchQueryTerm>> queryMap

    public SavedSearch(String id, String label, String queryString, Type type, Date creationDate) {
        this.id = id
        this.label = label
        this.queryString = queryString
        this.type = type
        this.creationDate = creationDate
        queryMap = toMap(queryString)
    }

    /**
     * Add a term to the map of terms. Check if a term with the same name already exists. If yes, add the new value to
     * the value list, otherwise add a new term.
     *
     * @param terms map of terms
     * @param parameterName name of the term to be added
     * @param term term to be added
     */
    private void addTerm(Map<String, SearchQueryTerm> terms, String parameterName, SearchQueryTerm term) {
        def oldTerms = terms.get(parameterName)

        if (oldTerms) {
            boolean termFound = false

            oldTerms.each {oldTerm ->
                if (term.name == oldTerm.name) {
                    oldTerm.values.add(term.values[0])
                    termFound = true
                }
            }
            if (!termFound) {
                oldTerms.add(term)
            }
        }
        else {
            terms.put(parameterName, [term])
        }
    }

    /**
     * Return the value of the parameter with the key "query" from the query string
     *
     * @return value of the parameter with the key "query" or null
     */
    String getQuery() {
        def result

        queryMap.each {
            if (it.key == SearchParamEnum.QUERY.getName()) {
                result = it.value[0].name
            }
        }
        return result
    }

    /**
     * parse query=something&facetValues[]=affiliate_fct:goethe&facetValues[]=affiliate_fct:gerig&facetValues[]=type_fct:mediatype_002
     */
    private Map<String, SearchQueryTerm> toMap(String queryString) {
        def result = [:]
        List<SearchQueryTerm> searchQueryTermList = []

        FacetEnum.values().each {
            if (it.isSearchFacet()) {
                searchQueryTermList.add(new SearchQueryTerm(it.getName()))
            }
        }

        // add empty list elements to get the correct order of the facet values
        result.put(SearchParamEnum.FACETVALUES.getName(), searchQueryTermList)

        // special handling for time facet
        def beginTime = null
        def endTime = null

        queryString.split('&').each {
            String[] parameter = it.split('=')
            String parameterName = URLDecoder.decode(parameter[0], "UTF-8")

            if (parameter.size() > 1 && SEARCH_PARAMETERS.containsKey(parameterName)) {
                String parameterValue = URLDecoder.decode(parameter[1], "UTF-8")
                SearchQueryTerm term = new SearchQueryTerm(parameterValue)

                // special handling for time facet
                if (term.name == FacetEnum.BEGIN_TIME.name) {
                    beginTime = term
                }
                else if (term.name == FacetEnum.END_TIME.name) {
                    endTime = term
                }
                else {
                    addTerm(result, parameterName, term)
                }
            }
        }
        // convert time facet values into human readable format
        if (beginTime || endTime) {
            try {
                addTerm(result, "time", new SearchQueryTerm(TimeFacetHelper.convertTimeFacetValues
                        (beginTime?.values?.getAt(0), endTime?.values?.getAt(0))))
            }
            catch (Exception e) {
                log.error "cannot parse time facet value", e
            }
        }
        return result
    }
}
