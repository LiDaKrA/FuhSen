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
import de.ddb.common.ApiConsumer
import de.ddb.common.ApiResponse


/**
 * Set of Methods that encapsulate index based requests to the ElasticSearch server
 *
 * @author boz
 *
 */
class ElasticSearchService {

    def configurationService
    def transactional = false

    /**
     * Return the number of documents for a given type
     * 
     * @param type the mapping type
     * @return the number of documents for a given type
     */
    def int getDocumentCountByType(String type) {
        int count = -1

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/" + type + "/_search", false)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            count = response.hits.total
        }

        return count
    }

    /**
     * Refresh the ddb index
     */
    def void refresh() {
        log.info "refresh(): refreshing index ddb..."

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/_refresh", false, "")

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "Response: ${response}, finished refreshing index ddb."
        }
    }

    /**
     * Delete the entries of a list for a given indexType
     *
     * @param idList a list of ids
     * @param indexType the index type of the items to delete
     */
    boolean deleteTypeEntriesByIds(List<String> idList, String indexType) {
        log.info "deleteIndexTypeByIds()"

        def postBody = ''
        idList.each { id ->
            postBody = postBody + '{ "delete" : { "_index" : "ddb", "_type" : "' + indexType + '", "_id" : "' + id + '" } }\n'
        }
        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/" + indexType + "/_bulk", false, postBody)

        if(apiResponse.isOk()){
            refresh()
            return true
        }else{
            return false
        }
    }
}
