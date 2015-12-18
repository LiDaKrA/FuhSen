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
$(function() {
  if (jsPageName === "persons") {
    var randomSeed = $('#var-to-js').attr('data-random-seed');
    var pgTitle = $('#var-to-js').attr('data-pgTitle');
    History.pushState({sort:randomSeed}, pgTitle + " " +randomSeed, "?sort="+randomSeed);
    document.title = pgTitle + " - " + messages.ddbnext.Deutsche_Digitale_Bibliothek();
    var socialMediaManager = new SocialMediaManager();
    socialMediaManager.integrateSocialMedia();


    var container = document.querySelector('#columns');
    imagesLoaded( container, function() {
      new Masonry( container, {
        // options
        isOriginLeft: false,
        columnWidth: 170,
        itemSelector: '.pin'
      });
    });

    $(window).bind("load", function() {
        $('.small-loader').remove();
        $('.persons-overview-overlay-waiting').remove();
        $('.persons-overview-overlay-modal').remove();
      });

  }

});
