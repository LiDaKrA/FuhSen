package de.ddb.next

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin

import org.junit.*

import de.ddb.common.BookmarksService
import de.ddb.common.beans.Bookmark
import de.ddb.common.beans.Folder
import de.ddb.common.constants.FolderConstants
import de.ddb.common.constants.Type


@TestMixin(IntegrationTestMixin)
class BookmarkServiceIntegrationTests {

    private static final def SIZE = 99999

    def bookmarksService

    /** The userId is refreshed in setUp() for every test method*/
    def userId = null


    /**
     * Is called before every test method.
     * Creates a new userId for the test.
     */
    void setUp() {
        println "--------------------------------------------------------------------"
        println "Setup tests"
        userId = UUID.randomUUID() as String
        logStats()
    }

    /**
     * Is called after every test method.
     * Cleanup user content created by a test method
     */
    void tearDown() {
        println "Cleanup tests"

        List<Bookmark> bookmarks = bookmarksService.findBookmarksByUserId(userId)
        println "User bookmarks after test: " + bookmarks.size()

        List<Folder> folders = bookmarksService.findAllFolders(userId)
        println "User folders after test: " + folders.size()

        boolean contentDeleted = bookmarksService.deleteAllUserContent(userId)
        println "User content deleted: : " + contentDeleted

        logStats()
        println "--------------------------------------------------------------------"
    }


    def logStats() {
        println "userId " + userId
        println "Index has " + bookmarksService.getFolderCount() + " folders"
        println "Index has " + bookmarksService.getBookmarkCount() + " bookmarks"
    }

    private String createBookmark(String itemId, String folderId) {
        long now = new Date().getTime()
        return bookmarksService.createBookmark(new Bookmark(
        String.valueOf(now),
        userId,
        itemId,
        new Date().getTime(),
        Type.CULTURAL_ITEM,
        [folderId],
        "",
        now))
    }

    def createNewFolder() {
        def now = System.currentTimeMillis()
        def folderTitle= 'Favorites-' + new Date().getTime().toString()
        def isPublic = true
        def publishingName = FolderConstants.PUBLISHING_NAME_USERNAME.getValue()

        Folder newFolder = new Folder(
                null,
                userId,
                folderTitle,
                "",
                isPublic,
                publishingName,
                false,
                "",
                null,
                now,
                now,
                null)
        return bookmarksService.createFolder(newFolder)
    }

    def createNewInstitutionFolder(String institutionId) {
        def now = System.currentTimeMillis()
        def folderTitle= 'Highlights-' + institutionId
        def isPublic = true
        def publishingName = FolderConstants.PUBLISHING_NAME_USERNAME.getValue()

        Folder newFolder = new Folder(
                null,
                userId,
                folderTitle,
                "",
                isPublic,
                publishingName,
                false,
                "",
                null,
                now,
                now,
                [institutionId])
        return bookmarksService.createFolder(newFolder)
    }


    // Folder
    @Test void shouldCreateNewFolder() {
        def folderId = createNewFolder()
        assertNotNull folderId
        log.info "Created a bookmark folder with the ID: ${folderId}"
    }

    @Test void shouldGetAllUserFolders() {

        def folderId = createNewFolder()
        assert folderId

        def folderList = bookmarksService.findAllFolders(userId)
        assertTrue folderList.size() >0

        if(folderList) {
            log.info "The user with the ID: ${userId} has: "
            folderList.each { it ->
                log.info "- ${it}"
            }
        } else {
            log.info 'empty folder.'
        }
    }

    @Test void shouldGetAllFolders() {

        def folderId = createNewFolder()
        assert folderId

        def folderList = bookmarksService.findAllPublicUnblockedFolders()
        assertTrue folderList.size() >0

        if(folderList) {
            log.info ": "
            folderList.each { it ->
                log.info "- ${it}"
            }
        } else {
            log.info 'empty folder.'
        }
    }

    @Test void shouldFindAllPublicFoldersDaily() {
        def now = new Date()
        //Create a new folder with an old creation date set
        Folder newFolder = new Folder(
                null,
                userId,
                "Old folder",
                "",
                true,
                "My old folder",
                false,
                "",
                null,
                0,
                0,
                null)
        def folderId = bookmarksService.createFolder(newFolder)
        assertNotNull folderId

        //Create a new folder with creationdate now

        folderId = createNewFolder()
        assertNotNull folderId

        def folderList = bookmarksService.findAllPublicFoldersDaily(now)
        assertTrue folderList.size() > 0
    }

    // Bookmark
    @Test void shouldSaveBookmarkInFolder() {
        def folderId = createNewFolder()
        def itemId = 'foobar'
        def bookmarkId = createBookmark(itemId, folderId)
        assertNotNull bookmarkId
        log.info 'bookmark is saved, ID is: ' + bookmarkId
    }

    @Test void shouldFindBookmarksByFolderId() {
        def folderId = createNewFolder()
        def itemId = 'foobarbaz'
        def bookmarkId = createBookmark(itemId, folderId)
        def bookmarks = bookmarksService.findBookmarksByFolderId(userId, folderId)
        assert bookmarks.size() > 0
    }

    @Test void shouldFindBookmarkedItems() {
        def folderId = createNewFolder()
        def itemId = 'F2D23TGU7NMP5MGVF647Q63X3E32W4YI'
        def bookmarkId = createBookmark(itemId, folderId)
        def foundBookmarkedItems = bookmarksService.findBookmarksForItemIds(userId, [itemId])
        assert foundBookmarkedItems.size() > 0
    }

    // Favorites
    @Test void shouldAddItemToUserFavorite() {
        log.info "should add item to the user's Favorites"

        def itemId = UUID.randomUUID() as String
        def folderId = 'foo'
        String favoriteId = createBookmark(itemId, folderId)
        assert favoriteId != null

        log.info "The user ${userId} just added item ${itemId} to their Favorites folder favoriteId"
    }

    @Test void shouldFindFoldersByTitle() {
        log.info "the bookmark service should find folders by its title."
        def now = System.currentTimeMillis()
        Folder newFolder = new Folder(
                null,
                userId,
                FolderConstants.MAIN_BOOKMARKS_FOLDER.getValue(),
                "",
                BookmarksService.IS_PUBLIC,
                FolderConstants.PUBLISHING_NAME_USERNAME.getValue(),
                false,
                "",
                null,
                now,
                now,
                null)
        String folderId = bookmarksService.createFolder(newFolder)
        log.info "the bookmark service created a ${FolderConstants.MAIN_BOOKMARKS_FOLDER.value} folder(${folderId}) for a user(${userId})"

        def favFolderList = bookmarksService.findFoldersByTitle(userId, FolderConstants.MAIN_BOOKMARKS_FOLDER.value)
        log.info "The user(${userId}) has ${favFolderList.size()} folders with the title `Favorites`"

        if(!favFolderList) {
            favFolderList = bookmarksService.findFoldersByTitle(userId, FolderConstants.MAIN_BOOKMARKS_FOLDER.value)
            log.info "Second try, the user(${userId}) has ${favFolderList.size()} folders with the title `Favorites`"
        }

        assert favFolderList.size() == 1
        assertEquals favFolderList[0].folderId, folderId
    }

    @Test void shouldGetAllUserFavorites() {
        def firstItemId = 'foobarbaz'
        def secondItemId = UUID.randomUUID() as String
        def folderId = 'foo'

        // if the user don't have a favorite list, then the service should create it.
        def firstFav = createBookmark(firstItemId, folderId)
        def allFavs = bookmarksService.findBookmarksByUserId(userId)
        assert allFavs.size() > 0
    }

    @Test void shouldDeleteFavoritesByItemIDs() {
        log.info "the bookmark service should delete favorites by item IDs."

        def firstItemId = 'foobarbaz'
        def folderId = 'foo'

        log.info "adding item ${firstItemId} to the folder Favorite."

        def firstFav = createBookmark(firstItemId, folderId)
        def allFavs = bookmarksService.findBookmarksByUserId(userId)
        assert allFavs.size() == 1
        assert allFavs[0].itemId == firstItemId

        def itemIds = [firstItemId]
        assert bookmarksService.deleteBookmarksByItemIds(userId, itemIds) == true

        def emptyFavs = bookmarksService.findBookmarksByUserId(userId)
        assert emptyFavs.size() == 0
    }

    @Test void shouldFindFavoritesByItemIds() {
        log.info "the bookmark service should find favorites by item IDs."

        def folderId = 'foo'

        def firstItemId = 'F2D23TGU7NMP5MGVF647Q63X3E32W4YIn84O2mBlSiassU1aNYIysA'
        log.info "adding item ${firstItemId} to the folder Favorite."
        def firstFavId = createBookmark(firstItemId, folderId)
        def secondItemId = 'U3TWCZVFIHOC6A65ICIC3UIEYRCR2LKL'
        log.info "adding item ${secondItemId} to the folder Favorite."

        def secondFavId = createBookmark(secondItemId, folderId)

        def itemIds = [firstItemId, secondItemId]

        def foundBookmarkedItems = bookmarksService.findBookmarkedItemsInFolder(userId, itemIds, null)
        assert foundBookmarkedItems.size() > 0
    }

    @Test void shouldNotAddSameItemIdMoreThanOnce() {
        log.info "the bookmark service should _not_ add the same item IDs more than once to favorites."

        def folderId = 'foo'

        def firstItemId = 'F2D23TGU7NMP5MGVF647Q63X3E32W4YI'
        log.info "adding item ${firstItemId} to the folder Favorite."
        def firstFavId = createBookmark(firstItemId, folderId)

        def secondItemId = 'F2D23TGU7NMP5MGVF647Q63X3E32W4YI'
        log.info "adding item ${secondItemId} _again_ to the folder Favorite."
        def secondFavId = createBookmark(secondItemId, folderId)

        assert secondFavId == null
    }

    @Test void shouldFindFavoriteByItemId() {
        log.info "the bookmark service should find favorite by item ID"

        def folderId = 'foo'

        def firstItemId = 'F2D23TGU7NMP5MGVF647Q63X3E32W4YI'
        log.info "adding item ${firstItemId} to the folder Favorite."
        def firstFavId = createBookmark(firstItemId, folderId)

        def favoritesForItem = bookmarksService.findBookmarkedItemsInFolder(userId, [firstItemId], null)
        assert favoritesForItem != null
        assert favoritesForItem.size() == 1
        assert favoritesForItem[0].itemId == firstItemId
        log.info favoritesForItem
    }

    @Test void shouldReturnMoreThanTenFavorites() {
        log.info "the bookmark service should find more than 10 favorites"

        def folderId = 'foo'

        11.times {
            def favId = createBookmark(UUID.randomUUID() as String, folderId)
            log.info("Bookmark ${favId} is created." )
        }

        def allFavs = bookmarksService.findBookmarksByUserId(userId)
        log.info('all favorites is more than 10: ' + allFavs.size())
        assert allFavs.size() > 10
    }

    @Test void shouldSaveInstitutionAsFavorites() {
        log.info "should save institution as user's Favorites"
        // should add a cultural item to user's favorite list.

        def folderId = 'foo'

        // if the user don't have a favorite list, then the service should create it.
        Bookmark newBookmark = new Bookmark(
                null,
                userId,
                UUID.randomUUID() as String,
                new Date().getTime(),
                Type.INSTITUTION,
                [folderId],
                "",
                new Date().getTime())
        def favoriteId = bookmarksService.createBookmark(newBookmark)
        assert favoriteId != null
        log.info "The user ${userId} just added an institution ${newBookmark.itemId} to their Favorites folder(favoriteId)"

        def favoritesForInstitution = bookmarksService.findBookmarkedItemsInFolder(userId, [newBookmark.itemId], null)
        log.info("fav is: ${favoritesForInstitution }")
        assert favoritesForInstitution.size() == 1
        assert favoritesForInstitution[0].itemId == newBookmark.itemId
        assert favoritesForInstitution[0].type == Type.INSTITUTION
    }

    // TODO: problem Favorites VS Bookmarks?
    // @Test void shouldSaveFavoritesWithOptionalTitleOrDescription() { assert false }
    // @Test void shouldFindFavoritesByUserIdAndFolderId() { assert false }

    @Test void shouldCreateNewFolderWithDescription() {

        def now = System.currentTimeMillis()
        def folderTitle = 'Favorites-' + new Date().getTime().toString()
        def description = 'folder description'
        def publishingName = FolderConstants.PUBLISHING_NAME_USERNAME.value
        def isPublic = true

        Folder newFolder = new Folder(
                null,
                userId,
                folderTitle,
                description,
                isPublic,
                publishingName,
                false,
                "",
                null,
                now,
                now,
                null)
        String folderId = bookmarksService.createFolder(newFolder)

        def folders = bookmarksService.findAllFolders(userId)
        assert folders[0].description == description

        log.info "folder's description ${folders[0].description}"
    }


    @Test void shouldFindFavoriteById() {

        def now = System.currentTimeMillis()
        def folderTitle= 'Favorites-' + new Date().getTime().toString()
        def isPublic = true

        Folder newFolder = new Folder(
                null,
                userId,
                folderTitle,
                "",
                isPublic,
                FolderConstants.PUBLISHING_NAME_USERNAME.getValue(),
                false,
                "",
                null,
                now,
                now,
                null)
        String itemId = UUID.randomUUID() as String
        String folderId = bookmarksService.createFolder(newFolder)
        def favoriteId = createBookmark(itemId, folderId)

        assert favoriteId != null
        log.info "The user ${userId} just added item ${itemId} to their Favorites folder favoriteId ${favoriteId}"

        def favorite = bookmarksService.findBookmarkById(favoriteId)
        assert favorite.bookmarkId == favoriteId
    }

    //    @Test void shouldCopyFavoritesToFolders() {
    //        log.info "should copy more than one favorites to more than one folder."
    //        def folderTitle = 'foo'
    //
    //        // create a favorite that not belongs to any folder, i.e. folder: [].
    //        userId = UUID.randomUUID() as String
    //        def itemId = UUID.randomUUID() as String
    //        def favoriteId = bookmarksService.addBookmark(userId, itemId, null, Type.CULTURAL_ITEM, [folderTitle])
    //
    //        // create two folders
    //        def folderId = createNewFolder()
    //        def otherFolderId = createNewFolder()
    //
    //        // copy the favorite to the new folder, i.e., folder: [${folderId}]
    //        bookmarksService.copyBookmarksToFolders([favoriteId], [folderId, otherFolderId])
    //        def found = bookmarksService.findBookmarkById(favoriteId)
    //        log.info "found: ${found.bookmarkId}"
    //        assert found.folders.size() == 3
    //    }

    @Test void shouldChangeFolderTitleOrDescription() {
        log.info "should change folder's title or its description."
        def now = System.currentTimeMillis()
        def folderTitle = 'foo'

        Folder newFolder = new Folder(
                null,
                userId,
                folderTitle,
                "",
                BookmarksService.IS_PUBLIC,
                FolderConstants.PUBLISHING_NAME_USERNAME.getValue(),
                false,
                "",
                null,
                now,
                now,
                null)
        String folderId = bookmarksService.createFolder(newFolder)
        log.info "the bookmark service created a ${folderTitle} folder(${folderId}) for a user(${userId})"

        def newTitle = "bar"
        def newDescription = "new desc"

        newFolder.folderId = folderId
        newFolder.title = newTitle
        newFolder.description = newDescription
        bookmarksService.updateFolder(newFolder)

        def updatedFolder = bookmarksService.findFolderById(folderId)
        assert updatedFolder.title == newTitle
        assert updatedFolder.description == newDescription
    }

    @Test void shouldRemoveFavoritesFromFolder() {
        log.info "should remove a few favorites from a folder."

        def now = System.currentTimeMillis()
        def folderTitle = 'foo'

        Folder newFolder = new Folder(
                null,
                userId,
                folderTitle,
                "",
                BookmarksService.IS_PUBLIC,
                FolderConstants.PUBLISHING_NAME_USERNAME.getValue(),
                false,
                "",
                null,
                now,
                now,
                null)
        String folderId = bookmarksService.createFolder(newFolder)
        log.info "the bookmark service created a ${folderTitle} folder(${folderId}) for a user(${userId})"

        // create two favorites
        def favoriteId = createBookmark(UUID.randomUUID() as String, folderId)
        def otherFavoriteId = createBookmark(UUID.randomUUID() as String, folderId)

        def favorites = bookmarksService.findBookmarksByUserId(userId)

        favorites.each { it ->
            assert it.folders[0] == folderId
        }

        bookmarksService.removeBookmarksFromFolder([favoriteId, otherFavoriteId], folderId)

        def favorite = bookmarksService.findBookmarkById(favoriteId)
        assert favorite == null

        def otherFavorite = bookmarksService.findBookmarkById(otherFavoriteId)
        assert otherFavorite == null
    }

    @Test void shouldDeleteFolder() {
        def folderId = createNewFolder()

        def aNewFolder = bookmarksService.findFolderById(folderId)
        assert aNewFolder.folderId == folderId

        bookmarksService.deleteFolder(folderId)

        def folder = bookmarksService.findFolderById(folderId)
        assert folder == null
    }

    @Test void shouldDeleteAllUserFavorites() {
        def folderId = 'foo'

        def favoriteId = createBookmark(UUID.randomUUID() as String, folderId)
        def otherFavId = createBookmark(UUID.randomUUID() as String, folderId)

        bookmarksService.deleteAllUserBookmarks(userId)

        def userFavs = bookmarksService.findBookmarksByUserId(userId)

        assert userFavs.size() == 0
    }

    @Test void shouldDeleteAllUserFolders() {
        createNewFolder()
        createNewFolder()

        def userFolder = bookmarksService.findAllFolders(userId)
        assert userFolder.size() == 2

        bookmarksService.deleteAllUserFolders(userId)

        userFolder = bookmarksService.findAllFolders(userId)
        assert userFolder.size() == 0
    }

    @Test void shouldOrderBookmarks() {
        String folderId = createNewFolder()
        String firstBookmarkId = createBookmark(UUID.randomUUID() as String, folderId)
        Folder folder = bookmarksService.findFolderById(folderId)
        assert folder.bookmarks.equals([firstBookmarkId])
        String secondBookmarkId = createBookmark(UUID.randomUUID() as String, folderId)
        folder = bookmarksService.findFolderById(folderId)
        assert folder.bookmarks.equals([
            firstBookmarkId,
            secondBookmarkId
        ])
        String thirdBookmarkId = createBookmark(UUID.randomUUID() as String, folderId)
        folder = bookmarksService.findFolderById(folderId)
        assert folder.bookmarks.equals([
            firstBookmarkId,
            secondBookmarkId,
            thirdBookmarkId
        ])
        folder.moveBookmark(thirdBookmarkId, 1)
        bookmarksService.updateFolder(folder)
        folder = bookmarksService.findFolderById(folderId)
        assert folder.bookmarks.equals([
            firstBookmarkId,
            thirdBookmarkId,
            secondBookmarkId
        ])
        folder.moveBookmark(firstBookmarkId, 1)
        bookmarksService.updateFolder(folder)
        folder = bookmarksService.findFolderById(folderId)
        assert folder.bookmarks.equals([
            thirdBookmarkId,
            firstBookmarkId,
            secondBookmarkId
        ])
        bookmarksService.deleteAllUserBookmarks(userId)
        folder = bookmarksService.findFolderById(folderId)
        assert folder.bookmarks.equals([])
        bookmarksService.deleteFolder(folderId)
    }

    @Test void shouldGetFolderbyInstitutionId() {
        String folderId = createNewInstitutionFolder("NNTMQJNFQCQOV5QI3EG3XBHXYWMNFACE")

        Folder folder = bookmarksService.findFolderByInstitutionId("NNTMQJNFQCQOV5QI3EG3XBHXYWMNFACE")
        assert folder.institutionIds as Set == [
            "NNTMQJNFQCQOV5QI3EG3XBHXYWMNFACE"] as Set
        assert folder.folderId == folderId
    }

    @Test void shouldHandleNotExistingFolderbyInstitutionId() {
        Folder folder = bookmarksService.findFolderByInstitutionId("2GJUO7RSKB56546VZZIK5GN7GZUY")
        assert folder == null
    }

    @Test void shouldGetOnlyOneFolderbyInstitutionId() {
        String folderId1 = createNewInstitutionFolder("ABC")
        String folderId2 = createNewInstitutionFolder("ABC")

        Folder folder = bookmarksService.findFolderByInstitutionId("ABC")

        assert (folder.folderId == folderId1) || (folder.folderId == folderId2)
    }
}
