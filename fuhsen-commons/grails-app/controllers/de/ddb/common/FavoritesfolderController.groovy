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

import de.ddb.common.beans.Folder
import de.ddb.common.beans.User
import de.ddb.common.constants.FolderConstants

class FavoritesfolderController {
    def bookmarksService
    def userService

    def createFavoritesFolder() {
        log.info "createFavoritesFolder " + request.JSON
        int result = response.SC_BAD_REQUEST
        String title = sanitizeTextInput(request.JSON.title)
        String description = sanitizeTextInput(request.JSON.description)
        User user = userService.getUserFromSession()
        if (user) {
            if (!bookmarksService.folderExists(user.id, title)) {
                long now = System.currentTimeMillis()
                Folder folder = new Folder(
                        null,
                        user.getId(),
                        title,
                        description,
                        false,
                        user.getUsername(),
                        false,
                        "",
                        null,
                        now,
                        now,
                        null)
                if (bookmarksService.createFolder(folder)) {
                    result = response.SC_CREATED
                    flash.message = "ddbcommon.favorites_folder_create_succ"
                }
            } else {
                result = response.SC_CONFLICT
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "createFavoritesFolder returns " + result
        render(status: result)
    }

    def deleteFavoritesFolder() {
        log.info "deleteFavoritesFolder " + request.JSON
        def result = response.SC_BAD_REQUEST
        boolean deleteItems = request.JSON.deleteItems
        def folderId = request.JSON.folderId
        def User user = userService.getUserFromSession()
        if (user) {
            def foldersOfUser = bookmarksService.findAllFolders(user.getId())

            // 1) Check if the current user is really the owner of this folder, else deny
            // 2) Check if the folder is a default favorites folder -> if true, deny
            boolean isFolderOfUser = false
            boolean isDefaultFavoritesFolder = false
            foldersOfUser.each {
                if (it.folderId == folderId) {
                    isFolderOfUser = true
                    if (it.title == FolderConstants.MAIN_BOOKMARKS_FOLDER.value) {
                        isDefaultFavoritesFolder = true
                    }
                }
            }
            if (isFolderOfUser) {
                if (isDefaultFavoritesFolder) {
                    result = response.SC_FORBIDDEN

                }else{
                    def favorites = bookmarksService.findBookmarksByFolderId(user.getId(), folderId)

                    // delete items in ALL folders
                    if (deleteItems) {
                        // Find itemIDs of the selected folder
                        def itemIds = []
                        favorites.each {
                            itemIds.add(it.itemId)
                        }
                        // Delete itemIds in ALL folders
                        bookmarksService.deleteBookmarksByItemIds(user.getId(), itemIds)
                    }else{
                        // delete items only in the current folder
                        def bookmarkIds = []
                        favorites.each {
                            bookmarkIds.add(it.bookmarkId)
                        }
                        bookmarksService.deleteDocumentsByTypeAndIds(user.getId(), bookmarkIds, "bookmark")
                    }
                    bookmarksService.deleteFolder(folderId)
                    result = response.SC_OK
                    flash.message = "ddbcommon.favorites_folder_delete_succ"
                }
            } else {
                result = response.SC_UNAUTHORIZED
                flash.error = "ddbcommon.favorites_folder_delete_unauth"
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "deleteFavoritesFolder returns " + result
        render(status: result)
    }

    def editFavoritesFolder() {
        log.info "editFavoritesFolder " + request.JSON
        def result = response.SC_BAD_REQUEST
        def id = request.JSON.id
        def title = sanitizeTextInput(request.JSON.title)
        def description = sanitizeTextInput(request.JSON.description)
        def publishingType = request.JSON.name
        def isPublic = request.JSON.isPublic
        def User user = userService.getUserFromSession()
        if (user) {
            def publishingName = ""
            if (publishingType == FolderConstants.PUBLISHING_NAME_FULLNAME.getValue()) {
                publishingName = user.getFirstnameAndLastnameOrNickname()
            } else {
                publishingName = user.getUsername()
            }

            List foldersOfUser = bookmarksService.findAllFolders(user.getId())
            Folder folder = null

            // 1) Check if the current user is really the owner of this folder, else deny
            // 2) Check if the folder is a default favorites folder -> if true, deny
            // 3) If the user will publish an empty list -> deny
            boolean isFolderOfUser = false
            boolean isDefaultFavoritesFolder = false
            foldersOfUser.each {
                if (it.folderId == id) {
                    folder = it
                    // check if the favorites list is blocked
                    if (it.isBlocked) {
                        isPublic = false
                    }

                    isFolderOfUser = true
                    if (it.title == FolderConstants.MAIN_BOOKMARKS_FOLDER.value) {
                        isDefaultFavoritesFolder = true
                    }
                }
            }

            // Check if folder has at least one bookmark, empty folder cannot set public, see DDBNEXT-1517
            def bookmarkCount = bookmarksService.countBookmarksInFolder(user.getId(), folder.folderId)

            if (!isPublic || bookmarkCount > 0) {
                if(isFolderOfUser && !isDefaultFavoritesFolder){
                    folder.title = title
                    folder.description = description
                    folder.isPublic = isPublic
                    folder.publishingName = publishingName
                    bookmarksService.updateFolder(folder)
                    result = response.SC_OK
                    flash.message = "ddbcommon.favorites_folder_edit_succ"
                } else {
                    result = response.SC_UNAUTHORIZED
                }
            } else {
                //To work with flash.error, the response code must be 200
                result = response.SC_OK
                flash.error = "ddbcommon.favorites_folder_empty_cannot_publish"
            }
        } else {
            result = response.SC_UNAUTHORIZED
        }
        log.info "editFavoritesFolder returns " + result
        render(status: result)
    }

    def getFavoritesFolder() {
        log.info "getFavoritesFolder " + params.id
        def result = response.SC_NOT_FOUND
        def User user = userService.getUserFromSession()
        if (user) {
            Folder folder = bookmarksService.findFolderById(params.id)
            log.info "getFavoritesFolder returns " + folder
            folder.setBlockingToken("") // Don't expose the blockingToken to Javascript!
            render(folder as JSON)
        } else {
            result = response.SC_UNAUTHORIZED
            log.info "getFavoritesFolder returns " + result
            render(status: result)
        }
    }

    /**
     * Get a sorted list of all bookmark folders. The main folder is marked with "isMainFolder".
     *
     * @return sorted list of all bookmark folders
     */
    def getFavoritesFolders() {
        log.info "getFavoritesFolders"
        def User user = userService.getUserFromSession()
        if (user) {
            def mainFolder = bookmarksService.findMainBookmarksFolder(user.getId())
            def folders = bookmarksService.findAllFolders(user.getId())
            folders.find {it.folderId == mainFolder.folderId}.isMainFolder = true
            folders.each {it.blockingToken = ""} // Don't expose the blockingToken to Javascript
            //Sort the folders by updatedDate
            folders = folders.sort{a,b -> b.updatedDate <=> a.updatedDate }
            log.info "getFavoritesFolders returns " + folders
            render(folders as JSON)
        } else {
            log.info "getFavoritesFolders returns " + response.SC_UNAUTHORIZED
            render(status: response.SC_UNAUTHORIZED)
        }
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
