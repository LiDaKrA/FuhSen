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

/* We tell JS Hint, that $ and _ are not global.
 * See Also: http://www.jshint.com/docs/#config
 */
/* global $:false, _: false */

/* Even if jsContextPath is global, we load it first before any other JavaScripts.
 * We therefore tell JS Hint, jsContextPath is not global
 */
/* global jsContextPath: false */

(function() {
  'use strict';

  // TODO: remove `use strict` via automate script in the production mode.
  // SEE:
  // http://scriptogr.am/micmath/post/should-you-use-strict-in-your-production-javascript

  // TODO: use other pattern for namespace in JS
  // SEE:
  // http://enterprisejquery.com/2010/10/how-good-c-habits-can-encourage-bad-javascript-habits-part-1/
  var ddb = {

    Config : {
      ddbBackendUrl : '/apis/institutions'
    },

    // TODO: does not work => cache for all institution, including children
    // and their descendants
    $all : $('li.institution-listitem'),

    institutionsByFirstChar : null,

    //global variable storing the filter state onlyInstitutionsWithData
    onlyInstitutionsWithData : false,

    // find index[All| A | B |...| Z | 0-9] with no members after filtered
    // by sectors.
    findNoMember : function(visible) {
      return _.reduce(ddb.institutionsByFirstChar, function(memo, array, key) {
        if (_.intersection(array, visible).length === 0) {
          memo.push(key);
        }
        return memo;
      }, []);

    },

    filterDescendants : function(institution, memory, selectedSector, parentList) {
      var onlyInstitutionsWithData = ddb.onlyInstitutionsWithData;

      if (institution.children && institution.children.length > 0) {
        // when an institution has a least one child.
        _.reduce(institution.children, function(otherMemory, child) {

          if (!onlyInstitutionsWithData || child.hasItems) {
            if (selectedSector.length === 0 || _.contains(selectedSector, child.sector)) {
              otherMemory.push(child);
              // the institution is the parent.
              parentList.push(institution);
            }
          }
          ddb.filterDescendants(child, otherMemory, selectedSector, parentList, onlyInstitutionsWithData);
          return otherMemory;
        }, memory);
      }
    },

    findElements : function(list) {
      var idList = _.pluck(list, 'id');

      return $('li.institution-listitem').filter(function() {
        return _.contains(idList, $(this).data('institution-id'));
      });
    },

    skipUmlaut : function(firstChar) {
      if (firstChar === "Ä") {
        firstChar = firstChar.replace(/Ä/g, 'A');
      } else if (firstChar === "Ö") {
        firstChar = firstChar.replace(/Ö/g, 'O');
      } else if (firstChar === "Ü") {
        firstChar = firstChar.replace(/Ü/g, 'U');
      }
      return firstChar;
    },

    getInstitutionsByFirstChar : function(onFilterSelect, onIndexClick, onPageLoad) {
      if (ddb.institutionsByFirstChar === null) {
        $.getJSON(jsContextPath + ddb.Config.ddbBackendUrl, function(response) {
          ddb.institutionsByFirstChar = response.data;

          $('.filter').show();
          // call the callback, once data is loaded.
          onPageLoad();
          onIndexClick();
          onFilterSelect();
          window.onhashchange = ddb.onHashChange;
        }).error(function() {
          /*
           * when we fail to fetch the JSON via AJAX, then we do not activate the JS-feature.
           */
        });
      }

    },

    onPageLoad : function() {
      var hash = window.location.hash.substring(1);
      ddb.styleIndex(hash);
      if (hash === '' || hash.toLowerCase() === 'all' || hash === 'list') {
        /*
         * We check if the user returns to the page using the web browser's back button and if a filter was performed
         * before.
         */
        var isChecked;
        if ($('.multiselect').is(':visible')) {
          isChecked = $('.multiselect option:selected').filter(':selected').length ||
                      $('.multiselect option[value="onlyInstitutionsWithData"]').prop("selected");
        }
        else {
          isChecked = $('.sector-facet input:checked').filter(':checked').length ||
                      $('.institution-with-data input:checkbox').prop('checked');
        }

        // apply the filter, if the filters is not empty.
        if (isChecked) {
          ddb.applyFilter();
        }
      } else {
        ddb.applyFilter();
      }
    },

    /**
     * Filter selection only for the desktop view!
     *
     * Filter selction for mobile view is handled in the multiselect callback onChange
     */
    onFilterSelect : function() {
      $('.sector-facet input:checkbox').click(function() {
        var hash = window.location.hash.substring(1);
        ddb.styleIndex(hash);
        ddb.applyFilter();
      });

      $('.institution-with-data input:checkbox').click(function() {
        var hash = window.location.hash.substring(1);
        ddb.styleIndex(hash);
        ddb.applyFilter();
      });
    },

    /* Function Callback for the URI's hash change event. */
    onHashChange : function() {
      var hash = window.location.hash.substring(1);
      ddb.styleIndex(hash);
      ddb.applyFilter();
    },

    styleIndex : function(hash) {
      if (hash === '' || hash.toLowerCase() === 'all' || hash === 'list') {
        var $allHref = $('#first-letter-index a[href="#All"]');
        var $allLi = $allHref.parent();
        $allLi.addClass('active');
        $allHref.addClass('selected');
      } else {
        var $aHref = $('#first-letter-index a[href="' + '#' + hash + '"]');
        var $li = $aHref.parent();

        if ($li.hasClass('disabled')) {
          // $('#no-match-message').css('display', 'block');
          $('#no-match-message').addClass('visible');
          return false;
        }
        // style the selected index.
        $li.addClass('active');
        $li.addClass('selected');
        // TODO: refactor this, a lot of duplicate code.
        // reset other indexes to the initial style.
        var $firstCharLinks = $('#first-letter-index a');
        var $otherLinks = $firstCharLinks.not($aHref);
        $otherLinks.parent().removeClass('active');
        $otherLinks.removeClass('selected');
      }

      return true;
    },

    applyFilter : function() {
      var institutionList = ddb.getInstitutionAsList();
      var sectors = ddb.getSelectedSectors();
      var firstLetter = ddb.getFirstLetter();

      ddb.retrieveFilterOnlyInstitutionsWithData();

      ddb.filter(institutionList, sectors, firstLetter);
      // count all currently highlighted institutions
      var count = $('.institution-listitem.highlight').length;
      if (count === 0) {
        // count all currently visible institutions
        count = $('.institution-listitem').length - $('.institution-listitem.off').length
            - $('.institution-listitem.off').find('.institution-listitem:not(.off)').length;
      }
      $('#selected-count').text(count);
    },

    getInstitutionAsList : function() {
      if (ddb.institutionList) {
        return ddb.institutionList;
      } else {
        ddb.institutionList = _.chain(ddb.institutionsByFirstChar).values().flatten().value();
      }
      return ddb.institutionList;
    },

    getFirstLetter : function() {
      var result = '';
      var hashValue = $('#first-letter-index').find('li.active').find('a').attr('href');

      if (hashValue) {
        var hash = hashValue.substring(1).toLowerCase();
        if (hash !== '' && hash !== 'all' && hash !== 'list') {
          result = hash;
        }
      }

      result = result.toUpperCase();

      return result;
    },

    /*
     * get an array of selected sectors, for example: [sec_1, sec_3]
     */
    getSelectedSectors : function() {

      /*
       * Now we have two sector widgets. Based on the screen resolution, we show either the checkboxes or multiselect.
       *
       * Depends on which widget is visible, we get the selected values.
       */
      var allSelectedSectors = $('.sector-facet input:checked');
      if ($('.multiselect').is(':visible')) {
        allSelectedSectors = $('.multiselect option:selected');
      }

      return _.reduce(allSelectedSectors, function(sectors, el) {
        //Do not add "onlyInstitutionsWithData" which comes from the multiselect!
        if ($(el).val() !== "onlyInstitutionsWithData") {
          sectors.push($(el).val());
        }

        return sectors;
      }, []);
    },

    /**
     *
     */
    retrieveFilterOnlyInstitutionsWithData : function() {
      var institutionWithData = false;

      //Mobile view
      if ($('.multiselect').is(':visible')) {
        institutionWithData = $('.multiselect option[value="onlyInstitutionsWithData"]').prop("selected");
      }
      //Desktop view
      else {
        institutionWithData = $('.institution-with-data input:checkbox').prop('checked');
      }

      ddb.onlyInstitutionsWithData = institutionWithData;
    },

    /**
     * Applies the selected filters (sector, data, letter) to the institution list.
     */
    filter : function(institutionList, sectors, firstLetter) {
      var parentList = [];

      // reset the view to empty.
      var $listItems = $('li.institution-listitem');
      $listItems.addClass('off');
      $listItems.removeClass('highlight');

      // reset the index
      var $currentIndex = $('#first-letter-index');
      $currentIndex.html(ddb.$index.html());

      // Check for institutions that provide data filter
      var onlyInstitutionsWithData = ddb.onlyInstitutionsWithData;
      var institutionsFilteredByData = ddb.filterOnlyInstitutionsWithData(institutionList, onlyInstitutionsWithData);

      /*
       * Case 1: Sector yes, Char no
       *
       * When at least one sector selected _and_ no first letter filter; e.g. sector = ['Media'], index = All
       */
      if (sectors.length > 0 && firstLetter === '') {
        var filteredBySector = ddb.filterBySectors(institutionsFilteredByData, sectors, parentList);
        var visible = _.union(_.uniq(parentList), filteredBySector);

        var hasNoMember = ddb.findNoMember(visible);
        ddb.showResult(_.union(parentList, filteredBySector), filteredBySector);
        ddb.updateIndex(hasNoMember);
      }
      /*
       * Case 2: Sector yes, Char yes
       *
       * when at least one sector selected _and_ one of the first letter is selected, e.g., sector = ['Library',
       * 'Media'], index = 'B'
       */
      // In this case, we don't need a parent list. TODO: refactor
      /*
       * 1. we collect all root institutions start with the selected firstLetter, for example 'W', including their
       * children. The children do *not* have to start with the selected first letter.
       *
       * 2. we start to apply the sector filter, for example [Library] to all institutions(roots and their children)
       * collected from the first step.
       */
      else if (sectors.length > 0 && firstLetter !== '') {
        var filteredByFirstLetter = ddb.institutionsByFirstChar[firstLetter];
        var filteredByData = ddb.filterOnlyInstitutionsWithData(filteredByFirstLetter, onlyInstitutionsWithData);
        filteredBySector = _.reduce(filteredByData, function(memory, institution) {
          var firstChar = institution.firstChar;
          if (firstChar === "Ä" || firstChar === "Ü" || firstChar === "Ö") {
            firstChar = ddb.skipUmlaut(firstChar);
          }

          if (firstChar === firstLetter) {
            if (_.contains(sectors, institution.sector)) {
              memory.push(institution);
            }
            ddb.filterDescendants(institution, memory, sectors, parentList);
          }

          return memory;
        }, []);

        parentList = _.filter(parentList, function(parent) {
          return parent.firstChar === firstLetter;
        });

        visible = _.union(parentList, filteredBySector);
        ddb.showResult(visible, filteredBySector);

        // find all root institutions filtered by sectors. get the first letter, e.g., only As and Ls
        // show only A and L in Index.
        var filtered = ddb.filterBySectors(institutionsFilteredByData, sectors, parentList);

        hasNoMember = ddb.findNoMember(_.union(_.uniq(parentList), filtered));
        ddb.updateIndex(hasNoMember);
      }

      /*
       * Case 3: Case 3: Sector no, Char yes
       *
       * When no sector selected _and_ one of the first letter is selected. e.g. sector = [], index = 'C'
       */
      else if (sectors.length === 0 && firstLetter !== '') {
        var institutionsByLetter = ddb.institutionsByFirstChar[firstLetter];
        var institutionsByData = ddb.filterOnlyInstitutionsWithData(institutionsByLetter, onlyInstitutionsWithData);
        var institutionsBySector = ddb.filterBySectors(institutionsByData, sectors, parentList);

        visible = _.union(_.uniq(parentList), institutionsBySector);
        ddb.showResult(visible, null);

        hasNoMember = ddb.findNoMember(_.union(_.uniq(parentList), institutionsFilteredByData));
        ddb.updateIndex(hasNoMember);
      }
      /*
       * Case 4: Sector no, Char no" when no sector is selected _and_ no first letter filter. e.g. sector = [], index =
       * All
       */
      else {
        ddb.styleIndex('All');
        if (onlyInstitutionsWithData) {
          filteredBySector = ddb.filterBySectors(institutionsFilteredByData, sectors, parentList);
          visible = _.union(_.uniq(parentList), filteredBySector);
          ddb.showResult(visible, null);

          hasNoMember = ddb.findNoMember(visible);
          ddb.updateIndex(hasNoMember);
        } else {
          $('#institution-list').empty().html(ddb.$institutionList.html());
        }
      }
    },

    filterBySectors : function(institutionList, sectors, parentList) {
      var reduced = _.reduce(institutionList, function(memory, institution) {

        if (sectors.length === 0 || _.contains(sectors, institution.sector)) {
          memory.push(institution);
        }
        ddb.filterDescendants(institution, memory, sectors, parentList);

        return memory;
      }, []);

      return reduced;
    },

    filterOnlyInstitutionsWithData : function(institutionList, onlyInstitutionsWithData) {
      var reduced = _.reduce(institutionList, function(memory, institution) {
        // Check if the institution is member of the selected filter
        if (!onlyInstitutionsWithData || institution.hasItems) {
          memory.push(institution);
        }

        return memory;
      }, []);

      return reduced;
    },

    showAll : function() {
      $('#no-match-message').addClass('off');
      $('li.institution-listitem').removeClass('off');
    },

    updateIndex : function(hasNoMember) {
      if (hasNoMember) {
        // enable all index. It means visually that the index all not
        // grey.
        $('#first-letter-index li').removeClass('disabled');

        // update index view, i.e., A..Z
        _.each(hasNoMember, function(letter) {
          var $aHref = $('#first-letter-index a[href="' + '#' + letter + '"]');
          $aHref.parent().addClass('disabled');
          $aHref.click(function(e) {
            e.preventDefault();
          });
        });
      }

    },

    // visible institutions are filtered institutions and their descendants.
    showResult : function(visibleInstitution, filteredBySector) {
      var $msg = $('#no-match-message');

      // view manipulation
      if (visibleInstitution.length) {
        // hide the 'no result' message
        $msg.addClass('off');

        ddb.findElements(filteredBySector).addClass('highlight');
        var $visible = ddb.findElements(visibleInstitution);
        $visible.removeClass('off');
      } else {
        $msg.removeClass('off');
        $msg.addClass('visible');
      }

    },

    onIndexClick : function() {
      // we catch the click event on index, does *not* when the user goes
      // directly
      // to a page with #{first-character}, for example: //institutions#A
      var $firstCharLinks = $('#first-letter-index a');
      $firstCharLinks.click(function(event) {
        event.preventDefault();
        var $this = $(this);
        var $li = $this.parent();

        if ($li.hasClass('disabled')) {
          return false;
        }
        // style the selected index.
        $li.addClass('active');
        $this.addClass('selected');

        // reset other indexes to the initial style.
        var $otherLinks = $firstCharLinks.not(this);
        $otherLinks.parent().removeClass('active');
        // $otherLinks.removeAttr('style');
        $otherLinks.removeClass('selected');

        if (history.pushState) {
          history.pushState({}, '', $this.attr('href'));
          // TODO for android 2.3.3 we have to pass the clicked first letter.
          ddb.applyFilter();
        } else {
          // TODO: test on IE8,9
          window.location.hash = this.hash;
        }

        return false;
      });
    }
  };

  $(function() {
    var institutionList = $('#institution-list');

    $('.multiselect').multiselect({
      buttonClass : 'btn btn-small',
      buttonWidth : 'auto',
      maxHeight : false,
      field_NoneSelected : messages.ddbnext.InstitutionPage_FilterBySector,
      onChange : function() {
        ddb.applyFilter();
      },
      buttonText : function(options) {
        if (options.length === 0) {
          var textNode = $(document.createElement('span')).html(this.field_NoneSelected);
          textNode.append($(document.createElement('b')).addClass('caret'));
          return textNode;
        } else if (options.length > 4) {
          return options.length + ' selected <b class="caret"></b>';
        } else {
          var selected = '';
          options.each(function() {
            selected += $(this).text() + ', ';
          });
          return selected.substr(0, selected.length - 2) + ' <b class="caret"></b>';
        }
      }
    });

    // Only execute the script when the user is in the institution list page.
    if (institutionList.length) {

      // When the User Agent enables JS, shows the `filter by sector` Check Boxes.
      ddb.$index = $('#first-letter-index').clone(true, true);
      ddb.$institutionList = institutionList.clone();
      ddb.getInstitutionsByFirstChar(ddb.onFilterSelect, ddb.onIndexClick, ddb.onPageLoad);
    }

  });

}());
