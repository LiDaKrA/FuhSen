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


import grails.converters.JSON
import groovy.json.*
import de.ddb.common.beans.Bookmark
import de.ddb.common.beans.Folder
import de.ddb.common.constants.FolderConstants


/**
 * Set of Methods that encapsulate REST-calls to the BookmarksService
 *
 * @author crh
 *
 */
class BookmarksService {

    public static final boolean IS_PUBLIC = false
    public static final int DEFAULT_SIZE = 9999

    def elasticSearchService
    def configurationService
    def transactional = false

    /**
     * Create a new bookmark folder.
     *
     * @param newFolder Folder object to persist
     * @return the newly created folder ID.
     */
    String createFolder(Folder newFolder) {
        log.info "createFolder(): creating a new folder: ${newFolder}"

        String newFolderId = null

        def postBody = [
            user: newFolder.userId,
            title : newFolder.title,
            description: newFolder.description,
            isPublic : newFolder.isPublic,
            publishingName : newFolder.publishingName,
            isBlocked : false,
            blockingToken : "",
            createdAt : newFolder.creationDate.getTime(),
            updatedAt : newFolder.creationDate.getTime(),
            institutionIds : newFolder.institutionIds
        ]
        def postBodyAsJson = postBody as JSON

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/folder", false, postBodyAsJson)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            newFolderId = response._id
            elasticSearchService.refresh()
        }

        return newFolderId
    }

    /**
     * Returns the number of folders in the index
     * @return the number of folders in the index
     */
    int getFolderCount() {
        log.info "getFolderCount()"
        return elasticSearchService.getDocumentCountByType("folder")
    }

    /**
     * Returns the number of bookmarks in the index
     * @return the number of bookmarks in the index
     */
    int getBookmarkCount() {
        log.info "getBookmarkCount()"
        return elasticSearchService.getDocumentCountByType("bookmark")
    }


    /**
     * Find all public folder for a given userId
     * @param userId the userId to search the folders
     *
     * @return all public folder for a given userId
     */
    List<Folder> findAllPublicFolders(String userId, def mustBeUnblocked=false) {
        log.info "findAllPublicFolders(userId)"

        List<Folder> folders = findAllFolders(userId)
        List<Folder> publicFolders = []
        folders?.each {
            if(it.isPublic){
                if ((!mustBeUnblocked) || ( mustBeUnblocked && !it.isBlocked)) {
                    publicFolders.add(it)
                }
            }
        }
        return publicFolders
    }

    /**
     * Find all folders for a given userid.
     *
     * @param userId for which userid to search the folders
     * @param offset How many initial results should be skipped
     * @param size How many results should be returned
     *
     * @return a lists of folders for a given userid
     */
    List<Folder> findAllFolders(String userId, int offset=0, int size=DEFAULT_SIZE) {
        log.info "findAllFolders()"

        List<Folder> folderList = []

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folder/_search", false,
                ["q":userId, "size":"${size}", "from":"${offset}"])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            resultList.each { it ->
                try {
                    def folder = mapSourceToFolder(it)
                    if(folder.isValid()){
                        folderList.add(folder)
                    }else{
                        log.error "findAllFolders(): found corrupt folder: "+folder
                    }
                } catch(Exception e) {
                    log.error e.message + " findAllFolders(): found corrupt folder: " + it._source
                }
            }
        }
        return folderList
    }

    /**
     * Find all folders with the field "institutionIds" filled out.
     * Sort the result by "updatedAt".
     *
     * @return a lists of folders
     */
    def findAllFoldersWithInstitutions(int offset = 0, int size = 20) {
        List<Folder> folderList = []
        int count = -1
        def query = """{"sort": [{"updatedAt": {"order": "desc"}}],
                        "query": {"filtered" : {"filter": {"and": [{"exists": {"field": "institutionIds"}},
                                                                   {"term": {"isBlocked": "false"}}]}}}}"""
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(),
                "/ddb/folder/_search", false, ["source": query, "size": "${size}", "from": "${offset}"])

        if (apiResponse.isOk()) {
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            resultList.each {it ->
                try {
                    def folder = mapSourceToFolder(it)
                    if (folder.isValid()) {
                        folderList.add(folder)
                    }
                    else {
                        log.error "findAllFoldersWithInstitutions(): found corrupt folder: " + folder
                    }
                }
                catch (Exception e) {
                    log.error e.message + " findAllFoldersWithInstitutions(): found corrupt folder: " + it._source
                }
            }
            count = response.hits.total
        }
        return ["folders": folderList, "count": count]
    }

    /**
     * Find all folders sorted by the field updatedAt
     *
     * @param offset How many initial results should be skipped
     * @param size How many results should be returned
     *
     * @return a lists of folders for a given userid
     */
    def findAllPublicUnblockedFolders(int offset=0, int size=20) {
        log.info "findAllFolders(${offset} ${size})"

        List<Folder> folderList = []
        int count = -1

        def sortQuery = '{"sort" : [{ "updatedAt" : {"order" : "desc"}}],"query" : {"bool": {"must" : [{"term" : { "isPublic" : "true" }},{"term" : { "isBlocked" : "false" }}]}}}'

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folder/_search", false,
                ["source":sortQuery, "size":"${size}", "from":"${offset}"])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            resultList.each { it ->
                try {
                    def folder = mapSourceToFolder(it)
                    if(folder.isValid()){
                        folderList.add(folder)
                    }else{
                        log.error "findAllFolders(): found corrupt folder: "+folder
                    }
                } catch(Exception e) {
                    log.error e.message + " findAllFolders(): found corrupt folder: " + it._source
                }
            }

            count = response.hits.total
        }
        return ["folders":folderList, "count":count]
    }

    /**
     * Return all public folders that were created from a specific date in a range of 24 hours
     *
     * @return a list of public folders for a specific date
     */
    List<Folder> findAllPublicFoldersDaily(Date date) {
        log.info "findAllPublicFoldersOfToday()"

        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE,      cal.getActualMinimum(Calendar.MINUTE))
        cal.set(Calendar.SECOND,      cal.getActualMinimum(Calendar.SECOND))
        cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND))

        def timeFrom = cal.getTimeInMillis()
        def timeTo = timeFrom + 86400000

        List<Folder> folderList = []

        def dateQuery = '{"query": {"range" : {"createdAt" : {"gte" : ' + timeFrom + ',"lte" : ' + timeTo + ',"boost" : 2.0}}}}'

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folder/_search", false,
                ["source":dateQuery, "size":"${DEFAULT_SIZE}"])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            resultList.each { it ->
                def folder = mapSourceToFolder(it)
                if(folder.isValid()){
                    if (folder.isPublic) {
                        folderList.add(folder)
                    }
                }else{
                    log.error "findAllFolders(): found corrupt folder: "+folder
                }
            }
        }
        return folderList
    }


    private Folder mapSourceToFolder(def it) {
        def description = "null"
        if (!JsonUtil.isAnyNull(it._source.description)) {
            description = it._source.description
        }

        def createdAt = 0
        if (!JsonUtil.isAnyNull(it._source.createdAt)) {
            createdAt = it._source.createdAt
        }

        def updatedAt = 0
        if (!JsonUtil.isAnyNull(it._source.updatedAt)) {
            updatedAt = it._source.updatedAt
        }

        def institutionIds = null
        if (!JsonUtil.isAnyNull(it._source.institutionIds)) {
            institutionIds = it._source.institutionIds
        }

        def folder = new Folder(
                it._id,
                it._source.user,
                it._source.title,
                description,
                it._source.isPublic,
                it._source.publishingName,
                it._source.isBlocked,
                it._source.blockingToken,
                it._source.bookmarks as List,
                createdAt,
                updatedAt,
                institutionIds as List
                )
        return folder
    }

    /**
     * List all bookmarks in a folder that belongs to the user.
     *
     * @param userId    the ID whose the folders and bookmarks belongs to.
     * @param folderId  the ID of a certain folder. Use {@link #findAllFolders} to find out the folder IDs.
     * @return          a list of bookmarks.
     */
    List<Bookmark> findBookmarksByFolderId(String userId, String folderId) {
        log.info "findBookmarksByFolderId(): find bookmarks for the user (${userId}) in the folder ${folderId}"

        List<Bookmark> all = []
        def query = ["q":"\"${userId}\" AND folder:\"${folderId}\"".encodeAsURL(), "size":"${DEFAULT_SIZE}"]
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/bookmark/_search", false, query, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits.unique { a ->
                a._source.item
            }

            resultList.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type,
                        it._source.folder as List,
                        it._source.description,
                        it._source.updatedAt
                        )

                if(bookmark.isValid()){
                    all.add(bookmark)
                }else{
                    log.error "findBookmarksByFolderId(): found corrupt bookmark: "+bookmark
                }
            }
        }
        return all
    }

    /**
     * Returns the number of bookmarks in a folder that belongs to the user.
     *
     * @param userId    the ID whose the folders and bookmarks belongs to.
     * @param folderId  the ID of a certain folder. Use {@link #findAllFolders} to find out the folder IDs.
     * @return          the number of bookmarks in a folder that belongs to the user.
     */
    def countBookmarksInFolder(String userId, String folderId) {
        log.info "countBookmarksInFolder(): count bookmarks for the user (${userId}) in the folder ${folderId}"

        def count = -1

        def query = ["q":"\"${userId}\" AND folder:\"${folderId}\"".encodeAsURL(), "size":"${DEFAULT_SIZE}"]
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/bookmark/_search", false, query, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            count = response.hits.total
        }

        return count
    }

    /**
     * Returns the bookmarks for a given folderId.
     *
     * @param folderId the folder id to search for
     * @param offset the offset of the search
     * @param size the number of items returned by the search
     *
     * @return the bookmarks for a given folderId.
     */
    List<Bookmark> findBookmarksByFolderId(String folderId, int offset=0, int size=DEFAULT_SIZE) {
        log.info "findBookmarksByFolderId(): find bookmarks in the folder ${folderId}"

        List<Bookmark> all = []

        def query = ["q":"folder:\"${folderId}\"".encodeAsURL(), "size":"${size}", "from":"${offset}"]
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/bookmark/_search", false, query, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            resultList.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type,
                        it._source.folder as List,
                        it._source.description,
                        it._source.updatedAt
                        )
                if(bookmark.isValid()){
                    all.add(bookmark)
                }else{
                    log.error "findBookmarksByFolderId(): found corrupt bookmark: "+bookmark
                }
            }
        }

        return all
    }

    List<Bookmark> findBookmarksByPublicFolderId(String folderId) {
        log.info "findBookmarksByPublicFolderId(): find bookmarks in the folder ${folderId}"

        List<Bookmark> all = []

        Folder folder = findPublicFolderById(folderId)
        if(folder == null || !folder.isPublic){
            return []
        }

        def query = ["q":"folder:\"${folderId}\"".encodeAsURL(), "size":"${DEFAULT_SIZE}"]
        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/bookmark/_search", false, query, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            resultList.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type,
                        it._source.folder as List,
                        it._source.description,
                        it._source.updatedAt
                        )
                if(bookmark.isValid()){
                    all.add(bookmark)
                }else{
                    log.error "findBookmarksByPublicFolderId(): found corrupt bookmark: "+bookmark
                }
            }
        }

        return all
    }


    /**
     * Bookmark a cultural item in a folder for a certain user.
     *
     * @param bookmark  The Bookmark object to persist
     * @return          the created bookmark ID.
     */
    String createBookmark(Bookmark bookmark) {
        log.info "createBookmark()"

        // If no folder is given -> put it in the default favorites folder
        if(bookmark.folders == null || bookmark.folders.size()==0){
            Folder mainBookmarksFolder = findFoldersByTitle(bookmark.userId, FolderConstants.MAIN_BOOKMARKS_FOLDER.value)[0]
            bookmark.folders = [mainBookmarksFolder.folderId]
        }

        // If the given folder already contains an item with the same itemId -> skip
        List<Bookmark> bookmarkedItemsInFolder = findBookmarkedItemsInFolder(bookmark.userId, [bookmark.itemId], bookmark.folders[0])
        if(bookmarkedItemsInFolder.size() > 0 ){
            log.warn('The itemId is already in the folder')
            return null
        }

        String newBookmarkId = null

        def postBody = [
            user: bookmark.userId.toString(),
            folder: bookmark.folders,
            item: bookmark.itemId.toString(),
            createdAt: bookmark.creationDate.getTime(),
            type: bookmark.type.toString(),
            description: bookmark.description.toString(),
            updatedAt: new Date().getTime()
        ]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/bookmark", false, postBody as JSON)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            newBookmarkId = response._id
            elasticSearchService.refresh()
        }

        //Refresh the updateDate on each folder the bookmark has been added
        bookmark.folders.each {
            Folder folder = findFolderById(it)
            if (folder) {
                folder.addBookmark(newBookmarkId)
                updateFolder(folder)
            } else {
                log.warn "Cannot find bookmark folder: " + it
            }
        }

        return newBookmarkId
    }

    /**
     * Given a list of cultural item IDs, find which are bookmarked by the user.
     *
     * @param userId     the ID who bookmarked the cultural items.
     * @param itemIdList a list of cultural item IDs.
     * @return           the list of bookmarked items.
     */
    List<Bookmark> findBookmarksForItemIds(String userId, List<String> itemIdList) {
        log.info "findBookmarksForItemIds(): itemIdList ${itemIdList}"

        List<Bookmark> bookmarks = []

        def postBody = [filter: [terms: [item: itemIdList]]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/bookmark/_search", false, postBody as JSON, ["q":"user:\"${userId}\""])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "response as application/json: ${response}"

            response.hits.hits.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type,
                        it._source.folder as List,
                        it._source.description,
                        it._source.updatedAt
                        )

                if(bookmark.isValid()){
                    bookmarks.add(bookmark)
                }else{
                    log.error "findBookmarksForItemIds(): found corrupt bookmark: "+bookmark
                }
            }
        }

        return bookmarks
    }



    /**
     * Delete all entries of a given indexType for a userId.
     *
     * @param userId    the ID who belong these items.
     * @param idList    a list of ids
     * @param indexType the index type of the items to delete
     */
    private boolean deleteDocumentsByTypeAndIds(String userId, List<String> idList, String indexType) {
        log.info "deleteIndexTypeByIds()"

        boolean isBookmark = indexType.equalsIgnoreCase("bookmark")
        def postBody = ''
        idList.each { id ->
            postBody = postBody + '{ "delete" : { "_index" : "ddb", "_type" : "' + indexType + '", "_id" : "' + id + '" } }\n'
            if (isBookmark) {
                deleteBookmarkFromFolder(userId, id)
            }
        }
        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/" + indexType + "/_bulk", false, postBody)

        if(apiResponse.isOk()){
            elasticSearchService.refresh()
            return true
        }else{
            return false
        }
    }

    List<Folder> findFoldersByTitle(String userId, String title) {
        log.info "findFoldersByTitle(): finding a folder with the title ${title} for the user: ${userId}"

        List<Folder> all = []

        def postBody = [filter: [term: [title: title]]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/folder/_search", false, postBody as JSON, ["q":"user:\"${userId}\""])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits
            resultList.each { it ->
                Folder folder = mapSourceToFolder(it)

                if(folder.isValid()){
                    all.add(folder)
                }else{
                    log.error "findFoldersByTitle(): found corrupt folder: "+folder
                }
            }
        }

        return all
    }

    Folder findMainBookmarksFolder(String userId) {
        log.info "findMainBookmarksFolder()"
        Folder folder = null
        List allFolders = findAllFolders(userId)
        allFolders.each {
            if(it.title == FolderConstants.MAIN_BOOKMARKS_FOLDER.value){
                folder = it
            }
        }
        return folder
    }

    List<Bookmark> findBookmarksByUserId(String userId, int size = DEFAULT_SIZE) {
        log.info "findBookmarksByUserId()"

        List<Bookmark> all = []

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/bookmark/_search", false, "", ["q":"user:\"${userId}\"", "size":"${DEFAULT_SIZE}"])

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            def resultList = response.hits.hits

            resultList.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type,
                        it._source.folder as List,
                        it._source.description,
                        it._source.updatedAt
                        )

                if(bookmark.isValid()){
                    all.add(bookmark)
                }else{
                    log.error "findBookmarksByUserId(): found corrupt bookmark: "+bookmark
                }
            }
        }

        return all
    }

    boolean deleteBookmarksByItemIds(String userId, List<String> itemIds) {
        log.info "deleteBookmarksByItemIds()"
        def bookmarkIds = []
        def allBookmarks = findBookmarksByUserId(userId, DEFAULT_SIZE)
        allBookmarks.each { it ->
            if(it.itemId  in itemIds) {
                bookmarkIds.add(it.bookmarkId)
            }
        }
        return deleteDocumentsByTypeAndIds(userId, bookmarkIds, "bookmark")
    }

    /**
     * Removed the bookmarks and folder for a given userId
     *
     * @param userId the id of the user
     */
    void deleteAllUserContent(String userId) {
        deleteAllUserBookmarks(userId)
        deleteAllUserFolders(userId)
    }

    /**
     * Deletes all {@link Folder} belonging to a user
     * @param userId the id of the user
     *
     * @return <code>true</code> if the user bookmarks has been deleted
     */
    boolean deleteAllUserBookmarks(String userId) {
        log.info "deleteAllUserBookmarks()"
        def bookmarkIds = []
        def allBookmarksOfUser = findBookmarksByUserId(userId, DEFAULT_SIZE)
        allBookmarksOfUser.each { it ->
            bookmarkIds.add(it.bookmarkId)
        }
        return deleteDocumentsByTypeAndIds(userId, bookmarkIds, "bookmark")
    }

    /**
     * Deletes all {@link Folder} belonging to a user
     *
     * @param userId the id of the user
     *
     * @return <code>true</code> if at least one folder has been deleted for the given userId
     */
    boolean deleteAllUserFolders(String userId) {
        log.info "deleteAllUserFolders()"
        List<Folder> allUserFolders = findAllFolders(userId)

        List<String> folderIds = []

        allUserFolders.each { it ->
            folderIds.add(it.folderId)
        }

        return deleteDocumentsByTypeAndIds(userId, folderIds, "folder")

    }

    List<Bookmark> findBookmarkedItemsInFolder(String userId, List<String> itemIdList, String folderId) {
        log.info "findBookmarkedItemsInFolder(): itemIdList ${itemIdList}"

        def all = []

        def queryParameter = [:]
        if(folderId) {
            queryParameter = ["q":"user:\"${userId}\" AND folder:\"${folderId}\"".encodeAsURL(),"size":"${DEFAULT_SIZE}"]
        } else {
            queryParameter = ["q":"user:\"${userId}\"".encodeAsURL(),"size":"${DEFAULT_SIZE}"]
        }

        def postBody = [filter: [terms: [item: itemIdList]]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/bookmark/_search", false, postBody as JSON, queryParameter, [:], true)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "response as application/json: ${response}"
            def resultList = response.hits.hits
            resultList.each { it ->
                def bookmark = new Bookmark(
                        it._id,
                        it._source.user,
                        it._source.item,
                        it._source.createdAt,
                        it._source.type,
                        it._source.folder as List,
                        it._source.description,
                        it._source.updatedAt
                        )
                if(bookmark.isValid()){
                    all.add(bookmark)
                }else{
                    log.error "findBookmarkedItemsInFolder(): found corrupt bookmark: "+bookmark
                }
            }
        }

        return all
    }

    boolean isBookmarkOfUser(String itemId, String userId) {
        log.info "isBookmarkOfUser()"
        boolean result = false
        def bookmarks = findBookmarkedItemsInFolder(userId, [itemId], null)
        if (bookmarks != null && (bookmarks.size() > 0)) {
            result = true
        }
        return result
    }

    Bookmark findBookmarkById(String bookmarkId) {
        log.info "findBookmarkById()"

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/bookmark/${bookmarkId}", false, [:])

        if(apiResponse.isOk()){
            def it = apiResponse.getResponse()
            Bookmark bookmark = new Bookmark(
                    it._id,
                    it._source.user,
                    it._source.item,
                    it._source.createdAt,
                    it._source.type,
                    it._source.folder as List,
                    it._source.description,
                    it._source.updatedAt
                    )

            if(bookmark.isValid()){
                return bookmark
            }else{
                log.error "findBookmarkById(): found corrupt bookmark: "+bookmark
            }
        }
        return null
    }


    Folder findFolderById(String folderId) {
        log.info "findFolderById()"

        ApiResponse apiResponse = ApiConsumer.getJson(configurationService.getElasticSearchUrl(), "/ddb/folder/${folderId}", false, [:])

        if(apiResponse.isOk()){
            def it = apiResponse.getResponse()

            Folder folder = mapSourceToFolder(it)
            if(folder.isValid()){
                return folder
            }else{
                log.error "findFolderById(): found corrupt folder: "+folder
            }
        }
        return null
    }

    /**
     * Returns the first folder that matches the given institutionId
     *
     * @param institutionId the institutionId to search for
     * @return the first folder that matches the given institutionId. If no folder matches the id <code>null<code> is returned
     */
    Folder findFolderByInstitutionId(String institutionId) {
        log.info "findFolderByInstitutionId()"

        String baseUrl = configurationService.getElasticSearchUrl()

        if (baseUrl) {
            ApiResponse apiResponse = ApiConsumer.getJson(baseUrl, "/ddb/folder/_search", false, ["q":"institutionIds:"+institutionId,"size":"1"])

            if(apiResponse.isOk()){
                if (apiResponse.getResponse().hits.total > 0) {
                    def resultList = apiResponse.getResponse().hits.hits
                    def it = resultList[0]

                    Folder folder = mapSourceToFolder(it)
                    if(folder.isValid()){
                        return folder
                    }else{
                        log.error "findFolderByInstitutionId(): found corrupt folder: "+folder
                    }
                }
            }
        }
        return null
    }

    Folder findPublicFolderByInstitutionId(String institutionId) {
        Folder folder = findFolderByInstitutionId(institutionId)
        if(folder && folder?.isPublic){
            return folder
        }
        return null
    }

    Folder findPublicFolderById(String folderId) {
        log.info "findPublicFolderById()"

        Folder folder = findFolderById(folderId)
        if(folder?.isPublic){
            return folder
        }else{
            return null
        }
    }

    boolean folderExists(String userId, String folderTitle) {
        log.info "folderExists()"
        boolean result = false
        findAllFolders(userId).each {
            if(it.title == folderTitle) {
                result = true
            }
        }
        return result
    }

    void updateFolder(Folder folder) {
        updateFolder(folder, true)
    }

    void updateFolder(Folder folder, boolean updateUpdatedAt) {
        log.info "updateFolder()"

        def postBody = [doc: [title: folder.title, isPublic: folder.isPublic, publishingName: folder.publishingName,
                isBlocked: folder.isBlocked, blockingToken: folder.blockingToken,
                bookmarks: folder.bookmarks, institutionIds: folder.institutionIds]]

        if (folder.description) {
            postBody["doc"]["description"] = folder.description
        }
        if (updateUpdatedAt) {
            postBody["doc"]["updatedAt"] = new Date().getTime()
        }

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/folder/${folder.folderId}/_update", false, postBody as JSON)

        if(apiResponse.isOk()){
            elasticSearchService.refresh()
        }
    }

    void updateBookmarkDescription(String bookmarkId, String newDescription) {
        log.info "updateBookmarkDescription()"

        def postBody = [doc: [description: newDescription, updatedAt: System.currentTimeMillis()]]

        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/bookmark/${bookmarkId}/_update", false, postBody as JSON)

        if(apiResponse.isOk()){
            elasticSearchService.refresh()
        }
    }

    void removeBookmarksFromFolder(List<String> bookmarkIds, String folderId) {
        log.info "removeBookmarksFromFolder(): bookmarkIds="+bookmarkIds

        def postBody = ''
        bookmarkIds.each { it ->
            postBody = postBody +
                    '{ "delete" : {"_id" : "'+ it + '", "_type" : "bookmark", "_index" : "ddb"}}'+
                    '{ "script" : "ctx._source.folder.remove(otherFolder);", "params" : { "otherFolder" : "' + folderId + '"}}\n'
        }
        ApiResponse apiResponse = ApiConsumer.postJson(configurationService.getElasticSearchUrl(), "/ddb/bookmark/_bulk", false, postBody)

        if(apiResponse.isOk()){
            elasticSearchService.refresh()
        }
    }

    /**
     * Delete the bookmark id from the bookmark list of the folder.
     *
     * @param userId
     * @param bookmarkId
     */
    private void deleteBookmarkFromFolder(String userId, String bookmarkId) {
        log.info "deleteBookmarkFromFolder()"
        Bookmark bookmark = findBookmarkById(bookmarkId)
        if (bookmark?.folders) {
            bookmark.folders.each {folderId ->
                Folder folder
                if (folderId) {
                    folder = findFolderById(folderId)
                }
                else {
                    folder = findMainBookmarksFolder(userId)
                }
                if (folder) {
                    folder.deleteBookmark(bookmarkId)
                    updateFolder(folder)
                }
            }
        }
        else {
            Folder folder = findMainBookmarksFolder(userId)
            if (folder) {
                folder.deleteBookmark(bookmarkId)
                updateFolder(folder)
            }
        }
    }

    void deleteFolder(String folderId) {
        log.info "deleteFolder()"

        ApiResponse apiResponse = ApiConsumer.deleteJson(configurationService.getElasticSearchUrl(), "/ddb/folder/${folderId}", false)

        if(apiResponse.isOk()){
            def response = apiResponse.getResponse()
            log.info "Is folder with the ID ${folderId} deleted(true/false)? ${response.ok}"
            elasticSearchService.refresh()
        }
    }
}
