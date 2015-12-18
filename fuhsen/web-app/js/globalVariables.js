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

// Create a global namespace for ddb
de = de || {};
de.ddb = de.ddb || {};
de.ddb.next = de.ddb.next || {};

var jsContextPath = "";
var jsLanguage = "";
var jsPageName = "";
var jsLatitude = "";
var jsLongitude = "";
var jsLoggedIn = "";

$(document).ready(function() {
  var jsVariablesDiv = $('#globalJsVariables');
  var jsPageMeta = $('meta[name=page]').attr("content");
  if (jsVariablesDiv) {
    if (jsVariablesDiv.attr('data-js-context-path')) {
      jsContextPath = jsVariablesDiv.attr('data-js-context-path');
    }
    if (jsVariablesDiv.attr('data-js-language')) {
      jsLanguage = jsVariablesDiv.attr('data-js-language');
    }
    if (jsVariablesDiv.attr('data-js-longitude')) {
      jsLongitude = jsVariablesDiv.attr('data-js-longitude');
    }
    if (jsVariablesDiv.attr('data-js-latitude')) {
      jsLatitude = jsVariablesDiv.attr('data-js-latitude');
    }
    if (jsVariablesDiv.attr('data-js-loggedin')) {
      jsLoggedIn = jsVariablesDiv.attr('data-js-loggedin');
    }
  }
  if (jsPageMeta) {
    jsPageName = jsPageMeta;
  }
});
