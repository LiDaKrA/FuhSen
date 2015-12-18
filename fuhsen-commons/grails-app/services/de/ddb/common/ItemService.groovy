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

import static groovyx.net.http.ContentType.*
import groovy.json.*

import java.util.regex.Matcher
import java.util.regex.Pattern

import net.sf.json.JSONArray
import net.sf.json.JSONNull
import net.sf.json.JSONObject

import org.apache.commons.codec.binary.Base32
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.context.NoSuchMessageException
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.constants.FacetEnum
import de.ddb.common.exception.ItemNotFoundException

/**
 * Common method used in the item context of ddb.
 *
 * @author boz
 */

class ItemService {

    private static final def HTTP ='http://'
    private static final def HTTPS ='https://'

    private static final def MAX_LENGTH_FOR_ITEM_WITH_BINARY = 270
    private static final def MAX_LENGTH_FOR_ITEM_WITH_NO_BINARY = 350

    private static final SOURCE_PLACEHOLDER = '{0}'
    private static final def THUMBNAIL = 'mvth'
    private static final def PREVIEW= 'mvpr'
    private static final def FULL = 'full'
    private static final def ORIG= 'orig'
    private static final def IMAGE= 'image/jpeg'
    private static final def AUDIOMP3 = 'audio/mp3'
    private static final def AUDIOMPEG = 'audio/mpeg'
    private static final def VIDEOMP4 = 'video/mp4'
    private static final def VIDEOFLV = 'video/flv'
	private static final def TEXTXML = 'text/xml'
    private static final def PDF= 'application/pdf'

    private static final def MAX_CHILDREN= 250

    //Autowire the grails application bean
    def messageSource
    LinkGenerator grailsLinkGenerator
    def configurationService
    def cultureGraphService
    def entityService
    def languageService

    def transactional=false

    def findItemById(id) {

        final def componentsPath = "/items/" + id + "/"
        final def viewPath = componentsPath + "view"

        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), viewPath)
        if (!apiResponse.isOk()) {
            if (apiResponse.status == ApiResponse.HttpStatus.HTTP_404) {
                throw new ItemNotFoundException()
            }
            else {
                log.error "findItemById: could not load view component for item \"" + id + "\""
                apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            }
        }

        def json = apiResponse.getResponse()
        def institution = json.item?.institution

        if (!institution) {
            throw new ItemNotFoundException()
        }

        // institution logo
        String institutionLogoUrl = grailsLinkGenerator.resource("plugin": "ddb-common", "dir": "images",
        "file": "/placeholder/searchResultMediaInstitution.png").toString()
        String institutionId = json.item.institution."logo-institution-ddbid"

        if (!institutionId && !json.item.institution.logo?.toString()?.trim()?.isEmpty()) {
            institutionId = getProviderDdbId(json.item.institution.logo.toString())
        }
        if (institutionId) {
            institutionLogoUrl = grailsLinkGenerator.resource("dir": "binary", "file": institutionId + "/list/1.jpg")
        }

        String originUrl = filterOutSurroundingTag(json.item.origin.toString())

        def item = json.item

        def title = shortenTitle(id, item)
        def fields
        if (json.item.fields instanceof JSONArray || json.item.fields instanceof List<?>) {
            def displayFieldsTag = json.item.fields.findAll{ it."@usage".contains('display') }
            // https://jira.deutsche-digitale-bibliothek.de/browse/DDBNEXT-1744
            // Ensure that "fields" always contains an array of field objects.
            if (displayFieldsTag[0].field[0]) {
                fields = displayFieldsTag[0].field.findAll()
            }
            else {
                fields = [displayFieldsTag[0].field]
            }
        }
        else {
            // old data format
            fields = json.item.fields.field
        }

        def viewerUri = buildViewerUri(item, componentsPath)

        return ['uri': '', 'viewerUri': viewerUri, 'institution': institution, 'item': item, 'title': title,
            'fields': fields, pageLabel: json.pagelabel, 'institutionImage': institutionLogoUrl, 'originUrl': originUrl]
    }

    private shortenTitle(id, item) {
        def title = item.title

        def hasBinary = !fetchBinaryList(id).isEmpty()

        if(title.size() <= MAX_LENGTH_FOR_ITEM_WITH_NO_BINARY) {
            return title
        }

        if(hasBinary && title.size() > MAX_LENGTH_FOR_ITEM_WITH_BINARY) {
            return apendDotDot(title.substring(0, MAX_LENGTH_FOR_ITEM_WITH_BINARY))
        } else if(title.size() > MAX_LENGTH_FOR_ITEM_WITH_NO_BINARY) {
            return apendDotDot(title.substring(0, MAX_LENGTH_FOR_ITEM_WITH_NO_BINARY))
        }

        return title
    }

    def apendDotDot(String shortenedTitle){
        def lastSpaceIndex = shortenedTitle.lastIndexOf(' ')
        def shortenedTitleUntilLastSpace  = shortenedTitle.substring(0, lastSpaceIndex)
        shortenedTitleUntilLastSpace + '...'
    }

    def buildViewerUri(item, componentsPath) {
        if(item.viewers instanceof JSONNull){
            return ''
        }
        if(item.viewers?.viewer == null || item.viewers?.viewer?.isEmpty()) {
            return ''
        }

        def viewerPrefix = item.viewers.viewer.url.toString()

        if(viewerPrefix.contains(SOURCE_PLACEHOLDER)) {
            def withoutPlaceholder = viewerPrefix.toString() - SOURCE_PLACEHOLDER
            def binaryServerUrl = configurationService.getBinaryUrl()

            //Security check: if the binaryServerUrl is configured with an ending ".../binary/", this has to be removed
            int firstOccuranceOfBinaryString = binaryServerUrl.indexOf("/binary/")
            if(firstOccuranceOfBinaryString >= 0){
                binaryServerUrl = binaryServerUrl.substring(0, firstOccuranceOfBinaryString)
            }

            def sourceUri = new URL(new URL(binaryServerUrl), componentsPath + 'source').toString()
            def encodedSourceUri= URLEncoder.encode sourceUri, 'UTF-8'
            return withoutPlaceholder + encodedSourceUri
        }
    }

    /**
     * Extract the institution id from the given logo URL and calculate the DDB id of the institution.
     *
     * @param institutionLogoUrl URL pointing to the provider logo
     * @return DDB id for the institution the logo belongs to
     */
    def String getProviderDdbId(String institutionLogoUrl) {
        String result = null
        int startIndex = institutionLogoUrl.indexOf("/edit/")
        if (startIndex > 0) {
            String itemId = institutionLogoUrl.substring(startIndex + 6, startIndex + 14)
            result = new Base32().encodeAsString(("www_fiz-karlsruhe_de" + itemId).encodeAsSHA1())
        }
        return result
    }

    def findBinariesById(id) {
        def prev = parseBinaries(fetchBinaryList(id))
        return prev
    }

    def fetchBinaryList(id) {
        def result = []
        def apiResponse = ApiConsumer.getXml(configurationService.getBackendUrl(), "/items/" + id + "/binaries")
        if (apiResponse.isOk()) {
            def binaries = apiResponse.getResponse()
            result = binaries.binary
        }
        else if (apiResponse.status != ApiResponse.HttpStatus.HTTP_404) {
            log.error "binary list could not be fetched"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return result
    }


    def binariesCounter(binaries){
        def images = 0
        def audios = 0
        def videos = 0
        binaries.each {
            if (it.'orig'.'uri'.'audio') {
                audios++
            } else if (it.'orig'.'uri'.'video') {
                videos++
            } else if (it.'thumbnail'.'uri') {
                images++
            }
        }
        return (['images':images,'audios':audios,'videos':videos])
    }

    def parseBinaries(binaries) {
        def BINARY_SERVER_URI = grailsLinkGenerator.getContextPath()
        def binaryList = []
        def bidimensionalList = []
        String position
        String path
        String type
        String category
        String htmlStrip
        //creation of a bi-dimensional list containing the binaries separated for position
        binaries.each { x ->
            if(x.'@position'.toString() != position){
                def subList = []
                bidimensionalList[x.'@position'.toInteger()-1] = subList
                position = x.'@position'.toString()
            }
            bidimensionalList[x.'@position'.toInteger()-1].add(x)
        }
        //creation of a list of binary maps from the bi-dimensional list
        bidimensionalList.each { y ->
            def binaryMap = [
                'orig' : ['title':'', 'uri': ['image':'','audio':'','video':'','pdf':'', 'xml':''],'author':'', 'rights':''],
                'preview' : ['title':'', 'uri':'', 'author':'', 'rights':''],
                'thumbnail' : ['title':'', 'uri':'','author':'', 'rights':''],
                'full' : ['title':'', 'uri':'','author':'', 'rights':''],
                'checkValue' : "",
            ]
            y.each { z ->
                path = z.'@path'
                type = z.'@mimetype'
                category = z.'@category'
                //check against the parameter "category"
				// WDRCROSS-106 patch since category comes like 'other' in the response 				
                if (category == FULL || category == "other" ) {
                    if(type.contains(IMAGE)) {
                        binaryMap.'orig'.'uri'.'image' = BINARY_SERVER_URI + z.'@path'
                        if(!binaryMap.'orig'.'title') {
                            htmlStrip = z.'@name'
                            binaryMap.'orig'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                        }
                    }
                    else if(type.contains(AUDIOMP3)||type.contains(AUDIOMPEG)){
                        binaryMap.'orig'.'uri'.'audio' = BINARY_SERVER_URI + z.'@path'
                        htmlStrip = z.'@name'
                        binaryMap.'orig'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                    }
                    else if(type.contains(VIDEOMP4)||type.contains(VIDEOFLV)){
                        binaryMap.'orig'.'uri'.'video' = BINARY_SERVER_URI + z.'@path'
                        htmlStrip = z.'@name'
                        binaryMap.'orig'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                    }
                    else if(type.contains(PDF)){
                        binaryMap.'orig'.'uri'.'pdf' = BINARY_SERVER_URI + z.'@path'
                        htmlStrip = z.'@name'
                        binaryMap.'orig'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                    }
					else if(type.contains(TEXTXML)){
						binaryMap.'orig'.'uri'.'xml' = BINARY_SERVER_URI + z.'@path'
						htmlStrip = z.'@name'
						binaryMap.'orig'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
					}
                    binaryMap.'orig'.'author'= z.'@name2'
                    binaryMap.'orig'.'rights'= z.'@name3'

                    //filling the category "full" for back-compatibility
                    htmlStrip = z.'@name'
                    binaryMap.'full'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                    binaryMap.'full'.'uri' = BINARY_SERVER_URI + z.'@path'
                    binaryMap.'full'.'author'= z.'@name2'
                    binaryMap.'full'.'rights'= z.'@name3'

                    binaryMap.'checkValue' = "1"
                }
                else if (category == PREVIEW) {
                    htmlStrip = z.'@name'
                    binaryMap.'preview'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                    binaryMap.'preview'.'uri' = BINARY_SERVER_URI + z.'@path'
                    binaryMap.'preview'.'author'= z.'@name2'
                    binaryMap.'preview'.'rights'= z.'@name3'
                    binaryMap.'checkValue' = "1"
                }
                else if (category == THUMBNAIL) {
                    htmlStrip = z.'@name'
                    binaryMap.'thumbnail'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                    binaryMap.'thumbnail'.'uri' = BINARY_SERVER_URI + z.'@path'
                    binaryMap.'thumbnail'.'author'= z.'@name2'
                    binaryMap.'thumbnail'.'rights'= z.'@name3'
                    binaryMap.'checkValue' = "1"
                }
                //check against the binary path
                else if (!category) {
                    if(path.contains(ORIG)) {
                        if(type.contains(IMAGE)) {
                            binaryMap.'orig'.'uri'.'image' = BINARY_SERVER_URI + z.'@path'
                            if(!binaryMap.'orig'.'title') {
                                htmlStrip = z.'@name'
                                binaryMap.'orig'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                            }
                        }
                        else if(type.contains(AUDIOMP3)||type.contains(AUDIOMPEG)){
                            binaryMap.'orig'.'uri'.'audio' = BINARY_SERVER_URI + z.'@path'
                            htmlStrip = z.'@name'
                            binaryMap.'orig'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                        }
                        else if(type.contains(VIDEOMP4)||type.contains(VIDEOFLV)){
                            binaryMap.'orig'.'uri'.'video' = BINARY_SERVER_URI + z.'@path'
                            htmlStrip = z.'@name'
                            binaryMap.'orig'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                        }

                        binaryMap.'orig'.'author'= z.'@name2'
                        binaryMap.'orig'.'rights'= z.'@name3'
                        binaryMap.'checkValue' = "1"
                    }
                    else if(path.contains(PREVIEW)) {
                        htmlStrip = z.'@name'
                        binaryMap.'preview'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                        binaryMap.'preview'.'uri' = BINARY_SERVER_URI + z.'@path'
                        binaryMap.'preview'.'author'= z.'@name2'
                        binaryMap.'preview'.'rights'= z.'@name3'
                        binaryMap.'checkValue' = "1"
                    } else if (path.contains(THUMBNAIL)) {
                        htmlStrip = z.'@name'
                        binaryMap.'thumbnail'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                        binaryMap.'thumbnail'.'uri' = BINARY_SERVER_URI + z.'@path'
                        binaryMap.'thumbnail'.'author'= z.'@name2'
                        binaryMap.'thumbnail'.'rights'= z.'@name3'
                        binaryMap.'checkValue' = "1"
                    } else if (path.contains(FULL)) {
                        htmlStrip = z.'@name'
                        binaryMap.'full'.'title' = htmlStrip.replaceAll("<(.|\n)*?>", '')
                        binaryMap.'full'.'uri' = BINARY_SERVER_URI + z.'@path'
                        binaryMap.'full'.'author'= z.'@name2'
                        binaryMap.'full'.'rights'= z.'@name3'
                        binaryMap.'checkValue' = "1"
                    }
                }
            }
            if(binaryMap.'checkValue'){
                binaryList.add(binaryMap)
            }
        }
        return binaryList
    }

    def getParent(itemId){
        if (itemId == null) {
            throw new IllegalArgumentException("itemId must not be null!")
        }

        final def parentsPath = "/items/" + itemId + "/parents/"
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), parentsPath)
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return apiResponse.getResponse().hierarchy
    }
	
	def getView(itemId){
		if (itemId == null) {
			throw new IllegalArgumentException("itemId must not be null!")
		}
		
		// VWPATENT-
		// get children first and see whether there is a child (fld) patent
		def children = getChildren(itemId)
		children.each { child ->
			itemId = child.id	
		}

		final def parentsPath = "/items/" + itemId + "/view/"
		def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), parentsPath)
		if(!apiResponse.isOk()){
			log.error "Json: Json file was not found"
			apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
		}
		return apiResponse.getResponse()
	}

    def getChildren(itemId, maxChildren = MAX_CHILDREN){
        if (itemId == null) {
            throw new IllegalArgumentException("itemId must not be null!")
        }

        final def childrenPath = "/items/" + itemId + "/children/"
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), childrenPath, false, ["rows" : maxChildren])
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return apiResponse.getResponse().hierarchy
    }

    /**
     * Follow redirects.
     */
    private def findRealUrl(url) {
        HttpURLConnection conn = url.openConnection()
        conn.followRedirects = false
        conn.requestMethod = 'HEAD'
        if(conn.responseCode in [301, 302]) {
            if (conn.headerFields.'Location') {
                return findRealUrl(conn.headerFields.Location.first().toURL())
            } else {
                throw new RuntimeException('Failed to follow redirect')
            }
        }
        return url
    }

    /**
     * Get the content of the given URL and follow redirects.
     *
     * @param url URL
     * @return content of that URL
     */
    byte[] getContent(URL url) {
        return findRealUrl(url).bytes
    }

    def fetchXMLMetadata(id) {
        def result = []
        def apiResponse = ApiConsumer.getXml(configurationService.getBackendUrl(), "/items/" + id + "/aip")
        if (apiResponse.isOk()) {
            result = apiResponse.getResponse().toXmlString()
        }
        else if (apiResponse.status != ApiResponse.HttpStatus.HTTP_404) {
            log.error "XMLMetadata: XML file could not be fetched"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return result
    }

    def getSimilarItems(itemId){
        def query = "id:"+itemId

        def retVal = null
        def queryParams = ["query": query, "fields": "affiliate,label,description", "rows" : "5"]

        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl(), "search/mlt", false, queryParams)

        if(apiResponse.isOk()){
            retVal = apiResponse.getResponse()
        }
        return retVal
    }

    String filterOutSurroundingTag(String text){
        Pattern pattern = Pattern.compile("<.*>(.+?)</.*>")
        Matcher matcher = pattern.matcher(text)
        matcher.find()
        String out = text
        try{
            out = matcher.group(1)
        }catch(Exception e){}
        return out
    }

    def buildLicenseInformation(def item, httpRequest){
        def licenseInformation

        if(item.item?.license && !item.item.license.isEmpty()){

            def licenseId = item.item.license."@resource"

            def propertyId = convertUriToProperties(licenseId)

            licenseInformation = [:]

            def text
            def url
            def img
            def imgContext

            try{
                def locale = languageService.getBestMatchingLocale(RequestContextUtils.getLocale(httpRequest))
                text = messageSource.getMessage("ddbnext.license.text."+propertyId, null, locale)
                url = messageSource.getMessage("ddbnext.license.url."+propertyId, null, locale)
                imgContext = configurationService.getContextUrl()
                img = messageSource.getMessage("ddbnext.license.img."+propertyId, null, locale)
            }catch(NoSuchMessageException e){
                log.error "findById(): no I18N information for license '"+licenseInformation.id+"' in license.properties"
            }
            if(!text){
                text = item.item.license.$
            }
            if(!url){
                url = item.item.license["@url"].toString()
            }

            licenseInformation.text = text
            licenseInformation.url = url
            licenseInformation.img = img
            licenseInformation.imgContext = imgContext
        }

        return licenseInformation
    }

    def convertUriToProperties(def uri){
        if(uri){
            // http://creativecommons.org/licenses/by-nc-nd/3.0/de/

            def converted = uri.toString()
            converted = converted.replaceAll("http://","")
            converted = converted.replaceAll("https://","")
            converted = converted.replaceAll("[^A-Za-z0-9]", ".")
            if(converted.startsWith(".")){
                converted = converted.substring(1)
            }
            if(converted.endsWith(".")){
                converted = converted.substring(0, converted.size()-1)
            }
            return converted
        }else{
            return ""
        }
    }

    def convertToHtmlLink = { field ->
        def fieldValue = field.value.toString()
        if(fieldValue.startsWith(HTTP) || fieldValue.startsWith(HTTPS)) {
            field.value = '<a href="' + fieldValue + '">' + fieldValue + '</a>'
        }
        return field
    }

    def createEntityLinks(fields, ddbUrl){
        fields.each {
            // https://jira.deutsche-digitale-bibliothek.de/browse/DDBNEXT-1166
            // Do not create entity link for this field.
            if (it.'@id' != "flex_arch_033") {
                def valueTags = []

                if(it.value instanceof List){
                    valueTags = it.value
                } else {
                    valueTags.add(it.value)
                }

                valueTags.each { valueTag ->
                    if (valueTag instanceof JSONObject || valueTag instanceof HashMap) {
                        def resource = valueTag."@resource"

                        if(resource != null && !resource.isEmpty()){
                            if(cultureGraphService.isValidGndUri(resource)){
                                def entityId = cultureGraphService.getGndIdFromGndUri(resource)

                                valueTag."@entityId" = entityId
                                valueTag."@entityDdbUri" = ddbUrl + "/entity/" + entityId
                                try {
                                    entityService.getEntityDetails(entityId)
                                    valueTag."@isLink" = true
                                } catch (Exception e) {
                                    valueTag."@isLink" = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the xml source for a given item id
     * @param itemId the item id
     * @return the xml source for a given item id
     */
    def getItemXmlSource(itemId) {
        def result = []
        def apiResponse = ApiConsumer.getXml(configurationService.getBackendUrl(), "/items/" + itemId + "/source")

        if (apiResponse.isOk()) {
            result = apiResponse.getResponse().toXmlString()
        }
        else if (apiResponse.status != ApiResponse.HttpStatus.HTTP_404) {
            log.error "XMLMetadata: XML file could not be fetched"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return result
    }

    /**
     * Returns the indexing-profile for a given item id
     * @param itemId the item id
     * @return the indexing-profile for a given item id
     */
    def getItemIndexingProfile(itemId){
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl() ,'/items/'+itemId+'/indexing-profile')

        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        return apiResponse.getResponse()
    }

    /**
     * Returns the value of FacetEnum.APD_LEVEL_OF_DESCRIPTION_FCT for a given item id
     * @param itemId the item id
     * @return the value of FacetEnum.APD_LEVEL_OF_DESCRIPTION_FCT for a given item id
     */
    def getItemLevelOfDescription(itemId){
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl() ,'/search', false, ["query":itemId, "facet": FacetEnum.APD_LEVEL_OF_DESCRIPTION_FCT.getName()])
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        def itemLevelOfDescription = ""
        if(apiResponse.getResponse()){
            //iterate over all facets
            apiResponse.getResponse().facets.each(){ facet ->
                //iterate over all values of the FacetEnum and add matching names to the information
                if (facet['field'] == FacetEnum.APD_LEVEL_OF_DESCRIPTION_FCT.getName()) {
                    if (facet['facetValues'] && facet['facetValues']?.size() > 0) {
                        itemLevelOfDescription = facet['facetValues'].get(0).value
                    }
                }

            }
        }
        return itemLevelOfDescription
    }

    /**
     * Determines the total number of items and the number of items with digitalisat=true.
     *
     * @return item numbers
     */
    def getNumberOfItems() {
        def searchParams = [:]

        searchParams.put("query", "*")
        searchParams.put("offset", "0")
        searchParams.put("rows", "0")
        searchParams.put("facet", "category")
        searchParams.put("category", "dwpi")

        ApiResponse responseWrapper =
                ApiConsumer.getJson(configurationService.getBackendUrl(), "/search", false, searchParams)

        if (!responseWrapper.isOk()) {
            responseWrapper.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }

        int total = responseWrapper.getResponse().numberOfResults

        searchParams.put("facet", ["category", "digitalisat"])
        searchParams.put("digitalisat", true)
        responseWrapper = ApiConsumer.getJson(configurationService.getBackendUrl(), "/search", false, searchParams)
        if (!responseWrapper.isOk()) {
            responseWrapper.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }

        int withDigitizedMedia = responseWrapper.getResponse().numberOfResults

        return [total: total, withDigitizedMedia: withDigitizedMedia]
    }

    /**
     * Returns the value of FacetEnum.APD_DOCUMENT_TYPE_FCT for a given item id
     * @param itemId the item id
     * @return the value of FacetEnum.APD_DOCUMENT_TYPE_FCT for a given item id
     */
    def getApdDocumentType(itemId){
        def apiResponse = ApiConsumer.getJson(configurationService.getBackendUrl() ,'/search', false, ["query":itemId, "facet": FacetEnum.APD_DOCUMENT_TYPE_FCT.getName()])
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        def documentType = ""
        if(apiResponse.getResponse()){
            //iterate over all facets
            apiResponse.getResponse().facets.each(){ facet ->
                //iterate over all values of the FacetEnum and add matching names to the information
                if (facet['field'] == FacetEnum.APD_DOCUMENT_TYPE_FCT.getName()) {
                    if (facet['facetValues'] && facet['facetValues']?.size() > 0) {
                        documentType = facet['facetValues'].get(0).value
                    }
                }

            }
        }
        return documentType
    }

}
