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

de.ddb.common = de.ddb.common || {};

de.ddb.common.Error = function (container) {
  this.errorContainerName = ".errors-container";
  this.container = container;
};

$.extend(de.ddb.common.Error.prototype, {
  hideError : function() {
    var errorContainer = $(this.container).find(this.errorContainerName);
    if (errorContainer.length > 0) {
      $(errorContainer).remove();
    }
  },

  showError : function(errorHtml) {
    var errorContainer = $(this.container).find(this.errorContainerName).length > 0 ?
      $(this.container).find(this.errorContainerName) : $(document.createElement('div'));
    var errorIcon = $(document.createElement('i'));
    errorContainer.addClass('errors-container');
    errorIcon.addClass('icon-exclamation-sign');
    errorContainer.html(errorHtml);
    errorContainer.prepend(errorIcon);
    $(this.container).prepend(errorContainer);
  }
});
