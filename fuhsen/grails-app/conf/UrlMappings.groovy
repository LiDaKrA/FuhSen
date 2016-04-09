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
class UrlMappings {

    static mappings = {
        //@formatter:off
        "/$controller/$action?/$id?"{ constraints { /* apply constraints here */ } }
        //@formatter:on

        "/searchresults/products/$q?" {
            controller="search"
            action="products"
        }
		
		"/searchresults/organizations/$q?" {
			controller="search"
			action="organizations"
		}

        "/searchresults/$q?" {
            controller="search"
            action="results"
        }

        "/facets/$q?" {
            controller="facets"
            action="facetsList"
        }

        "/facets/calculateTimeFacetDays/$q?" {
            controller="facets"
            action="calculateTimeFacetDays"
        }

        "/facets/calculateTimeFacetDates/$q?" {
            controller="facets"
            action="calculateTimeFacetDates"
        }

        "/rolefacets/$q?" {
            controller="facets"
            action="getRolesForFacetValue"
        }

        "/entityfacets/$q?" {
            controller="facets"
            action="entityFacetsList"
        }

        "/search/facets/" {
            controller="facets"
            action="allFacetsList"
        }

        "/informationitem/$id"{
            controller="search"
            action="informationItem"
        }

        "/content/$dir**" {
            controller="content"
            action="staticcontent"
        }

        "/advancedsearch" {
            controller="advancedsearch"
            action="fillValues"
        }

        "/advancedsearch/search" {
            controller="advancedsearch"
            action="executeSearch"
        }

        "/" {
            controller="index"
            action="index"
        }

        "/item/$id" {
            controller="item"
            action="findById"
        }

        "/item/xml/$id" {
            controller="item"
            action="showXml"
        }

        "/apis/favorites" {
            controller="favorites"
            action="getFavorites"
        }

        "/item/sendpdf/$id" {
            controller="item"
            action="sendPdf"
        }

        name delFavorites: "/apis/favorites/_delete" {
            controller="favorites"
            action=[POST: "deleteFavorites"]
        }

        "/apis/favorites/$id/_move" {
            controller="favorites"
            action=[POST: "moveFavorite"]
        }

        "/apis/favorites/folders" {
            controller="favoritesfolder"
            action="getFavoritesFolders"
        }

        "/apis/favorites/folder/create" {
            controller="favoritesfolder"
            action="createFavoritesFolder"
        }

        "/apis/favorites/folder/delete" {
            controller="favoritesfolder"
            action="deleteFavoritesFolder"
        }

        "/apis/favorites/folder/edit" {
            controller="favoritesfolder"
            action="editFavoritesFolder"
        }

        "/apis/favorites/folder/get/$id" {
            controller="favoritesfolder"
            action="getFavoritesFolder"
        }

        "/apis/favorites/_get" {
            controller="favorites"
            action=[POST: "filterFavorites"]
        }

        "/apis/favorites/copy" {
            controller="favorites"
            action="copyFavorites"
        }

        "/apis/favorites/comment" {
            controller="favorites"
            action=[POST: "setComment"]
        }

        "/apis/favorites/togglePublish" {
            controller="favorites"
            action=[POST: "togglePublish"]
        }

        "/apis/favorites/$id" {
            controller="favorites"
            action=[GET: "getFavorite", POST: "addFavorite", DELETE: "deleteFavorite"]
        }

        "/user/favorites/$id?" {
            controller="favoritesview"
            action="favorites"
        }

        "/user/$userId/favorites/$folderId" {
            controller="favoritesview"
            action="publicFavorites"
        }

        // This URL-Map is also used hard-coded in favorites.js file.
        "/user/$userId/favorites/allpublicfolders/$selectedFolderId" {
            controller="favoritesview"
            action="allpublicfolders"
        }
        "/apis/savedsearches" {
            controller="savedsearches"
            action=[GET: "getSavedSearches", PUT: "addSavedSearch"]
        }

        "/apis/savedsearches/$id" {
            controller="savedsearches"
            action=[PUT: "updateSavedSearch"]
        }

        name delSavedSearches: "/apis/savedsearches/_delete" {
            controller="savedsearches"
            action=[POST: "deleteSavedSearches"]
        }

        "/apis/savedsearches/_get" {
            controller="savedsearches"
            action=[POST: "isSavedSearch"]
        }

        "/apis/clusteredInstitutionsmap" {
            controller="apis"
            action=[GET:"clusteredInstitutionsmap"]
        }

        "/about-us/institutions" {
            controller="institution"
            action="show"
        }

        "/about-us/institutions/item/$id" {
            controller="institution"
            action="showInstitutionsTreeByItemId"
        }

        "/apis/institutions" {
            controller="institution"
            action="getJson"
        }

        "/entity/$id" {
            controller="entity"
            action="index"
        }

        "/entity/ajax/searchresults" {
            controller="entity"
            action="getAjaxSearchResultsAsJson"
        }

        "/entity/ajax/rolesearchresults" {
            controller="entity"
            action="getAjaxRoleSearchResultsAsJson"
        }

        "/entity/search/person/$q?" {
            controller="entity"
            action="personsearch"
        }

        "/institution/ajax/highlights" {
            controller="institution"
            action="getInstitutionHighlights"
        }

        "/persons"{
            controller="entity"
            action="persons"
        }

        "/binary/$filename**" {
            controller="apis"
            action="binary"
        }

        "/static/$filename**" {
            controller="apis"
            action="staticFiles"
        }


        "/user/registration" {
            controller="user"
            action="registration"
        }

        "/user/resetPassword" {
            controller="user"
            action="passwordResetPage"
        }

        "/user/profile" {
            controller="user"
            action="profile"
        }

        "/user/savedsearches" {
            controller="user"
            action=[GET: "getSavedSearches", POST: "sendSavedSearches"]
        }

        "/user/confirm/$id/$token" {
            controller="user"
            action="confirm"
        }

        "/user/changePassword" {
            controller="user"
            action="passwordChangePage"
        }
        "/user/delete" {
            controller="user"
            action="delete"
        }

        "/user/apikey" {
            controller="user"
            action="showApiKey"
        }

        "/user/apikey/request" {
            controller="user"
            action="requestApiKey"
        }

        "/user/apikey/delete" {
            controller="user"
            action="deleteApiKey"
        }

        "/login" {
            controller="user"
            action="index"
        }

        "/login/doLogin" {
            controller="user"
            action="doLogin"
        }

        "/login/doLogout" {
            controller="user"
            action="doLogout"
        }

        "/login/openId" {
            controller="user"
            action="requestOpenIdLogin"
        }

        "/login/doOpenIdLogin" {
            controller="user"
            action="doOpenIdLogin"
        }

        "/compare/$firstId/with/$secondId" {
            controller="compare"
            action="index"
        }

        "/multipolygone/$id" {
            controller="map"
            action="multipolygone"
        }

        "/items3d/$id" {
            controller="items3d"
            action="index"
        }

        "/lists/$id" {
            controller="lists"
            action="index"
        }
		
		//fuhsen url mappings
		"/ldw/pep/$q?" {
			controller="pep"
			action="index"
		}
		
		"/ldw/twitter/$q?" {
			controller="twitter"
			action="index"
		}

		"/loadingresults/$q?" {
			controller="search"
			action="loadingResults"
		}
		
        "500"(controller: "error", action: "badRequest", exception: de.ddb.common.exception.BadRequestException)
        "500"(controller: "error", action: "auth", exception: de.ddb.common.exception.AuthorizationException)
        "500"(controller: "error", action: "itemNotFound", exception: de.ddb.common.exception.ItemNotFoundException)
        "500"(controller: "error", action: "entityNotFound", exception: de.ddb.common.exception.EntityNotFoundException)
        "500"(controller: "error", action: "favoritelistNotFound", exception: de.ddb.common.exception.FavoritelistNotFoundException)
        "500"(controller: "error", action: "conflict", exception: de.ddb.common.exception.ConflictException)
        "500"(controller: "error", action: "serverError", exception: de.ddb.common.exception.ConfigurationException)
        "500"(controller: "error", action: "serverError", exception: de.ddb.common.exception.BackendErrorException)
        "500"(controller: "error", action: "uncaughtException")
        "404"(controller: "error", action: "defaultNotFound")

    }
}
