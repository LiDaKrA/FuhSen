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

import org.ccil.cowan.tagsoup.Parser

import de.ddb.common.JsonUtil
import de.ddb.common.beans.Bookmark
import de.ddb.common.beans.Folder
import de.ddb.common.beans.User
import de.ddb.common.constants.Type


/**
 * This controller is mainly used for AJAX calls.
 * All other favorites requests should be handled by FavoritesviewController
 *
 * @author boz
 */
class FavoritesController {
    def bookmarksService
    def userService

    def addFavorite() {
        log.info "addFavorite " + params.folderId + "," + params.id + "," + params.reqObjectType
        def result = response.SC_BAD_REQUEST
        def User user = userService.getUserFromSession()
        if (user) {
            Type bookmarkType = Type.valueOfName(params.reqObjectType)
            if (!bookmarkType) {
                bookmarkType = Type.CULTURAL_ITEM
            }
            Bookmark bookmark = new Bookmark(
                    null,
                    user.id,
                    params.id,
                    new Date().getTime(),
                    bookmarkType,
                    params.folderId ? [params.folderId]: null,
                    "",
                    new Date().getTime())
            if (bookmarksService.createBookmark(bookmark)) {
                result = response.SC_CREATED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "addFavorite returns " + result
        render(status: result)
    }

    def copyFavorites() {
        log.info "copyFavorites " + request.JSON
        def result = response.SC_BAD_REQUEST
        def favoriteIds = request.JSON.ids
        def folderIds = request.JSON.folders
        def User user = userService.getUserFromSession()
        if (user) {
            // Check if the folders to copy to are actually folders owned by this user (security)
            def foldersOfUser = bookmarksService.findAllFolders(user.id)
            boolean foldersOwnedByUser = true
            def allFolderIds = foldersOfUser.collect {it.folderId}
            folderIds.each {
                if (!(it in allFolderIds)) {
                    foldersOwnedByUser = false
                }
            }

            if(foldersOwnedByUser){
                folderIds.each { folderId ->
                    favoriteIds.each { favoriteId ->
                        Bookmark favoriteToCopy = bookmarksService.findBookmarkById(favoriteId)
                        String itemId = favoriteToCopy.itemId
                        // Check if the item already exists in the list
                        List favoritesInTargetFolder = bookmarksService.findBookmarkedItemsInFolder(user.id, [itemId], folderId)
                        // if not -> add it
                        if(favoritesInTargetFolder.size() == 0){
                            Bookmark bookmark = new Bookmark(
                                    null,
                                    user.id,
                                    itemId,
                                    favoriteToCopy.creationDate.getTime(),
                                    favoriteToCopy.type,
                                    [folderId],
                                    "",
                                    new Date().getTime())
                            bookmarksService.createBookmark(bookmark)
                        }
                    }
                }
                result = response.SC_NO_CONTENT
                flash.message = "ddbcommon.favorites_copy_succ"
            } else {
                result = response.SC_UNAUTHORIZED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "copyFavorites returns " + result
        render(status: result)
    }

    def deleteFavorite() {
        log.info "deleteFavorite " + params.id
        def result = response.SC_NOT_FOUND
        def User user = userService.getUserFromSession()
        if (user) {
            if (bookmarksService.deleteBookmarksByItemIds(user.id, [params.id])) {
                result = response.SC_NO_CONTENT
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteFavorite returns " + result
        render(status: result)
    }

    def deleteFavorites() {
        log.info "deleteFavorites " + request.JSON
        def itemIds = request.JSON.ids
        def folderId = request.JSON.folderId
        def result = response.SC_NOT_FOUND
        def User user = userService.getUserFromSession()
        if (user) {
            // Check if the items all belong to the current user
            boolean itemsAreOwnedByUser = true
            List<Bookmark> allBookmarksInFolder = bookmarksService.findBookmarksByFolderId(user.id, folderId)
            def allItemIdsInFolder = allBookmarksInFolder.collect {it.itemId}

            itemIds.each {
                if (!(it in allItemIdsInFolder)) {
                    itemsAreOwnedByUser = false
                }
            }

            if (itemsAreOwnedByUser) {
                if (itemIds?.size() > 0) {
                    //The complete list of folders from where bookmarks has been removed
                    Set<Folder> affectedFolders = []

                    def bookmarksToDelete = []

                    // Special case: if bookmarks are deleted in the main favorites folder -> delete them everywhere
                    //def mainFavoriteFolder = favoritesPageService.getMainFavoritesFolder()
                    def mainFavoriteFolder = bookmarksService.findMainBookmarksFolder(user.id)

                    //MainFolder
                    if(folderId == mainFavoriteFolder.folderId) {
                        bookmarksToDelete = bookmarksService.findBookmarkedItemsInFolder(user.id, itemIds, null)
                        bookmarksToDelete.each { b ->
                            b.folders.each { f ->
                                if (!JsonUtil.isAnyNull(f)) {
                                    Folder folder = bookmarksService.findFolderById(f)
                                    affectedFolders.add(folder)
                                }
                            }
                        }

                        bookmarksService.deleteBookmarksByItemIds(user.id, itemIds)
                    } else {
                        //other folders
                        bookmarksToDelete = bookmarksService.findBookmarkedItemsInFolder(user.id, itemIds, folderId)

                        def favoriteIds = bookmarksToDelete.collect { it.bookmarkId }
                        bookmarksService.removeBookmarksFromFolder(favoriteIds, folderId)

                        affectedFolders.add(bookmarksService.findFolderById(folderId))
                    }

                    //If the affectedFolders are public and has no items left -> set folder to private DDBNEXT-1517
                    affectedFolders.each { folder ->
                        if (folder.isPublic) {
                            def bookmarkCount = bookmarksService.countBookmarksInFolder(user.id, folder.folderId)
                            if (bookmarkCount == 0) {
                                folder.isPublic = false
                                flash.message = "ddbcommon.favorites_folder_empty_set_to_private"
                            }
                        }
                        bookmarksService.updateFolder(folder)
                    }
                    result = response.SC_NO_CONTENT
                }
                else {
                    result = response.SC_NOT_FOUND
                }
            } else {
                result = response.SC_UNAUTHORIZED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteFavorites returns " + result
        render(status: result)
    }

    /**
     * Filter out a list of item id's so that the result contains only those item id's which are already bookmarked.
     *
     * @return filtered list of item id's
     */
    def filterFavorites() {
        log.info "filterFavorites " + request.JSON
        User user = userService.getUserFromSession()
        if (user) {
            Folder mainFavoritesFolder = bookmarksService.findMainBookmarksFolder(user.id)
            def bookmarks = bookmarksService.findBookmarkedItemsInFolder(user.id, request.JSON, mainFavoritesFolder.folderId)
            List result = bookmarks.collect {it.itemId}
            log.info "filterFavorites returns " + result
            render(result as JSON)
        } else {
            log.info "filterFavorites returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def getFavorite() {
        log.info "getFavorite " + params.id
        def result = response.SC_NOT_FOUND
        User user = userService.getUserFromSession()
        if (user) {
            def bookmark = bookmarksService.findBookmarkedItemsInFolder(user.id, [params.id], null)
            log.info "getFavorite returns " + bookmark
            render(bookmark as JSON)
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "getFavorite returns " + result
        render(status: result)
    }

    def getFavorites() {
        log.info "getFavorites"
        User user = userService.getUserFromSession()
        if (user) {
            Folder folder
            if (params.folderId) {
                folder = bookmarksService.findFolderById(params.folderId)
            }
            else {
                folder = bookmarksService.findMainBookmarksFolder(user.id)
            }
            def result = bookmarksService.findBookmarksByFolderId(user.id, folder.folderId)
            log.info "getFavorites returns " + result
            render(result as JSON)
        } else {
            log.info "getFavorites returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def moveFavorite() {
        log.info "moveFavorite " + params.id + "," + request.JSON
        User user = userService.getUserFromSession()
        if (user) {
            def folder
            if (request.JSON.folderId) {
                folder = bookmarksService.findFolderById(request.JSON.folderId)
            } else {
                folder = bookmarksService.findMainBookmarksFolder(user.id)
            }
            if (folder) {
                folder.moveBookmark(params.id, request.JSON.position)
                bookmarksService.updateFolder(folder)
                render(status: response.SC_NO_CONTENT)
            } else {
                log.info "moveFavorite returns " + response.SC_NOT_FOUND
                render(status: response.SC_NOT_FOUND)
            }
        } else {
            log.info "moveFavorite returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
    }

    def setComment() {
        log.info "setComment " + request.JSON
        def result = response.SC_BAD_REQUEST
        def id = request.JSON.id
        def text = request.JSON.text
        Parser tagsoupParser = new Parser()
        XmlSlurper slurper = new XmlSlurper(tagsoupParser)
        String cleanedText = slurper.parseText(text).text()
        cleanedText = sanitizeTextInput(cleanedText)
        def User user = userService.getUserFromSession()
        if (user) {
            // 1) Check if the current user is really the owner of this bookmark, else deny
            Bookmark bookmark = bookmarksService.findBookmarkById(id)
            boolean isBookmarkOfUser = false
            if (bookmark.userId == user.id) {
                isBookmarkOfUser = true
            }
            if (isBookmarkOfUser) {
                bookmarksService.updateBookmarkDescription(id, cleanedText)
                result = response.SC_OK
            } else {
                result = response.SC_UNAUTHORIZED
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "setComment returns " + result
        render(status: result)
    }

    def togglePublish() {
        log.info "togglePublish " + request.JSON
        def result = response.SC_BAD_REQUEST
        User user = userService.getUserFromSession()
        if (user) {
            // 1) Check if the current user is really the owner of this folder, else deny
            Folder folder = bookmarksService.findFolderById(request.JSON.id)
            boolean isFolderOfUser = false
            if (folder.userId == user.id) {
                isFolderOfUser = true
            }

            // 2) Check if folder has at least one bookmark, empty folder cannot be published see DDBNEXT-1517
            def bookmarkCount = bookmarksService.countBookmarksInFolder(user.id, folder.folderId)
            if (folder.isPublic || bookmarkCount > 0) {
                if(isFolderOfUser && !folder.isBlocked){
                    folder.isPublic = !folder.isPublic
                    bookmarksService.updateFolder(folder)
                    result = response.SC_OK
                } else {
                    result = response.SC_UNAUTHORIZED
                }
            } else {
                //To work with flash.error, the response code must be 200
                result = response.SC_OK
                flash.error =  "ddbcommon.favorites_folder_empty_cannot_publish"
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "togglePublish returns " + result
        render(status: result)
    }

    // private methods

    private String sanitizeTextInput(String input) {
        String output = ""
        if (input) {
            Parser tagsoupParser = new Parser()
            XmlSlurper slurper = new XmlSlurper(tagsoupParser)
            output = input
            output = slurper.parseText(output).text()
            output = output.replaceAll("\\\"", "''")
            output = output.replaceAll("´", "'")
            output = output.replaceAll("`", "'")
        }
        return output
    }
}
