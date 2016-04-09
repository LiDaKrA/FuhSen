
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

import java.util.regex.Pattern

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder

import de.ddb.common.constants.CategoryFacetEnum
import de.ddb.common.constants.CortexConstants
import de.ddb.common.constants.EntityFacetEnum
import de.ddb.common.constants.FacetEnum
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.constants.Type

/**
 * Set of services used in the SearchController for views/search
 *
 * @author ema
 */
class SearchService {
    //Name of search-cookie
    private static final String SEARCH_COOKIE_NAME = "searchParameters"

    //CharacterEncoding of query-String
    private static final String CHARACTER_ENCODING = "UTF-8"

    private static final int MAX_PAGE_NUMBER_TO_SHOW = 5

    public static final def THUMBNAIL_FACET = "digitalisat"

    def grailsApplication
    def configurationService
    def itemService
    def paginationService

    def transactional = false

    def getFacets(Map reqParameters, Map urlQuery, String key, int currentDepth){
        List facetValues = []
        def facets = urlQuery
        facets[SearchParamEnum.FACET.getName()] = []
        if(reqParameters.get(SearchParamEnum.FACETVALUES.getName()).getClass().isArray()){
            reqParameters.get(SearchParamEnum.FACETVALUES.getName()).each{ facetValues.add(it) }
        }else{
            facetValues.add(reqParameters.get(SearchParamEnum.FACETVALUES.getName()).toString())
        }
        facetValues.each {
            def tmpVal = java.net.URLDecoder.decode(it.toString(), "UTF-8")
            List tmpSubVal = tmpVal.split("=")

            //In some exceptions there are facets values that in its name there is a = or more
            //For example: tmpVal = "fct_name=factValue=MoreFacetValue"
            //Then we can't split for =
            //We have to separate the values in another way, like
            //"fct_name=factValue=MoreFacetValue".minus('fct_name' + =)
            //And we get the right result "factValue=MoreFacetValue"
            def tmpSubFacetVal = tmpVal.minus(tmpSubVal[0] + '=')

            if(!facets[SearchParamEnum.FACET.getName()].contains(tmpSubVal[0]))
                facets[SearchParamEnum.FACET.getName()].add(tmpSubVal[0].toString())
            if(!facets[tmpSubVal[0]]){
                facets[tmpSubVal[0]]=[tmpSubFacetVal]
            }else {
                facets[tmpSubVal[0]].add(tmpSubFacetVal)
            }
        }
        return facets
    }

    /**
     * This method converts the "facetValues[]" parameter of a request to an url encoded query String
     *
     * @param reqParameters the requestParameter
     * @return the url encoded query String for facetValues parameter
     */
    def facetValuesToUrlQueryString(GrailsParameterMap reqParameters){
        def res = ""
        def facetValues = facetValuesRequestParameterToList(reqParameters)

        if(facetValues != null){
            res = facetValuesToUrlQueryString(facetValues)
        }

        return res
    }

    /**
     * This method converts a list of facetValues to an url encoded query String
     *
     * @param facetValues the List of facetValues
     * @return the url encoded query String for facetValues parameter
     */
    def facetValuesToUrlQueryString(List facetValues){
        def res = ""

        facetValues.each{
            res += "&facetValues%5B%5D="+it.encodeAsURL()
        }

        return res
    }

    /**
     * This methods get all "facetValues[]" parameter from the request and returns them as a list
     *
     * @param reqParameters the request parameter map
     * @return a list with all "facetValues[]" parameter
     */
    def facetValuesRequestParameterToList(GrailsParameterMap reqParameters) {
        def urlFacetValues = []
        def requestParamValues = reqParameters.get(SearchParamEnum.FACETVALUES.getName())

        if (requestParamValues != null){
            //The facetValues request parameter could be of type Array or String
            if(requestParamValues.getClass().isArray()){
                urlFacetValues = requestParamValues as List
            }else{
                urlFacetValues.add(requestParamValues)
            }
        }

        return urlFacetValues
    }

    /**
     * Creates the urls for the main facets of the non js version of the facet search
     *
     *
     * @param reqParameters the request parameter
     * @param urlQuery the urlQuery object
     * @param requestObject the request object
     *
     * @return a map containing the facet name as key and the url as value
     */
    def buildMainFacetsUrl(GrailsParameterMap reqParameters, LinkedHashMap urlQuery, HttpServletRequest requestObject, def facetsList){
        def mainFacetsUrls = [:]

        facetsList.each {
            def searchQuery = (urlQuery[SearchParamEnum.QUERY.getName()]) ? urlQuery[SearchParamEnum.QUERY.getName()] : ""

            //remove the main facet from the URL (the main facet is selected in this request)
            if(urlQuery[SearchParamEnum.FACET.getName()] && urlQuery[SearchParamEnum.FACET.getName()].contains(it)){
                mainFacetsUrls.put(it,requestObject.forwardURI+'?'+SearchParamEnum.QUERY.getName()+'='+searchQuery+"&"+SearchParamEnum.OFFSET.getName()+"=0&"+SearchParamEnum.ROWS.getName()+"="+urlQuery[SearchParamEnum.ROWS.getName()]+facetValuesToUrlQueryString(reqParameters))
            }
            //add the main facet from the URL (the main facet is deselected in this request)
            else{
                mainFacetsUrls.put(it,requestObject.forwardURI+'?'+SearchParamEnum.QUERY.getName()+'='+searchQuery+"&"+SearchParamEnum.OFFSET.getName()+"=0&"+SearchParamEnum.ROWS.getName()+"="+urlQuery[SearchParamEnum.ROWS.getName()]+"&facets%5B%5D="+it+facetValuesToUrlQueryString(reqParameters))
            }
        }

        return mainFacetsUrls
    }

    /**
     * Creates the urls for the sub facets of the non JS version of the facet search
     *
     * @param reqParameters the request parameter
     * @param facets the list of available facets for this search request
     * @param mainFacetsUrl the urls of the main facets
     * @param urlQuery the urlQuery
     * @param requestObject the request object from the controller
     *
     * @return a map containing the main facet name as key and a map as value (containing all subfacets storing the name, count and url)
     */
    def buildSubFacetsUrl(GrailsParameterMap reqParameters, List facets, LinkedHashMap mainFacetsUrl, LinkedHashMap urlQuery, HttpServletRequest requestObject){
        def searchQuery = (urlQuery[SearchParamEnum.QUERY.getName()]) ? urlQuery[SearchParamEnum.QUERY.getName()] : ""

        def res = [:]
        urlQuery[SearchParamEnum.FACET.getName()].each{
            if(it!=THUMBNAIL_FACET){
                facets.each { x->
                    if(x.field == it && x.numberOfFacets>0){
                        res[x.field] = []
                        x.facetValues.each{ y->
                            //only proceed if the facetValue is of type main facet.
                            if (mainFacetsUrl[x.field] != null) {

                                //Create a map which contains the facet name, count and url for the view
                                def tmpFacetValuesMap = ["fctValue": y["value"].encodeAsHTML(),"url":"",cnt: y["count"],selected:""]

                                //Convert the facetValues[] parameter of the request from an array/string to a list. List entries can be changed (add/remove)!
                                def urlFacetValues = facetValuesRequestParameterToList(reqParameters)

                                //The current facetValue in the target request parameter form
                                def facetValueParameter = x.field+"="+y["value"]

                                if(urlFacetValues.contains(facetValueParameter)){
                                    //remove the facetValueParameter from the urlFacetValues (the facet was selected in this request)
                                    urlFacetValues.remove(facetValueParameter)
                                    def url = requestObject.forwardURI+'?'+SearchParamEnum.QUERY.getName()+'='+searchQuery+"&"+SearchParamEnum.OFFSET.getName()+"=0&"+SearchParamEnum.ROWS.getName()+"="+urlQuery[SearchParamEnum.ROWS.getName()]+"&facets%5B%5D="+x.field+facetValuesToUrlQueryString(urlFacetValues)

                                    tmpFacetValuesMap["url"] = url
                                    tmpFacetValuesMap["selected"] = "selected"
                                }
                                else{
                                    //add the facetValueParameter to the urlFacetValues (the facet was deselected in this request)
                                    urlFacetValues.add(facetValueParameter)

                                    def url = requestObject.forwardURI+'?'+SearchParamEnum.QUERY.getName()+'='+searchQuery+"&"+SearchParamEnum.OFFSET.getName()+"=0&"+SearchParamEnum.ROWS.getName()+"="+urlQuery[SearchParamEnum.ROWS.getName()]+"&facets%5B%5D="+x.field+facetValuesToUrlQueryString(urlFacetValues)
                                    tmpFacetValuesMap["url"] = url
                                }

                                res[x.field].add(tmpFacetValuesMap)
                            }
                        }
                    }
                }
            }
        }
        return res
    }

    /**
     *
     * Build the list of facets to be rendered in the non javascript version of search results
     *
     * @param urlQuery the urlQuery
     * @return list of all facets filtered
     */
    def buildSubFacets(LinkedHashMap urlQuery, def facetsList){
        def emptyFacets = facetsList.clone()
        def res = []
        //We want only the first 10 facets
        urlQuery["facet.limit"] = 10

        List selectedFacets = []

        if (urlQuery[SearchParamEnum.FACET.getName()] instanceof List<?>) {
            selectedFacets = urlQuery[SearchParamEnum.FACET.getName()]
        } else if (urlQuery[SearchParamEnum.FACET.getName()] instanceof String) {
            selectedFacets.add(urlQuery[SearchParamEnum.FACET.getName()])
        }

        selectedFacets.each{
            if(it != THUMBNAIL_FACET){
                emptyFacets.remove(it)
                def tmpUrlQuery = urlQuery.clone()
                tmpUrlQuery[SearchParamEnum.ROWS.getName()]=1
                tmpUrlQuery[SearchParamEnum.OFFSET.getName()]=0
                tmpUrlQuery.remove(it)
                def apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, tmpUrlQuery)
                if(!apiResponse.isOk()){
                    log.error "Json: Json file was not found"
                    apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                }
                def jsonResp = apiResponse.getResponse()
                jsonResp.facets.each{ facet->
                    if(facet.field==it){
                        res.add(facet)
                    }
                }
            }
        }
        //fill the remaining empty facets
        emptyFacets.each{
            res.add([field: it, numberOfFacets: 0, facetValues: []])
        }
        return res
    }

    def buildPagination(int resultsNumber, LinkedHashMap queryParameters, String getQuery) {
        def res = [firstPg:null,lastPg:null,prevPg:null,nextPg:null,pages:[]]
        //if resultsNumber greater rows number no buttons else we can start to create the pagination
        def currentRows = queryParameters[SearchParamEnum.ROWS.getName()].toInteger()
        def currentOffset = queryParameters[SearchParamEnum.OFFSET.getName()].toInteger()
        if (!getQuery.contains(SearchParamEnum.ROWS.getName())) {
            getQuery += "&" + SearchParamEnum.ROWS.getName() + "=" + currentRows
        }
        if(resultsNumber>currentRows){
            //We are not at the first page
            if(currentOffset>0){
                def offsetPrev = currentOffset - currentRows
                def offsetFirst = 0

                res["firstPg"]= setOffset(getQuery, currentOffset, offsetFirst)
                res["prevPg"]= setOffset(getQuery, currentOffset, offsetPrev)
            }
            //We are not at the last page
            if(currentOffset+currentRows<resultsNumber){
                def offsetNext = currentOffset + currentRows
                def offsetLast = ((Math.ceil(resultsNumber/currentRows)*currentRows)-currentRows).toInteger()

                res["lastPg"]= setOffset(getQuery, currentOffset, offsetLast)
                res["nextPg"]= setOffset(getQuery, currentOffset, offsetNext)
            }
        }

        //Calculate pagination numbers
        int currentPageNumber = (currentOffset/currentRows)+1
        List paginationNumbers = paginationService.getPagesNumbers(currentPageNumber, Math.ceil(resultsNumber/currentRows).toInteger(), MAX_PAGE_NUMBER_TO_SHOW)
        for(int i=0; i<paginationNumbers.size; i++){
            def tmpPageOffset = (paginationNumbers.get(i)*currentRows)-currentRows
            def tmpEntry = ["pageNumber": paginationNumbers.get(i), "url":setOffset(getQuery, currentOffset, tmpPageOffset),active:false]
            if(paginationNumbers.get(i) == currentPageNumber){
                tmpEntry["active"] = true
            }
            res["pages"].add(tmpEntry)
        }

        return res
    }

    def setOffset(String url, int oldOffserValue, int newOffsetValue){
        def result = ""
        if(url.contains(SearchParamEnum.OFFSET.getName())){
            result = url.replaceAll(SearchParamEnum.OFFSET.getName()+'='+oldOffserValue, SearchParamEnum.OFFSET.getName()+'='+newOffsetValue)
        }else{
            result = url+'&'+SearchParamEnum.OFFSET.getName()+'='+newOffsetValue
        }
        return result
    }

    def buildPaginatorOptions(LinkedHashMap queryMap){
        def pageFilter = [10, 20, 40, 60, 100]
        if(!pageFilter.contains(queryMap[SearchParamEnum.NUMBER_RESULTS.getName()].toInteger()))
            pageFilter.add(queryMap[SearchParamEnum.NUMBER_RESULTS.getName()].toInteger())
        return [pageFilter: pageFilter.sort(), pageFilterSelected: queryMap[SearchParamEnum.NUMBER_RESULTS.getName()].toInteger(), sortResultsSwitch: queryMap[SearchParamEnum.SORT.getName()]]
    }

    def buildClearFilter(LinkedHashMap urlQuery, String baseURI){
        def res = baseURI+'?'
        urlQuery.each{ key, value ->
            if(!key.toString().contains(SearchParamEnum.FACET.getName()) && !key.toString().contains(SearchParamEnum.FACETVALUES.getName()) && !key.toString().contains("fct")){
                res+='&'+key+'='+value
            }
        }

        res+='&clearFilter=true'
        return res
    }

    /**
     *
     * Gives you back the HTML title with "strong" attributes trimmed to desired length
     *
     * @param title
     * @param length
     * @return String title
     */
    def trimTitle(String title, int length){
        def matchesMatch = title =~ /(?m)<match>(.*?)<\/match>/
        def cleanTitle = title.replaceAll("<match>", "").replaceAll("</match>", "")
        def index = cleanTitle.length() > length ? cleanTitle.substring(0,length).lastIndexOf(" ") : -1
        def tmpTitle = index >= 0 ? cleanTitle.substring(0,index) + "..." : cleanTitle
        StringBuilder replacementsRegex = new StringBuilder("(")
        if(matchesMatch.size()>0){
            matchesMatch.each{
                if (replacementsRegex.size() > 1) {
                    replacementsRegex.append("|")
                }
                replacementsRegex.append(Pattern.quote(it[1]))
            }
            replacementsRegex.append(")")
            tmpTitle = tmpTitle.replaceAll(replacementsRegex.toString(), '<strong>$1</strong>')
        }
        return tmpTitle
    }

    /**
     *
     * Gives you back the string trimmed to desired length
     *
     * @param text
     * @param length
     * @return String text trimmed
     */
    def trimString(String text, int length){
        if(text.length()>length)
            return text.substring(0, text.substring(0,length).lastIndexOf(" "))+"..."
        return text
    }

    /**
     * Generate Map that can be used to call Search on Search-Server.
     *
     * For row and sort params the fallowing fallback has been implemented
     * 1) try to use the values from the request parameter -> 2) try to use the value in the cookie -> 3) set a default value
     *
     * @param reqParameters The request parameters
     * @param cookieValues The searchParameters cookie values
     *
     * @return Map with keys used for Search on Search-Server
     */
    def convertQueryParametersToSearchParameters(Map reqParameters, Map cookieValues = [:]) {
        def urlQuery = [:]
        def numbersRangeRegex = /^[0-9]+$/

        if (!urlQuery[SearchParamEnum.FACET.getName()]) {
            urlQuery[SearchParamEnum.FACET.getName()] = []
        }

        if (reqParameters[SearchParamEnum.QUERY.getName()]) {
            urlQuery[SearchParamEnum.QUERY.getName()] = getMapElementOfUnsureType(reqParameters, SearchParamEnum.QUERY.getName(), "*")
        }else{
            urlQuery[SearchParamEnum.QUERY.getName()] = "*"
        }

		urlQuery[SearchParamEnum.NUMBER_RESULTS.getName()] = 10.toInteger()
		reqParameters[SearchParamEnum.NUMBER_RESULTS.getName()] = urlQuery[SearchParamEnum.NUMBER_RESULTS.getName()]
		
//		//VWPATENT-29 - Scroll Funktion
//		//ROWS should not be read neither from cookies nor from request parameters
//		//But only from a configuration parameter (which was never the case)
//		urlQuery[SearchParamEnum.ROWS.getName()] = 20.toInteger()
//        //if (reqParameters[SearchParamEnum.ROWS.getName()] == null || !(reqParameters[SearchParamEnum.ROWS.getName()]=~ numbersRangeRegex)) {
//        //    if (cookieValues.containsKey(SearchParamEnum.ROWS.getName())) {
//        //        urlQuery[SearchParamEnum.ROWS.getName()] = cookieValues[SearchParamEnum.ROWS.getName()]
//        //    } else {
//        //        urlQuery[SearchParamEnum.ROWS.getName()] = 20.toInteger()
//        //    }
//        //} else {
//        //    urlQuery[SearchParamEnum.ROWS.getName()] = getMapElementOfUnsureType(reqParameters, SearchParamEnum.ROWS.getName(), "20").toInteger()
//        //}
//        reqParameters[SearchParamEnum.ROWS.getName()] = urlQuery[SearchParamEnum.ROWS.getName()]

		//VWPATENT-29 - Scroll Funktion
		//OFFSET is only modified when request type is AJAX
		if (reqParameters[SearchParamEnum.REQ_TYPE.getName()] != null && reqParameters[SearchParamEnum.REQ_TYPE.getName()] == "ajax" ) {
			if (reqParameters[SearchParamEnum.OFFSET.getName()] == null || !(reqParameters[SearchParamEnum.OFFSET.getName()]=~ numbersRangeRegex)) {
				urlQuery[SearchParamEnum.OFFSET.getName()] = 0.toInteger()
			} else {
				urlQuery[SearchParamEnum.OFFSET.getName()] = getMapElementOfUnsureType(reqParameters, SearchParamEnum.OFFSET.getName(), "0").toInteger()
			}
		}
		else
			urlQuery[SearchParamEnum.OFFSET.getName()] = 0.toInteger()
		
		reqParameters[SearchParamEnum.OFFSET.getName()] = urlQuery[SearchParamEnum.OFFSET.getName()]
			
			
//        //<--input query=rom&facetValues%5B%5D=begin_time%3D%5B*+TO+365249%5D&facetValues%5B%5D=end_time%3D%5B0+TO+*%5D&facetValues%5B%5D=place_fct%3DItalien
//        //-->output query=rom&facet=begin_time&facet=end_time&facet=place_fct&place_fct=Italien&begin_time=%5B*+TO+365249%5D&end_time=%5B0+TO+*%5D
//        if(reqParameters[SearchParamEnum.FACETVALUES.getName()]){
//            urlQuery = this.getFacets(reqParameters, urlQuery,SearchParamEnum.FACET.getName(), 0)
//        }
//
//        if(reqParameters.get(SearchParamEnum.FACETS.getName())){
//            if(!urlQuery[SearchParamEnum.FACET.getName()].contains(reqParameters.get(SearchParamEnum.FACETS.getName())))
//                urlQuery[SearchParamEnum.FACET.getName()].add(reqParameters.get(SearchParamEnum.FACETS.getName()))
//        }
//
//        if(reqParameters[SearchParamEnum.MINDOCS.getName()]) {
//            urlQuery[SearchParamEnum.MINDOCS.getName()] = getMapElementOfUnsureType(reqParameters, SearchParamEnum.MINDOCS.getName(), "")
//        }

        /* Setting the sort parameter is implemented by a fallback: First check for sort parameter in the request parameter than in the cookie */

//        //1) Check if sort is available in the request parameters
//        if(reqParameters[SearchParamEnum.SORT.getName()] != null) {
//            //Check for sort type. Relevance can be ignored, because its used as default in the backend!
//            if (  (reqParameters[SearchParamEnum.SORT.getName()]=~ /^random_[0-9]+$/)
//            || reqParameters[SearchParamEnum.SORT.getName()]==SearchParamEnum.SORT_ALPHA_ASC.getName()
//            || reqParameters[SearchParamEnum.SORT.getName()]==SearchParamEnum.SORT_ALPHA_DESC.getName()
//            ){
//                urlQuery[SearchParamEnum.SORT.getName()] = getMapElementOfUnsureType(reqParameters, SearchParamEnum.SORT.getName(), "")
//            }
//            else if (reqParameters[SearchParamEnum.SORT.getName()]==SearchParamEnum.SORT_TIME_ASC.getName() ||
//            reqParameters[SearchParamEnum.SORT.getName()]==SearchParamEnum.SORT_TIME_DESC.getName()
//            ){
//                urlQuery[SearchParamEnum.SORT.getName()] = getMapElementOfUnsureType(reqParameters, SearchParamEnum.SORT.getName(), "")
//                urlQuery[SearchParamEnum.SORT_FIELD.getName()] = FacetEnum.BEGIN_TIME.getName()
//            }
//        }
//        //2) Check cookie for SORT_ALPHA_ASC, SORT_ALPHA_DESC, SORT_TIME_ASC and SORT_TIME_DESC
//        else if (cookieValues.containsKey(SearchParamEnum.SORT.getName())) {
//            if (   cookieValues[SearchParamEnum.SORT.getName()]==SearchParamEnum.SORT_ALPHA_ASC.getName()
//            || cookieValues[SearchParamEnum.SORT.getName()]==SearchParamEnum.SORT_ALPHA_DESC.getName())
//            {
//                urlQuery[SearchParamEnum.SORT.getName()] = cookieValues[SearchParamEnum.SORT.getName()]
//            }
//            else if (cookieValues[SearchParamEnum.SORT.getName()]==SearchParamEnum.SORT_TIME_ASC.getName()
//            || cookieValues[SearchParamEnum.SORT.getName()]==SearchParamEnum.SORT_TIME_DESC.getName())
//            {
//                urlQuery[SearchParamEnum.SORT.getName()] = cookieValues[SearchParamEnum.SORT.getName()]
//                urlQuery[SearchParamEnum.SORT_FIELD.getName()] = FacetEnum.BEGIN_TIME.getName()
//            }
//        }
//
//        //3) Set selected sort value as NEW request parameter. This is important to write the new search cookie
//        if (urlQuery[SearchParamEnum.SORT.getName()]) {
//            reqParameters[SearchParamEnum.SORT.getName()] = urlQuery[SearchParamEnum.SORT.getName()]
//        }
//
//
//        if(reqParameters[SearchParamEnum.VIEWTYPE.getName()] == null || (!(reqParameters[SearchParamEnum.VIEWTYPE.getName()]=~ /^list$/) && !(reqParameters[SearchParamEnum.VIEWTYPE.getName()]=~ /^grid$/))) {
//            urlQuery[SearchParamEnum.VIEWTYPE.getName()] = SearchParamEnum.VIEWTYPE_LIST.getName()
//            reqParameters[SearchParamEnum.VIEWTYPE.getName()] = SearchParamEnum.VIEWTYPE_LIST.getName()
//        } else {
//            urlQuery[SearchParamEnum.VIEWTYPE.getName()] = getMapElementOfUnsureType(reqParameters, SearchParamEnum.VIEWTYPE.getName(), "")
//        }
//
//        def isThumbnailFiltered = reqParameters[SearchParamEnum.IS_THUMBNAILS_FILTERED.getName()]
//        if (!isThumbnailFiltered) {
//            isThumbnailFiltered = cookieValues[SearchParamEnum.IS_THUMBNAILS_FILTERED.getName()]
//        }
//        if (isThumbnailFiltered == "false") {
//            urlQuery[SearchParamEnum.FACET.getName()]?.remove(THUMBNAIL_FACET)
//            urlQuery.remove(THUMBNAIL_FACET)
//        }
//		
//        else if (reqParameters["controller"] == "search" && reqParameters["action"] == "results") {
//            urlQuery[SearchParamEnum.FACET.getName()].add(THUMBNAIL_FACET)
//            urlQuery[THUMBNAIL_FACET] = "true"
//        }

        return urlQuery
    }

    /**
     * Generate Map that can be used to call Autocomplete and Search Facets on Search-Server
     *
     * @param reqParameters
     * @return Map with keys used for Search on Search-Server
     */
    def convertQueryParametersToSearchFacetsParameters(Map reqParameters) {
        def urlQuery = [:]

        if (reqParameters["searchQuery"]!=null && reqParameters["searchQuery"].length()>0){
            urlQuery["searchQuery"] = getMapElementOfUnsureType(reqParameters, "searchQuery", "*")
        }else{
            urlQuery["searchQuery"] = "*"
        }

        if (reqParameters[SearchParamEnum.QUERY.getName()]!=null && reqParameters[SearchParamEnum.QUERY.getName()].length()>0){
            urlQuery[SearchParamEnum.QUERY.getName()] = getMapElementOfUnsureType(reqParameters, SearchParamEnum.QUERY.getName(), "*")
        }else{
            urlQuery[SearchParamEnum.QUERY.getName()] = "*"
        }

        //<--input query=rom&facetValues%5B%5D=begin_time%3D%5B*+TO+365249%5D&facetValues%5B%5D=end_time%3D%5B0+TO+*%5D&facetValues%5B%5D=place_fct%3DItalien
        //-->output query=rom&facet=begin_time&facet=end_time&facet=place_fct&place_fct=Italien&begin_time=%5B*+TO+365249%5D&end_time=%5B0+TO+*%5D        if(reqParameters[SearchParamEnum.FACETVALUES.getName()]){
        if(reqParameters[SearchParamEnum.FACETVALUES.getName()]){
            urlQuery = this.getFacets(reqParameters, urlQuery,SearchParamEnum.FACET.getName(), 0)
        }

        if(reqParameters.get(SearchParamEnum.FACETS.getName())){
            urlQuery[SearchParamEnum.FACET.getName()] = (!urlQuery[SearchParamEnum.FACET.getName()])?[]:urlQuery[SearchParamEnum.FACET.getName()]
            if(!urlQuery[SearchParamEnum.FACET.getName()].contains(reqParameters.get(SearchParamEnum.FACETS.getName())))
                urlQuery[SearchParamEnum.FACET.getName()].add(reqParameters.get(SearchParamEnum.FACETS.getName()))
        }

        if(reqParameters["sortDesc"] != null && ((reqParameters["sortDesc"]== "true") || (reqParameters["sortDesc"]== "false"))){
            urlQuery["sortDesc"] = getMapElementOfUnsureType(reqParameters, "sortDesc", "")
        }

        if (reqParameters[SearchParamEnum.IS_THUMBNAILS_FILTERED.getName()] == "false") {
            urlQuery[SearchParamEnum.FACET.getName()]?.remove(THUMBNAIL_FACET)
            urlQuery.remove(THUMBNAIL_FACET)
        }
        else {
            def facetValues = reqParameters[SearchParamEnum.FACETVALUES.getName()]

            if (facetValues == "category=" + CategoryFacetEnum.DWPI.getName()) {
                if (!urlQuery[SearchParamEnum.FACET.getName()]) {
                    urlQuery[SearchParamEnum.FACET.getName()] = []
                }
                urlQuery[SearchParamEnum.FACET.getName()].add(THUMBNAIL_FACET)
                urlQuery[THUMBNAIL_FACET] = "true"
            }
        }

        //We ask for a maximum of 301 facets
        urlQuery["facet.limit"] = CortexConstants.MAX_FACET_SEARCH_RESULTS

        return urlQuery
    }

    /**
     * Utility-method to fix a groovy-inconvenience. Parameter map values can either be a single String or
     * an Array of Strings (e.g. if the parameter was defined twice in the URL). To handle this, get the
     * parameters over this method.
     * @param map The parameter map
     * @param elementName The map key
     * @param defaultValue The default value if no value was found for the key
     * @return The value or the defaultValue if no value was found
     */
    private String getMapElementOfUnsureType(map, elementName, defaultValue){
        if (map[elementName]?.class.isArray()){
            if(map[elementName].size() > 0){
                return map[elementName][0]
            } else {
                return defaultValue
            }
        }else{
            if(map[elementName]){
                return map[elementName]
            } else {
                return defaultValue
            }
        }

    }

    /**
     * Generate Map that contains GET-parameters used for search-request by ddb-next.
     *
     * @param reqParameters
     * @return Map with keys used for search-request by ddb-next.
     */
    def getSearchGetParameters(Map reqParameters) {
        def searchParams = [:]
        def requiredParams = [
            SearchParamEnum.QUERY.getName(),
            SearchParamEnum.OFFSET.getName(),
            SearchParamEnum.ROWS.getName(),
            SearchParamEnum.SORT.getName(),
            SearchParamEnum.VIEWTYPE.getName(),
            SearchParamEnum.CLUSTERED.getName(),
            SearchParamEnum.IS_THUMBNAILS_FILTERED.getName(),
            SearchParamEnum.FACETVALUES.getName(),
            SearchParamEnum.FACETS.getName()
        ]
        for (entry in reqParameters) {
            if (requiredParams.contains(entry.key)) {
                searchParams[entry.key] = entry.value
            }
        }
        return searchParams
    }

    /**
     * Generate Map that contains GET-parameters used for item-detail-request by ddb-next.
     *
     * @param reqParameters
     * @return Map with keys used for item-detail-request by ddb-next.
     */
    def getSearchCookieParameters(Map reqParameters) {
        def searchCookieParameters = [:]
        def requiredParams = [
            SearchParamEnum.QUERY.getName(),
            SearchParamEnum.OFFSET.getName(),
            SearchParamEnum.ROWS.getName(),
            SearchParamEnum.SORT.getName(),
            SearchParamEnum.VIEWTYPE.getName(),
            SearchParamEnum.CLUSTERED.getName(),
            SearchParamEnum.IS_THUMBNAILS_FILTERED.getName(),
            SearchParamEnum.FACETVALUES.getName(),
            SearchParamEnum.FACETS.getName(),
            SearchParamEnum.FIRSTHIT.getName(),
            SearchParamEnum.LASTHIT.getName(),
            SearchParamEnum.KEEPFILTERS.getName()
        ]
        for (entry in reqParameters) {
            if (requiredParams.contains(entry.key)) {
                searchCookieParameters[entry.key] = entry.value
            }
        }
        return searchCookieParameters
    }

    /**
     *
     * Used in FacetsController gives you back an array containing the following Map: {facet value, localized facet value, count results}
     *
     * @param facets list of facets fetched from the backend
     * @param fctName name of the facet field required
     * @param numberOfElements number of elements to return
     * @param matcher facetValues must match this string
     * @param locale for formating numbers
     * @param filterRoles indicates if the role values should be filtered from the list
     *
     * @return List of Map
     */
    def getSelectedFacetValues(net.sf.json.JSONObject facets, String fctName, int numberOfElements, String matcher, Locale locale, boolean filterRoles){
        def res = [type: fctName, values: []]
        def allFacetFilters = configurationService.getFacetsFilter()

        int max = (numberOfElements != -1 && facets.numberOfFacets>numberOfElements)?numberOfElements:facets.numberOfFacets
        for(int i=0;i<max;i++){


            if (filterRoles && facets.facetValues[i].value.toString() =~ /_\d+_/) {
                continue
            }

            //Check if facet value has to be filtered
            boolean filterFacet = false
            for(int k=0; k<allFacetFilters.size(); k++){
                if(fctName == allFacetFilters[k].facetName && facets.facetValues[i].value.toString() == allFacetFilters[k].filter){
                    filterFacet = true
                    break
                }
            }

            if(!filterFacet){
                if(matcher && facets.facetValues[i].value.toString().toLowerCase().contains(matcher.toLowerCase())){
                    def facetValue = facets.facetValues[i].value
                    def firstIndexMatcher = facetValue.toLowerCase().indexOf(matcher.toLowerCase())
                    facetValue = facetValue.substring(0, firstIndexMatcher)+"<strong>"+facetValue.substring(firstIndexMatcher,firstIndexMatcher+matcher.size())+"</strong>"+facetValue.substring(firstIndexMatcher+matcher.size(),facetValue.size())
                    res.values.add([value: facets.facetValues[i].value, localizedValue: facetValue, count: String.format(locale, "%,d", facets.facetValues[i].count.toInteger())])
                } else {
                    res.values.add([value: facets.facetValues[i].value, localizedValue: this.getI18nFacetValue(fctName, facets.facetValues[i].value.toString()), count: String.format(locale, "%,d", facets.facetValues[i].count.toInteger())])
                }
            }
        }

        return res
    }


    /**
     *
     * Used in FacetsController gives you back an array containing the following Map: {facet value, localized facet value, count results}
     *
     * @param facets list of facets fetched from the backend
     * @param fctName name of the facet field required
     * @param numberOfElements number of elements to return
     * @param matcher facetValues must match this string
     * @param locale for formating numbers
     *
     * @return List of Map
     */
    def getRolesForFacetValue(net.sf.json.JSONObject facets, String fctName, int numberOfElements, Locale locale){
        def res = [type: fctName, values: []]

        int max = (numberOfElements != -1 && facets.numberOfFacets>numberOfElements)?numberOfElements:facets.numberOfFacets
        for(int i=0;i<max;i++){
            def facetValue = facets.facetValues[i].value
            //Select only values that contains _1_, which indicates that they are a role
            if (facetValue =~ /_\d+_/) {
                res.values.add([value: facetValue, localizedValue: facetValue, count: String.format(locale, "%,d", facets.facetValues[i].count.toInteger())])
            }
        }
        return res
    }


    /**
     * Used in FacetsController gives you back an array containing the following Map: {facet value, localized facet value, count results}
     *
     * @param facets list of facets fetched from the backend
     * @param fctName name of the facet field required
     * @param numberOfElements number of elements to return
     * @return List of Map
     */
    def getSelectedFacetValuesFromOldApi(List facets, String fctName, int numberOfElements, String matcher, Locale locale){
        def res = [type: fctName, values: []]
        def allFacetFilters = configurationService.getFacetsFilter()

        facets.each{
            if(it.field==fctName){
                int max = (numberOfElements != -1 && it.facetValues.size()>numberOfElements)?numberOfElements:it.facetValues.size()
                for(int i=0;i<max;i++){
                    //Check if facet value has to be filtered
                    boolean filterFacet = false
                    for(int k=0; k<allFacetFilters.size(); k++){
                        if(fctName == allFacetFilters[k].facetName && it.facetValues[i].value.toString() == allFacetFilters[k].filter){
                            filterFacet = true
                            break
                        }
                    }

                    if(!filterFacet){
                        if(matcher && this.getI18nFacetValue(fctName, it.facetValues[i].value.toString()).toLowerCase().contains(matcher.toLowerCase())){
                            def localizedValue = this.getI18nFacetValue(fctName, it.facetValues[i].value.toString())
                            def firstIndexMatcher = localizedValue.toLowerCase().indexOf(matcher.toLowerCase())
                            localizedValue = localizedValue.substring(0, firstIndexMatcher)+"<strong>"+localizedValue.substring(firstIndexMatcher,firstIndexMatcher+matcher.size())+"</strong>"+localizedValue.substring(firstIndexMatcher+matcher.size(),localizedValue.size())
                            res.values.add([value: it.facetValues[i].value, localizedValue: localizedValue, count: String.format(locale, "%,d", it.facetValues[i].count.toInteger())])
                        }else if(!matcher)
                            res.values.add([value: it.facetValues[i].value, localizedValue: this.getI18nFacetValue(fctName, it.facetValues[i].value.toString()), count: String.format(locale, "%,d", it.facetValues[i].count.toInteger())])
                    }
                }
            }
        }
        return res
    }

    /**
     * Ensure the random seed is restored for the following frontend request.
     *
     * @param params request parameters from previous frontend call
     * @param urlQuery request parameters from previous backend call containing random seed
     * @return request parameters for following frontend call
     */
    def handleRandomSort(params, urlQuery) {
        def result = params ? params.clone() : [:]
        def sortType = result.get(SearchParamEnum.SORT.getName())

        if (!sortType?.startsWith(SearchParamEnum.SORT_RANDOM.getName()) && urlQuery["randomSeed"]) {
            result.put(SearchParamEnum.SORT.getName(), urlQuery["randomSeed"])
        }
        return result
    }

    /**
     *
     * Gives you back the passed facet value internationalized
     *
     * @param facetName
     * @param facetValue
     * @return String i18n facet value
     */
    def getI18nFacetValue(facetName, facetValue){
        def appCtx = grailsApplication.getMainContext()

        def res = ""

        try {
            if (facetName == EntityFacetEnum.PERSON_GENDER_FCT.getName()) {
                res = appCtx.getMessage(EntityFacetEnum.PERSON_GENDER_FCT.getI18nPrefix() + facetValue, null,
                        LocaleContextHolder.getLocale())
            }
            else {
                def enu = FacetEnum.valueOfName(facetName.toString())

                if (enu == FacetEnum.LICENSE) {
                    res = appCtx.getMessage(enu.getI18nPrefix() + itemService.convertUriToProperties(facetValue), null,
                            LocaleContextHolder.getLocale())
                }
                else if (enu?.isI18nFacet()) {
                    res = appCtx.getMessage(enu.getI18nPrefix() + facetValue, null, LocaleContextHolder.getLocale())
                }
                else {
                    res = facetValue
                }
            }
        } catch (NoSuchMessageException nsme) {
            log.error("Cannot localize facet value: " + facetValue + " " + nsme.message)
            res = facetValue
        }
        return res
    }

    /**
     * Converts the params list received from the frontend during a request to get all the facets to be displayed in the flyout widget.
     *
     * @param reqParameters the params variable containing all the req parameters
     * @return a map containing all the converted request parameters ready to be submitted to the related service to fetch the right facets values
     */
    def convertFacetQueryParametersToFacetSearchParameters(Map reqParameters) {
        def urlQuery = [:]

        //Search Query param
        if (reqParameters.searchQuery == null) {
            urlQuery[SearchParamEnum.QUERY.getName()] = '*'
        }
        else {
            urlQuery[SearchParamEnum.QUERY.getName()] = reqParameters.searchQuery
        }

        //Search row + offset
        if (reqParameters[SearchParamEnum.ROWS.getName()] == null || reqParameters[SearchParamEnum.ROWS.getName()] == -1) {
            urlQuery[SearchParamEnum.ROWS.getName()] = 1
        } else {
            urlQuery[SearchParamEnum.ROWS.getName()] = reqParameters[SearchParamEnum.ROWS.getName()]
        }

        if (reqParameters[SearchParamEnum.OFFSET.getName()] == null)
            urlQuery[SearchParamEnum.OFFSET.getName()] = 0
        else urlQuery[SearchParamEnum.OFFSET.getName()] = reqParameters[SearchParamEnum.OFFSET.getName()]

        //<--input query=rom&facetValues%5B%5D=begin_time%3D%5B*+TO+365249%5D&facetValues%5B%5D=end_time%3D%5B0+TO+*%5D&facetValues%5B%5D=place_fct%3DItalien
        //-->output query=rom&facet=begin_time&facet=end_time&facet=place_fct&place_fct=Italien&begin_time=%5B*+TO+365249%5D&end_time=%5B0+TO+*%5D            urlQuery = getFacets(reqParameters, urlQuery,SearchParamEnum.FACET.getName(), 0)
        if(reqParameters[SearchParamEnum.FACETVALUES.getName()]){
            urlQuery = getFacets(reqParameters, urlQuery,SearchParamEnum.FACET.getName(), 0)
        }

        //Facet name
        if(reqParameters.get("name")){
            urlQuery[SearchParamEnum.FACET.getName()] = (!urlQuery[SearchParamEnum.FACET.getName()])?[]:urlQuery[SearchParamEnum.FACET.getName()]
            if(!urlQuery[SearchParamEnum.FACET.getName()].contains(reqParameters.get("name")))
                urlQuery[SearchParamEnum.FACET.getName()].add(reqParameters.get("name"))
        }

        //Thumbnail filtered
        if (reqParameters[SearchParamEnum.IS_THUMBNAILS_FILTERED.getName()] == "false") {
            urlQuery[SearchParamEnum.FACET.getName()]?.remove(THUMBNAIL_FACET)
            urlQuery.remove(THUMBNAIL_FACET)
        }
        else {
            def facetValues = reqParameters[SearchParamEnum.FACETVALUES.getName()]

            if (facetValues == "category=" + CategoryFacetEnum.DWPI.getName()) {
                if (!urlQuery[SearchParamEnum.FACET.getName()]) {
                    urlQuery[SearchParamEnum.FACET.getName()] = []
                }
                urlQuery[SearchParamEnum.FACET.getName()].add(THUMBNAIL_FACET)
                urlQuery[THUMBNAIL_FACET] = "true"
            }
        }

        //We ask for a maximum of 1000 facets
        urlQuery["facet.limit"] = 1000

        return urlQuery
    }

    def checkAndReplaceMediaTypeImages(def searchResult){
        searchResult.results.docs.each {
            def preview = it.preview
            if(preview.thumbnail == null ||
            preview.thumbnail instanceof net.sf.json.JSONNull ||
            preview.thumbnail.toString().trim().isEmpty() ||
            (preview.thumbnail.toString().startsWith("http://content") &&
            preview.thumbnail.toString().contains("/placeholder/"))
            ){
                def mediaTypes = []
                if(preview.media instanceof String){
                    mediaTypes.add(preview.media)
                }else{
                    mediaTypes.addAll(preview.media)
                }
                def mediaType = mediaTypes[0]
                if(mediaType != null){
                    mediaType = mediaType.toString().toLowerCase().capitalize()
                }
                if(mediaType != "Audio" &&
                mediaType != "Image" &&
                mediaType != "Institution" &&
                mediaType != "Sonstiges" &&
                mediaType != "Text" &&
                mediaType != "Unknown" &&
                mediaType != "Video"){
                    mediaType = "Unknown"
                }
                def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
                preview.thumbnail = g.resource("plugin": "ddb-common", "dir": "images",
                "file": "/placeholder/searchResultMedia"+mediaType+".png").toString()
            }
        }
        return searchResult
    }

    /**
     * Performs a search request on the backend.
     * Used in the EntityController in the /search/institute
     *
     * @param query the name of the entity
     * @param offset the search offset
     * @param rows the number of search results
     *
     * @return the serach result
     */
    def doInstitutionSearch(def query) {
        def searchPreview = [:]

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/search', false, query)
        if(!apiResponse.isOk()){
            def message = "doInstitutionSearch(): Search response contained error"
            log.error message
            throw new RuntimeException(message)
        }

        def jsonSearchResult = apiResponse.getResponse()
        searchPreview["docs"] = jsonSearchResult.results?.docs
        searchPreview["totalResults"] = jsonSearchResult.numberOfResults
        return searchPreview
    }

    def setCategory(Map urlQuery, String category) {
        //Check if the category is already set
        if (urlQuery[FacetEnum.CATEGORY.getName()] == category) {
            return
        }

        //Check if other facets has been selected as filter
        if(urlQuery[SearchParamEnum.FACET.getName()] && urlQuery[SearchParamEnum.FACET.getName()] != "null"){
            //MANY facets has been selected as filter
            if(urlQuery[SearchParamEnum.FACET.getName()] instanceof Collection<?>){
                urlQuery[SearchParamEnum.FACET.getName()].add(FacetEnum.CATEGORY.getName())
            }
            //ONE facet has been selected as filter
            else {
                def tempFacet = urlQuery[SearchParamEnum.FACET.getName()]
                urlQuery[SearchParamEnum.FACET.getName()] = []
                urlQuery[SearchParamEnum.FACET.getName()].add(FacetEnum.CATEGORY.getName())
                urlQuery[SearchParamEnum.FACET.getName()].add(tempFacet)
            }
        }
        //NO facet has been selected as filter
        else {
            urlQuery[SearchParamEnum.FACET.getName()] = FacetEnum.CATEGORY.getName()
        }
        urlQuery[FacetEnum.CATEGORY.getName()] = category
    }

    /**
     * Create Cookie with search-parameters for use on other pages
     * convert HashMap containing parameters to JSON
     *
     * @param requestObject request object
     * @param reqParameters request-parameters
     * @param additionalParams additional params
     * @param searchType item, entity or institution
     *
     * @return Cookie with search-parameters
     */
    def createSearchCookie(HttpServletRequest requestObject, Map reqParameters, Map additionalParams, Map oldCookieValues=[:], Type searchType) {
        def jSonObject = new JSONObject()

        //Create Cookie with search-parameters for use on other pages
        //convert HashMap containing parameters to JSON
        if (additionalParams) {
            for (entry in additionalParams) {
                reqParameters[entry.key] = entry.value
            }
        }

        //restore oldCookie values, omit the facetValues of the current searchType
        def searchParams
        for (cookie in requestObject.cookies) {
            if (cookie.name == SEARCH_COOKIE_NAME + requestObject.contextPath) {
                searchParams = cookie.value
            }
        }
        if (searchParams) {
            def jSonSlurper = new JsonSlurper()
            try{
                jSonObject = jSonSlurper.parseText(searchParams)
            }catch(Exception e){
                log.error "getSearchCookieAsMap(): Could not parse search params: "+searchParams, e
            }
        }
        jSonObject.remove(searchType.getName() + "_" + SearchParamEnum.FACETVALUES.getName())


        //set actual request params in the cookie
        Map paramMap = getSearchCookieParameters(reqParameters)
        for (entry in paramMap) {
            def key = entry.key

            //special handling for the facetValues[] parameter. Add the searchType as a prefix
            if (key.contains(SearchParamEnum.FACETVALUES.getName())) {
                key = searchType.getName() + "_" + key
            }

            if (entry.value instanceof String[]) {
                //First reset than accumulate!
                jSonObject.remove(key)

                for (entry1 in entry.value) {
                    jSonObject.accumulate(key, URLEncoder.encode(entry1, CHARACTER_ENCODING))
                }
            }
            else if (entry.value instanceof String){
                jSonObject.put(key, URLEncoder.encode(entry.value, CHARACTER_ENCODING))
            }
            else {
                jSonObject.put(key, entry.value)
            }
        }

        def cookie = new Cookie(SEARCH_COOKIE_NAME + requestObject.contextPath, jSonObject.toString())
        //Set the cookie path to "/", so all search pages (items, entities, institutions) are using the same cookie!
        cookie.path = "/"
        cookie.maxAge = -1
        return cookie
    }

    /**
     * Reads the cookie containing the search-Parameters and fills the values in Map.
     *
     * @param request
     * @return Map with key-values from cookie
     */
    def getSearchCookieAsMap(HttpServletRequest requestObject, Cookie[] cookies) {
        def searchParams
        def searchParamsMap = [:]
        for (cookie in cookies) {
            if (cookie.name == SEARCH_COOKIE_NAME + requestObject.contextPath) {
                searchParams = cookie.value
            }
        }
        if (searchParams) {
            def jSonSlurper = new JsonSlurper()
            try{
                searchParamsMap = jSonSlurper.parseText(searchParams)
            }catch(Exception e){
                log.error "getSearchCookieAsMap(): Could not parse search params: "+searchParams, e
            }
            for (entry in searchParamsMap) {
                if (entry.value instanceof String) {
                    entry.value = URLDecoder.decode(entry.value, CHARACTER_ENCODING)
                }
                else if (entry.value instanceof List) {
                    String[] arr = new String[entry.value.size()]
                    def i = 0
                    for (entry1 in entry.value) {
                        if (entry1 instanceof String) {
                            entry1 = URLDecoder.decode(entry1, CHARACTER_ENCODING)
                        }
                        arr[i] = entry1
                        i++
                    }
                    entry.value = arr
                }
            }
        }
        return searchParamsMap
    }

    /**
     * Check if its not an ajax request and searchCookie contains keepFilters=true.
     * If yes, expand requestParameters with facets and return true.
     * Otherwise return false
     *
     * @param cookieMap the searchParameters cookie map
     * @param requestParameters the request parameters
     * @param additionalParams additional request parameters
     * @param searchType the search type (item, entity, institution)
     *
     * @return boolean <code>true</code>
     */
    def checkPersistentFacets(Map cookieMap, Map requestParameters, Map additionalParams, Type searchType) {
        def retVal = false

        //Check persistent facets neither for ajax requests or for requests containing clearFilter params
        if(! (requestParameters["reqType"] == "ajax") && !(requestParameters["clearFilter"] == "true")){

            //Check if the keepfilter flag is set in the cookie
            if (cookieMap[SearchParamEnum.KEEPFILTERS.getName()] && cookieMap[SearchParamEnum.KEEPFILTERS.getName()] == "true") {
                additionalParams[SearchParamEnum.KEEPFILTERS.getName()] = "true"

                //The cookie key for the facetValue is searchType dependent!
                def facetValueCookieKey = searchType.getName() + "_" +SearchParamEnum.FACETVALUES.getName()
                if (!requestParameters[SearchParamEnum.FACETVALUES.getName()] && cookieMap[facetValueCookieKey]) {
                    requestParameters[SearchParamEnum.FACETVALUES.getName()] = cookieMap[facetValueCookieKey]
                    retVal = true
                }
            }
        }

        return retVal
    }
}
