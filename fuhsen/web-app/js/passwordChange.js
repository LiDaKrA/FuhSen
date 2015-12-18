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

  $("#password-change-form").validate({

    rules : {
      oldpassword : {
        required : true
      },
      newpassword : {
        required : true,
        minlength : 8
      // TODO change the minimum if different
      },
      confnewpassword : {
        required : true,
        equalTo : "#newpassword"
      },
      errorClass : "help-inline"
    },
    messages : {

      oldpassword : {
        required : $("#error-messages").children('li').eq(0).children('a').text(),
        minlength : $("#error-messages").children('li').eq(1).children('a').text()
      },
      newpassword : {
        required : $("#error-messages").children('li').eq(0).children('a').text(),
        minlength : $("#error-messages").children('li').eq(1).children('a').text()
      },
      confnewpassword : {
        required : $("#error-messages").children('li').eq(0).children('a').text(),
        equalTo : $("#error-messages").children('li').eq(2).children('a').text()
      }

    }

  });

});
