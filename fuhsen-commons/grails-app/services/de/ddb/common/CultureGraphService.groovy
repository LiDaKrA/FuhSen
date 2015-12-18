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

import de.ddb.common.ApiConsumer
import de.ddb.common.ApiResponse
import de.ddb.common.constants.CultureGraphEnum

/**
 * Service class for accesing the CultureGraphService
 * 
 * @author boz
 */
class CultureGraphService {

    public final static String GND_URI_PREFIX = "http://d-nb.info/gnd/"

    def configurationService

    def transactional=false

    def getCultureGraph(String gndId) {
        def query = [:]
        query[CultureGraphEnum.THUMB_WIDTH.getName()]=270
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getCulturegraphUrl(), "/entityfacts/" + gndId, false, query)
        if(!apiResponse.isOk()){
            log.error "getCultureGraph(): culturegraph api under: "+configurationService.getCulturegraphUrl() + "/entityfacts/" + gndId +" returned unsuccessfully"
        }

        return apiResponse
    }

    boolean isValidGndUri(String uriToTest){
        if(uriToTest != null && uriToTest.startsWith(GND_URI_PREFIX)){
            return true
        }else{
            return false
        }
    }

    String getGndIdFromGndUri(String uri){
        String id = ""
        if(uri != null && uri.startsWith(GND_URI_PREFIX)){
            id = uri.substring(GND_URI_PREFIX.length())
        }
        return id
    }
}
