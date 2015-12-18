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
      var popupAnchor = $('.sendmail-link-popup-anchor');

      var content = $(document.createElement('input'));
      content.attr('type', 'email');
      content.attr('id', 'send-pdf');
      var title = $(document.createElement('a'));
      title.addClass('sendmail-link');
      var titleString = $(document.createElement('span'));
      var popupSpan = popupAnchor.children("span");
      if (popupSpan != null && popupSpan.length > 0) {
        titleString.html(popupAnchor.attr("title"));
      }
      titleString.appendTo(title);
      var popupManager = new PopupManagerSendPdf();
      popupManager.registerPopupSendmail(popupAnchor[0], title, content, 162, -31);
    });

// Popup Manager --
PopupManagerSendPdf = function() {
  this.init();
};

$.extend(PopupManagerSendPdf.prototype, {

  init : function() {
  },

  registerPopupSendmail : function(anchorTag, title, content, offsetX, offsetY) {

    var closeTitle = messages.ddbnext.Close;
    var popupDialogWrapper = $(document.createElement('div'));
    var popupDialogTitle = $(document.createElement('div'));
    var popupDialogFooter = $(document.createElement('div'));
    var popupDialogCloseImage = $(document.createElement('div'));
    var popupDialogCloseButton = $(document.createElement('a'));
    var popupDialogSubmitButton = $(document.createElement('button'));
    var popupDialogContent = $(document.createElement('div'));

    popupDialogCloseImage.attr('title', closeTitle);

    popupDialogWrapper.addClass('popup-sendpdf-dialog-wrapper');
    popupDialogFooter.addClass('popup-dialog-footer');
    popupDialogCloseImage.addClass('popup-dialog-close-image');
    popupDialogCloseButton.addClass('popup-dialog-button');
    popupDialogSubmitButton.addClass('btn-padding');
    popupDialogTitle.addClass('popup-dialog-title bb');
    popupDialogContent.addClass('popup-dialog-content');
    popupDialogSubmitButton.html($("#i18ntranslateSend").data("val"));

    if (title) {
      popupDialogTitle.html(title);
    }
    if (content) {
      content.appendTo(popupDialogContent);
    }

    popupDialogCloseButton.attr('href', '#');

    popupDialogCloseImage.appendTo(popupDialogCloseButton);
    popupDialogTitle.appendTo(popupDialogWrapper);
    popupDialogContent.appendTo(popupDialogWrapper);
    popupDialogCloseButton.appendTo(popupDialogWrapper);
    popupDialogSubmitButton.appendTo(popupDialogWrapper);
    popupDialogFooter.appendTo(popupDialogWrapper);

    popupDialogWrapper.css('margin-top', offsetY + 'px');

    popupDialogWrapper.insertAfter(anchorTag);
    popupDialogWrapper.hide();

    popupDialogCloseButton.click(function() {
      popupDialogWrapper.fadeOut('fast', function() {
          content.show();
          popupDialogSubmitButton.show();
          $("#pdfMessage").hide();
      });
    });

    popupDialogSubmitButton.click(function() {
        var emailaddress = $("#send-pdf").val();
        if( !isValidEmailAddress( emailaddress ) ) {
            alert($("#i18ntranslateValidEmail").data("val"));
            }else{
             var url = window.location.protocol + "//" + window.location.host+$('.sendmail-link-popup-anchor').attr('href');
               $.ajax({
                url: url,
                data: "email="+emailaddress,
                dataType: 'text',
                type: "GET"
               }).done(function(data)  {
                 content.hide();
                 popupDialogSubmitButton.hide();
                 popupDialogContent.append( "<p id='pdfMessage'>"+data+"</p>" );
                });
        }
    });

    $(anchorTag).click(function(event) {
      if (event.which === 1) {
        event.preventDefault();
        popupDialogWrapper.fadeIn('fast', function() {
          popupDialogWrapper.find('input')[0].select();
        });
      }
    });
  }
});
function isValidEmailAddress(emailAddress) {
    var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
    return pattern.test(emailAddress);
};