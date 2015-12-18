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
import org.springframework.web.servlet.support.RequestContextUtils as RCU

import de.ddb.common.ApiConsumer

class IndexController {
	
		def configurationService
		def itemService
	
		def index() {
			
			render(view: "index")
			
		}
}

/*
class IndexController {

    def configurationService
    def itemService

    def index() {
        def path

        def staticUrl = configurationService.getStaticUrl()
        def locale = RCU.getLocale(request)

        // fetch the DDB news from static server.
        if(locale.toString().substring(0, 2)== "de") {
            path = "de/homepage.xml"
        } else {
            path = "en/homepage.xml"
        }

        def query = [ client: "DDB-NEXT" ]
        // Submit a request via GET
        def apiResponse = ApiConsumer.getText(staticUrl, path, false, query)
        if(!apiResponse.isOk()){
            log.error "text: Text file was not found"
            apiResponse.throwException(request)
        }
        def response = apiResponse.getResponse()

        def articles = retrieveArguments(response)
        render(view: "index", model: [articles: articles, stats: itemService.getNumberOfItems()])
    }

    private def retrieveArguments(def content){
        def title = fetchTitle(content)
        def uri = fetchUri(content)
        def src = fetchSrc(content)
        def articles = new ArrayList()
        def article
        if (title != null) {
            for(def i=0; i<title.size() ; i++) {
                article = [title:title[i][1], uri:uri[i][1], src:src[i][1]]
                articles.add(article)
            }
        }
        return articles
    }

    private def fetchTitle(content) {
        def titleMatch = content =~ /(?s)<caption>(.*?)<\/caption>/
        if (titleMatch)
            return titleMatch
    }

    private def fetchUri(content) {
        def uriMatch = content =~ /(?s)<uri>(.*?)<\/uri>/
        if (uriMatch)
            return uriMatch
    }

    private def fetchSrc(content) {
        def imageUriMatch = content =~ /(?s)<imageUri>(.*?)<\/imageUri>/
        if (imageUriMatch)
            return imageUriMatch
    }
}
*/