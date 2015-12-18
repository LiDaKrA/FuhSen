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

import org.codehaus.groovy.grails.web.util.WebUtils

import de.ddb.common.constants.FacetEnum
import de.ddb.common.constants.SearchParamEnum

abstract class CommonInstitutionService {
    def configurationService

    /**
     * Remove all objects from the institution list which are no institutions.
     *
     * @param institution list original list containing also tectonics
     *
     * @return institution list without tectonics
     */
    private def filterInstitutions(institutions) {
        return institutions.findAll{it.type == "institution"}
    }

    def getFacetValues(String provName) {
        log.debug("get facets values for: ${provName}")
        int shortLength = 50
        String shortQuery = (provName.length() > shortLength ? provName.substring(0, shortLength) : provName)
        def uriPath = "/search/facets/" + FacetEnum.PROVIDER_FCT.getName()
        def query = [(SearchParamEnum.QUERY.getName()):"${shortQuery}" ]
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), uriPath, false, query)
        if(!apiResponse.isOk()){
            log.error "Json: json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return apiResponse.getResponse()
    }

    def getInstitutionViewByItemId(String id) {
        log.debug("get institution view by item id: ${id}")
        def result
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), "/items/" + id + "/view")
        if (apiResponse.isOk()) {
            result = apiResponse.getResponse()
        }
        return result
    }

    def getParentsOfInstitutionByItemId(String id) {
        log.debug("get parent of institution by item id: ${id}")
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), "/items/" + id + "/parents")
        if(!apiResponse.isOk()){
            log.error "Json: json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return filterInstitutions(apiResponse.getResponse().hierarchy)
    }

    def getProviderObjectCount(String provName) {
        def result = 0
        def query = [(SearchParamEnum.QUERY.getName()):"*",
            (SearchParamEnum.FACET.getName()):FacetEnum.PROVIDER_FCT.getName(),
            (FacetEnum.PROVIDER_FCT.getName()):"${provName}",
            (SearchParamEnum.ROWS.getName()):"0"]
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), "/search", false, query)
        if(!apiResponse.isOk()){
            log.error "Json: json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        def jsonResult = apiResponse.getResponse()
        def providerFct = jsonResult.facets.find {e -> FacetEnum.valueOfName(e.field) == FacetEnum.PROVIDER_FCT}
        if (providerFct?.facetValues?.count?.size() > 0) {
            result = providerFct.facetValues.count[0]
        }
        return result
    }
}
