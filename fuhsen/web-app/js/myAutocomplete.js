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
var monkeyPatchAutocomplete = function() {
  $.ui.autocomplete.prototype._renderItem = function(ul, item) {
    var termLength = this.term.length;

    var urlEncodedItem = encodeURIComponent(item.label);

    // DDBNEXT-1270: Filter all items that contains special backend sort chars
    if ((urlEncodedItem.indexOf("%C2%98") !== -1) || (urlEncodedItem.indexOf("%C2%9C") !== -1)) {
      return ul;
    }

    var highLightedItemPart = "<span style='font-weight:bold;'>" + item.label.substring(0, termLength) + "</span>";
    var normalItemPart = item.label.substring(termLength);

    var autocompleteItem = highLightedItemPart + normalItemPart;

    return $("<li></li>").data("item.autocomplete", item).append("<a>" + autocompleteItem + "</a>").appendTo(ul);
  };
};

/**
 * Return a function performing an AJAX request to the given url.
 */
var getAjaxSuggest = function(url) {
  return function (request, response) {
    $.ajax({
      url : url,
      dataType : "jsonp",
      data : {
        query : request.term
      },
      success : function(data) {
        response($.map(data, function(n) {
          return {
            label : n,
            value : n
          };
        }));
      }
    });
  };
};

$(function() {

  //The AJAX endpoint depends on page
  var ajaxCall = null;

  if (jsPageName === "index" || jsPageName === "results") {
    ajaxCall = getAjaxSuggest(jsContextPath + "/apis/autocomplete/");
  } else if (jsPageName === "searchperson") {
    ajaxCall = getAjaxSuggest(jsContextPath + "/apis/entitiesAutocomplete/");
  }

  if (ajaxCall) {
    monkeyPatchAutocomplete();
    //TODO restore this functionality when we have an internal fuhsen knowledge base
    /*$('input.query').autocomplete({
      source : ajaxCall,
      minLength : 1,
      open : function() {
        $(this).removeClass("ui-corner-all").addClass("ui-corner-top");
        $(this).autocomplete("widget").css('width', (parseInt($(this).outerWidth()) - 6) + 'px');
      },
      select : function(a, b) {
        $(this).val(b.item.value);
        // $(this).parents('form').submit();
      },
      close : function() {
        $(this).removeClass("ui-corner-top").addClass("ui-corner-all");
      }
    });*/
    $(window).resize(
        function() {
          var mainInputs = $('input[type="search"].query');
          mainInputs.each(function(index, input) {
            var mainInput = $(input);
            var position = $(mainInput).offset();
            $(mainInput).autocomplete("widget").css('left', position.left + 'px');
            $(mainInput).autocomplete("widget").css('top',
                (position.top + $(mainInput).outerHeight()) + 'px');
            $(mainInput).autocomplete("widget").css('width',
                (parseInt($(mainInput).outerWidth()) - 6) + 'px');
            if ($(window).width() < 768 && $(mainInput).parents('#form-search').length < 1) {
              $(mainInput).autocomplete("close");
            }
          });
        });
  };
});
