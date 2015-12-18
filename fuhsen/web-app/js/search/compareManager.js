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
 * Compare Manager
 * Handles all coparison functionality on the results page
 */
de.ddb.next.search.CompareManager = function() {
};

$.extend(de.ddb.next.search.CompareManager.prototype, {

  /**
   * Initialize the components for the object comparison
   */
  initComparison : function() {
    var currObjInstance = this;

    // Comparison should only works with Javascript. So remove the CSS class
    // 'off' from the compare components
    $('.compare').removeClass("off");
    $('.compare-objects').removeClass("off");

    $('.compare-objects .fancybox-toolbar-close').click(function(event) {
      event.preventDefault();
      // Get the index of the compare-object.
      var index = $(event.target).attr("data-index");
      currObjInstance.removeCompareCookieParameter(index);
      currObjInstance.renderCompareObjects();
    });

    currObjInstance.renderCompareObjects();
  },

  /**
   * This functions selects the item from the result list and try to store some parameters in the comparison cookie.
   */
  selectCompareItem: function(id) {
    var currObjInstance = this;

    var itemId = id;

    // select the thumbnail image of the selected item
    var image = $('#thumbnail-' + itemId + ' img');
    var imageSrc = null;

    if (image.attr("src").indexOf(itemId) !== -1) {
      imageSrc = image.attr("src");
    }

    var text = image.attr("alt");
    if (text.length > 30) {
      text = text.substr(0, 30) + "...";
    }

    currObjInstance.setCompareCookieParameter(itemId, imageSrc, text);

    currObjInstance.renderCompareObjects();
  },

  /**
   * This funtion is responsible for rendering the compare components.
   * There are two compare obejects which can hold either
   * <ul>
   * <li>a default text</li>
   * <li>an image of the item</li>
   * <li>a text of the item</li>
   * </ul>
   *
   * The rendering is based on the item values stored in the comparison cookie.
   *
   */
  renderCompareObjects: function() {
    var currObjInstance = this;

    var cookieVal = currObjInstance.getComparisonCookieVal();

    // Rendering the value for both compare-objects
    $('.compare-object').each(
        function(index) {
          // closure index starts with 0!
          var itemNumber = index + 1;

          // Retrieve the elements via JQuery
          var compareObjectId = '#compare-object' + itemNumber;
          var compareLink = $(compareObjectId + ' .compare-link');
          var compareImage = $(compareObjectId + ' .compare-img');
          var compareText = $(compareObjectId + ' .compare-text');
          var compareDefault = $(compareObjectId + ' .compare-default');
          var compareRemove = $(compareObjectId + ' .fancybox-toolbar-close');
          var compareBgImage = $(compareObjectId + ' .compare-default-pic');

          // Get the associated item id from the cookie
          var cookieId = (cookieVal !== null) ? cookieVal['id' + itemNumber]
              : null;

          // Set default message if no cookie exists or itemId is null
          if (cookieVal === null || cookieId === null) {
            compareDefault.removeClass("off");
            compareBgImage.removeClass("off");
            compareText.addClass("off");
            compareImage.addClass("off");
            compareRemove.addClass("off");
          } else {
            var cookieSrc = cookieVal['src' + itemNumber];
            var cookieText = cookieVal['text' + itemNumber];

            //Hide default message
            compareDefault.addClass("off");
            compareBgImage.addClass("off");
            compareRemove.removeClass("off");

            // be sure to get the latest compare items and url queries (facets etc.) for the anchor reference. So use an click event for this issue
            compareLink.off();
            compareLink.on("click", function() {
              //Update the url of the link
              var urlQuery = window.location.search;
              var url = jsContextPath + '/item/' + cookieId + urlQuery;
              compareLink.attr("href", url);
            });

            // show the item's image or text
            if (cookieSrc !== null && cookieSrc.length !== -1) {
              compareImage.attr("src", cookieSrc);
              compareImage.attr("alt", cookieText);
              compareImage.attr("title", cookieText);

              compareText.addClass("off");
              compareImage.removeClass("off");
            } else {
              compareText.html(cookieText);

              compareText.removeClass("off");
              compareImage.addClass("off");
            }
          }
        });

    currObjInstance.setComparisonButtonState();
    currObjInstance.setItemCompareButtonState();
  },

  /**
   * Try to set an item to the comparison cookie. The cookie has two slots for holding two unequal items. Equalsness is checked via the item id.
   *
   * An item is set to the first free slot found in the cookie.
   *
   * The value of the cookie is in JSON format and can hold the id, src and text of an item.
   */
  setCompareCookieParameter: function(itemId, imgSrc, text) {
    var currObjInstance = this;

    var cookieVal = currObjInstance.getComparisonCookieVal();

    de.ddb.next.search.hideError();

    // if the cookie exists, check if the first or second compare slot is free
    if (cookieVal !== null) {
      if ((cookieVal.id1 !== null) && (cookieVal.id2 !== null)) {
        de.ddb.next.search.showError(messages.ddbnext.SearchResultsCompareItemOnly2);
      } else if ((cookieVal.id1 === itemId) || (cookieVal.id2 === itemId)) {
        de.ddb.next.search.showError(messages.ddbnext.SearchResultsCompareItemAlreadySet);
      } else if (cookieVal.id1 === null) {
        cookieVal.id1 = itemId;
        cookieVal.src1 = imgSrc;
        cookieVal.text1 = text;
        itemAdded = true;
      } else if (cookieVal.id2 === null) {
        cookieVal.id2 = itemId;
        cookieVal.src2 = imgSrc;
        cookieVal.text2 = text;
        itemAdded = true;
      }
    }
    // if the cookie don't exist create a new value and set the item on the
    // first position
    else {
      cookieVal = {
        id1 : itemId,
        src1 : imgSrc,
        text1 : text,
        id2 : null,
        src2 : null,
        text2 : null
      };
    }

    // Set the cookie
    currObjInstance.setComparisonCookieVal(cookieVal);
  },

  /**
   * Removes an comparison item from the cookie.
   * The index parameters can have the values 1 and 2
   */
  removeCompareCookieParameter: function(index) {
    var currObjInstance = this;

    var cookieVal = currObjInstance.getComparisonCookieVal();
    de.ddb.next.search.hideError();

    //It's not possible to change to !==
    if ((1 !== index) && (2 !== index)) {
      return;
    }

    if (cookieVal !== null) {
      cookieVal['id' + index] = null;
      cookieVal['src' + index] = null;
      cookieVal['text' + index] = null;

      currObjInstance.setComparisonCookieVal(cookieVal);
    }
  },

  /**
   * Activating and deactivating of the compare icon of each item.
   * The state of the item depends on the item id's stored in the cookie
   */
  setItemCompareButtonState: function() {
    var currObjInstance = this;

    var cookieVal = currObjInstance.getComparisonCookieVal();

    //1) Add functionality for selecting result list items for comparison
    $('.compare').each(function() {
      $(this).removeClass("disabled");

      //remove old click handler
      $(this).off();

      //add new click handler for selecting
      $(this).click(function(event) {
        event.stopPropagation();
        var item = $(event.target);
        var itemId = item.attr('data-iid');
        currObjInstance.selectCompareItem(itemId);
      });
    });

    //2) Add functionality for deselecting result list items from comparison
    var selectedItems = $('.compare').filter(
        function() {
          if (cookieVal) {
            return ($(this).attr('data-iid') === cookieVal.id1 || $(this).attr('data-iid') === cookieVal.id2);
          }
     });

    selectedItems.each(function() {
      $(this).addClass("disabled");

      //remove old click handler
      $(this).off();

      //add new click handler for deselecting
      $(this).click(function(event) {
        event.preventDefault();
        cookieVal = currObjInstance.getComparisonCookieVal();

        // Get the index of the compare-object.
        var index = -1;
        if ($(this).attr('data-iid') === cookieVal.id1) {
          index = 1;
        } else if ($(this).attr('data-iid') === cookieVal.id2) {
          index = 2;
        }

        currObjInstance.removeCompareCookieParameter(index);
        currObjInstance.renderCompareObjects();
        currObjInstance.setItemCompareButtonState();
      });
    });
  },

  /**
   * Enable/Disable the comparison button.
   * For this the selected items id's stored in the cookie are needed.
   *
   * Activate the button if two items are selected. Otherwise disable the button.
   */
  setComparisonButtonState: function() {
    var currObjInstance = this;

    var cookieVal = currObjInstance.getComparisonCookieVal();

    // The compare buton is disabled by default
    var compareButton = $('#compare-button .button');
    compareButton.addClass('disabled');
    compareButton.find('div').removeClass('button');

    // Enable the compare button only if two items are selected for comparison
    if ((cookieVal !== null) && (cookieVal.id1 !== null) && (cookieVal.id2 !== null)) {
      compareButton.removeClass('disabled');
      compareButton.find('div').addClass('button');

      // be sure to get the latest compare items and url queries (facets etc.) for the anchor reference. So use an click event for this issue
      compareButton.off();
      compareButton.on("click", function() {
        var urlQuery = window.location.search;
        var url = jsContextPath + '/compare/' + cookieVal.id1 + '/with/'
            + cookieVal.id2 + urlQuery;
        window.location = url;
      });
    }
  },

  getComparisonCookieVal: function() {
    var cookieName = 'compareParameters' + jsContextPath;
    var cookieVal = $.cookies.get(cookieName);

    return cookieVal;
  },

  setComparisonCookieVal: function(cookieVal) {
    var cookieName = 'compareParameters' + jsContextPath;
    $.cookies.set(cookieName, cookieVal);
  }
});