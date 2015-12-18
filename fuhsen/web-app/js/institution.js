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
$(document).ready(function() {

  if (jsPageName === "institution") {

    var defaultRowCount = 10;

    var allRowCount = 0;

    var windowWidth = 0;

    var carouselWidth = 1080;

    var carouselHeight = 175;

    var windowLarge = 1185;

    var windowMediumMax = 965;

    var windowMediumMin = 753;

    var windowSmallMax = 661;

    var windowSmallMin = 451;

    var disableCarouselArrows = function() {
      $(".btn-prev").addClass("disabled");
      $(".btn-prev").off('click');
      $(".btn-next").addClass("disabled");
      $(".btn-next").off('click');
    };

    var getNewSearchResults = function(offset, rows, institutionid) {
      $.ajax({
        type : 'GET',
        dataType : 'json',
        async : true,
        url : jsContextPath + '/institution/ajax/highlights?offset=' + offset + '&rows=' + rows + '&institutionid=' + institutionid,
        complete : function(data) {
          var jsonResponse = $.parseJSON(data.responseText);
          var items = $.parseHTML(jsonResponse.html);

          if(items) {
            $(".highlights").removeClass("off");

            //Adds the items from the search to the carousel. Doing this one by one to avoid problems with the carousel.
            $.each(items, function(index, value) {
              if (value.tagName === 'DIV') {
                $("#items").triggerHandler("insertItem", [ value, "end", true ]);
              }
            });
          }

          allRowCount = jsonResponse.resultCount;

          windowWidth = $(window).width();

          if (($(window).width() >= windowLarge && $("#items > div").size() < 6) ||
              (((windowWidth >= windowMediumMax && windowWidth < windowLarge) || (windowWidth >= windowSmallMax && windowWidth < windowMediumMin)) && $("#items > div").size() < 4) ||
              (((windowWidth >= windowMediumMin && windowWidth < windowMediumMax) || (windowWidth >= windowSmallMin && windowWidth < windowSmallMax)) && $("#items > div").size() < 3) ||
              (windowWidth < windowSmallMin && $("#items > div").size() < 2)
             ) {
            disableCarouselArrows();
          }

        }
      });

    };

    var initCarousel = function() {
      $(".btn-prev").removeClass("disabled");
      $(".btn-next").removeClass("disabled");

      var carouselItems = $("#items");

      $('div.carousel').show();

      if ($(".item .caption").length > 0) {
        $(".item .caption").dotdotdot({});
      }

      $(".btn-next").click(
          function() {
            carouselItems.trigger("next", 1);

            var currentLoadItems = $(".preview-item");
            var currentVisibleItems = carouselItems.triggerHandler("currentVisible");
            var numberOfVisibleItems = currentVisibleItems.length;
            var currentPosition = carouselItems.triggerHandler("currentPosition");
            var nextVisbleItem = currentPosition + numberOfVisibleItems;

            if ((nextVisbleItem > (currentLoadItems.length - 1)) && (currentLoadItems.length < allRowCount)) {
              var institutionid = $("#institution-id").data("institutionid");
              //var urlParameters = "?institutionid=" + institutionid + "&offset=" + currentLoadItems.length + "&rows=" + defaultRowCount;
              //var History = window.History;
              //History.pushState("", document.title, decodeURI(urlParameters));

              // Initialize Search results
              getNewSearchResults(currentLoadItems.length, defaultRowCount, institutionid);
            }
          });

      $(".btn-prev").click(function() {
        carouselItems.trigger("prev", 1);
      });

      windowWidth = $(window).width();

      if ((windowWidth >= windowMediumMax && windowWidth < windowLarge) || (windowWidth >= windowSmallMax && windowWidth < windowMediumMin)) {
        carouselWidth = 720;
      } else if ((windowWidth >= windowMediumMin && windowWidth < windowMediumMax) || (windowWidth >= windowSmallMin && windowWidth < windowSmallMax)){
        carouselWidth = 540;
      } else if (windowWidth < windowSmallMin){
        carouselWidth = 180;
      }

      if (carouselItems.length) {
        carouselItems.carouFredSel({
          infinite : false,
          width : carouselWidth,
          height : carouselHeight,
          align : false,
          auto : false,
          scroll : 1,
          items : {
              minimum : 1
          },
          prev : {
            button : ".btn-prev"
          },
          next : {
            button : ".btn-next"
          }
        });
      }
    };

    var initPage = function() {
      var institutionid = $("#institution-id").data("selectedinstitutionid");

      initCarousel();

      // Initialize Search results
      getNewSearchResults(0, defaultRowCount, institutionid);

      var socialMediaManager = new SocialMediaManager();
      de.ddb.common.search.initHistorySupport(null);
      de.ddb.next.search.paginationWidget = new de.ddb.next.PaginationWidget();
      socialMediaManager.integrateSocialMedia();
    };

    initPage();
  };

});
