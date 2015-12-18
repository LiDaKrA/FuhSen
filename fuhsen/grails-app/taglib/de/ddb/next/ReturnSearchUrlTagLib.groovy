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
package de.ddb.next

class ReturnSearchUrlTagLib {
    static namespace = "ddb"

    /**
     * Used to return the correct action-URL in the search form in the header of the page.
     * Default URL is the search
     * Other options are search for institution & search for persons
     * Has a drawback that the controller/actions are hard-coded in the code below. Changes to the UrlMappings.groovy will break functionality
     */
    def getSearchUrl={attrs, body ->
        def url =g.createLink(controller:'search', action:'loadingResults')
        //if ((attrs.controllerName=="search" && attrs.actionName=="institution") ||(attrs.controllerName=="entity" && attrs.actionName=="personsearch")) {
        //    url=g.createLink(controller:attrs.controllerName, action:attrs.actionName)
        //} 
        out << url.toString()
    }
}
