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

import net.sf.json.JSON

import grails.converters.JSON

import java.text.SimpleDateFormat

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.support.RequestContextUtils

import de.ddb.common.beans.Folder
import de.ddb.common.beans.FolderList

/**
 * Service class for the FolderList mapping in the elastic search
 * 
 * @author boz
 */
class ListsService {

    public static final int DEFAULT_SIZE = 9999

    def bookmarksService
    def favoritesService
    def configurationService
    def elasticSearchService
    def transactional = false

    /**
     * Create a new {@link FolderList}
     *
     * @param newFolder FolderList object to persist
     * @return the id of the created FolderList
     */
    String createList(FolderList newFolderList) {
        log.info "createList(): creating a new folder: ${newFolderList}"

        String newFolderListId = null

        def postBody = [
            title : newFolderList.title,
            createdAt: newFolderList.creationDate.getTime(),
            users: newFolderList.users,
            folders: newFolderList.folders
        ]

        def postBodyAsJson = postBody as JSON
        log.info "postBodyAsJson" + postBodyAsJson
        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/folderList", false, postBodyAsJson)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            newFolderListId = response._id
            elasticSearchService.refresh()
        }
        return newFolderListId
    }

    /**
     * Finds all {@link FolderList} of the index
     *
     * @return all {@link FolderList} of the index
     */
    List<FolderList> findAllLists() {
        log.info "findAllLists()"

        List<FolderList> folderLists = []

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folderList/_search", false)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits

            resultList.each { it ->
                def folderList = mapJsonToFolderList(it)

                if(folderList && folderList.isValid()){
                    folderLists.add(folderList)
                }else{
                    log.error "findAllListsByUserId(): found corrupt list: " + folderList
                }
            }
        }
        return folderLists
    }

    /**
     * Finds all {@link FolderList} belonging to a userId
     * @param userId the id of a user
     * 
     * @return all {@link FolderList} belonging to a userId
     */
    List<FolderList> findListsByUserId(String userId) {
        log.info "findAllListsByUserId()"

        List<FolderList> folderLists = []

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folderList/_search", false,
                ["q":userId, "size":"${DEFAULT_SIZE}"])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits

            resultList.each { it ->
                def folderList = mapJsonToFolderList(it)

                if(folderList && folderList.isValid()){
                    folderLists.add(folderList)
                }else{
                    log.error "findAllListsByUserId(): found corrupt list: " + folderList
                }
            }
        }
        return folderLists
    }

    /**
     * Finds a {@link FolderList} by its id
     * @param listId the id of the list to search for
     * 
     * @return a {@link FolderList}
     */
    FolderList findListById(String listId) {
        log.info "findListById()"
        def retVal = null

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folderList/${listId}", false, [:])

        if(apiResponse.isOk()){
            def it = apiResponse.getResponse()
            retVal = mapJsonToFolderList(it)
        }
        return retVal
    }

    /**
     * Maps the JSON from a elasticsearch request to an {@link FolderList} instance
     * 
     * @return a {@link FolderList} instance of the JSON
     */
    private FolderList mapJsonToFolderList(def json) {

        def folderList = new FolderList(
                json._id,
                json._source.title,
                json._source.createdAt,
                json._source.users,
                json._source.folders
                )

        if(folderList.isValid()){
            return folderList
        }else{
            log.error "findFolderById(): found corrupt folder: " + folderList
        }
        return null
    }

    /**
     * Returns the number of lists available in the elasticsearch index
     * @return the number of lists in the search index
     */
    int getListCount() {
        log.info "getListCount()"
        return elasticSearchService.getDocumentCountByType("folderList")
    }

    /**
     * Deletes all lists belonging to a userId
     *
     * @param userId the id of a user
     *
     * @return <code>true</code> if at least one list has been deleted for the given userId
     */
    boolean deleteAllUserLists(String userId) {
        log.info "deleteAllUserLists()"
        List<FolderList> allUserFolders = findListsByUserId(userId)
        List<String> folderIds = []

        allUserFolders.each { it ->
            folderIds.add(it.folderListId)
        }
        return elasticSearchService.deleteTypeEntriesByIds(folderIds, "folderList")
    }

    /**
     * Returns all public folders
     *
     * @return all public folders
     */
    def getDdbAllPublicFolders(int offset=0, int size=20) {
        def result = bookmarksService.findAllPublicUnblockedFolders(offset, size)
        def folders = result.folders
        def folderCount = result.count

        //Enhance the folder object with additional information
        enhanceFolderInformation(folders)

        return ["count":folderCount, "folders":folders]
    }

    /**
     * Return all folders with a filled out "insititutionIds" attribute.
     *
     * @return all "collection" folders
     */
    def getCollections(int offset = 0, int size = 20) {
        def result = bookmarksService.findAllFoldersWithInstitutions(offset, size)

        //Enhance the folder object with additional information
        enhanceFolderInformation(result.folders)

        return ["count": result.count, "folders": result.folders]
    }

    /**
     * 
     * @param userId
     * @return
     */
    def getPublicFoldersForList(String listId, int offset=0, int size=20) {
        //Use a Set to avoid duplicates
        List<Folder> folders = []
        FolderList folderList = findListById(listId)

        //Retrieve the folder by userId and by folderId
        folderList?.users?.each {
            folders.addAll(bookmarksService.findAllPublicFolders(it, true))
        }

        folderList?.folders?.each {
            def folder = bookmarksService.findFolderById(it)
            if (folder && !folder.isBlocked) {
                folders.add(folder)
            }
        }

        //Sort the folders by updatedDate
        folders = folders.sort{a,b -> b.updatedDate <=> a.updatedDate }

        //Do the paging
        def range = offset..(offset+size-1)
        def foldersPaged = []
        range.each {
            if (it < folders.size()) {
                foldersPaged.add(folders.getAt(it))
            }
        }

        //Enhance the folder object with additional information
        enhanceFolderInformation(foldersPaged)

        return ["count":folders.size(), "folders":foldersPaged]
    }

    /**
     * Sorts the found folders and adds the number of favorites
     * @param folders the folder to enhance
     *
     * @return the enhanced Folder
     */
    private enhanceFolderInformation(def folders) {
        def request = RequestContextHolder.currentRequestAttributes().request
        Locale locale = RequestContextUtils.getLocale(request)

        folders.each {
            //Set the blocking token to ""
            it.blockingToken = ""

            //Get the first item of the folder
            def firstBookmark
            if (it.bookmarks) {
                firstBookmark = bookmarksService.findBookmarkById(it.bookmarks.get(0))
            }
            if (firstBookmark) {
                def itemMd = favoritesService.retrieveItemMD([firstBookmark], locale)
                it.thumbnailItemMetaData = itemMd.get(0)
                //Retrieve the number of favorites
                it.count = it.bookmarks.size()
            }
            //Fallback for old folder:if there is no bookmark list in the folder, get the image path of the oldest item
            else {
                List favoritesOfFolder = bookmarksService.findBookmarksByPublicFolderId(it.folderId)
                favoritesOfFolder.sort{it.creationDate}
                if (favoritesOfFolder.size() > 0) {
                    def itemMd = favoritesService.retrieveItemMD([favoritesOfFolder.get(0)], locale)
                    it.thumbnailItemMetaData = itemMd.get(0)
                }
                //Retrieve the number of favorites
                it.count = favoritesOfFolder.size()
            }

            it.creationDateFormatted = formatDate(it.creationDate, locale)
            it.updatedDateFormatted = formatDate(it.updatedDate, locale)
        }
    }

    /**
     * 
     * @param oldDate
     * @param locale
     * @return
     */
    private String formatDate(Date oldDate, Locale locale) {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd.MM.yyy")
        newFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"))
        return newFormat.format(oldDate)
    }
}
