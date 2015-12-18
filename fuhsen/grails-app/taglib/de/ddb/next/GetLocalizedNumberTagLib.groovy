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

import org.springframework.web.servlet.support.RequestContextUtils

class GetLocalizedNumberTagLib {

    static namespace = "ddb"
    def languageService

    /**
     * Gives you back a localized representation of the number
     */
    def getLocalizedNumber = { attrs, body ->

        def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))

        def inputString = body()
        def outputString = ""
        if(inputString){
            try{
                outputString = String.format(locale, "%,d", inputString.toInteger())
            }catch(NumberFormatException ex){
                outputString = inputString
            }
        }
        out << outputString
    }
}
