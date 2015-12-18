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
//IMPORTANT FOR MERGING: This is the main function that has to be called when we are in the search results page
$(function() {

  if (jsPageName === "favorites") {
    var socialMediaManager = new SocialMediaManager();
    socialMediaManager.integrateSocialMedia();
    $("#favoritesCopyDialog select").MultiSelect({css_class_selected: "multiselect-selected-folder"});
    $('.page-input').removeClass('off');
    $('.page-nonjs').addClass("off");
    // workaround for ffox + ie click focus - prevents links that load dynamic
    // content to be focussed/active.
    $("a.noclickfocus").live('mouseup', function() {
      $(this).blur();
    });

    $('.page-filter select').change(function() {
      var url = updateURLParameter(window.location.href, 'rows', this.value);
      var order = getParam("order");
      url = updateURLParameter(url, 'order', order);
      window.location.href = url;
      return false;
    });

    $('#checkall').checkAll('#slaves input:checkbox', {
      reportTo : function() {
        var prefix = this.prop('checked') ? 'un' : '';
        this.next().text(prefix + 'check all');
        $('#slaves').trigger('change');
      }
    });

    //Managing "delete" and "copy" buttons
    $('#slaves').change(function(){
      if($(this).find(':checkbox:checked').length>0){
        $('.delete-btn button').removeClass('disabled');
        $('.options-buttons-container').removeClass('mobile-off');
      }else{
        $('.delete-btn button').addClass('disabled');
        $('.options-buttons-container').addClass('mobile-off');
      }
    });

    $('.delete-btn button').click(function(){
      if($(this).hasClass('disabled')){
        return false;
      }
    });

    updateNavigationUrl();

    $('.page-input').keyup(function(e) {
        if (e.keyCode === 13) {
          if (/^[0-9]+$/.test(this.value)) {
            var resultPagesCountText = $('.total-pages').text();
            var resultPagesCountInt = parseInt(resultPagesCountText.replace(/[^0-9]/g, ''));

            if (parseInt(this.value) <= 0) {
              this.value = 1;
            } else if (parseInt(this.value) > resultPagesCountInt) {
              this.value = $('.total-pages').text();
            }
          } else {
            this.value = 1;
          }
          $('.page-input').attr('value', this.value);

          var paramsArray = [['offset', (this.value - 1) *
          getParamWithDefault("rows", 20)], ['rows', getParamWithDefault("rows", 20)], ['order', getParam("order")]];

          window.location.href = $.addParamToUrl(jsContextPath + "/user/favorites/", paramsArray, null, paramsArray, false);
        }
      });

    /** Delete favorites */
    $('#favorites-remove').submit(function() {
      var selected = [];
      $('#slaves input:checked').each(function() {
        selected.push($(this).attr('value'));
      });
      $('.totalNrSelectedObjects').html(selected.length);
      $('#favoritesDeleteConfirmDialog').modal('show');
      $('#id-confirm').click(function() {
        var selected = [];
        $('#slaves input:checked').each(function() {
          selected.push($(this).attr('value'));
        });
        var folderId = $('#folder-list').attr('data-folder-selected');
        var body = {
          ids : selected,
          folderId : folderId
        };
        jQuery.ajax({
          type : 'POST',
          contentType : "application/json; charset=utf-8",
          traditional : true,
          url : jsContextPath + "/apis/favorites/_delete",
          data : JSON.stringify(body),
          dataType : "json",
          success : function() {
            window.setTimeout('location.reload();', 500);
          }
        });
        $('#slaves input:checked').each(function() {
          selected.push($(this).attr('checked', false));
        });
        $('#favoritesDeleteConfirmDialog').modal('hide');
      });
      return false;
    });

    /** Send email */
    $(".sendbookmarks").click(function(event) {
      event.preventDefault();
      $('#favoritesModal').modal('show');
      return false;
    });

    /** Create folder */
    $('#folder-create').submit(function() {
      $('#folderCreateConfirmDialog').modal('show');
      return false;
    });

    $('#create-confirm').click(function() {
        var title = $('#folder-create-name').val();
        if (title.length > 0) {
          hideError();
          var body = {
          title : title,
          description : $('#folder-create-description').val()
          };
          jQuery.ajax({
            type : 'POST',
            contentType : "application/json; charset=utf-8",
            traditional : true,
            url : jsContextPath + "/apis/favorites/folder/create",
            data : JSON.stringify(body),
            dataType : "json",
            success : function() {
              window.setTimeout('location.reload();', 500);
            }
          });
        } else {
          showError(messages.ddbnext.favorites_list_Title_Required);
      }
      $('#folderCreateConfirmDialog').modal('hide');
      return false;
    });

    /** Delete folder */
    $(".deletefolders").click(function(event) {
      event.preventDefault();
      $('#folderDeleteConfirmDialog').modal('show');

      var folderId = $(this).attr('data-folder-id');

      $('#delete-confirm').click(function() {
        var body = {
          folderId : folderId,
          deleteItems : $('#folder-delete-check').is(':checked')
        };
        jQuery.ajax({
          type : 'POST',
          contentType : "application/json; charset=utf-8",
          traditional : true,
          url : jsContextPath + "/apis/favorites/folder/delete",
          data : JSON.stringify(body),
          dataType : "json",
          success : function() {
            window.setTimeout('location.reload();', 500);
          }
        });
        $('#folderDeleteConfirmDialog').modal('hide');
      });
      return false;

    });

    /** Copy favorites */
    $('#favorites-copy').submit(function() {
      var selected = [];
      $('#slaves input:checked').each(function() {
        selected.push($(this).attr('value'));
      });

      if (selected.length > 0) {
        $('#favoritesCopyDialog').modal('show');
        $('#copy-confirm').click(function() {
          var selected = [];
          $('#slaves input:checked').each(function() {
            selected.push($(this).attr('data-bookmark-id'));
          });

          var selectedFolders = $('.favorites-copy-selection').val();

          var body = {
            ids : selected,
            folders : selectedFolders
          };
          jQuery.ajax({
            type : 'POST',
            contentType : "application/json; charset=utf-8",
            traditional : true,
            url : jsContextPath + "/apis/favorites/copy",
            data : JSON.stringify(body),
            dataType : "json",
            success : function() {
              window.setTimeout('location.reload();', 500);
            }
          });
          $('#slaves input:checked').each(function() {
            selected.push($(this).attr('checked', false));
          });
          $('#favoritesCopyDialog').modal('hide');
        });
      }
      return false;
    });

    /** Edit folder */
    $('.editfolder').click(
        function() {

          var folderId = $(this).attr('data-folder-id');

          // First get current values of the folder
          jQuery.ajax({
            type : 'GET',
            contentType : "application/json; charset=utf-8",
            traditional : true,
            url : jsContextPath + "/apis/favorites/folder/get/" + folderId,
            dataType : "json",
            success : function(data) {

              // Then set the values to the GUI
              var oldFolderTitle = data.title;
              var oldFolderDescription = data.description;
              var isPublic = data.isPublic;
              var publishingName = data.publishingName;
              var isBlocked = data.isBlocked;

              $('#folder-edit-id').val(folderId);
              $('#folder-edit-name').val(oldFolderTitle);
              $('#folder-edit-description').val(oldFolderDescription);
              if (isPublic) {
                $('#folder-edit-privacy-public').attr('checked', 'checked');
              } else {
                $('#folder-edit-privacy-private').attr('checked', 'checked');
              }
              $('#folder-edit-publish-name option[value="' + publishingName + '"]').attr(
                  'selected', 'selected');
              if (isBlocked) {
                $('#folder-edit-privacy-area').addClass('off');
              } else {
                $('#folder-edit-privacy-area').removeClass('off');
              }

              $('#folderEditConfirmDialog').modal('show');

              // Then collect the updated values
              $('#edit-confirm').click(function() {
                var isPublic = false;
                if ($('#folder-edit-privacy-public').is(':checked')) {
                  isPublic = true;
                }

                var title = $('#folder-edit-name').val();
                var body = {
                  id : $('#folder-edit-id').val(),
                  title : title,
                  description : $('#folder-edit-description').val(),
                  isPublic : isPublic,
                  name : $('#folder-edit-publish-name').find(":selected").val()
                };
                if (title.length > 0) {
                    hideError();
                    jQuery.ajax({
                      type : 'POST',
                      contentType : "application/json; charset=utf-8",
                      traditional : true,
                      url : jsContextPath + "/apis/favorites/folder/edit",
                      data : JSON.stringify(body),
                      dataType : "json",
                      success : function() {
                        window.setTimeout('location.reload();', 500);
                      }
                    });
                } else {
                    showError(messages.ddbnext.favorites_list_Title_Required);
                }
                $('#folderEditConfirmDialog').modal('hide');
              });

            }
          });

          return false;
        });

    /** Publish folder */
    $('.publishfolder').click(function() {

      var folderId = $(this).attr('data-folder-id');
      var body = {
        id : folderId
      };

      jQuery.ajax({
        type : 'POST',
        contentType : "application/json; charset=utf-8",
        traditional : true,
        url : jsContextPath + "/apis/favorites/togglePublish",
        data : JSON.stringify(body),
        dataType : "json",
        success : function() {
          window.setTimeout('location.reload();', 500);
        }
      });
      return false;
    });

    /** Open comment favorites */
    $('.comment-text-clickanchor').click(function() {

      var bookmarksId = $(this).attr('data-bookmark-id');
      var textField = $("#comment-text-" + bookmarksId);
      var inputField = $("#comment-input-" + bookmarksId);
      var buttonField = $("#comment-button-" + bookmarksId);

      $(textField).addClass("off");
      $(inputField).removeClass("off");
      $(buttonField).removeClass("off");
      $(inputField).focus();

      inputField.animate({
        height : "100px"
      }, 200, function() {
      });

      return false;
    });

    /** Cancel comment favorites */
    $('.comment-cancel').click(function() {

      var bookmarksId = $(this).attr('data-bookmark-id');
      var textField = $("#comment-text-" + bookmarksId);
      var dynamicTextField = $("#comment-text-dyn-" + bookmarksId);
      var inputField = $("#comment-input-" + bookmarksId);
      var buttonField = $("#comment-button-" + bookmarksId);

      inputField.animate({
        height : "20px"
      }, 200, function() {
        $(textField).removeClass("off");
        $(inputField).addClass("off");
        $(buttonField).addClass("off");
      });

      var originalComment = $(dynamicTextField).text().trim();
      var defaultMessage = messages.ddbnext.Favorites_Comment_Label().trim();
      if (originalComment === defaultMessage) {
        $(inputField).val("");
      } else {
        $(inputField).val(originalComment.trim());
      }

      return false;
    });

    /** Save comment favorites */
    $('.comment-save').click(function() {

      var bookmarksId = $(this).attr('data-bookmark-id');
      var textField = $("#comment-text-" + bookmarksId);
      var dynamicTextField = $("#comment-text-dyn-" + bookmarksId);
      var inputField = $("#comment-input-" + bookmarksId);
      var buttonField = $("#comment-button-" + bookmarksId);

      var body = {
        id : bookmarksId,
        text : $(inputField).val()
      };
      jQuery.ajax({
        type : 'POST',
        contentType : "application/json; charset=utf-8",
        traditional : true,
        url : jsContextPath + "/apis/favorites/comment",
        data : JSON.stringify(body),
        dataType : "json",
        success : function() {

          var newInput = $(inputField).val();
          if (newInput.trim()) {
            $(dynamicTextField).text(newInput);
          } else {
            $(dynamicTextField).text(messages.ddbnext.Favorites_Comment_Label);
          }

          inputField.animate({
            height : "20px"
          }, 200, function() {
            $(textField).removeClass("off");
            $(inputField).addClass("off");
            $(buttonField).addClass("off");

            window.setTimeout('location.reload();', 100);
          });

        }
      });

      return false;
    });

    /* Ranking manager*/
    $('.rank-input').on('change', function(){
      var newPosition = parseInt($(this).val());
      if(newPosition !== "NaN"){
        var parentElement = $(this).parents('.rank-wrapper');
        updateRanking (parentElement.attr('data-bookmark-id'), parentElement.attr('data-folder-id'), newPosition - 1);
      }
    });
    $('.rank-arrows .up, .rank-arrows .down').on('click', function(){
      if(!$(this).parent().hasClass('disabled')){
        var parentElement = $(this).parents('.rank-wrapper');
        var currentPosition = parseInt(parentElement.find('.rank-input').val()) - 1;
        if($(this).hasClass('up') && currentPosition > 0){
          updateRanking (parentElement.attr('data-bookmark-id'), parentElement.attr('data-folder-id'),
                         currentPosition - 1);
        }else if($(this).hasClass('down')){
          updateRanking (parentElement.attr('data-bookmark-id'), parentElement.attr('data-folder-id'),
                         currentPosition + 1);
        }
      }
    });
  }

});

//init the toggle for the mobile accordion
$.toggleElement();

function updateNavigationUrl() {
  $.urlParam = function(name) {
    var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
    return results[1] || 0;
  };
  var offset = getParam('offset');
  if ((offset == null) || (offset < 1)) {
    $(".page-nav .prev-page").addClass("off");
    $(".page-nav .first-page").addClass("off");
  }

  try {
    var offset_endPg = $(".last-page").find('a').attr("href").match(/offset=([0-9]+)/);
    var offset_nextPg = $(".next-page").find('a').attr("href").match(/offset=([0-9]+)/);
    if (offset_endPg && offset_nextPg && parseInt(offset_nextPg[1]) > parseInt(offset_endPg[1])) {
      $(".page-nav .next-page").addClass("off");
      $(".page-nav .last-page").addClass("off");
    }
  } catch (e) {
    // TODO: the endPg / nextPg throws errors on empty favorites list
  }
}

function getParam(name) {
  var regexS = "[\\?&]" + name + "=([^&#]*)";
  var regex = new RegExp(regexS);
  var tmpURL = window.location.href;
  var results = regex.exec(tmpURL);
  if (results == null) {
    return "";
  } else {
    return results[1];
  }
}

function getParamWithDefault(name, defaultValue) {
  var result = getParam(name);

  if (result === "") {
    result = defaultValue;
  }
  return result;
}

function updateURLParameter(url, param, paramVal) {
  var newAdditionalURL = "";
  var tempArray = url.split("?");
  var baseURL = tempArray[0];
  var additionalURL = tempArray[1];
  var temp = "";
  if (additionalURL) {
    tempArray = additionalURL.split("&");
    for (i = 0; i < tempArray.length; i++) {
      if (tempArray[i].split('=')[0] !== param) {
        newAdditionalURL += temp + tempArray[i];
        temp = "&";
      }
    }
  }

  var rows_txt = temp + "" + param + "=" + paramVal;
  return baseURL + "?" + newAdditionalURL + rows_txt;
}

function hideError() {
    $('.errors-container').remove();
}

function showError(errorHtml) {
    var errorContainer = ($('.favorites-results-content').find('.errors-container').length > 0) ? $(
        '.favorites-results-content').find('.errors-container') : $(document.createElement('div'));
    var errorIcon = $(document.createElement('i'));
    errorContainer.addClass('errors-container');
    errorContainer.html(errorHtml);
    errorContainer.prepend(errorIcon);

    $('.favorites-results-content').prepend(errorContainer);
}

function clean() {
  $('#folder-create-name').val("");
  $('#folder-create-description').val("");
}

function updateRanking (bookmarkId, folderId, newPosition){
  var body = {
    folderId : folderId,
    position : newPosition
  };
  $.ajax({
    type: "POST",
    contentType : "application/json; charset=utf-8",
    traditional : true,
    url : jsContextPath + "/apis/favorites/"+bookmarkId+"/_move",
    data : JSON.stringify(body),
    dataType : "json",
    success: function(){
      window.location = window.location;
    },
    error: function(){return false;}
  });
}

$('#alle-listen').click(function(event){
  event.preventDefault();
  var url = jsContextPath + "/user/" +  $(this).attr("data-userId") + "/favorites/allpublicfolders/"+ $(this).attr("data-selectedFolderId");
  $.get(url, function(data) {
    $('#public-folders').empty();
    $('#public-folders').append(data);

    var newUrl = updateURLParameter(window.location.href, 'showLinkAllList', false);
    window.location.href = newUrl;
  });
});
