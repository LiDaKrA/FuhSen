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


class GetCurrentUrlTagLib {

    static namespace = "ddbcommon"

    def getCurrentUrl = { attrs, body  ->
        def helper = new org.springframework.web.util.UrlPathHelper()

        def reqURI = helper.getOriginatingRequestUri(request)
        def qryStr = helper.getOriginatingQueryString(request)
        def urlString = reqURI

        if (qryStr) {
            urlString += "?" + qryStr
        }

        out << urlString
    }
}
