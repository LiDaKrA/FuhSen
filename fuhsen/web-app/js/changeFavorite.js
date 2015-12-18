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

addFavoriteEvent($(".favorite-add #idFavorite"));

/**
 * This function applies to the favorites in search result page, item detail view and institution detail view.
 */
function addFavoriteEvent(jElemFavorite) {
  jElemFavorite.parent().click(function(event) {
    event.preventDefault();
    changeFavoriteState(jElemFavorite);
    return false;
  });
}

function changeFavoriteState(jElemFavorite) {
  disableFavorite(jElemFavorite.parent());
  var vActn = jElemFavorite.attr("data-actn");
  var objectType=jElemFavorite.attr("data-objecttype");
  if (vActn === "POST") { // Currently only allow to add favorites, not to delete them
    var url = jsContextPath + "/apis/favorites/" + jElemFavorite.attr("data-itemid")
        + '/?reqType=ajax';
    $.ajax({
      type : vActn,
      dataType : 'json',
      async : true,
      url : url + "&reqActn=add&reqObjectType=" + objectType,
      complete : function(data) {
        if (vActn === "POST") {
          addToFavorites(jElemFavorite, data,objectType);
        }
      }
    });
  }
}

function addToFavorites(jElemFavorite, data,objectType) {
  switch (data.status) {
  case 200:
  case 201:
    // -- success
    $("#favorite-confirmation").modal("show");
    $.post(jsContextPath + "/apis/favorites/folders", function(folders) {
      if (folders.length > 1) {
        var itemId = jElemFavorite.attr("data-itemid");

        $("#favorite-folders").empty();
        $.each(folders, function(index, folder) {
          if (!folder.isMainFolder) {
            // show select box with all folder names
            var selectEntry = "<option value=" + folder.folderId + ">"
                + folder.title.charAt(0).toUpperCase() + folder.title.slice(1) + "</option>";

            $("#favorite-folders").append(selectEntry);
          }
        });
        $("#favoriteId").val(itemId);
        $("#addToFavoritesConfirm").click(
            function() {
              $("#favorite-confirmation").modal("hide");
              var folderList = $("#favorite-folders").val();

              if (folderList) {
                $.each(folderList, function(index, value) {
                  $.post(jsContextPath + "/apis/favorites/" + itemId + "?folderId=" + value + "&reqObjectType=" +
                    objectType);
                });
              }

              $("#idFavorite").parent().parent().attr('title',
                  messages.ddbnext.favorites_already_saved);
            });
      } else {
        window.setTimeout(function() {
          $("#favorite-confirmation").modal("hide");
        }, 1500);
      }
    });
    break;
  case 400:
    // -- bad request
    break;
  case 401:
    // -- handle unauthorized
    break;
  case 500:
    // -- internal error
    break;
  default:
    // -- bad response
    break;
  }
}

  /**
   * Disable a favorite button.
   *
   * @param div DIV element which handles the favorite event
   */
  function disableFavorite(div) {
    div.unbind("click");
    div.removeAttr("title");
    div.removeClass("add-to-favorites");
    div.removeClass("favorite-add");
    div.addClass("added-to-favorites");
    div.addClass("favorite-selected");
    div.attr('title', messages.ddbnext.favorites_already_saved);
  }
