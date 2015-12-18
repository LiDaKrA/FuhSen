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

import java.text.SimpleDateFormat

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.json.*
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.beans.Folder
import de.ddb.common.beans.User
import de.ddb.common.constants.FacetEnum
import de.ddb.common.constants.FolderConstants
import de.ddb.common.constants.SearchParamEnum
import de.ddb.common.constants.Type
import de.ddb.common.exception.EntityNotFoundException

class FavoritesService {
    static final String ORDER_ASC = "asc"
    static final String ORDER_DESC = "desc"

    static final String ORDER_BY_DATE = "date"
    static final String ORDER_BY_NUMBER = "number"
    static final String ORDER_BY_TITLE = "title"

    def transactional = false
    def bookmarksService
    def sessionService
    def grailsApplication
    def searchService
    def configurationService
    def messageSource
    def entityService
    def languageService

    def sortFolders(allFoldersInformations, Closure folderAccess = { o -> o }){
        allFoldersInformations = allFoldersInformations.sort({ o1, o2 ->
            if (isMainBookmarkFolder(folderAccess(o1))) {
                return -1
            }
            else if (isMainBookmarkFolder(folderAccess(o2))) {
                return 1
            }
            else {
                return folderAccess(o1).updatedDate.time < folderAccess(o2).updatedDate.time ? 1 : -1
            }
        })

        //Check for empty titles
        for (def folderInfo : allFoldersInformations) {
            if(folderAccess(folderInfo).title.trim().isEmpty()){
                folderAccess(folderInfo).title = "-"
            }
        }
        return allFoldersInformations
    }

    def Locale getLocale() {
        def webUtils = WebUtils.retrieveGrailsWebRequest()
        return languageService.getBestMatchingLocale(RequestContextUtils.getLocale(webUtils.getCurrentRequest()))
    }

    private def isMainBookmarkFolder(folder) {
        return folder.title == FolderConstants.MAIN_BOOKMARKS_FOLDER.value
    }

    def createAllFavoritesLink(Integer offset, Integer rows, String order, String by, Integer lastPgOffset, String folderId){
        def first = createFavoritesLinkNavigation(0, rows, order, by, folderId)
        if (offset < rows){
            first = null
        }
        def last = createFavoritesLinkNavigation(lastPgOffset, rows, order, by, folderId)
        if (offset >= lastPgOffset){
            last = null
        }
        return [
            firstPg: first,
            prevPg: createFavoritesLinkNavigation(offset.toInteger()-rows, rows, order, by, folderId),
            nextPg: createFavoritesLinkNavigation(offset.toInteger()+rows, rows, order, by, folderId),
            lastPg: last
        ]
    }

    def private createFavoritesLinkNavigation(offset,rows,order,by,folderId){
        def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
        return g.createLink(controller:'favoritesview', action: 'favorites',params:[(SearchParamEnum.OFFSET.getName()):offset,(SearchParamEnum.ROWS.getName()):rows, (SearchParamEnum.ORDER.getName()):order, (SearchParamEnum.BY.getName()):by,id:folderId])
    }

    def createAllPublicFavoritesLink(Integer offset, Integer rows, String order, String by, Integer lastPgOffset, String userId, String folderId){
        def first = createPublicFavoritesLinkNavigation(0, rows, order, userId, folderId, by)
        if (offset < rows){
            first = null
        }
        def last = createPublicFavoritesLinkNavigation(lastPgOffset, rows, order, userId, folderId, by)
        if (offset >= lastPgOffset){
            last = null
        }
        return [
            firstPg: first,
            prevPg: createPublicFavoritesLinkNavigation(offset.toInteger()-rows, rows, order, userId, folderId, by),
            nextPg: createPublicFavoritesLinkNavigation(offset.toInteger()+rows, rows, order, userId, folderId, by),
            lastPg: last
        ]
    }
    def private createPublicFavoritesLinkNavigation(Integer offset, Integer rows, String order, String userId, String folderId, String by){
        def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
        return g.createLink(controller:'favoritesview', action: 'publicFavorites', params:[userId: userId, folderId: folderId, (SearchParamEnum.OFFSET.getName()):offset, (SearchParamEnum.ROWS.getName()):rows, (SearchParamEnum.ORDER.getName()):order, (SearchParamEnum.BY.getName()):by])
    }

    /**
     * Retrieve from Backend the Metadata for the items retrieved from the favorites list
     * @param items
     * @return
     */
    def retrieveItemMD(List items, Locale locale){
        //Prepares a QUERY with many IDs and returns a list of the elements
        def allRes = returnItemsMD(items, locale,"search")

        // Add empty items for all orphaned elasticsearch bookmarks
        if(items.size() > allRes.size()){
            def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
            def dummyThumbnail = g.resource("plugin": "ddb-common", "dir": "images",
            "file": "/placeholder/searchResultMediaUnknown.png").toString()
            def dummyLabel = messageSource.getMessage("ddbcommon.Item_No_Longer_Exists", null, LocaleContextHolder.getLocale())
            def foundItemIds = allRes.collect{ it.id }
            items.each{
                // item not found
                if(!(it.itemId in foundItemIds)){
                    def thumbnail = dummyThumbnail
                    def label = dummyLabel

                    if(it.type == Type.ENTITY){
                        def entity = [:]
                        def entityDetails
                        try {
                            entityDetails = entityService.getEntityDetails(it.itemId)
                            label = entityDetails?.preferredName
                            thumbnail = entityDetails?.thumbnail
                        }
                        catch (EntityNotFoundException ee) {
                        }
                        def professions = entityDetails?.professionOrOccupation
                        def subtitle = ""
                        professions.each {
                            subtitle += it
                            if(it != professions.last())
                                subtitle +=", "
                        }
                        entity["id"] = it.itemId
                        entity["view"] = []
                        entity["label"] = label
                        entity["category"] = "Entity"
                        entity["preview"] = [:]
                        entity["preview"]["title"] = label
                        entity["preview"]["subtitle"] = subtitle
                        entity["preview"]["media"] = ["entity"]
                        entity["preview"]["thumbnail"] = thumbnail
                        allRes.add((net.sf.json.JSONObject) entity)
                        foundItemIds.add(it.itemId)
                    }
                    else{
                        def emptyDummyItem = [:]
                        emptyDummyItem["id"] = it.itemId
                        emptyDummyItem["view"] = []
                        emptyDummyItem["label"] = label
                        emptyDummyItem["latitude"] = ""
                        emptyDummyItem["longitude"] = ""
                        emptyDummyItem["category"] = "orphaned"
                        emptyDummyItem["preview"] = [:]
                        emptyDummyItem["preview"]["title"] = label
                        emptyDummyItem["preview"]["subtitle"] = ""
                        emptyDummyItem["preview"]["media"] = ["unknown"]
                        emptyDummyItem["preview"]["thumbnail"] = thumbnail
                        net.sf.json.JSONObject jsonDummyItem = (net.sf.json.JSONObject) emptyDummyItem
                        allRes.add(jsonDummyItem)
                    }
                }
            }
        }
        return allRes
    }

    /**
     * Creates an OR query to get all the Items MD in one query
     * @param items
     * @param locale
     * @return
     */
    private List returnItemsMD(List items, Locale locale, String endpoint) {
        def step = 20
        def orQuery=""
        def allRes = []

        //In Apd we need to check for specific objects which do not belog to archives
        //For this we need an additional query and then compare elements from each list
        def allArchiveRes = []
        def archiveParams = [
            (SearchParamEnum.FACETS.getName()): FacetEnum.SECTOR_FCT.getName(),
            (SearchParamEnum.FACETVALUES.getName()): FacetEnum.SECTOR_FCT.getName() + "=sec_01"
        ]

        items.eachWithIndex() { it, i ->
            if ( (i==0) || ( ((i>1)&&(i-1)%step==0)) ){
                orQuery=it.itemId
            }else if (i%step==0){
                orQuery=orQuery + " OR "+ it.itemId
                queryBackend(orQuery, locale,endpoint).each { item ->
                    allRes.add(item)
                }
                queryBackend(orQuery, locale,endpoint,archiveParams).each { item ->
                    allArchiveRes.add(item)
                }
                orQuery=""
            }else{
                orQuery+=" OR "+ it.itemId
            }
        }
        if (orQuery){
            queryBackend(orQuery,locale,endpoint).each { item ->
                allRes.add(item)
            }
            queryBackend(orQuery, locale,endpoint,archiveParams).each { item ->
                allArchiveRes.add(item)
            }
        }


        // add context path to thumbnails
        def contextPath = ServletContextHolder.servletContext.contextPath
        allRes.each {item ->
            def thumbnail = item?.preview?.thumbnail
            if (thumbnail && !thumbnail.startsWith(contextPath)) {
                item.preview.thumbnail = contextPath + thumbnail
            }
        }

        /**
         * If an element is found in the AllArchiveRes, then the elements isArchive is true!
         */
        allRes.each { item ->
            item.isArchive = allArchiveRes.find{it.id == item.id}
        }
        return allRes
    }

    private def orderFavoritesByNumber(def favorites, String folderId, String order) {
        def result = []

        // first use bookmark list in folder to order the favorites
        Folder folder = bookmarksService.findFolderById(folderId)
        def bookmarkIdsInFolder = folder?.bookmarks
        bookmarkIdsInFolder.each {bookmarkIdInFolder ->
            def favorite = favorites.find {it.bookmark.bookmarkId == bookmarkIdInFolder}
            if (favorite) {
                favorites.remove(favorite)
                result.add(favorite)
            }
        }

        // second add all favorites which are not present in bookmark list of the folder at the end
        result.addAll(favorites)

        // add orderNumber to all favorites
        result.eachWithIndex {favorite, index ->
            favorite.orderNumber = index
        }

        // update bookmark list in folder with the current list
        if (folder) {
            bookmarkIdsInFolder = result*.bookmark.bookmarkId
            folder.bookmarks = bookmarkIdsInFolder
            bookmarksService.updateFolder(folder, false)
        }

        if (order == ORDER_DESC) {
            result = result.reverse()
        }
        return result
    }

    def orderFavorites(def favorites, String folderId, String order, String by) {
        // order by number to get the "orderNumber" property filled out
        def result = orderFavoritesByNumber(favorites, folderId, order)
        if (order == ORDER_ASC) {
            if (by == ORDER_BY_DATE) {
                result = result.sort{ a, b ->
                    a.bookmark.creationDate.time <=> b.bookmark.creationDate.time
                }
            }
            else if (by == ORDER_BY_TITLE) {
                result = result.sort{it.label.toLowerCase()}.reverse()
            }
        }
        else { // desc
            if (by == ORDER_BY_TITLE) {
                result = result.sort{it.label.toLowerCase()}
            }
            else if (by == ORDER_BY_DATE) {
                result = result.sort{ a, b ->
                    b.bookmark.creationDate.time <=> a.bookmark.creationDate.time
                }
            }
        }
        return result
    }

    List pageList(List list, int rows, int offset) {
        def result
        if (offset != 0) {
            result = list.drop(offset)
            result = result.take(rows)
        } else {
            result = list.take(rows)
        }
        return result
    }

    def private queryBackend(String query, Locale locale, String endpoint, Map additionalParams=[:]){

        def params = [query: "id:(" + query + ")"]
        if (additionalParams!=[:]) {
            params<<additionalParams
        }
        def urlQuery = searchService.convertQueryParametersToSearchParameters(params)
        urlQuery[SearchParamEnum.OFFSET.getName()]=0
        urlQuery[SearchParamEnum.ROWS.getName()]=21
        def apiResponse = ApiConsumer.getJson(configurationService.getApisUrl() ,'/apis/'+endpoint, false, urlQuery)
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
        def resultsItems = apiResponse.getResponse()

        //Replacing the mediatype images when not coming from backend server
        resultsItems = searchService.checkAndReplaceMediaTypeImages(resultsItems)

        return resultsItems["results"]["docs"]
    }

    def getAllFoldersPerUser(String userId) {
        if (userId != null) {
            return bookmarksService.findAllFolders(userId)
        }
        else {
            log.info "getFavorites returns " + response.SC_UNAUTHORIZED
            return null
        }
    }

    def getFavoriteList(User user, Folder folder, String order = ORDER_ASC, String by = ORDER_BY_TITLE) {
        Locale locale = getLocale()
        List favorites = bookmarksService.findBookmarksByFolderId(user.id, folder.folderId)
        List favoritesWithMetadata = retrieveItemMD(favorites, locale)
        List result = addBookmarkToFavResults(favoritesWithMetadata, favorites, locale)
        result = addFolderToFavResults(result, folder)
        result = addCurrentUserToFavResults(result, user)
        return orderFavorites(result, folder.folderId, order, by)
    }

    def getFolderList(String userId) {
        def folderList = []
        bookmarksService.findAllFolders(userId).each { folder ->
            def container = [:]
            List favorites = bookmarksService.findBookmarksByFolderId(userId, folder.folderId)
            container["folder"] = folder
            container["count"] = favorites.size()
            folderList.add(container)
        }
        return sortFolders(folderList) {o -> o.folder}
    }

    List addBookmarkToFavResults(allRes, List items, Locale locale) {
        def all = []
        def temp = []
        allRes.each { searchItem->
            temp = []
            temp = searchItem
            for(int i=0; i<items.size(); i++){
                if(items.get(i).itemId == searchItem.id){
                    temp["bookmark"] = items.get(i).getAsMap()
                    temp["bookmark"]["creationDateFormatted"] = formatDate(items.get(i).creationDate, locale)
                    temp["bookmark"]["updateDateFormatted"] = formatDate(items.get(i).updateDate, locale)
                    break
                }
            }
            all.add(temp)
        }
        return all
    }

    List addFolderToFavResults(allRes, Folder folder) {
        def all = []
        def temp = []
        allRes.each { searchItem->
            temp = searchItem
            temp["folder"] = folder.getAsMap()
            all.add(temp)
        }
        return all
    }

    /**
     * Format a date depending on the given locale.
     *
     * German: 08.05.2014, 19:34 Uhr
     * English: 08.05.2014, 7:34 PM
     *
     * Java has a built-in date format "FULL" which returns nearly the needed format, only a few modifications are
     * necessary.
     *
     * @param date date to be formatted
     * @param locale locale
     *
     * @return formatted date string
     */
    public String formatDate(Date date, Locale locale = getLocale()) {
        String datePattern = "dd.MM.yyyy, " +
                SimpleDateFormat.getTimeInstance(SimpleDateFormat.FULL, locale).toLocalizedPattern()
        // remove time zone
        datePattern = datePattern.substring(0, datePattern.length() - 1)
        // remove seconds from English pattern
        datePattern = datePattern.replace(":ss", "")
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern)
        return dateFormat.format(date)
    }

    List addCurrentUserToFavResults(allRes, User user) {
        def userJson = [:]
        userJson["id"] = user.id
        userJson["username"] = user.username
        userJson["status"] = user.status
        userJson["firstname"] = user.firstname
        userJson["lastname"] = user.lastname
        userJson["email"] = user.email

        allRes.each { searchItem ->
            searchItem["user"] = userJson
        }
        return allRes
    }

    def createFavoritesFolderIfNotExisting(User user){
        log.info "createFavoritesFolderIfNotExisting()"
        def mainFavoriteFolder = bookmarksService.findMainBookmarksFolder(user.getId())

        if(mainFavoriteFolder == null){
            def publishingName = user.getUsername()
            def now = System.currentTimeMillis()
            Folder newFolder = new Folder(
                    null,
                    user.getId(),
                    FolderConstants.MAIN_BOOKMARKS_FOLDER.getValue(),
                    "",
                    false,
                    publishingName,
                    false,
                    "",
                    null,
                    now,
                    now,
                    null)
            String folderId = bookmarksService.createFolder(newFolder)
            log.info "createFavoritesFolderIfNotExisting(): no favorites folder yet -> created it: "+folderId
        }
    }
}
