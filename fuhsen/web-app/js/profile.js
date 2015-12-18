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

  $("#user-profile-form").validate({

    rules : {
      username : {
        required : true,
        minlength : 2
      // TODO change the minimum if different
      },
      email : {
        required : true,
        email : true
      },
      errorClass : "help-inline"
    },
    messages : {

      username : {
        required : $("#error-messages").children('li').eq(0).children('a').text(),
        minlength : $("#error-messages").children('li').eq(1).children('a').text()
      },
      email : {
        required : $("#error-messages").children('li').eq(0).children('a').text(),
        email : $("#error-messages").children('li').eq(2).children('a').text()
      }
    },
    errorPlacement: function(error, element) {
      $(element).parent().prev().children().addClass("label-error");
      error.insertAfter(element);
    }

  });

  $('#delete-account').click(function() {
    $('#msDeleteAccount').modal('show');
  });

  $('#delete-account-confirm').click(function() {
    $.get(jsContextPath + "/user/delete", function(data) {
      $("body").html(data);
    });
  });
});
