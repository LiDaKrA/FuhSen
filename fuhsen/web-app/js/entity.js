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
$(document).ready(
    function() {

      if (jsPageName === "entity") {

        var socialMediaManager = new SocialMediaManager();
        socialMediaManager.integrateSocialMedia();

        var defaultRowCount = 10;

        var allRowCount = 0;

        var windowWidth = 0;

        var carouselWidth = 800;

        var carouselHeight = 180;

        var windowLarge = 1185;

        var windowMediumMax = 965;

        var windowMediumMin = 753;

        var windowSmallMax = 661;

        var windowSmallMin = 451;

        var disableCarouselArrows = function() {
          $("#previous").addClass("disabled");
          $("#previous").off('click');
          $("#next").addClass("disabled");
          $("#next").off('click');
        };

        var getNewSearchResults = function(query, offset, rows, entityid) {
          $.ajax({
            type : 'GET',
            dataType : 'json',
            async : true,
            url : jsContextPath + '/entity/ajax/searchresults?query=' + query + '&offset=' + offset
                + '&rows=' + rows + '&entityid=' + entityid,
            complete : function(data) {
              var jsonResponse = $.parseJSON(data.responseText);
              var items = $.parseHTML(jsonResponse.html);

              if(items) {
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
          var carouselItems = $("#items");

          $('div.carousel').show();

          if ($(".item .caption").length > 0) {
            $(".item .caption").dotdotdot({});
          }

          $("#next").click(
              function() {
                carouselItems.trigger("next", 1);

                var currentLoadItems = $(".preview-item");
                var currentVisibleItems = carouselItems.triggerHandler("currentVisible");
                var numberOfVisibleItems = currentVisibleItems.length;
                var currentPosition = carouselItems.triggerHandler("currentPosition");
                var nextVisbleItem = currentPosition + numberOfVisibleItems;

                if ((nextVisbleItem > (currentLoadItems.length - 1))
                    && (currentLoadItems.length < allRowCount)) {
                  var query = $("#entity-title").html();
                  var History = window.History;
                  var urlParameters = "?query=" + query + "&offset=" + currentLoadItems.length
                      + "&rows=" + defaultRowCount;
                  History.pushState("", document.title, decodeURI(urlParameters));

                  // Initialize Search results
                  getNewSearchResults(query, currentLoadItems.length, defaultRowCount, entityid);
                }
              });

          $("#previous").click(function() {
            carouselItems.trigger("prev", 1);
          });

          windowWidth = $(window).width();

          if ((windowWidth >= windowMediumMax && windowWidth < windowLarge) || (windowWidth >= windowSmallMax && windowWidth < windowMediumMin)) {
            carouselWidth = 600;
          } else if ((windowWidth >= windowMediumMin && windowWidth < windowMediumMax) || (windowWidth >= windowSmallMin && windowWidth < windowSmallMax)){
            carouselWidth = 400;
          } else if (windowWidth < windowSmallMin){
            carouselWidth = 200;
          }

          if (carouselItems.length) {
            carouselItems.carouFredSel({
              infinite : false,
              circular: false,
              width : carouselWidth,
              height : carouselHeight,
              align : false,
              auto : false,
              scroll : 1,
              items : {
                  minimum : 1
              },
              prev : {
                button : "#previous"
              },
              next : {
                button : "#next"
              }
            });
          }
        };

        var initPage = function() {
          initCarousel();

          var query = $("#entity-title").html();
          var entityid = $("#entity-id").attr("data-entityid");

          // Initialize Search results
          getNewSearchResults(query, 0, defaultRowCount, entityid);
        };

        initPage();

        if($("div.external-links ul li.external-link").length > 1) {
          $("div.external-links").removeClass("off");
        }

      }
    });