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

/* Search namespace  */
de.ddb.next.search = de.ddb.next.search || {};

/**
 * Facets Manager
 *
 * The main intend of this object is to - retrieving facet data via ajax -
 * handling all events coming from the DOM tree (add/remove facets, show next
 * page etc.) - updating the facets values in the browser url. This is
 * important for navigating with back/next button of the browser - triggering
 * the flyoutWidget to render the facets
 *
 * Do all rendering in the flyoutWidget!
 *
 * Do not make synchronous AJAX calls in this class. Otherwise the GUI might freeze!
 */
de.ddb.next.search.FacetsManager = function(fetchResultsList, category, path) {
  this.fetchResultsList = fetchResultsList;

  //Category is a special facet for defining a document as an Institution ("Institution") or document ("Kultur")
  this.category = category;

  //search and entity facets use different paths
  this.path = path;

  this.init();
};

/**
 * Extend the prototyp of the FacetsManager with jQuery
 */
$.extend(de.ddb.next.search.FacetsManager.prototype, {
  timeFacet: null,
  yearFacet: null,
  yearFacet2: null,
  connectedflyoutWidget : null,
  currentOffset : 0,
  currentRows : -1, // all facets
  currentFacetField : null,
  currentFacetValuesSelected : [],
  currentFacetValuesNotSelected : [],
  allFacets : null,
  currentPage : 1,
  searchFacetValuesTimeout : 0,
  errorCaught : false,
  keyCode : {
    ALT : 18,
    BACKSPACE : 8,
    CAPS_LOCK : 20,
    COMMA : 188,
    CONTROL : 17,
    DELETE : 46,
    DOWN : 40,
    END : 35,
    ENTER : 13,
    ESCAPE : 27,
    HOME : 36,
    INSERT : 45,
    LEFT : 37,
    NUMPAD_ADD : 107,
    NUMPAD_DECIMAL : 110,
    NUMPAD_DIVIDE : 111,
    NUMPAD_ENTER : 108,
    NUMPAD_MULTIPLY : 106,
    NUMPAD_SUBTRACT : 109,
    PAGE_DOWN : 34,
    PAGE_UP : 33,
    PERIOD : 190,
    RIGHT : 39,
    SHIFT : 16,
    SPACE : 32,
    TAB : 9,
    UP : 38
  },

  /**
   * Initialize the new instance.
   */
  init : function() {
    var currObjInstance = this;
    currObjInstance.timeFacet = new de.ddb.next.search.TimeFacet(currObjInstance);
    
    $(".js.facets-list").removeClass("off");
  },

  /**
   * Makes an AJAX request to fetch the role values for the currently selected facet field and value
   *
   * @param facetValueContainer: The DOM element
   * @param facetValue: The selected (main) facet value
   * @param facetField: The selected facet field. E.g: "affiliate_fct"
   * @param role: A specific role. E.g: "_1_affiliate_fct_involved"
   */
  fetchRoleFacetValues : function(facetValueContainer, facetValue, facetField) {
    var currObjInstance = this;

    var oldParams = de.ddb.common.search.getUrlVars();
    var fctValues = '';
    var isThumbnailFiltered = '';
    var queryParam = '&query=' + facetValue;
    
    //Looking for existing facetvalues[] in the window url parameters
    if (oldParams['facetValues%5B%5D']) {
      $.each(oldParams['facetValues%5B%5D'], function(key, value) {
        fctValues = (value.indexOf(currObjInstance.currentFacetField) >= 0) ? fctValues : fctValues + '&facetValues%5B%5D=' + value;
      });
    }
    if (oldParams['isThumbnailFiltered'] && String(oldParams['isThumbnailFiltered']) === 'false') {
      isThumbnailFiltered = '&isThumbnailFiltered=false';
    }

//    if (this.category) {
//      fctValues += '&facetValues%5B%5D=category%3D' + currObjInstance.category;
//    }

    var url = jsContextPath + '/rolefacets' + '?name=' + facetField + '&facetValues%5B%5D=' + facetField + "%3D" + facetValue + '&searchQuery='
    + oldParams['query'] + queryParam + fctValues + isThumbnailFiltered
    + '&offset=' + this.currentOffset + '&rows=' + this.currentRows;

    var request = $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : url,
      complete : function() {
        var parsedResponse = jQuery.parseJSON(request.responseText);
        if (parsedResponse.values.length > 0) {
          currObjInstance.connectedflyoutWidget.renderRoleValues(facetValueContainer, facetValue, facetField, parsedResponse);
        }
      }
    });
  },

  /**
   * Makes an AJAX request to fetch the facet values for the currently selected facet field
   */
  fetchFacetValues : function(flyoutWidget, facetQuery) {
    if (flyoutWidget != null) {
      this.connectedflyoutWidget = flyoutWidget;
    }
    var oldParams = de.ddb.common.search.getUrlVars();
    var searchQueryParam = '';
    var currObjInstance = this;
    var fctValues = '';
    var isThumbnailFiltered = '';
    var facetQueryParam = '';
    var categoryParam = '';

    //Looking for existing facetvalues[] in the window url parameters
    if (oldParams['facetValues%5B%5D']) {
      $.each(oldParams['facetValues%5B%5D'], function(key, value) {
        var eachFacetName = value.split('%3D')[0];
        fctValues = (eachFacetName === currObjInstance.currentFacetField) ? fctValues
            : fctValues + '&facetValues%5B%5D=' + value;
      });
    }
    
    if (oldParams['isThumbnailFiltered'] && String(oldParams['isThumbnailFiltered']) === 'true')
    	isThumbnailFiltered = '&isThumbnailFiltered=true';
    else
    	isThumbnailFiltered = '&isThumbnailFiltered=false';
    

    if (oldParams['query']) {
      searchQueryParam = '&searchQuery=' + oldParams['query'];
    }

    if (facetQuery) {
      facetQuery = encodeURIComponent(facetQuery);
      facetQueryParam = '&query=' + facetQuery;
    }

//    if (this.category) {
//      fctValues += '&facetValues%5B%5D=category%3D' + currObjInstance.category;
//    }
    
    this.connectedflyoutWidget.renderFacetLoader();
    $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : jsContextPath + currObjInstance.path + '?name=' + currObjInstance.currentFacetField + searchQueryParam + facetQueryParam + fctValues + isThumbnailFiltered
          + '&offset=' + this.currentOffset + '&rows=' + this.currentRows + categoryParam,
      complete : function(data) {
        var parsedResponse = jQuery.parseJSON(data.responseText);
        // Initialization of currentFacetValuesSelected /
        // currentFacetValuesNotSelected
        currObjInstance.initializeFacetValuesStructures(parsedResponse.values);
        currObjInstance.connectedflyoutWidget.initializeFacetValues(parsedResponse.type,
            currObjInstance.currentFacetValuesNotSelected);
      }
    });
  },

  /**
   * Initialize the structures for the pagination logic inside facets flyoutWidget
   */
  initPagination : function() {
    this.currentPage = 1;
    this.currentOffset = 0;
    var currObjInstance = this;
    if (Math.round((this.currentFacetValuesNotSelected.length) / 10) > 1) {
      this.connectedflyoutWidget.paginationLiPrev.click(function(e) {
        e.preventDefault();
        currObjInstance.goPrevPage();
        return false;
      });
      this.connectedflyoutWidget.paginationLiNext.click(function(e) {
        e.preventDefault();
        currObjInstance.goNextPage();
        return false;
      });
      this.connectedflyoutWidget.paginationLiNext.removeClass('off');
    }
    this.connectedflyoutWidget.setFacetValuesPage(this.currentPage);
  },

  /**
   * Initialize the FacetValues structures
   */
  initializeFacetValuesStructures : function(responseFacetValues) {
    // LeftBody will not exists on the first opening of the flyout
    if (this.connectedflyoutWidget.facetLeftContainer) {
      var currObjInstance = this;
      var selectedList = this.connectedflyoutWidget.facetLeftContainer
          .find('.selected-items li');

      this.currentFacetValuesSelected = [];
      this.currentFacetValuesNotSelected = responseFacetValues;

      if (selectedList.length > 0) {
        selectedList.each(function() {
          var tmpFacetValue = decodeURIComponent($(this).attr('data-fctvalue'));
          currObjInstance.currentFacetValuesSelected.push(tmpFacetValue);
          currObjInstance.currentFacetValuesNotSelected = jQuery.grep(
              currObjInstance.currentFacetValuesNotSelected, function(element) {
                return element.value !== tmpFacetValue;
              });
        });
      }
    }
  },

  /**
   * Shows the facet values in the Flyout window for the next page.
   */
  goNextPage : function() {
    this.currentOffset += 10;
    this.currentPage += 1;
    this.connectedflyoutWidget.renderFacetValues(this.currentFacetField,
        this.currentFacetValuesNotSelected.slice(this.currentOffset));
    this.connectedflyoutWidget.setFacetValuesPage(this.currentPage);

    if (this.currentPage + 1 > Math
        .ceil((this.currentFacetValuesNotSelected.length) / 10)) {
      this.connectedflyoutWidget.paginationLiNext.addClass('off');
    }
    this.connectedflyoutWidget.paginationLiPrev.removeClass('off');
  },

  /**
   * Shows the facet values in the Flyout window for the previous page.
   */
  goPrevPage : function() {
    this.currentOffset -= 10;
    this.currentPage -= 1;
    this.connectedflyoutWidget.renderFacetValues(this.currentFacetField,
        this.currentFacetValuesNotSelected.slice(this.currentOffset));
    this.connectedflyoutWidget.setFacetValuesPage(this.currentPage);

    if (this.currentPage - 1 < 1) {
      this.connectedflyoutWidget.paginationLiPrev.addClass('off');
    }
    this.connectedflyoutWidget.paginationLiNext.removeClass('off');
  },

  /**
   * Handles the selection of a facet value in the Flyout window.
   * Update the window url and Triggers a new search request!
   */
  selectFacetValue : function(facetValue, localizedValue) {
    var currObjInstance = this;
    

    // update selection lists
    this.currentFacetValuesSelected.push(facetValue);
    this.currentFacetValuesNotSelected = jQuery.grep(this.currentFacetValuesNotSelected,
        function(element) {
          return element.value !== facetValue;
        });
    currObjInstance.timeFacet.closeFormIfEmpty(); 

    // render the selected facet
    var facetValueContainer = this.connectedflyoutWidget.renderSelectedFacetValue(facetValue, localizedValue);

    // if the facet field contains the string "_role" -> search for role values
    if (currObjInstance.currentFacetField.indexOf("_role") >= 0) {
      currObjInstance.fetchRoleFacetValues(facetValueContainer, facetValue, currObjInstance.currentFacetField);
    }

    // add event listener for removing facet
    facetValueContainer.find('.facet-remove').click(function(event) {
      event.preventDefault();
      currObjInstance.unselectFacetValue(facetValueContainer);
      return false;
    });

    // add event listener for add more facet filters
    if (this.currentFacetValuesSelected.length === 1) {
      this.connectedflyoutWidget.renderAddMoreFiltersButton(currObjInstance.currentFacetField);
      this.connectedflyoutWidget.addMoreFilters.click(function() {
        currObjInstance.connectedflyoutWidget.build($(this));
      });
    }
    this.connectedflyoutWidget.close();

    //Update the search params
    var paramsArray = de.ddb.common.search.addFacetValueToParams(this.currentFacetField, facetValue);

    //Perform a search with the new facet value
    currObjInstance.fetchResultsList($.addParamToCurrentUrl(paramsArray), function() {
      currObjInstance.unselectFacetValue(facetValueContainer, true);
    });

    $('.clear-filters').removeClass('off');
    $('.keep-filters').removeClass('off');

  },

  /**
   * Handles the unselection of a facet value.
   * Update the window url and Triggers a new search request!
   */
  unselectFacetValue : function(element, unselectWithoutFetch) {
    var currObjInstance = this;

    var facetFieldFilter = element.parents('.facets-item');
    var facetValue = decodeURIComponent(element.attr('data-fctvalue'));

    if (this.connectedflyoutWidget.opened) {
      this.connectedflyoutWidget.close();
      this.currentFacetValuesSelected = jQuery.grep(this.currentFacetValuesSelected,
          function(el) {
            return el !== decodeURIComponent(element.attr('data-fctvalue'));
          });
    }
    // if in the list there is only one element means that is the case
    // of the
    // last element that we are going to remove
    if (facetFieldFilter.find('.selected-items li[data-fctvalue]').length === 1) {
      this.connectedflyoutWidget.removeAddMoreFiltersButton(facetFieldFilter,
          facetFieldFilter.find('.add-more-filters'));
    }

    // Remove facet and all role based facets belonging to this facet
    // from the URL
    var facetsToRemove = [];
    facetsToRemove.push(['facetValues[]', facetFieldFilter.find('.h3').attr('data-fctname') + '=' + facetValue]);

    var roleFacets = element.find('span.role-facet-value');
    $.each((roleFacets), function() {
      facetsToRemove.push(['facetValues[]', $(this).attr("facetfield") + '=' + $(this).attr("roleValue")]);
    });

    var newUrl = $.removeParamFromUrl(facetsToRemove);
    if (decodeURIComponent(newUrl).indexOf('facetValues[]') === -1) {
      de.ddb.common.search.removeSearchCookieParameter('facetValues[]');
    }
    if (!unselectWithoutFetch) {
      $.addParamToCurrentUrl([['offset', 0]], newUrl);
      currObjInstance.fetchResultsList(newUrl);
    }
    element.remove();

    if ($('.facets-list').find('li[data-fctvalue]').length === 0) {
      $('.clear-filters').addClass('off');
      $('.keep-filters').addClass('off');
    }
  },

  /**
   * Handles the selection of a role facet.
   * Update the window url and Triggers a new search request!
   */
  selectRoleFacetValue : function(facetField, facetValue) {
    var currObjInstance = this;
    var paramsArray = de.ddb.common.search.addFacetValueToParams(facetField, facetValue);
    currObjInstance.fetchResultsList($.addParamToCurrentUrl(paramsArray));
    currObjInstance.timeFacet.closeFormIfEmpty(); 

  },

  /**
   * Handles the unselection of a role facet.
   * Update the window url and Triggers a new search request!
   */
  unselectRoleFacetValue : function(facetField, facetValue) {
    var currObjInstance = this;
    var newUrl = $.removeParamFromUrl([['facetValues[]', facetField + '=' + facetValue]]);

    if (decodeURIComponent(newUrl).indexOf('facetValues[]') === -1) {
      de.ddb.common.search.removeSearchCookieParameter('facetValues[]');
    }

    $.addParamToCurrentUrl([['offset', 0]], newUrl);
    currObjInstance.fetchResultsList(newUrl);
  },

  /**
   * Search handler for facet values.
   * Triggers a facet search and updates the values shown in the flyout window.
   */
  initializeFacetValuesDynamicSearch : function(inputSearchElement) {
    var currObjInstance = this;
    var timer;
    currObjInstance.timeFacet.closeFormIfEmpty(); 
    inputSearchElement.keyup(function(e) {
      clearInterval(timer);
      var code = (e.keyCode ? e.keyCode : e.which);
      var inputValue = this.value;
      if (code !== currObjInstance.keyCode.SHIFT
          && code !== currObjInstance.keyCode.CONTROL
          && code !== currObjInstance.keyCode.ALT
          && code !== currObjInstance.keyCode.LEFT && code !== currObjInstance.keyCode.UP
          && code !== currObjInstance.keyCode.RIGHT
          && code !== currObjInstance.keyCode.DOWN
          && code !== currObjInstance.keyCode.ENTER
          && code !== currObjInstance.keyCode.TAB) {
        var d = new Date();
        currObjInstance.searchFacetValuesTimeout = d.getTime();

        timer = setTimeout(function() {
        currObjInstance.connectedflyoutWidget.parentMainElement.find(
            '.flyout-right-container').remove();
        currObjInstance.connectedflyoutWidget.buildStructure();
        currObjInstance.fetchFacetValues(null, inputValue);
        currObjInstance.currentPage = 1;
        currObjInstance.currentOffset = 0;

        }, 500);
      }
    });
  },


  /**
   * Initialize the Flyout widget when the page is loaded in a asynchronic way.
   * It initialize all selected facets and role facets
   */
  initializeOnLoad : function(connectedflyoutWidget) {
    var currObjInstance = this;
    $(".js.facets-list").removeClass("off");

    this.connectedflyoutWidget = connectedflyoutWidget;
    var paramsFacetValues = de.ddb.common.search.getFacetValuesFromUrl();

    if (paramsFacetValues) {

      $('.clear-filters').removeClass('off');
      $('.keep-filters').removeClass('off');

      var selectedFacets = {};
      $.each(paramsFacetValues, function(key, value) {
        var decodedElement = decodeURIComponent(value.replace(/\+/g, '%20')).split('=');
        var fctField = decodedElement[0];
        var fctValue = decodedElement[1];
        if (!selectedFacets[fctField]) {
          selectedFacets[fctField] = [];
        }
        selectedFacets[fctField].push(fctValue);
      });

      // handle selected facets
      $.each(selectedFacets,
              function(fctField, fctValues) {
                  currObjInstance.connectedflyoutWidget.mainElement = $('.facets-list')
                      .find('a[data-fctname="' + fctField + '"]');
                  currObjInstance.connectedflyoutWidget.parentMainElement = currObjInstance.connectedflyoutWidget.mainElement
                      .parent();
                  currObjInstance.currentFacetField = currObjInstance.connectedflyoutWidget.mainElement
                      .attr('data-fctname');
                  currObjInstance.connectedflyoutWidget.buildLeftContainer();
                  currObjInstance.connectedflyoutWidget.parentMainElement.find(
                      '.input-search-fct-container').hide();

                  //set field as active
                  currObjInstance.connectedflyoutWidget.parentMainElement.addClass('active');

                  $.each(fctValues, function() {
                    //Check if the value is a role. They need special handling!
                    var facetValue = this;
                    
                    //Skip if the facetValue is a role
                    if (de.ddb.next.search.isRole(facetValue)) {
                      return;
                    }

                    var localizedValue = de.ddb.next.search.getLocalizedFacetValue(fctField, this);
                    if ( typeof(localizedValue) === 'function') {
                      localizedValue = localizedValue();
                    }

                    var selectedFacetValue = currObjInstance.connectedflyoutWidget.renderSelectedFacetValue(this, localizedValue);

                    if (fctField.indexOf("_role") >= 0) {
                      currObjInstance.fetchRoleFacetValues(selectedFacetValue, facetValue, fctField);
                    }

                    selectedFacetValue.find('.facet-remove').click(function(event) {
                      event.preventDefault();
                      currObjInstance.unselectFacetValue(selectedFacetValue);
                      return false;
                    });
                  });

                  currObjInstance.connectedflyoutWidget
                      .renderAddMoreFiltersButton(fctField);
                  currObjInstance.connectedflyoutWidget.addMoreFilters.click(function() {
                    currObjInstance.connectedflyoutWidget.build($(this));
                  });
              });
    }

    //init TimeFacet
    currObjInstance.timeFacet.initOnLoad();
  }
});
