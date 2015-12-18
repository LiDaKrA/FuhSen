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

          if (jsPageName === "compare") {

            var socialMediaManager = new SocialMediaManager();
            socialMediaManager.integrateSocialMedia();

            if (navigator.appName.indexOf("Internet Explorer") === -1) {
              if ($(window).width() > 530) {
                mediaQuery = true;
              } else {
                mediaQuery = false;
              }
            }

            $(function() {
              var totImagesFirst = $(".first .gallery-images li").size();
              var totImagesSecond = $(".second .gallery-images li").size();
              var totVideosFirst = $(".first .gallery-videos li").size();
              var totVideosSecond = $(".second .gallery-videos li").size();
              var totAudiosFirst = $(".first .gallery-audios li").size();
              var totAudiosSecond = $(".second .gallery-audios li").size();
              var currentGalleryFirst = "";
              var currentGallerySecond = "";

              if (totImagesFirst > 0) {
                currentGalleryFirst = "images";
              } else if (totVideosFirst > 0) {
                currentGalleryFirst = "videos";
              } else if (totAudiosFirst > 0) {
                currentGalleryFirst = "audios";
              }

              if (totImagesSecond > 0) {
                currentGallerySecond = "images";
              } else if (totVideosSecond > 0) {
                currentGallerySecond = "videos";
              } else if (totAudiosSecond > 0) {
                currentGallerySecond = "audios";
              }
              updatePreview($("div."+currentGalleryFirst+".first"), "first");
              updatePreview($("div."+currentGallerySecond+".second"), "second");
            });

            var updatePreview = function(gallerydiv, position) {
              var a = gallerydiv.find("ul").children('li').eq(0).children('a');
              var previewUri = $(a).attr("href");
              var previewHref = $(a).attr("data-content");
              var type = $(a).attr("data-type");
              var title = $(a).attr("title");
              var title_text = title;
              var title_tooltip = title;
              var author = $(a).attr("data-author");
              var rights = $(a).attr("data-rights");
              var item_title = $(".item-title.first span").text();
              var offset = 0;
              var first = true;
              if (position === "second") {
                first = false;
                item_title = $(".item-title.second span").text();
                offset = $(".first .previews-list li").size();
                $(".second .previews").each(function() {
                  var secondPos = parseInt($(this).siblings().find("a.show-lightbox").attr("data-pos"))+offset;
                  $(this).attr("data-pos", messages.ddbnext.BinaryViewer_ImageCount(secondPos));
                });
              }
              // Title limited to 200 characters
              title_text = $.cutoffStringAtSpace(title, 200);

              // The tooltip of the title should be limited to 270 characters
              title_tooltip = $.cutoffStringAtSpace(title, 270);

              // The text and the tooltip of the author should be limited to 270
              // characters
              author = $.cutoffStringAtSpace(author, 270);

              // The text and the tooltip of the rights should be limited to 270
              // characters
              rights = $.cutoffStringAtSpace(rights, 270);

              // Item title limited to 350 characters
              item_title = $.cutoffStringAtSpace(item_title, 350);

              $.hideErrors();
              if (type === "image") {
                if (first) {
                  $(".first .viewer-icon").parent().addClass("off");
                  $(".first .previews").each(function() {
                    if ($(this).attr("href") === previewHref) {
                      $(this).parent().removeClass("off");
                      return false;
                    } else {
                      $(this).parent().appendTo($(".first .previews-list"));
                    }
                  });
                  $(".first .no-previews").each(function() {
                    if ($(this).find("img").attr("src") === previewUri) {
                      $(this).parent().removeClass("off");
                      return false;
                    } else {
                      $(this).parent().appendTo($(".first .previews-list"));
                    }
                  });
                  $(".first .pdf-previews").each(function() {
                    if ($(this).attr("href") === previewHref) {
                      $(this).parent().removeClass("off");
                      return false;
                    } else {
                      $(this).parent().appendTo($(".first .previews-list"));
                    }
                  });
                } else {
                  $(".second .viewer-icon").parent().addClass("off");
                  $(".second .previews").each(function() {
                    if ($(this).attr("href") === previewHref) {
                      $(this).parent().removeClass("off");
                      return false;
                    } else {
                      $(this).parent().appendTo($(".second .previews-list"));
                    }
                  });
                  $(".second .no-previews").each(function() {
                    if ($(this).find("img").attr("src") === previewUri) {
                      $(this).parent().removeClass("off");
                      return false;
                    } else {
                      $(this).parent().appendTo($(".second .previews-list"));
                    }
                  });
                  $(".second .pdf-previews").each(function() {
                    if ($(this).attr("href") === previewHref) {
                      $(this).parent().removeClass("off");
                      return false;
                    } else {
                      $(this).parent().appendTo($(".second .previews-list"));
                    }
                  });
                }
              } else {
                jwPlayerSetup(previewHref, previewUri, first);
              }
              if (first) {
                $(".first div.binary-title span").text(title_text);
                $(".first div.binary-title").attr("title", title_tooltip);

                $(".first div.binary-author span").text(author);
                $(".first div.binary-author").attr("title", author);

                $(".first div.binary-rights span").text(rights);
                $(".first div.binary-rights").attr("title", rights);

                $(".item-title.first span").text(item_title);
              } else {
                $(".second div.binary-title span").text(title_text);
                $(".second div.binary-title").attr("title", title_tooltip);

                $(".second div.binary-author span").text(author);
                $(".second div.binary-author").attr("title", author);

                $(".second div.binary-rights span").text(rights);
                $(".second div.binary-rights").attr("title", rights);

                $(".item-title.second span").text(item_title);
              }
            };

            var jwPlayerSetup = function(content, poster, firstElement) {
              var w = 440;
              var h = 320;
              var mediaQueryMatches = 1;
              if (firstElement) {
                if ($(".first .binary-viewer").length === 0) {
                  return;
                }
                $(".first .viewer-icon").parent().addClass("off");
                $(".first .binary-viewer").append('<div id="jwplayer-container-first"></div>');

                if (navigator.appName.indexOf("Internet Explorer") === -1) {
                  mediaQueryMatches = mediaQuery;
                }
                if (!mediaQueryMatches) {
                  w = 278;
                  h = 200;
                }
                $.initializeJwPlayer("jwplayer-container-first", content, poster, w, h, function() {
                  if ($.browser.msie && this.getRenderingMode() === "html5") {
                    $(".first .binary-viewer").find("[id*='jwplayer']").each(function() {
                      $(this).attr("unselectable", "on");
                    });
                  }
                }, function() {
                  if ($("#jwplayer-container-first")) {
                    $("#jwplayer-container-first").remove();
                  }
                  if ($(".first #jwplayer-container_wrapper")) {
                    $(".first #jwplayer-container_wrapper").remove();
                  }
                  if ($("#jwplayer-container-first").attr("type") === "application/x-shockwave-flash") {
                    $(".first binary-viewer-flash-upgrade").removeClass("off");
                  } else {
                    $(".first div.binary-viewer-error").removeClass("off");
                  }
                });
              } else {
                if ($(".second .binary-viewer").length === 0) {
                  return;
                }
                $(".second .viewer-icon").parent().addClass("off");
                $(".second .binary-viewer").append('<div id="jwplayer-container-second"></div>');
                if (navigator.appName.indexOf("Internet Explorer") === -1) {
                  mediaQueryMatches = mediaQuery;
                }
                if (!mediaQueryMatches) {
                  w = 278;
                  h = 200;
                }

                $.initializeJwPlayer("jwplayer-container-second", content, poster, w, h, function() {
                  if ($.browser.msie && this.getRenderingMode() === "html5") {
                    $(".second .binary-viewer").find("[id*='jwplayer']").each(function() {
                      $(this).attr("unselectable", "on");
                    });
                  }
                }, function() {
                  if ($("#jwplayer-container-second")) {
                    $("#jwplayer-container-second").remove();
                  }
                  if ($(".second #jwplayer-container_wrapper")) {
                    $(".second #jwplayer-container_wrapper").remove();
                  }
                  if ($("#jwplayer-container-second").attr("type") === "application/x-shockwave-flash") {
                    $(".second binary-viewer-flash-upgrade").removeClass("off");
                  } else {
                    $(".second div.binary-viewer-error").removeClass("off");
                  }
                });
              }
            };
            $(".previews").click(
                    function(e) {
                      e.preventDefault();
                      $.fancybox(
                              $(".previews"),
                              {
                                'padding' : 0,
                                'closeBtn' : false,
                                'overlayShow' : true,
                                'openEffect' : 'fade',
                                'closeEffect' : 'fade',
                                'prevEffect' : 'fade',
                                'nextEffect' : 'fade',
                                'tpl' : {
                                    wrap : '<div class="fancybox-wrap" tabIndex="-1"><div class="fancybox-skin"><div class="fancybox-toolbar">'
                                        + '<span title="' + messages.ddbnext.Close() + '" class="fancybox-toolbar-close" onclick="$.fancybox.close();"></span>'
                                        +'<span class="fancybox-toolbar-title">'
                                        + $("div.binary-title span").text()
                                        + '</span><br><div class="fancybox-pagination"><span></span></div></div>'
                                        + '<div class="fancybox-outer"><div class="fancybox-inner"><div class="fancybox-click-nav" onclick="$.fancybox.prev();"><div class="fancybox-nav"><span title="Previous" class="fancybox-prev" onclick="$.fancybox.prev();"></span></div></div><div class="fancybox-click-nav right" onclick="$.fancybox.next();"><div class="fancybox-nav"><span title="Next" class="fancybox-next" onclick="$.fancybox.next();"></span></div></div></div></div></div></div>',
                                    prev : '',
                                    next : ''
                                },
                                'afterLoad' : function() {
                                  var title = $.cutoffStringAtSpace($(this.element).attr('data-caption'), 150);
                                  var position = $(this.element).attr('data-pos') + ' '
                                      + $(".previews-list li").size();
                                  $("span.fancybox-toolbar-title").text(title);
                                  $("div.fancybox-pagination span").text(position);
                                }
                              });
                      if ($(".previews-list li").size() === 1) {
                        $(".fancybox-pagination").addClass("off");
                        $('.fancybox-click-nav').attr('onclick', "");
                        $('.fancybox-nav').remove();
                      }
                      return false;
                    });
            $(".show-lightbox").click(function(e) {
              e.preventDefault();
              $(".previews").trigger( "click" );
              return false;
            });
          }
        });
