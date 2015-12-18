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

/* Search namespace  */
de.ddb.next.search = de.ddb.next.search || {};

/**
 * Hovercard Information Item constructor
 */
de.ddb.next.search.HovercardInfoItem = function(element) {
  this.init(element);
};

$.extend(de.ddb.next.search.HovercardInfoItem.prototype, {

  infoButton : null,
  hovercard : null,
  iid : null,

  opened : false,
  lock : false,

  hoverTime : 0,
  hoverTimeout : 300,

  init : function(element) {
    var currObjInstance = this;
    this.infoButton = element;
    this.hovercard = this.infoButton.find('.hovercard-info-item');
    this.iid = this.hovercard.attr('data-iid');

    this.infoButton.mouseenter(function() {
      var d = new Date();
      currObjInstance.hoverTime = d.getTime();
      currObjInstance.open();
    });
    this.hovercard.mouseenter(function() {
      currObjInstance.lock = true;
    });
    this.hovercard.mouseleave(function() {
      currObjInstance.close();
    });
    this.infoButton.mouseleave(function() {
      setTimeout(
          function() {
            var currentD = new Date();
            if (!currObjInstance.lock
                && currObjInstance.hoverTime + currObjInstance.hoverTimeout - 100 < currentD
                    .getTime()) {
              currObjInstance.close();
            }
          }, currObjInstance.hoverTimeout);
    });
  },
  open : function() {
    if (!this.opened) {
      this.opened = true;
      this.hovercard.fadeIn('fast');
      if (this.hovercard.find('.small-loader').length !== 0) {
        this.fetchInformationItem();
      }
    }
  },
  close : function() {
    this.hovercard.fadeOut('fast');
    this.opened = false;
    this.lock = false;
  },
  fetchInformationItem : function() {
    var currObjInstance = this;
    $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : jsContextPath + '/informationitem/' + this.iid,
      complete : function(data) {
        var content = currObjInstance.hovercard.find('ul.unstyled');
        content.empty();
        var JSONresponse = jQuery.parseJSON(data.responseText);

        $.each(JSONresponse, function(key, value) {
          if (key !== 'last_update' && value !== "" && key.indexOf("normdata") === -1 && key.indexOf("fct_role") === -1 && key.indexOf("apd_keywords_fct") === -1) {
            var li = $(document.createElement('li'));
            var fieldName = $(document.createElement('span'));
            var fieldContent = $(document.createElement('span'));

            fieldName.addClass('fieldName');
            fieldContent.addClass('fieldContent');

            facetValues = [];
            for (i = 0; i < value.length; i++) {
              facetValues.push(value[i]);
            }
            fieldName.text(de.ddb.next.search.getLocalizedFacetField(key));
            fieldContent.text(facetValues.join('; '));

            li.append(fieldName);
            li.append(fieldContent);
            content.append(li);
          }
        });
      }
    });
  }
});