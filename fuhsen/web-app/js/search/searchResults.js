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
//IMPORTANT FOR MERGING: This is the main function that has to be called when we are in the search results page

/* Search namespace  */
de.ddb.next.search = de.ddb.next.search || {};

/**
 * Is called after the DOM has been initialized, the new handler passed in will
 * be executed immediately
 */
$(function() {

  if (jsPageName === "results" || jsPageName === "searchinstitution" || jsPageName === "searchperson") {
    // workaround for ffox + ie click focus - prevents links that load dynamic
    // content to be focussed/active.
    $("a.noclickfocus").live('mouseup', function() {
      $(this).blur();
    });

    // Fix for back-button problem with the searchfield: DDBNEXT-389
    if ($.browser.msie) {
      var queryCache = $("#querycache");
      var queryString = "";
      if (queryCache.length > 0) {
        queryString = queryCache.val();
      }
      $("#form-search-header .query").val(queryString);
    }

    //Callback function for history changes
    var stateManager = function(url) {
      $('#main-container').load(url + ' .search-results-container', function() {
        de.ddb.next.search.searchResultsInitializer();
      });
    };

    de.ddb.common.search.initHistorySupport(stateManager);
    //de.ddb.next.search.paginationWidget = new de.ddb.next.PaginationWidget();
    de.ddb.next.search.searchResultsInitializer();

    //Operations on the grid where triggered only once clicked and ignored if the viewtype grid was coming from the URL
    if (typeof de.ddb.common.search.getUrlVar('viewType')!=='undefined' && de.ddb.common.search.getUrlVar('viewType').toString()==='grid'){
      //In IE9<= creates an infinite loop due that doesn't support pushState.
      if (de.ddb.common.search.historySupport){
        $("#view-grid").trigger( "click" );
      }
    }
  }
});

de.ddb.next.search.fetchResultsList = function(url, errorCallback) {
  
  var divSearchResultsOverlayModal = $(document.createElement('div'));
  divSearchResultsOverlayModal.addClass('search-results-overlay-modal');
  var divSearchResultsOverlayWaiting = $(document.createElement('div'));
  divSearchResultsOverlayWaiting.addClass('search-results-overlay-waiting');
  
  var divSearchResultsOverlayImg = $(document.createElement('div'));
  divSearchResultsOverlayImg.addClass('small-loader');
  divSearchResultsOverlayWaiting.append(divSearchResultsOverlayImg);

  $('.search-results').append(divSearchResultsOverlayModal);
  $('.search-results').append(divSearchResultsOverlayWaiting);

  $.ajax({
    type : 'GET',
    dataType : 'json',
    async : true,
    url : url + '&reqType=ajax&infiniteScroll=false',
    success : function(data) {
      de.ddb.common.search.historyManager(url);
      if (!historySupport) {
        return;
      }

      $('.search-results-list').fadeOut(
          'fast',
          function() {
            var JSONresponse = data;
            if (JSONresponse.numberOfResults === 0) {
              $('.search-noresults-content').removeClass("off");
              $('.search-results-content').addClass("off");
            } else {
              $('.search-noresults-content').addClass("off");
              $('.search-results-content').removeClass("off");
            }
            $('.search-results-list').html(JSONresponse.results);
            $('.results-overall-index').html(JSONresponse.resultsOverallIndex);
            $('.page-input').attr('value', JSONresponse.page);
            $('.page-nonjs').html(JSONresponse.page);
            
            //Number of results hidden field
            //VWPATENT-29 - Scroll Funktion
            $('input[name=numberOfResultsH]').val(JSONresponse.numberOfResults);
            $('input[name=offsetH]').val("0");
            
            //Show or hide Loading more results div
            //Read rows parameter from configuration 20 is a hard-coded value
            if (JSONresponse.numberOfResults >  20)
            	$('.loading').css('visibility','visible');
            else
            	$('.loading').css('visibility','hidden');

            //de.ddb.next.search.paginationWidget.resetNavigationElements(JSONresponse);

            $('.search-results-list').fadeIn('fast');

            divSearchResultsOverlayImg.remove();
            divSearchResultsOverlayWaiting.remove();
            divSearchResultsOverlayModal.remove();

            $(window).trigger('searchChange');
          });
    },
    error : function() {
      divSearchResultsOverlayImg.remove();
      divSearchResultsOverlayWaiting.remove();
      divSearchResultsOverlayModal.remove();

      de.ddb.next.search.showError(messages.ddbnext.An_Error_Occured);

      if (errorCallback) {
        errorCallback();
      }
    }
  });
};

de.ddb.next.search.loadInfiniteScrollResultsList = function(url, errorCallback) {
	
	//Loading effect
	//var divSearchResultsOverlayModal = $(document.createElement('div'));
	//divSearchResultsOverlayModal.addClass('search-results-overlay-modal');
	//var divSearchResultsOverlayWaiting = $(document.createElement('div'));
	//divSearchResultsOverlayWaiting.addClass('search-results-overlay-waiting');
	$('input[name=isLoadingInformation]').val('true');
	
	var divSearchResultsOverlayImg = $(document.createElement('div'));
	divSearchResultsOverlayImg.addClass('small-loader');
	//divSearchResultsOverlayWaiting.append(divSearchResultsOverlayImg);

	$('#load_more_results').hide();
	$('.loading').append(divSearchResultsOverlayImg);
	
	//$('.search-results').append(divSearchResultsOverlayModal);
	//$('.search-results').append(divSearchResultsOverlayWaiting);

	  $.ajax({
	    type : 'GET',
	    dataType : 'json',
	    async : true,
	    url : url + '&reqType=ajax&infiniteScroll=true',
	    success : function(data) {
	    	//de.ddb.common.search.historyManager(url);
	    	//if (!historySupport) {
	    	//  return;
	    	//}
	    	
	    	//Make GoToTop button visible
	    	$('.fake').css('visibility','visible');    	
	    
	      $('.search-results-list').fadeOut(
	          'fast',
	          function() {
	            var JSONresponse = data;
	            if (JSONresponse.numberOfResults === 0) {
	              $('.search-noresults-content').removeClass("off");
	              $('.search-results-content').addClass("off");
	            } else {
	              $('.search-noresults-content').addClass("off");
	              $('.search-results-content').removeClass("off");
	            }
	            $('.results-list').append(JSONresponse.results);
	            $('.results-overall-index').html(JSONresponse.resultsOverallIndex);
	            $('.page-input').attr('value', JSONresponse.page);
	            $('.page-nonjs').html(JSONresponse.page);

	            //de.ddb.next.search.paginationWidget.resetNavigationElements(JSONresponse);

	            $('.search-results-list').fadeIn('fast');

	            divSearchResultsOverlayImg.remove();
	            $('input[name=isLoadingInformation]').val('false');	            
	            $('#load_more_results').show();
	            
	            //Show or hide Loading more results div
	            var rowsIntValue = parseInt($('input[name=rowH]').val());
	            var newOffSet = parseInt($('input[name=offsetH]').val()) + rowsIntValue;
	            var numberOfResultsIntValue = parseInt($('input[name=numberOfResultsH]').val());
	            if (newOffSet + rowsIntValue <=  numberOfResultsIntValue)
	            	$('.loading').css('visibility','visible');
	            else
	            	$('.loading').css('visibility','hidden');
	            
	            //divSearchResultsOverlayWaiting.remove();
	            //divSearchResultsOverlayModal.remove();

	            $(window).trigger('searchChange');
	          });
	    },
	    error : function() {
	      divSearchResultsOverlayImg.remove();
	      $('input[name=isLoadingInformation]').val('false');
	      $('#load_more_results').show();
	      //divSearchResultsOverlayWaiting.remove();
	      //divSearchResultsOverlayModal.remove();

	      de.ddb.next.search.showError(messages.ddbnext.An_Error_Occured);

	      if (errorCallback) {
	        errorCallback();
	      }
	    }
	  });
	};

de.ddb.next.search.hideError = function() {
  $('.errors-container').remove();
};

de.ddb.next.search.showError = function(errorHtml) {
  var errorContainer = ($('.search-results-list').find('.errors-container').length > 0) ? $(
      '.search-results-list').find('.errors-container') : $(document.createElement('div'));
  var errorIcon = $(document.createElement('i'));
  errorContainer.addClass('errors-container');
  errorContainer.html(errorHtml);
  errorContainer.prepend(errorIcon);
  $('.search-results-list').prepend(errorContainer);
  var offset = errorContainer.offset();
  $('html, body').animate({
    scrollTop: offset.top,
    scrollLeft: offset.left
  });
};

de.ddb.next.search.initializeFacets = function() {
  var facetsManager = null;

  if (jsPageName === "results") {
    facetsManager = new de.ddb.next.search.FacetsManager(de.ddb.next.search.fetchResultsList, "Kultur", "/facets");
  }
  else if (jsPageName === "searchinstitution") {
    facetsManager = new de.ddb.next.search.FacetsManager(de.ddb.next.search.fetchResultsList, "Institution", "/facets");
  } else if (jsPageName === "searchperson") {
    facetsManager = new de.ddb.next.search.FacetsManager(de.ddb.next.search.fetchResultsList, null, "/entityfacets");
  }

  var fctWidget = new de.ddb.next.search.FlyoutFacetsWidget(facetsManager);
  $('.facets-item a').each(function() {
    $(this).click(function(event) {
      event.preventDefault();
      fctWidget.build($(this));
    });
  });
  fctWidget.manageOutsideClicks(fctWidget);
};

de.ddb.next.search.searchResultsInitializer = function() {
  $(window).on("searchChange", function() {
    setHovercardEvents();
    var compareManager = new de.ddb.next.search.CompareManager();
    compareManager.initComparison();
    checkFavorites();
    checkSavedSearch();
  });

  $('.results-paginator-options').removeClass('off');
  $('.results-paginator-view').removeClass('off');
  $('.page-input').removeClass('off');
  $('.page-nonjs').addClass("off");

  $(window).trigger("searchChange");

  $('#form-search-header button').click(
      function() {
        var searchParameters = de.ddb.common.search.readCookie("searchParameters" + jsContextPath);
        if (searchParameters != null && searchParameters.length > 0) {
          searchParameters = searchParameters.substring(1, searchParameters.length - 1);
          searchParameters = searchParameters.replace(/\\"/g, '"');
          var json = $.parseJSON(searchParameters);
          if (json["rows"]) {
            $(this).append('<input type="hidden" name="rows" value="' + json["rows"] + '"/>');
          }
          if (json["clustered"]) {
            $(this).append(
                '<input type="hidden" name="clustered" value="' + json["clustered"] + '"/>');
          }
          if (json["isThumbnailFiltered"]) {
            $(this).append(
                '<input type="hidden" name="isThumbnailFiltered" value="'
                    + json["isThumbnailFiltered"] + '"/>');
          }
          if (json["viewType"]) {
            $(this).append(
                '<input type="hidden" name="viewType" value="' + json["viewType"] + '"/>');
          }
          if (json["sort"]) {
            //Only a_to_z and z_to_a should be considered
            if (json["sort"] === 'ALPHA_ASC' || json["sort"] === 'ALPHA_DESC') {
              $(this).append('<input type="hidden" name="sort" value="' + json["sort"] + '"/>');
            }
          }
        }
      });
  $('#form-search-header input').keyup(function(e) {
    if (e.keyCode === 13 && $.browser.msie && parseFloat($.browser.version) <= 8.0) {
      $('#form-search-header button').click();
    }
    return false;
  });
  $('#view-list').click(
      function() {
        $('.summary-main .title a').each(
            function(index, value) {
              $(this).closest( "div" ).removeClass("grid-title");
              var newTitle = value.title.toString();
              if (newTitle.length > 100) {
                newTitle = $.trim(newTitle).substring(0, 100).split(" ").slice(0, -1).join(" ")
                    + "...";
              }
              if ($(this).closest('.summary-main').find('.matches li span strong').length === 0
                  && jQuery.trim($(value).find('strong')).length > 0) {
                newTitle = jQuery.trim($(value).html());
              } else {
                var replacementsRegex = new StringBuilder();
                replacementsRegex.append("(");
                $(this).closest('.summary-main').find('.matches li span strong').each(
                    function(sindex, svalue) {
                      var tmpSvalueText = (svalue.innerHTML + '').replace(/([.?*+^$[\]\\(){}|-])/g,
                          "\\$1");
                      if (replacementsRegex.getLength() > 1) {
                        replacementsRegex.append("|");
                      }
                      replacementsRegex.append(tmpSvalueText);
                    });
                replacementsRegex.append(")");
                newTitle = newTitle.replace(new RegExp(replacementsRegex.toString(), 'gi'),
                    "<strong>\$1</strong>");
              }
              value.innerHTML = newTitle;
            });
        $('#view-list').addClass('selected');
        $('#view-list').attr("disabled", "disabled");
        $('#view-grid').removeClass('selected');
        $('#view-grid').removeAttr('disabled');
        $('.search-results').fadeOut(
            'fast',
            function() {
              $('.results-list .summary').addClass('row');
              $('.summary-main-wrapper').not('.summary-main-wrapper-gnd').addClass('span6');
              $('.thumbnail-wrapper').addClass('span3');
              $('.results-list .item-options').not('.entity-list .item-options').addClass('bl');
              $('.results-list .item-options .information').not(
                  '.entity-list .item-options .information').addClass('bb');
              $('.results-list .item-options .compare').not('.entity-list .item-options .compare')
                  .addClass('bb');
              $('.results-list').removeClass("grid");
              $('.search-results').fadeOut('fast');
              $('.summary .thumbnail-wrapper').each(function() {
                $(this).appendTo(this.parentNode);
              });
              $('.search-results').fadeIn('fast');
            });
        var paramsArray = [['viewType', 'list']];
        var newUrl = $.addParamToCurrentUrl(paramsArray);
        $('.page-nav a, .page-nav-mob a').each(function() {
          this.href = $.addParamToCurrentUrl(paramsArray, this.href.split("?")[1]);
        });

        $('.results-list a').each(function() {
          this.href = $.addParamToUrl(null, paramsArray, this.href.split("?")[0], this.href.split("?")[1]);
        });
        $('.clear-filters a').attr('href',
            $('.clear-filters a').attr('href').replace(/viewType=(list|grid)/i, 'viewType=list'));
        de.ddb.common.search.historyManager(newUrl);
        de.ddb.common.search.setSearchCookieParameter(paramsArray);
      });

//  de.ddb.next.search.paginationWidget.setPageInputKeyupHandler(
//      function(e, element){
//        if (e.keyCode === 13) {
//          if (/^[0-9]+$/.test(element.value)) {
//            var resultPagesCountText = $('.result-pages-count').text();
//            var resultPagesCountInt = parseInt(resultPagesCountText.replace(/[^0-9]/g, ''));
//
//            if (parseInt(element.value) <= 0) {
//              element.value = 1;
//            } else if (parseInt(element.value) > resultPagesCountInt) {
//              element.value = $('.result-pages-count').text();
//            }
//          } else {
//            element.value = 1;
//          }
//
//          $('.page-input').attr('value', element.value);
//
//          var valueInteger = parseInt(element.value.replace(/[^0-9]/g, ''));
//          var filterInteger = parseInt($('.page-filter').find("select").val());
//          var offset = (valueInteger - 1) * filterInteger;
//          var paramsArray = [['offset', offset]];
//          var newUrl = $.addParamToCurrentUrl(paramsArray);
//          de.ddb.next.search.fetchResultsList(newUrl);
//        }
//      }
//  );

//  de.ddb.next.search.paginationWidget.setNavigatorsClickHandler(
//      function(element) {
//        de.ddb.next.search.fetchResultsList(element.attr('href'));
//        $('html, body').animate({
//          scrollTop : 0
//        }, 1000);
//      }
//  );

//  de.ddb.next.search.paginationWidget.setPaginatorOptionsHandlers(
//      function(sortSelect, rowsSelect, closeButton){
//        var paramsArray = [['rows', rowsSelect.val()], ['sort', sortSelect.val()], ['offset', 0]];
//        closeButton.trigger('click');
//        de.ddb.next.search.fetchResultsList($.addParamToCurrentUrl(paramsArray));
//
//        if($('.clear-filters').attr('href')) {
//          $('.clear-filters').attr('href', $('.clear-filters').attr('href').replace(/sort=(RELEVANCE|ALPHA_DESC|ALPHA_ASC)/i, 'sort=' + sortSelect.val()));
//          $('.clear-filters').attr('href', $('.clear-filters').attr('href').replace(/rows=\d+/g, 'rows=' + rowsSelect.val()));
//        }
//        return false;
//      }
//  );

  $('#thumbnail-filter').click(function() {
    var valueCheck = $(this);
    var paramsArray = [['isThumbnailFiltered', 'false']];

    if (valueCheck.is(':checked')) {
      paramsArray = [['isThumbnailFiltered', 'true']];
    }
    paramsArray.push(['offset', 0]);
    var newUrl = $.addParamToCurrentUrl(paramsArray);
    de.ddb.next.search.fetchResultsList(newUrl);
  });
  $('#view-grid').click(
      function() {
        $('.summary-main .title a').each(
            function(index, value) {
              var newTitle = value.title.toString();
              if ($(this).closest('.summary-main').find('.matches li span strong').length === 0
                  && jQuery.trim($(value).find('strong')).length > 0) {
                newTitle = jQuery.trim($(value).html());
              } else {
                var replacementsRegex = new StringBuilder();
                replacementsRegex.append("(");
                $(this).closest('.summary-main').find('.matches li span strong').each(
                    function(sindex, svalue) {
                      var tmpSvalueText = (svalue.innerHTML + '').replace(/([.?*+^$[\]\\(){}|-])/g,
                          "\\$1");
                      if (replacementsRegex.getLength() > 1) {
                        replacementsRegex.append("|");
                      }
                      replacementsRegex.append(tmpSvalueText);
                    });
                replacementsRegex.append(")");
                newTitle = newTitle.replace(new RegExp(replacementsRegex.toString(), 'gi'),
                    "<strong>\$1</strong>");
              }
              if (newTitle.length > 60) {
                newTitle = $.trim(newTitle).substring(0, 60).split(" ").slice(0, -1).join(" ")
                    + "...";
              }
              $(this).closest( "div" ).addClass("grid-title");
              value.innerHTML = newTitle;
            });
        $('#view-list').removeClass('selected');
        $('#view-list').removeAttr('disabled');
        $('#view-grid').addClass('selected');
        $('#view-grid').attr("disabled", "disabled");
        $('.search-results').fadeOut(
            'fast',
            function() {
              // For no special line view of entity search results in grid ->
              // remove the not() statements again
              $('.results-list .summary').not('.entity-list .summary').removeClass('row');
              $('.results-list .summary-main-wrapper').not('.entity-list .summary-main-wrapper')
                  .removeClass('span6');
              $('.results-list .thumbnail-wrapper').not('.entity-list .thumbnail-wrapper')
                  .removeClass('span3');
              $('.results-list').not('.entity-list').addClass("grid");
              $('.search-results').fadeOut('fast');
              $('.results-list .summary .summary-main-wrapper').not(
                  '.entity-list .summary .summary-main-wrapper').each(function() {
                $(this).appendTo(this.parentNode);
              });
              $('.search-results').fadeIn('fast');
            });
        var paramsArray = [['viewType', 'grid']];
        var newUrl = $.addParamToCurrentUrl(paramsArray);
        $('.page-nav a, .page-nav-mob a').each(function() {
          this.href = $.addParamToCurrentUrl(paramsArray, this.href.split("?")[1]);
        });
        $('.results-list a').each(function() {
          this.href = $.addParamToUrl(null, paramsArray, this.href.split("?")[0], this.href.split("?")[1]);
        });
        $('.clear-filters a').attr('href',
            $('.clear-filters a').attr('href').replace(/viewType=(list|grid)/i, 'viewType=grid'));
        de.ddb.common.search.historyManager(newUrl);
        de.ddb.common.search.setSearchCookieParameter(paramsArray);
        historyedited = true;
      });
  $('#keep-filters').click(function() {
    var valueCheck = $(this);
    var paramsArray = [['keepFilters', 'false']];

    if (valueCheck.is(':checked')) {
      paramsArray = [['keepFilters', 'true']];
    }
    $.addParamToCurrentUrl(paramsArray);
    de.ddb.common.search.setSearchCookieParameter(paramsArray);
  });
  $('.clear-filters').click(function() {
    de.ddb.common.search.removeSearchCookieParameter('facetValues[]');
  });

  $('.type-selection').change(function(){
    var currentQuery = de.ddb.common.search.getUrlVar('query');
    var optionSelected = $('option:selected', this);
    window.location = optionSelected.val()+'?query='+currentQuery;
  });

  de.ddb.next.search.initializeFacets();

  function setHovercardEvents() {
    $('.information').each(function() {
      new de.ddb.next.search.HovercardInfoItem($(this));
    });
  }

  function addToSavedSearches() {
    $.urlParam = function(name) {
      var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
      if (results == null) {
        return null;
      } else {
        return results[1] || "";
      }
    };
    $.truncateTitle = function(string) {
      var result = "";
      var words = string.split(/\s+/);
      for ( var index = 0; index < 3 && index < words.length; index++) {
        if (result.length > 0) {
          result += " ";
        }
        result += words[index];
      }
      return result;
    };
    var queryString = decodeURIComponent($.urlParam("query").replace(/\+/g, '%20'));
    // take only the first 3 words as title
    $("#addToSavedSearchesTitle").val($.truncateTitle(queryString));
    $("#addToSavedSearchesModal").modal("show");
    $("#addToSavedSearchesConfirm").unbind("click");
    $("#addToSavedSearchesConfirm").click(function() {
      $("#addToSavedSearchesModal").modal("hide");
      var title = $("#addToSavedSearchesTitle").val();
      if (title.length > 0) {
        de.ddb.next.search.hideError();
        $.ajax({
          type : "PUT",
          contentType : "application/json",
          dataType : "json",
          url : jsContextPath + "/apis/savedsearches",
          data : JSON.stringify({
            query : window.location.search.substring(1),
            title : title,
            type : $("#addToSavedSearches").attr("data-type")
          })
        }).done(function() {
          disableSavedSearch($(".add-to-saved-searches"));
        });
      } else {
        de.ddb.next.search.showError(messages.ddbnext.Savedsearch_Without_Title);
      }
    });
  }

  /**
   * AJAX request to check if a result hit is already stored in the list of favorites.
   *
   * Install a click event handler to add a result hit to the list of favorites.
   */
  function checkFavorites() {
    var itemIds = [];

    // Only perform this check if a user is logged in
    if (jsLoggedIn === "true") {

      // collect all item ids on the page
      $(".search-results .summary-main .persist").each(function() {
        itemIds.push(extractItemId($(this).attr("href")));
      });

      // check if a result hit is already stored in the list of favorites
      $.ajax({
        type : "POST",
        url : jsContextPath + "/apis/favorites/_get",
        contentType : "application/json",
        data : JSON.stringify(itemIds),
        success : function(favoriteItemIds) {
          $.each(itemIds, function(index, itemId) {
            var div = $("[data-itemid='" + itemId + "']");

            if ($.inArray(itemId, favoriteItemIds) >= 0) {
              disableFavorite(div);
            }
            else {
              addFavoriteEvent(div);
            }
          });
        }
      });
    }
  }

  /**
   * Check if the current search string is already stored as a saved search.
   */
  function checkSavedSearch() {
    // Only perform this check if a user is logged in
    if (jsLoggedIn === "true") {

      $.ajax({
        type : "POST",
        contentType : "application/json",
        dataType : "json",
        url : jsContextPath + "/apis/savedsearches/_get",
        data : JSON.stringify({
          query : window.location.search.substring(1),
          type : $("#addToSavedSearches").attr("data-type")
        })
      }).statusCode( {
        200: function() { //SC_OK
          disableSavedSearch($(".add-to-saved-searches"));
        },
        204: function() { //SC_NO_CONTENT
          enableSavedSearch($(".added-to-saved-searches"));
        }
      });
    }
  }

  /**
   * Disable the saved search button.
   *
   * @param div DIV element which handles the saved search event
   */
  function disableSavedSearch(div) {
    $("#addToSavedSearches").unbind("click");
    div.removeClass("add-to-saved-searches");
    div.addClass("added-to-saved-searches");
    $("#addToSavedSearchesAnchor").addClass("off");
    $("#addToSavedSearchesSpan").removeClass("off");
  }

  /**
   * Enable the saved search button.
   *
   * @param div DIV element which handles the saved search event
   */
  function enableSavedSearch(div) {
    $("#addToSavedSearches").unbind("click");
    $("#addToSavedSearches").click(function() {
      addToSavedSearches();
    });
    div.removeClass("added-to-saved-searches");
    div.addClass("add-to-saved-searches");
    $("#addToSavedSearchesSpan").addClass("off");
    $("#addToSavedSearchesAnchor").removeClass("off");
  }

  /**
   * Extract the item id from the given URL.
   *
   * @param url the URL containing the item id
   *
   * @returns item id
   */
  function extractItemId(url) {
    var result = null;
    var parts = url.split("/");

    result = parts[parts.length - 1];

    var queryParameters = result.indexOf("?");

    if (queryParameters >= 0) {
      result = result.substring(0, queryParameters);
    }
    return result;
  }
};
