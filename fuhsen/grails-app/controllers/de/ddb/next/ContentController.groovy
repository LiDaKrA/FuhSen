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

import de.ddb.common.ApiConsumer
import de.ddb.common.exception.ItemNotFoundException

class ContentController {
    static defaultAction = "staticcontent"

    def configurationService
    def languageService

    def staticcontent() {
        try {
            def browserUrl = request.forwardURI.substring(request.contextPath.size())
            def location = params.dir ? params.dir : browserUrl.substring("/content".length())
            while (location.endsWith("/")) {
                location = location.substring(0, location.length() - 1)
            }

            // If first level dir is missing use the default context dir.
            if (!location) {
                redirect uri: new File(browserUrl, configurationService.getDefaultStaticPage()).toString() + "/"
                return
            }

            def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(request)).getLanguage()

            // Load the the file from $locale/$location.html.
            // If not found then load it from $locale/$location/index.html.
            def response
            try {
                response = retrieveFile(browserUrl, location, locale)
            }
            catch (ItemNotFoundException e) {
                // try default locale
                def defaultLocale = configurationService.getDefaultLanguage().getLanguage()

                if (defaultLocale != locale) {
                    response = retrieveFile(browserUrl, location, defaultLocale)
                }
                else {
                    throw new ItemNotFoundException()
                }
            }
            catch (RedirectException e) {
                redirect uri: e.uri
                return
            }

            def map = retrieveArguments(response)

            // Needed for the canonicalURL
            map << ["location": location, domainCanonic:configurationService.getDomainCanonic()]

            render(view: "staticcontent", model: map)
        }
        catch (ItemNotFoundException e) {
            forward controller: "error", action: "itemNotFound"
        }
    }

    private def retrieveArguments(def content){
        def title = fetchTitle(content)
        def author = fetchAuthor(content)
        def keywords = fetchKeywords(content)
        def robot = fetchRobots(content)
        def metaDescription = fetchMetaDescription(content)
        def body = fetchBody(content)
        return [
            title:title,
            author:author,
            keywords:keywords,
            robot:robot,
            metaDescription:metaDescription,
            content:body
        ]
    }

    /**
     * Retrieve a static file from the web server.
     *
     * First try $locale/$location.html and then $locale/$location/index.html.
     *
     * @param browserUrl request URL without context path
     * @param location file location
     * @param locale locale
     *
     * @return HTML content
     */
    private def retrieveFile(String browserUrl, String location, String locale) {
        def result
        def url = configurationService.getStaticUrl()
        def path = locale + "/" + location + ".html"
        def apiResponse = ApiConsumer.getText(url, path, false)
        if (apiResponse.isOk()) {
            result = apiResponse.getResponse()
        }
        else {
            if (!browserUrl.endsWith("/")) {
                throw new RedirectException("/" + params.controller + "/" + location + "/")
            }
            path = locale + "/" + location + "/index.html"
            apiResponse = ApiConsumer.getText(url, path, false)
            if (!apiResponse.isOk()) {
                throw new ItemNotFoundException()
            }
            result = apiResponse.getResponse()
        }
        return result
    }

    private def fetchBody(content) {
        def bodyMatch = content =~ /(?s)<body\b[^>]*>(.*?)<\/body>/
        return bodyMatch[0][1]
    }

    private def fetchAuthor(content) {
        def authorMatch = content =~ /(?s)<meta (.*?)name="author" (.*?)content="(.*?)"(.*?)\/>/
        if (authorMatch)
            return authorMatch[0][3]
    }

    private def fetchTitle(content) {
        def titleMatch = content =~ /(?s)<title\b[^>]*>(.*?)<\/title>/
        if (titleMatch)
            return titleMatch[0][1]
    }

    private def fetchKeywords(content) {
        def keywordMatch = content =~ /(?s)<meta (.*?)name="keywords" (.*?)content="(.*?)"(.*?)\/>/
        if (keywordMatch)
            return keywordMatch[0][3]
    }

    private def fetchMetaDescription(content) {
        def keywordMatch = content =~ /(?s)<meta (.*?)name="description" (.*?)content="(.*?)"(.*?)\/>/
        if (keywordMatch)
            return keywordMatch[0][3]
    }

    private def fetchRobots(content) {
        def robotMatch = content =~ /(?s)<meta (.*?)name="robots" (.*?)content="(.*?)"(.*?)\/>/
        if (robotMatch)
            return robotMatch[0][3]
    }

    static class RedirectException extends Exception {
        private String uri

        public RedirectException(String uri) {
            this.uri = uri
        }
    }
}