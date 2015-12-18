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

class GetBackendVersionTagLib {

    static namespace = "ddbcommon"

    def configurationService

    def getBackendVersion = { attrs, body ->
        try{
            def apiResponse = ApiConsumer.getText(configurationService.getBackendUrl(), "/version")
            if(!apiResponse.isOk()){
                log.error "Text: text file was not found"
                out << "unknown"
                return
                // Don't throw an error here: if an error is thrown in this taglib,
                // it will forward to the errorpage, where this taglib will get called again.
                // This will cause an endless exception cycle and an uncatchable error.
                // apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            }
            String buildVersionAndNumber = apiResponse.getResponse()
            String buildVersion = buildVersionAndNumber
            int indexVersionNumber = buildVersionAndNumber.indexOf("/")
            if(indexVersionNumber > 0){
                buildVersion = buildVersionAndNumber.substring(0, indexVersionNumber)
            }
            out << buildVersion
        }catch(Exception e) {
            out << "error"
        }
    }
}
