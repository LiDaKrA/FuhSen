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
modules = {
    ddbnext {
        defaultBundle 'ddbnext'
        dependsOn "images, cssscreen, cssprint, javascript"
		//dependsOn "images, cssscreen, cssprint, javascript, autocomplete, item"
    }

    images {  resource url:'/images/favicon.ico' }

    cssscreen {
        resource url:'/css/vendor/bootstrap-2.2.2/lib/bootstrap.css', bundle: 'screen'
        resource url:'/css/vendor/bootstrap-2.2.2/lib/bootstrap-responsive.css', bundle: 'screen'
        resource url:'/css/vendor/fancybox-2.1.4/lib/jquery.fancybox-2.1.4.css', bundle: 'screen'
        resource url:'/css/ddb.css', bundle: 'screen'
        //resource url:'/css/item.css', bundle: 'screen'
        //resource url:'/css/institutionList.css', bundle: 'screen'
        //resource url:'/css/institution.css', bundle: 'screen'
        //resource url:'/css/institutionMap.css', bundle: 'screen'
        resource url:'/css/results.css', bundle: 'screen'
        resource url:'/css/favorites.css', bundle: 'screen'
        resource url:'/css/staticContent.css', bundle: 'screen'
        resource url:'/css/error.css', bundle: 'screen'
        //resource url:'/css/institution.css', bundle: 'screen'
        resource url:'/css/viewer.css', bundle: 'screen'
        //resource url:'/css/itemHierarchy.css', bundle: 'screen'
        resource url:'/css/modalDialog.css', bundle: 'screen'
        resource url:'/css/advancedSearch.css', bundle: 'screen'
        resource url:'/css/entity.css', bundle: 'screen'
        resource url:'/css/registration.css', bundle: 'screen'
        resource url:'/css/userProfile.css', bundle: 'screen'
        resource url:'/css/login.css', bundle: 'screen'
        resource url:'/css/confirmation.css', bundle: 'screen'
        resource url:'/css/savedSearches.css', bundle: 'screen'
        resource url:'/css/socialMedia.css', bundle: 'screen'
        resource url:'/css/apiKey.css', bundle: 'screen'
        resource url:'/css/compare.css', bundle: 'screen'
        resource url:'/css/person.css', bundle: 'screen'
        resource url:'/css/persons.css', bundle: 'screen'
        resource url:'/css/searchTabulator.css', bundle: 'screen'
        //resource url:'/js/map/css/map.css', bundle: 'screen'
        //resource url:'/third-party/map/css/style.css', bundle: 'screen'
        //resource url:'/third-party/map/css/ddbPlacenamePopupList.css', bundle: 'screen'
        resource url:'/css/timeFacet.css'
        resource url:'/css/paginationWidget.css', bundle: 'screen'
        resource url:'/css/lists.css'
        resource url:'/css/hovercard.css'
    }

    cssprint {
        resource url:'/css/ddb.css', attrs:[media:'print'], bundle: 'print'
        //resource url:'/css/item.css', attrs:[media:'print'], bundle: 'print'
        //resource url:'/css/institutionList.css', attrs:[media:'print'], bundle: 'print'
        resource url:'/css/results.css', attrs:[media:'print'], bundle: 'print'
        resource url:'/css/print.css', attrs:[media:'print'], bundle: 'print'
        //resource url:'/third-party/map/css/style.css', attrs:[media:'print'], bundle: 'print'
        //resource url:'/third-party/map/css/ddbPlacenamePopupList.css', attrs:[media:'print'], bundle: 'print'
    }

    javascript {
        resource url:'/js/vendor/jquery-1.8.2/jquery-1.8.2.min.js'
        resource url:'/js/vendor/jquery.dotdotdot-1.5.1/jquery.dotdotdot-1.5.1.js'
        resource url:'/js/vendor/jquery.carouFredSel-6.2.0/jquery.carouFredSel-6.2.0-packed.js'
        resource url:'/js/vendor/bootstrap-2.2.2/bootstrap.js'
        resource url:'/js/vendor/bootstrap-2.2.2/bootstrap-collapse.js'
        resource url:'/js/vendor/bootstrap-2.2.2/bootstrap-button.js'
        resource url:'/js/vendor/bootstrap-2.2.2/bootstrap-modal.js'
        resource url:'/js/vendor/bootstrap-2.2.2/bootstrap-multiselect.js'
        resource url:'/js/vendor/jquery.validate-1.11.1/jquery.validate.min.js'
        resource url:'/js/vendor/jquery.fancybox-2.1.4/jquery.fancybox.pack.js'
        resource url:'/js/vendor/jquery.cookies-2.2.0/jquery.cookies.2.2.0.min.js'
        resource url:'/js/vendor/jquery.multiselect-1.1/jquery.multiselect-1.1.min.js'
        resource url:'/js/vendor/jquery.checkall-1.4/jquery.checkall.js'
        // resource url:'/js/vendor/respond-1.1.0/respond.src.js'
        resource url:'/js/socialMediaManager.js'
        resource url:'/js/stringBuilder.js'
        resource url:'/js/vendor/underscore-1.3.1/underscore-min.js'
        //resource url:'/js/vendor/jwplayer-6.2.3115/jwplayer.js'
        //resource url:'/js/jwplayerKey.js'
		//resource url:'/js/vendor/flowplayer-3.2.18/flowplayer-3.2.13.min.js'
        resource url:'/js/largeCookie.js'
        resource url:'/js/header.js'
        resource url:'/js/tooltip.js'
        resource url:'/js/vendor/json2-2010.11.17/json2.js'
        resource url:'/js/commonGlobalVariables.js'
        resource url:'/js/globalVariables.js'
        resource url:'/js/globalScripts.js'
        resource url:'/js/paginationWidget.js'
        resource url:'/js/institutionList.js'
        resource url:'/js/startPage.js'
        resource url:'/js/itemHierarchy.js'
        resource url:'/js/persistentLinksModalDialog.js'
        resource url:'/js/binariesViewer.js'
        resource url:'/js/advancedSearchPage.js'
        resource url:'/js/search/searchResults.js'
        resource url:'/js/favorites.js'
        resource url:'/js/registration.js'
        resource url:'/js/changeFavorite.js'
        resource url:'/js/profile.js'
        resource url:'/js/passwordChange.js'
        resource url:'/js/vendor/history-1.7.1/scripts/bundled/html4+html5/jquery.history.js'
        resource url:'/js/entity.js'
        resource url:'/js/item.js'
        resource url:'/js/institution.js'
        resource url:'/js/savedSearches.js'
        resource url:'/js/apiKey.js'
        resource url:'/js/compare.js'
        resource url:'/js/search/searchCookie.js'
        resource url:'/js/search/urlHelper.js'
        resource url:'/js/search/facetHelper.js'
        resource url:'/js/search/roleHelper.js'
        resource url:'/js/search/timeFacet.js'
        resource url:'/js/search/facetsManager.js'
        resource url:'/js/search/flyoutFacetsWidget.js'
        resource url:'/js/search/compareManager.js'
        resource url:'/js/search/hovercardInfoItem.js'
        resource url:'/js/resultsFavorites.js'
        resource url:'/js/search/searchinstitution.js'
        resource url:'/js/searchPerson.js'
        resource url:'/js/lists.js'
        resource url:'/js/persons.js'
        //resource url:'/js/vendor/masonry/masonry.pkgd.js'
        //resource url:'/js/vendor/masonry/imagesloaded.pkgd.min.js'
        resource url:'/js/publicFavorites.js'
    }

    /*item { resource url:'/js/persistentSendPdfMailModalDialog.js' }*/

    /*multipolygone {
        dependsOn "ddbnext"
        resource url:'/css/multipolygone.css'
        resource url:'/js/vendor/proj4-2.1.2/proj4.js'
        resource url:'/js/vendor/openlayers-2.14/OpenLayers.js'
        resource url:'/js/map/projections.js'
        resource url:'/js/map/map.js'
        //resource url:'/js/multipolygone.js' //used only for the prototype
    }*/

    /*autocomplete {
        resource url:'/css/vendor/autocomplete-1.10.2/jquery-ui-1.10.2.custom.min.css', bundle: 'screen'
        resource url:'/js/vendor/autocomplete-1.10.2/js/jquery-ui-1.10.2.custom.min.js'
        resource url:'/js/myAutocomplete.js'
    }*/

    // These are page specific bundles which should be merged back into ddbnext in the second step

    startpage {
        resource url:'/css/startPage.css', bundle: 'startpage'
        resource url:'/css/startPage.css', attrs:[media:'print'], bundle: 'startpageprint'
    }

    /*institution {
        dependsOn "ddbnext"
        resource url:'/js/vendor/proj4-2.1.2/proj4.js'
        resource url:'/js/vendor/openlayers-2.14/OpenLayers.js'
        resource url:'/js/map/projections.js'
        resource url:'/js/map/map.js'
        resource url:'/js/institutionsMapAdapter.js'
    }*/

    /*institutionlist {
        dependsOn "ddbnext"
        resource url:'/js/vendor/proj4-2.1.2/proj4.js'
        resource url:'/js/vendor/openlayers-2.14/OpenLayers.js'
        resource url:'/js/map/projections.js'
        resource url:'/js/map/map.js'
        resource url:'/js/institutionsMapAdapter.js'
    }

    items3d {
        dependsOn "ddbnext"
        resource url:'/js/3d/x3dom.js'
        resource url:'/css/3d/x3dom.css'
        resource url:'/css/items3d.css'
    }

    pdf {
        // This is the only working variant found! You must exclude 'zip,bundle' from the mappers list and
        // the CSS attributed with the exclude statement must NOT be used anywhere else in normal pages
        // or otherwise the resource plugin will produce invalid imports. It is best to provide a CSS exclusively used
        // for the PDF generation. Remember to to make sure that no other compression tool like the compress-plugin
        // is compressing the css after the resource-plugin, otherwise the PDF-export will fail, since
        // the plugin can not handle zipped ressources (see Config.groovy for that).
        resource url:'/css/itemPdf.css', exclude:'zip,bundle', attrs:[media:'print']
    }*/
	
	infiniteScrolling {
		resource url:'/js/search/infiniteScrolling.js'
	}
	
	loadingResults {
		resource url:'/js/search/loadingResults.js'
	}
	
}
