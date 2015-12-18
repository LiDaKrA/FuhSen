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

class IsCurrentLanguageTagLib {

    static namespace = "ddb"
    def languageService

    /**
     * Checks if the given "locale" attribute matches the currently set locale of the I18N. If it matches, the body is rendered,
     * otherwise not.
     */
    def isCurrentLanguage = {attrs, body ->
        def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request))
        def checkLocale = languageService.getBestMatchingLocale(attrs.locale)

        if(locale.getLanguage() == checkLocale.getLanguage()){
            out << body()
        }else{
            out << ""
        }
    }
}
