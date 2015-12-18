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
  if (jsPageName === "savedsearches") {
    $('.page-input').removeClass('off');
    $('.page-nonjs').addClass("off");
    // workaround for ffox + ie click focus - prevents links that load dynamic
    // content to be focussed/active.
    $("a.noclickfocus").live('mouseup', function() {
      $(this).blur();
    });

    $("#send-saved-searches").click(function(event) {
      event.preventDefault();
      $('#sendSavedSearchesModal').modal({
        remote : $(this).attr("href")
      });
    });

    $('.page-filter select').change(function() {
      var url = jsContextPath + "/user/savedsearches?rows=" + this.value;
      var order = getParam("order");
      if (order) {
        url += "&order=" + order;
      }
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
    $('.delete-btn button').addClass('disabled');
    $('#slaves').change(function(){
      if($(this).find(':checkbox:checked').length>0){
        $('.delete-btn button').removeClass('disabled');
      }else{
        $('.delete-btn button').addClass('disabled');
      }
    });

    $('.delete-btn button').click(function(){
      if($(this).hasClass('disabled')){
        return false;
      }
    });

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

            var paramsArray = [['offset', (this.value - 1)
                * getParamWithDefault("rows", 20)], ['rows', getParamWithDefault("rows",
                20)], ['order', getParam("order")]];

            window.location.href = $.addParamToUrl(jsContextPath + "/user/savedsearches/", paramsArray, null, paramsArray, false);
          }
        });

    $('#deleteSavedSearches').submit(function() {
      var selected = [];
      $('#slaves input:checked').each(function() {
        selected.push($(this).val());
      });
      $('#totalNrSelectedObjects').html(selected.length);
      $('#deleteSavedSearchesModal').modal('show');
      $('#id-confirm').click(function() {
        var selected = [];
        $('#slaves input:checked').each(function() {
          selected.push($(this).val());
        });
        var body = {
          ids : selected
        };
        jQuery.ajax({
          type : 'POST',
          contentType : "application/json; charset=utf-8",
          traditional : true,
          url : jsContextPath + "/apis/savedsearches/_delete",
          data : JSON.stringify(body),
          dataType : "json",
          success : function() {
            window.setTimeout('window.location.reload()', 500);
          }
        });
        $('#slaves input:checked').each(function() {
          selected.push($(this).attr('checked', false));
        });
        $('#deleteSavedSearchesModal').modal('hide');
      });
      return false;
    });

    $(".edit-saved-search").click(function() {
      $("#editSavedSearchId").val($(this).attr("id"));
      $("#editSavedSearchTitle").val($(this).attr("data-label"));
      $("#editSavedSearchModal").modal("show");
      $("#editSavedSearchConfirm").click(function() {
        $("#editSavedSearchModal").modal("hide");
        var title = $("#editSavedSearchTitle").val();
        if (title.length > 0) {
          hideError();
          $.ajax({
            type : "PUT",
            contentType : "application/json",
            dataType : "json",
            url : jsContextPath + "/apis/savedsearches/" + $("#editSavedSearchId").val(),
            data : JSON.stringify({
              title : title
            })
          }).done(function() {
          var editAnchor = $("#" + $("#editSavedSearchId").val());
          editAnchor.attr("data-label", title);
          var anchor = editAnchor.prev("a");
          anchor.text(title);
          anchor.attr("title", title);
          });
        } else {
          showError(messages.ddbnext.Savedsearch_Title_Required);
        }
      });
    });
  }
});

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
