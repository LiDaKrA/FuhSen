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
$(document)
    .ready(
        function() {

          SocialMediaManager = function() {
            this.init();
          };

          /** Capsulate main logic in object * */
          $
              .extend(
                  SocialMediaManager.prototype,
                  {

                    /** Configuration * */
                    socialMediaCookieName : "socialmedia/ddb-next",
                    socialMediaCookieValue : "allowed",
                    socialMediaCookieTTL : 1, // 1 = 1 day
                    urlToLike : null,
                    titleToLike : null,

                    /** Initialization * */
                    init : function() {
                    },

                    /**
                     * Main method. Call to integrate social network into the
                     * page *
                     */
                    integrateSocialMedia : function() {
                      this.registerClickHandlers();
                      this.applyCookieAllowed();
                    },

                    /**
                     * Register the click- and mouseover-events on the DOM
                     * objects *
                     */
                    registerClickHandlers : function() {
                      var self = this;

                      /** Fade in overlay when social icon is clicked * */
                      $(".socialmedia .social-locked ul li").mouseover(function() {
                        self.showPrivacyInformationOverlay();
                      });

                      /** Fade out overlay when overlay div is leaved * */
                      $(".socialmedia .social-locked .social-overlay-container").mouseleave(
                          function() {
                            self.hidePrivacyInformationOverlay();
                          });

                      /**
                       * If the user accepts the privacy tooltip -> attach
                       * social sites to page *
                       */
                      $(".socialmedia .social-accept").click(function() {
                        self.integrateSocialCodeInPage(true);
                      });

                      /**
                       * If the user revokes the social integration -> remove
                       * integration from page and delete cookie *
                       */
                      $(".socialmedia .social-open .social-lockagain").click(function() {
                        self.removeSocialCodeFromPage();
                      });
                    },

                    /** If access is allowed by cookie -> skip privacy tooltip * */
                    applyCookieAllowed : function() {
                      if (this.isSocialMediaCookieAllowed()) {
                        this.integrateSocialCodeInPage(false);
                      }
                    },

                    /** Displays the tooltip overlay with the privacy information * */
                    showPrivacyInformationOverlay : function() {
                      $(".socialmedia .social-locked .social-overlay-container").fadeIn(200);
                    },

                    /** Hides the tooltip overlay with the privacy information * */
                    hidePrivacyInformationOverlay : function() {
                      window.setTimeout(function() {
                        $(".socialmedia .social-locked .social-overlay-container").fadeOut(200);
                      }, 200);
                    },

                    /** Integrates the social network code into the page * */
                    integrateSocialCodeInPage : function(setCookie) {
                      this.checkForOpenGraphMetaTags();

                      $(".socialmedia .social-locked .social-overlay-container").css("display",
                          "none");
                      $(".socialmedia .social-locked").css("display", "none");
                      $(".socialmedia .social-open iframe").attr("scrolling", "no");
                      $(".socialmedia .social-open").css("display", "block");

                      var urlSelf = document.location.href;
                      if (this.urlToLike != null) {
                        urlSelf = this.urlToLike;
                      }
                      var urlSelfEncoded = encodeURIComponent(urlSelf);
                      var pageTitle = encodeURIComponent(window.document.title);
                      if (this.titleToLike != null) {
                        pageTitle = encodeURIComponent(this.titleToLike);
                      }
                      var languageISO2 = $(".socialmedia").attr("data-lang-iso2");
                      var languageFull = $(".socialmedia").attr("data-lang-full");

                      var urlFacebook = "https://www.facebook.com/plugins/like.php?locale="
                          + languageFull
                          + "&href="
                          + urlSelfEncoded
                          + "&send=false&layout=button_count&width=130&show_faces=false&action=like&colorscheme=light&amp;font&height=21";
                      var urlTwitter = "https://platform.twitter.com/widgets/tweet_button.html?url="
                          + urlSelfEncoded + "&counturl=" + urlSelfEncoded + "&text=" + pageTitle
                          + "&count=horizontal&lang=" + languageISO2;
                      var htmlGooglePlus = '<div class="g-plusone" data-size="medium" data-href="'
                          + urlSelf
                          + '"></div><script type="text/javascript">window.___gcfg = {lang: "'
                          + languageISO2
                          + '"}; (function() { var po = document.createElement("script"); po.type = "text/javascript"; po.async = true; po.src = "https://apis.google.com/js/plusone.js"; var s = document.getElementsByTagName("script")[0]; s.parentNode.insertBefore(po, s); })(); </script>';

                      $(".socialmedia .social-open .social-facebook iframe").attr("src",
                          urlFacebook);
                      $(".socialmedia .social-open .social-twitter iframe").attr("src", urlTwitter);
                      $(".socialmedia .social-open .social-googleplus").html(htmlGooglePlus);

                      if (setCookie) {
                        this.setSocialMediaCookie();
                      }
                    },

                    /** Removes the social network code from the page * */
                    removeSocialCodeFromPage : function() {
                      $(".socialmedia .social-open .social-facebook iframe").attr("src", "");
                      $(".socialmedia .social-open .social-twitter iframe").attr("src", "");
                      $(".socialmedia .social-open .social-googleplus").html("");

                      $(".socialmedia .social-locked").css("display", "block");
                      $(".socialmedia .social-open").css("display", "none");

                      this.removeSocialMediaCookie();
                    },

                    /**
                     * Sets a cookie that will immediately activate the social
                     * network integration *
                     */
                    setSocialMediaCookie : function() {
                      var expirationDate = new Date();
                      expirationDate.setDate(expirationDate.getDate() + this.socialMediaCookieTTL);
                      $.cookies.set(this.socialMediaCookieName, this.socialMediaCookieValue, {
                        expiresAt : expirationDate,
                        path : jsContextPath
                      });
                    },

                    /**
                     * Checks is the social network integration cookie is set
                     * and has the value 'allowed' *
                     */
                    isSocialMediaCookieAllowed : function() {
                      var socialMediaAllowed = $.cookies.get(this.socialMediaCookieName);
                      if (socialMediaAllowed === this.socialMediaCookieValue) {
                        return true;
                      }
                      return false;
                    },

                    /** Explicitely removes the social network cookie again * */
                    removeSocialMediaCookie : function() {
                      $.cookies.del(this.socialMediaCookieName, {
                        path : jsContextPath
                      });
                    },

                    /** Check for existing opengraph metatags to take config from * */
                    checkForOpenGraphMetaTags : function() {
                      var likeTitle = $("meta[property='og:title']").attr("content");
                      if (likeTitle !== null && likeTitle !== "") {
                        this.titleToLike = likeTitle;
                      }

                      var likeUrl = $("meta[property='og:url']").attr("content");
                      if (likeUrl !== null && likeUrl !== "") {
                        this.urlToLike = likeUrl;
                      }
                    }

                  });

        });