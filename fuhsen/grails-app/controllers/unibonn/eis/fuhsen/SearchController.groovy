/*
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
 *
 */
 
package unibonn.eis.fuhsen

import unibonn.eis.fuhsen.engine.QueryExecutor
import unibonn.eis.fuhsen.engine.RankingResults
import unibonn.eis.fuhsen.engine.SearchEngine
import unibonn.eis.fuhsen.wrapper.rdf.*
import grails.converters.JSON

import java.text.SimpleDateFormat
import java.util.UUID

import de.ddb.next.SearchFacetLists

import com.hp.hpl.jena.rdf.model.Model

import de.ddb.common.ApiConsumer
import de.ddb.common.SearchService
import de.ddb.common.constants.CategoryFacetEnum
import de.ddb.common.constants.FacetEnum
import de.ddb.common.constants.ProjectConstants
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.constants.Type
import de.ddb.common.exception.BadRequestException
import groovy.json.JsonSlurper

import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryExecution
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet

class SearchController {

	static defaultAction = "results"
	
	def searchService
	
    def results() {
				
		def cookieParametersMap = searchService.getSearchCookieAsMap(request, request.cookies)
		def urlQuery = searchService.convertQueryParametersToSearchParameters(params, cookieParametersMap)
		
		//The list of the NON JS supported facets for items
		def nonJsFacetsList = SearchFacetLists.personSearchNonJavascriptFacetList
		
		def mainFacetsUrl = searchService.buildMainFacetsUrl(params, urlQuery, request, nonJsFacetsList)
		
		def subFacetsUrl = [:]
		def selectedFacets = searchService.buildSubFacets(urlQuery, nonJsFacetsList)
		if(urlQuery[SearchParamEnum.FACET.getName()]){
			subFacetsUrl = searchService.buildSubFacetsUrl(params, selectedFacets, mainFacetsUrl, urlQuery, request)
		}
		
		Model model = session["Model"]
		//Printing to load in a triplet store and make tests
		//model.write(System.out, "TTL")
		
		if(params.reqType=="ajax"){
			
			def facetsValues = searchService.facetValuesRequestParameterToList(params)
			//log.info("Facets: "+facetsValues)
			
			log.info("Entity Summarization Starts: "+System.currentTimeMillis())
			def (Object filteredResultsItems, int resultsSize) = parseFacetedPersonsToJson(model, facetsValues)
			log.info("Entity Summarization Ends: "+System.currentTimeMillis())
			
			def resultsHTML = ""
			if (params.infiniteScroll=="true") {
				resultsHTML = g.render(template:"/search/resultsInfiniteScroll", model:[results: filteredResultsItems.results , confBinary: request.getContextPath(),
					offset: params[SearchParamEnum.OFFSET.getName()]]).replaceAll("\r\n", '')
			}
			else {
				resultsHTML = g.render(template:"/search/resultsList", model:[results: filteredResultsItems.results , confBinary: request.getContextPath(),
					offset: params[SearchParamEnum.OFFSET.getName()] ]).replaceAll("\r\n", '')
			}
			
			//log.debug("HTML: "+resultsHTML)
				
			def jsonReturn = [results: resultsHTML,
				//resultsPaginatorOptions: resultsPaginatorOptions,
				//resultsOverallIndex:resultsOverallIndex,
				//page: page,
				//totalPages: totalPages,
				//totalPagesFormatted: String.format(locale, "%,d", totalPages.toInteger()),
				//paginationURL: searchService.buildPagination(resultsItems.numberOfResults, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")),
				numberOfResults: resultsSize+"",
				offset: params[SearchParamEnum.OFFSET.getName()]
			]
			render (contentType:"text/json"){jsonReturn}
			
		}
		else {
		
			def (Object resultsItems, int resultsSize) = parsePersonsToJson(model)
			//log.info("results in JSON: "+resultsItems)
				
			def emptyString = ""
			
			render(view: "results", model: [
				//facetsList:emptyString,
				title: urlQuery[SearchParamEnum.QUERY.getName()],
				results: resultsItems,
				//entities: emptyString,
				//isThumbnailFiltered: false,
				clearFilters: searchService.buildClearFilter(urlQuery, request.forwardURI),
				//correctedQuery:emptyString,
				viewType:  "list",
				facets: [selectedFacets: selectedFacets, mainFacetsUrl: mainFacetsUrl, subFacetsUrl: subFacetsUrl],
				//resultsPaginatorOptions: resultsPaginatorOptions,
				//resultsOverallIndex:emptyString,
				//page: 1,
				//totalPages: 20,
				//paginationURL: emptyString,
				numberOfResultsFormatted: resultsSize,
				offset: params[SearchParamEnum.OFFSET.getName()],
				//keepFiltersChecked: false,
				activeType: "person"
			])
				
		}
		
	}

	def products() {
		
		def cookieParametersMap = searchService.getSearchCookieAsMap(request, request.cookies)
		def urlQuery = searchService.convertQueryParametersToSearchParameters(params, cookieParametersMap)
		
		//The list of the NON JS supported facets for items
		def nonJsFacetsList = SearchFacetLists.productSearchNonJavascriptFacetList
		
		def mainFacetsUrl = searchService.buildMainFacetsUrl(params, urlQuery, request, nonJsFacetsList)
		
		def subFacetsUrl = [:]
		def selectedFacets = searchService.buildSubFacets(urlQuery, nonJsFacetsList)
		if(urlQuery[SearchParamEnum.FACET.getName()]){
			subFacetsUrl = searchService.buildSubFacetsUrl(params, selectedFacets, mainFacetsUrl, urlQuery, request)
		}
		
		//The session variable "Model" contains all the results found in fuhsen search 
		Model model = session["Model"]
		//model.write(System.out, "TTL")
		
		if(params.reqType=="ajax"){
			
			def facetsValues = searchService.facetValuesRequestParameterToList(params)
			//log.info("Facets: "+facetsValues)
			
			def (Object filteredResultsItems, int resultsSize) = parseFacetedProductsToJson(model, facetsValues)
			
			def resultsHTML = ""
			if (params.infiniteScroll=="true") {
				resultsHTML = g.render(template:"/search/resultsInfiniteScroll", model:[results: filteredResultsItems.results , confBinary: request.getContextPath(),
					offset: params[SearchParamEnum.OFFSET.getName()]]).replaceAll("\r\n", '')
			}
			else {
				resultsHTML = g.render(template:"/search/resultsList", model:[results: filteredResultsItems.results , confBinary: request.getContextPath(), 
					offset: params[SearchParamEnum.OFFSET.getName()] ]).replaceAll("\r\n", '')
			}
			
			//log.debug("HTML: "+resultsHTML)
				
			def jsonReturn = [results: resultsHTML,
				//resultsPaginatorOptions: resultsPaginatorOptions,
				//resultsOverallIndex:resultsOverallIndex,
				//page: page,
				//totalPages: totalPages,
				//totalPagesFormatted: String.format(locale, "%,d", totalPages.toInteger()),
				//paginationURL: searchService.buildPagination(resultsItems.numberOfResults, urlQuery, request.forwardURI+'?'+queryString.replaceAll("&reqType=ajax","")),
				numberOfResults: resultsSize+"",
				offset: params[SearchParamEnum.OFFSET.getName()]
			]
			render (contentType:"text/json"){jsonReturn}
			
		}
		else {
			
			def (Object resultsItems, int resultsSize) = parseProductsToJson(model)
			//log.info("results in JSON: "+resultsItems)
			def emptyString = ""
			
			render(view: "results", model: [
				//facetsList:emptyString,
				title: urlQuery[SearchParamEnum.QUERY.getName()],
				results: resultsItems,
				//entities: emptyString,
				//isThumbnailFiltered: false,
				clearFilters: searchService.buildClearFilter(urlQuery, request.forwardURI),
				//correctedQuery:emptyString,
				viewType:  "list",
				facets: [selectedFacets: selectedFacets, mainFacetsUrl: mainFacetsUrl, subFacetsUrl: subFacetsUrl],
				//resultsPaginatorOptions: resultsPaginatorOptions,
				//resultsOverallIndex:emptyString,
				//page: 1,
				//totalPages: 20,
				//paginationURL: emptyString,
				numberOfResultsFormatted: resultsSize+"",
				offset: params[SearchParamEnum.OFFSET.getName()],
				//keepFiltersChecked: false,
				activeType: "product"
			])
		}
	}

	def organizations() {
		
		def cookieParametersMap = searchService.getSearchCookieAsMap(request, request.cookies)
		def urlQuery = searchService.convertQueryParametersToSearchParameters(params, cookieParametersMap)
		
		Model model = session["Model"]
		//model.write(System.out, "TTL")
		
		def (Object resultsItems, int resultsSize) = parseOrganizationsToJson(model)
		//log.info("results in JSON: "+resultsItems)
		
		//The list of the NON JS supported facets for items
		def nonJsFacetsList = SearchFacetLists.personSearchNonJavascriptFacetList
		
		def mainFacetsUrl = searchService.buildMainFacetsUrl(params, urlQuery, request, nonJsFacetsList)
		
		def subFacetsUrl = [:]
		def selectedFacets = searchService.buildSubFacets(urlQuery, nonJsFacetsList)
		if(urlQuery[SearchParamEnum.FACET.getName()]){
			subFacetsUrl = searchService.buildSubFacetsUrl(params, selectedFacets, mainFacetsUrl, urlQuery, request)
		}
			
		def emptyString = ""
		
		render(view: "results", model: [
			//facetsList:emptyString,
			title: urlQuery[SearchParamEnum.QUERY.getName()],
			results: resultsItems,
			//entities: emptyString,
			//isThumbnailFiltered: false,
			clearFilters: searchService.buildClearFilter(urlQuery, request.forwardURI),
			//correctedQuery:emptyString,
			viewType:  "list",
			facets: [selectedFacets: selectedFacets, mainFacetsUrl: mainFacetsUrl, subFacetsUrl: subFacetsUrl],
			//resultsPaginatorOptions: resultsPaginatorOptions,
			//resultsOverallIndex:emptyString,
			//page: 1,
			//totalPages: 20,
			//paginationURL: emptyString,
			numberOfResultsFormatted: resultsSize+"",
			offset: params[SearchParamEnum.OFFSET.getName()],
			//keepFiltersChecked: false,
			activeType: "organization"
		])
		
	}

	def loadingResults() {
		
		if(params.reqType=="ajax") {
			
			def cookieParametersMap = searchService.getSearchCookieAsMap(request, request.cookies)
			
			def urlQuery = searchService.convertQueryParametersToSearchParameters(params, cookieParametersMap)
			
			def resultsPaginatorOptions = searchService.buildPaginatorOptions(urlQuery)
			
			def inputFile = '{ "numberOfResults": 0, "results": [ { "name": "single", "docs": [], "numberOfDocs": 0 } ], "facets": [ { "facetValues": [], "numberOfFacets": 0, "field": "applicationYear_Year" }, { "facetValues": [], "numberOfFacets": 0, "field": "ipc_classification_fct" }, { "facetValues": [], "numberOfFacets": 0, "field": "patentnumber" }, { "facetValues": [], "numberOfFacets": 0, "field": "patentOffice_fct" }, { "facetValues": [], "numberOfFacets": 0, "field": "applicationYear_fct" }, { "facetValues": [], "numberOfFacets": 0, "field": "keywords_fct" }, { "facetValues": [], "numberOfFacets": 0, "field": "documentType_fct" }, { "facetValues": [], "numberOfFacets": 0, "field": "ipc_classificationFull_fct" }, { "facetValues": [], "numberOfFacets": 0, "field": "manual_codes_fct" } ], "entities": [], "correctedQuery": "diego holland", "highlightedTerms": [], "randomSeed": "" }'
			
			def resultsItems = new JsonSlurper().parseText(inputFile)
			
			SearchEngine se = new SearchEngine()
			Model resultsRdf = se.getResults("", urlQuery)
			
			//Generating a Unique UID for the search query
			UUID searchUID = UUID.randomUUID()
			log.info("Search UID: "+searchUID)
			
			if (session["Model"] == null)
				session["Model"] = resultsRdf
			else {
				log.info('Model is in session already - replacing results')
				session["Model"] = resultsRdf
			}
			
			def jsonReturn = [ responseCode: 'Ok']
			render (contentType:"text/json"){jsonReturn}
				
		}
		else {
			def queryString = params[SearchParamEnum.QUERY.getName()]
			render(view: "loadingResults", model: [ query : queryString ] )
		}				
	}
	
	private parsePersonsToJson(Model model) {
		
		def resultList = [:]
        def docs = []
		
		String query = ("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "	PREFIX type: <http://dbpedia.org/class/yago/> "
				+ "	PREFIX prop: <http://dbpedia.org/property/> "
				+ "	PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "	PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
				+ "SELECT ?person ?name ?depiction ?familyName ?givenName ?gender ?birthday ?occupation ?currentAddress ?currentWork WHERE { "
				+ "?person fuhsen:hadPrimarySource 'GOOGLE+' . "
				+ "?person foaf:name ?name . "
				+ "?person foaf:Image ?image . "
				+ "?image foaf:depiction ?depiction . "
				+ "OPTIONAL { ?name foaf:family_name ?familyName } . "
				+ "OPTIONAL { ?name foaf:givenname ?givenName } . "
				+ "OPTIONAL { ?person foaf:gender ?gender } . "
				+ "OPTIONAL { ?person foaf:birthday ?birthday } . "
				+ "OPTIONAL { ?person fuhsen:occupation ?occupation } . "
				+ "OPTIONAL { ?person fuhsen:placesLived ?placesLived . "
				+ "			  ?placesLived fuhsen:placesLivedprimary 'true' . "
				+ "			  ?placesLived fuhsen:livedAt ?currentAddress . } . "
				+ "OPTIONAL { ?person fuhsen:organization ?organization . "
				+ "			  ?organization fuhsen:organizationprimary 'true' . "
				+ "			  ?organization fuhsen:organizationtype 'work' . "
				+ "			  ?organization fuhsen:organizationname ?currentWork . } . "
				+ "} limit 500")
		
		QueryExecution qexec = QueryExecutionFactory.create(query, model)
		ResultSet results = qexec.execSelect()
		
		int aSize = 0
		
		while(results.hasNext()) {
			
			QuerySolution row = results.next();
			
			def tmpResult = [:]
			
			//TODO remove this condition is temporal due to problems in JSON translation
			if (row.get("name").literal)
			{
				tmpResult["id"] = row.get("person").toString()
				
				tmpResult["title"] = row.getLiteral("name").getString()					
	            String[] excerpts = prepareExcerptForPerson(row)
				tmpResult["excerpt"] = excerpts[0]
				tmpResult["excerpt1"] = excerpts[1]
	            tmpResult["image"] = row.getLiteral("depiction").toString()
				tmpResult["dataSource"] = "GOOGLE+"
	           
				docs.add(tmpResult)
				aSize = aSize + 1
			}
		}
		
		resultList["numberOfResults"] = aSize
		resultList["results"] = docs
		
		return [resultList, aSize]		
	}
	
	private parseOrganizationsToJson(Model model) {
		
		def resultList = [:]
		def docs = []
		
		int aSize = 0
			
		resultList["numberOfResults"] = aSize
		resultList["results"] = docs
		
		return [resultList, aSize]
	}
	
	
	private parseProductsToJson(Model model) {
			
		def resultList = [:]
		def docs = []
		
		String query = ("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "	PREFIX type: <http://dbpedia.org/class/yago/> "
				+ "	PREFIX prop: <http://dbpedia.org/property/> "
				+ "	PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "	PREFIX gr: <http://purl.org/goodrelations/v1#> "
				+ "	PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
				+ "SELECT ?product ?description ?depiction ?price ?location ?condition ?country WHERE { "
				+ "?product fuhsen:hadPrimarySource 'EBAY' . "
				+ "?product gr:description ?description . "
				+ "?product foaf:depiction ?depiction . "
				+ "?product fuhsen:price ?price . "
				+ "OPTIONAL { ?product fuhsen:location ?location . } . "
				+ "OPTIONAL { ?product gr:condition ?condition . } . "
				+ "OPTIONAL { ?product fuhsen:country ?country . } . "
				+ "} limit 500")
		
		QueryExecution qexec = QueryExecutionFactory.create(query, model)
		ResultSet results = qexec.execSelect()
		
		int aSize = 0
		
		while(results.hasNext()) {
			
			QuerySolution row = results.next();
			
			def tmpResult = [:]
			
			tmpResult["id"] = row.get("product").toString()
			tmpResult["title"] = row.getLiteral("description").getString()
			String[] excerpts = prepareExcerptForProduct(row)
			tmpResult["excerpt"] = excerpts[0]
			tmpResult["excerpt1"] = excerpts[1]
			tmpResult["image"] = row.getLiteral("depiction").toString()
			tmpResult["dataSource"] = "EBAY"
			
			docs.add(tmpResult)
			aSize = aSize + 1
		}
		
		resultList["numberOfResults"] = aSize
		resultList["results"] = docs
		
		return [resultList, aSize]
	}
	
	private def prepareExcerptForProduct(QuerySolution row) {
		
		String[] results = new String[2]
		
		String excerpt = ""
		if (row.getLiteral("location") != null)
			excerpt = "Location: "+row.getLiteral("location").toString()
		if (row.getLiteral("country") != null){
			excerpt += "&nbsp;&nbsp;&nbsp;&nbsp;"
			excerpt += "Country: "+row.getLiteral("country").toString()
		}
		
		results[0] = excerpt
		
		//New line
		String excerpt1 = ""
		if (row.getLiteral("price") != null){
			excerpt1 += "Price: "+row.getLiteral("price").toString()
		}
		if (row.getLiteral("condition") != null){
			excerpt1 += "&nbsp;&nbsp;&nbsp;&nbsp;"
			excerpt1 += "Condition: "+row.getLiteral("condition").toString()
		}
		results[1] = excerpt1
		
		return results
	}
	
	private def prepareExcerptForPerson(QuerySolution row) {
		
		String[] results = new String[2]
		
		String excerpt = ""
		
		if (row.getLiteral("gender") != null)
			excerpt = "Gender: "+row.getLiteral("gender").toString()
		if (row.getLiteral("birthday") != null){
			excerpt += "&nbsp;&nbsp;&nbsp;&nbsp;"
			excerpt += "Birthday: "+row.getLiteral("birthday").toString()
		}
		if (row.getLiteral("occupation") != null){
			excerpt += "&nbsp;&nbsp;&nbsp;&nbsp;"
			excerpt += "Occupation: "+row.getLiteral("occupation").toString()
		}
		
		results[0] = excerpt
		
		//New line
		String excerpt1 = ""
		if (row.getLiteral("currentAddress") != null){
			excerpt1 += "Address: "+row.getLiteral("currentAddress").toString()
		}
		if (row.getLiteral("currentWork") != null){
			excerpt1 += "&nbsp;&nbsp;&nbsp;&nbsp;"
			excerpt1 += "Work: "+row.getLiteral("currentWork").toString()
		}
		results[1] = excerpt1
		
		return results
		
	}
	
	//Methods to apply Facets on the results
	private parseFacetedProductsToJson(Model model, def facetsValues) {
		
		def resultList = [:]
		def docs = []
	
		String filters = ""
		for (var in facetsValues) {
			String[] facetSplited = var.toString().split("=")
			//log.info("Faceted Splited: "+facetSplited)
			if(facetSplited.length == 2)	{
				if (facetSplited[0] == "product_condition_fct") {
					filters = "?product gr:condition '"+facetSplited[1]+"' . "
				}
				if (facetSplited[0] == "product_price_fct") {
					filters = "?product fuhsen:price '"+facetSplited[1]+"' . "
				}
				if (facetSplited[0] == "product_country_fct") {
					filters = "?product fuhsen:country '"+facetSplited[1]+"' . "
				}
			}
			else {
				log.warn("Problems to get the value of facet: "+var)
			}
		}
		
		String query = ("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "	PREFIX type: <http://dbpedia.org/class/yago/> "
			+ "	PREFIX prop: <http://dbpedia.org/property/> "
			+ "	PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
			+ "	PREFIX gr: <http://purl.org/goodrelations/v1#> "
			+ "	PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
			+ "SELECT ?product ?description ?depiction ?price ?location ?condition ?country WHERE { "
			+ "?product fuhsen:hadPrimarySource 'EBAY' . "
			+ "?product gr:description ?description . "
			+ "?product foaf:depiction ?depiction . "
			+ "?product fuhsen:price ?price . "
			+ "OPTIONAL { ?product fuhsen:location ?location . } . "
			+ "OPTIONAL { ?product gr:condition ?condition . } . "
			+ "OPTIONAL { ?product fuhsen:country ?country . } . ")
	
		query += filters
		query += "} limit 500"
		
		QueryExecution qexec = QueryExecutionFactory.create(query, model)
		ResultSet results = qexec.execSelect()
	
		int aSize = 0
	
		while(results.hasNext()) {
		
			QuerySolution row = results.next();
		
			def tmpResult = [:]
		
			tmpResult["id"] = row.get("product").toString()
			tmpResult["title"] = row.getLiteral("description").getString()
			String[] excerpts = prepareExcerptForProduct(row)
			tmpResult["excerpt"] = excerpts[0]
			tmpResult["excerpt1"] = excerpts[1]
			tmpResult["image"] = row.getLiteral("depiction").toString()
	   
			docs.add(tmpResult)
			aSize = aSize + 1
		}
	
		resultList["numberOfResults"] = aSize
		resultList["results"] = docs
	
		return [resultList, aSize]
	}
	
	private parseFacetedPersonsToJson(Model model, def facetsValues) {
		
		def resultList = [:]
		def docs = []
	
		String filters = ""
		for (var in facetsValues) {
			String[] facetSplited = var.toString().split("=")
			//log.info("Faceted Splited: "+facetSplited)
			if(facetSplited.length == 2)	{
				if (facetSplited[0] == "person_gender_fct") {
					filters = "?person foaf:gender '"+facetSplited[1]+"' . "
				}
				if (facetSplited[0] == "person_birthday_fct") {
					filters = "?person foaf:birthday '"+facetSplited[1]+"' . "
				}
				if (facetSplited[0] == "person_occupation_fct") {
					filters = "?person fuhsen:occupation '"+facetSplited[1]+"' . "
				}
				if (facetSplited[0] == "person_livesat_fct") {
					filters = ("	?person fuhsen:placesLived ?placesLived . "
						+ "?placesLived fuhsen:placesLivedprimary 'true' . "
						+ "?placesLived fuhsen:livedAt '"+facetSplited[1]+"' . ")
				}
				if (facetSplited[0] == "person_worksat_fct") {
					filters = ("	?person fuhsen:organization ?organization . "
						+ "?organization fuhsen:organizationprimary 'true' . "
						+ "?organization fuhsen:organizationtype 'work' . "
						+ "?organization fuhsen:organizationname '"+facetSplited[1]+"' . ")
				}
				if (facetSplited[0] == "person_studiesat_fct") {
					filters = ("	?person fuhsen:organization ?organization . " 
						+ "?organization fuhsen:organizationprimary 'true' . "
						+ "?organization fuhsen:organizationtype 'school' . "
						+ "?organization fuhsen:organizationname '"+facetSplited[1]+"' . ")
				}
			}
			else {
				log.warn("Problems to get the value of facet: "+var)
			}
		}
		
		String query = ("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "	PREFIX type: <http://dbpedia.org/class/yago/> "
				+ "	PREFIX prop: <http://dbpedia.org/property/> "
				+ "	PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "	PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
				+ "SELECT ?person ?name ?depiction ?familyName ?givenName ?gender ?birthday ?occupation ?currentAddress ?currentWork WHERE { "
				+ "?person fuhsen:hadPrimarySource 'GOOGLE+' . "
				+ "?person foaf:name ?name . "
				+ "?person foaf:Image ?image . "
				+ "?image foaf:depiction ?depiction . "
				+ "OPTIONAL { ?name foaf:family_name ?familyName } . "
				+ "OPTIONAL { ?name foaf:givenname ?givenName } . "
				+ "OPTIONAL { ?person foaf:gender ?gender } . "
				+ "OPTIONAL { ?person foaf:birthday ?birthday } . "
				+ "OPTIONAL { ?person fuhsen:occupation ?occupation } . "
				+ "OPTIONAL { ?person fuhsen:placesLived ?placesLived . "
				+ "			  ?placesLived fuhsen:placesLivedprimary 'true' . "
				+ "			  ?placesLived fuhsen:livedAt ?currentAddress . } . "
				+ "OPTIONAL { ?person fuhsen:organization ?organization . "
				+ "			  ?organization fuhsen:organizationprimary 'true' . "
				+ "			  ?organization fuhsen:organizationtype 'work' . "
				+ "			  ?organization fuhsen:organizationname ?currentWork . } . ")
	
		query += filters
		query += "} limit 500"
		
		QueryExecution qexec = QueryExecutionFactory.create(query, model)
		ResultSet results = qexec.execSelect()
	
		int aSize = 0
	
		while(results.hasNext()) {
		
			QuerySolution row = results.next();
			
			def tmpResult = [:]
			
			//TODO remove this condition is temporal due to problems in JSON translation
			if (row.get("name").literal)
			{
				tmpResult["id"] = row.get("person").toString()
				
				tmpResult["title"] = row.getLiteral("name").getString()					
	            String[] excerpts = prepareExcerptForPerson(row)
				tmpResult["excerpt"] = excerpts[0]
				tmpResult["excerpt1"] = excerpts[1] 
	            tmpResult["image"] = row.getLiteral("depiction").toString()	
	           
				docs.add(tmpResult)
				aSize = aSize + 1
			}
			
		}
	
		resultList["numberOfResults"] = aSize
		resultList["results"] = docs
	
		return [resultList, aSize]
	}
	
	
}
