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
//IMPORTANT FOR MERGING: This is the main function that has to be called when we are in the saved searches page
$(function() {
  if (jsPageName === "publicFavorites") {

    /** Fade in report-favorites overlay when social icon is clicked * */
    $(".reportfav").mouseover(function() {
      showReportFavoritesOverlay();
    });

    /** Fade out report-favorites overlay when overlay div is leaved * */
    $(".reportfav").mouseleave(function() {
      hideReportFavoritesOverlay();
    });

    $('.page-input').removeClass('off');
    $('.page-nonjs').addClass("off");
    // workaround for ffox + ie click focus - prevents links that load dynamic
    // content to be focussed/active.
    $("a.noclickfocus").live('mouseup', function() {
      $(this).blur();
    });

    $("#favorites-list-send").click(function(event) {
      event.preventDefault();
      $('#sendFavoriteListModal').modal({
        remote : $(this).attr("href")
      });
    });
  }
});

function showReportFavoritesOverlay() {
  $(".reportfav .report-overlay-container").fadeIn(200);
}

function hideReportFavoritesOverlay() {
  window.setTimeout(function() {
    $(".reportfav .report-overlay-container").fadeOut(200);
  }, 200);
}

$(document).ready(function() {
  if (jsPageName === "publicFavorites") {
    var socialMediaManager = new SocialMediaManager();
    socialMediaManager.integrateSocialMedia();
  }
});