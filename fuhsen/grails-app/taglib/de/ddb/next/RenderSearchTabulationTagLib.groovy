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

class RenderSearchTabulationTagLib {

    static namespace = "ddb"

    /**
     * Renders the tabulation row for the search.
     *
     * @attr allCount, entitiesCount, placesCount, institutionCount, the count of every different search result
     */

    def renderSearchTabulation = { attrs, body ->
        out << render(template:"/search/searchTabulation",
        model:[totalResults: attrs.totalResults, query: attrs.query, active: attrs.active, rows: attrs.rows])
    }
}
