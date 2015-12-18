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
  $(".navigation").mouseenter(function() {
    $(".navigation > li").hover(function() {
      $(".navigation li").removeClass("active");
//      if (!$(this).hasClass('active-default')) {
//        $(".active-default ul").css('display', 'none');
//      } else {
//        $(".active-default ul").css('display', 'block');
//      }
      $(this).addClass("active");
    });
  }).mouseleave(function() {
    $(".navigation li").removeClass("active");
//    $(".active-default ul").css('display', 'block');
  });
  //Accessibility: keyboard for language selection
  $('.language-wrapper >a').focus(function() {
    $('.selector').fadeIn();
  });
  $('.language-wrapper .selector a').blur(function() {
    var languageSelector = $('.selector');
    languageSelector.fadeOut('fast', function() {
      languageSelector.removeAttr('style');
    });
  });
  $('.login-link-referrer').on( "click", function() {
    var referrerUrl = window.location.pathname + window.location.search + window.location.hash;
    $(this).attr("href", jsContextPath+"/login?referrer="+encodeURIComponent(referrerUrl));
  });
  $(".no-script").hide();
  $(".script").show();
});
