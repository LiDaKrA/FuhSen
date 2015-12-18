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

class GetLanguageLinkTagLib {

    static namespace = "ddb"
    def languageService


    /**
     * Renders a language switching link dependend on the current url params, the given locale and the internationalized name.
     */
    def getLanguageLink = {attrs, body ->
        def checkLocaleString = attrs.locale
        def localeclass = attrs.islocaleclass
        def locale = RequestContextUtils.getLocale(request)
        if(!locale){
            locale = languageService.getDefaultLocale()
        }

        boolean isLocale = false

        if(checkLocaleString && locale){
            def localeLanguage = locale.getLanguage()
            if(localeLanguage.equalsIgnoreCase(checkLocaleString)){
                isLocale = true
            }
        }

        if(isLocale){
            out << "<a class=\""+localeclass+"\">"+ddb.getCurrentLanguage(attrs)+"</a>"
        }else{
            fixParams(attrs)
            def linkUrl = createLink("url": attrs.params)
            def cleanedParams = attrs.params.clone()
            def directory = attrs.params?.dir
            if(!directory) {
                directory = ""
            }
            if(linkUrl.contains("staticcontent")){
                linkUrl = linkUrl.replaceAll("staticcontent", directory)
                cleanedParams.remove("dir")
            }
            cleanedParams.remove("passwd")
            cleanedParams.remove("conpasswd")
            cleanedParams.remove("oldpassword")
            cleanedParams.remove("newpassword")
            cleanedParams.remove("confnewpassword")
            cleanedParams.remove("controller")
            cleanedParams.remove("action")
            cleanedParams.remove("id")
            cleanedParams.put("lang", checkLocaleString)
            def paramString = "?"
            cleanedParams.each {
                if (it.value instanceof String[]) {
                    it.value.each { a ->
                        //The param key "facetValues[]" has values like this: "type_fct=mediatype_003". So we have to encode the key and value for the URL
                        paramString += it.key.encodeAsURL() + "=" + a.encodeAsURL() + "&"
                    }
                }
                else {
                    //The param key "facetValues[]" has values like this: "type_fct=mediatype_003". So we have to encode the key and value for the URL
                    paramString += it.key.encodeAsURL() + "=" + it.value.encodeAsURL() + "&"
                }
            }
            if(paramString.length() > 1){
                paramString = paramString.substring(0, paramString.length()-1)
            }
            out << "<a href=\""+(linkUrl+paramString).encodeAsHTML()+"\" >"+body()+"</a>"
        }
    }

    /**
     * Workaround for http://jira.grails.org/browse/GRAILS-9742
     */
    private def fixParams(attrs) {
        try {
            if (attrs?.params?.action) {
                attrs.params.action = attrs.params.action.GET
            }
        } catch (MissingPropertyException e) {
        }
    }
}
