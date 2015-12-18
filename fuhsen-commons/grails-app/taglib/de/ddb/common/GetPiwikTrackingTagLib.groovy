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


/**
 * Taglib for automatic integration of the piwik tracking sourcecode. The taglib ensures that the correct version
 * of the trackingcode (DEV or PROD) is included.
 * 
 * @author hla
 */
class GetPiwikTrackingTagLib {

    static namespace = "ddbcommon"

    def static trackingCodeCache = null
    def configurationService

    /**
     * Tag closure <g:piwik />, this tag has no attributes. 
     */
    def getPiwikTracking = { attrs, body ->

        //Only load the piwik tracking code one
        if(trackingCodeCache == null){
            def piwikFile = configurationService.getPiwikTrackingFile()

            try {
                trackingCodeCache = grailsApplication.getParentContext().getResource("file:"+piwikFile).inputStream.text
                log.info "Loaded and cached the Piwik tracking file from: "+piwikFile
            } catch (Exception e){
                log.error "The PiwikTrackingTagLib could not find the piwikFile: "+piwikFile, e
                trackingCodeCache = ""
            }
        }

        out << trackingCodeCache
    }
}