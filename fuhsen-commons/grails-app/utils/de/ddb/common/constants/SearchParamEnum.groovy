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
package de.ddb.common.constants

public enum SearchParamEnum {


    ROWS("rows"),
    OFFSET("offset"),
    SORT_RELEVANCE("RELEVANCE"),
    SORT_ALPHA_ASC("ALPHA_ASC"),
    SORT_ALPHA_DESC("ALPHA_DESC"),
    SORT_TIME_ASC("time_asc"),
    SORT_TIME_DESC("time_desc"),
    SORT_RANDOM("random"),
    SORT("sort"),
    SORT_FIELD("sort_field"),
    ORDER("order"),
    BY("by"),
    QUERY("query"),
	NUMBER_RESULTS("numResults"),
	REQ_TYPE("reqType"),	
    VIEWTYPE_LIST("list"),
    VIEWTYPE_GRID("grid"),
    VIEWTYPE("viewType"),
    CLUSTERED("clustered"),
    IS_THUMBNAILS_FILTERED("isThumbnailFiltered"),
    FACETVALUES("facetValues[]"),
    FACET("facet"),
    FACETS("facets[]"),
    FILTERVALUES("filterValues"),
    FIRSTHIT("firstHit"),
    LASTHIT("lastHit"),
    KEEPFILTERS("keepFilters"),
    NORMDATA("normdata"),
    CALLBACK("callback"),
    MINDOCS("minDocs"),
    ENTITY_ID("entityid"),
    ID("id"),
    RESETFILTERS("resetFilters"),

    private String name

    private SearchParamEnum(String name) {
        this.name = name
    }

    public String getName() {
        return name
    }
}
