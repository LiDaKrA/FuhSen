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

  if (jsPageName === "item") {
    var socialMediaManager = new SocialMediaManager();
    de.ddb.common.search.initHistorySupport(null);
    de.ddb.next.search.paginationWidget = new de.ddb.next.PaginationWidget();
    socialMediaManager.integrateSocialMedia();

    //Similar objects
    $(".similar-objects-items").hide();
    $(".similar-objects-header").removeClass("active");

    $(".similar-objects-header").click(function() {
      $(".similar-objects-items").toggle( "slow", function() {
      });

      $(".similar-objects-header").toggleClass("active");
    });

    $('.similar-objects-items').find('li').each(function() {
      new de.ddb.next.search.HovercardInfoItem($(this));
    });

    $('.page-input').removeClass('off');
    $('.page-nonjs').addClass("off");
    de.ddb.next.search.paginationWidget.setPageInputKeyupHandler(
        function(e, element){
          if (e.keyCode === 13) {
            if (/^[0-9]+$/.test(element.value)) {
              var resultPagesCountText = $('.result-pages-count').text();
              var resultPagesCountInt = parseInt(resultPagesCountText.replace(/[^0-9]/g, ''));

              if (parseInt(element.value) <= 0) {
                element.value = 1;
              } else if (parseInt(element.value) > resultPagesCountInt) {
                element.value = $('.result-pages-count').text();
              }
            } else {
              element.value = 1;
            }

            $('.page-input').attr('value', element.value);

            var valueInteger = parseInt(element.value.replace(/[^0-9]/g, ''));
            var paramsArray = [['hitNumber', valueInteger]];
            var newUrl = $.addParamToCurrentUrl(paramsArray);
            window.location = newUrl;
          }
        }
    );
  }

});