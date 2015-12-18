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
var page = {};
(function($) {
  "use strict";
  page.init = function(root) {
    var searchStateCookie = new LargeCookie('advancedsearch');

    var selectors = {
      groupWidget : ".search-group",
      group : ".search-field-group",
      removeGroupButton : "button.remove-group-button",
      addGroupButtonContainer : ".button-group",
      addGroupButton : "button.add-group-button, .add-group-link",
      row : ".search-field-row",

      removeButton : "button.remove-button",
      addButton : "button.add-button",

      resetButton : "button[type='reset']",
      facet : "select.facet-simple",
      facetDisabled : "select.facet-js",
      operator : "select.operator",
      globalOperator : ".global-operator",
      value : "input.value",
      matchValue : "select.match",
      facetValues : "select.facet-values",
      facetValueIdAttribute : "data-inputid",
      contextualHelp : "span.contextualHelp",
      contextualHelpTooltip : "div.tooltip"
    };

    //switches input text-field <-> select-box dependent on selected facet
    function showFacetValueInput($row) {

      if (!$row) {
        return;
      }

      var $targetEl = getTargetFacetValueElement($row);

      $row.find(selectors.value).hide();
      $row.find(selectors.facetValues).hide();

      $targetEl.show();

      // show/hide the Match selection field - this must be displayed only if the free text value field is displayed.
      var isEnum = $targetEl.filter(selectors.value).length === 0;

      if (isEnum) {
        $(selectors.matchValue, $row).hide();
      } else {
        $(selectors.matchValue, $row).show();
      }
    }

    // Each facet dropdown has an associated text field or a select list.
    // Each option element in the facet select list has a custom data-* attribute which points to the associated input.
    // This improves performance to avoid searching through all fields and selecting it directly
    function getTargetFacetValueElement($row) {
      var facetName = $row.find(selectors.facet).val();

      var $selectedOption = $("option[" + selectors.facetValueIdAttribute + "][value='" + facetName
          + "']", $row);

      // construct jQuery #id selector using option data-* attribute
      var $targetEl = $row.find("#" + $selectedOption.attr(selectors.facetValueIdAttribute));

      return $targetEl;
    }

    // helper to determine if a row has any values entered (Back button support)
    function hasFacetValueBeenEnteredForRow($row) {
      return getTargetFacetValueElement($row).val();
    }

    // helper to determine if a group has any values entered (Back button support)
    function haveFacetValuesBeenEnteredForGroup($group) {
      var ret = false;
      $(selectors.row, $group).each(function() {
        if (hasFacetValueBeenEnteredForRow($(this))) {
          ret = true;
          return false;
        }
      });

      return ret;
    }

    function removeGroup($groupWidget) {
      var $prev = $groupWidget;
      // when removing a group, only the last visible group needs to be reset, and the values copied up

      // shuffle group values up into previous group
      $groupWidget.nextAll(":visible").andSelf().filter(selectors.groupWidget)
          .each(
              function() {
                var $that = $(this), rowInputSelectors = [ selectors.facet, selectors.value,
                    selectors.matchValue, selectors.facetValues ], $prevRows = $(selectors.row,
                    $prev), $currentRows = $(selectors.row, $that);

                // copy the operator value in to the previous group
                var $prevOperator = $(selectors.operator, $prev);
                var $currentOperator = $(selectors.operator, $that);
                $prevOperator.val($currentOperator.val());
                resetFields($currentOperator);

                // for each row, copy the values into the previous groups row
                $prevRows.each(function(rowIndex) {
                  // find the previous group and get the related row by index
                  var $currentRow = $($currentRows.get(rowIndex));
                  var $prevRow = $(this);

                  // for each input, copy the value from the previous row
                  $(rowInputSelectors).each(function(inputIndex, selector) {
                    var $prevElements = $(selector, $prevRow);
                    var $currentElements = $(selector, $currentRow);

                    // use the index to copy current values into the corresponding previous element
                    $prevElements.each(function(index) {
                      var $current = $($currentElements[index]);
                      $(this).val($current.val());
                    });
                  });

                  // make sure the new row is visible if it's copy was also.
                  if ($currentRow.is(":visible")) {
                    $prevRow.show();
                  } else {
                    $prevRow.hide();
                  }
                  // redisplay the correct input depending on the selected facet
                  showFacetValueInput($prevRow);

                  // we are done with the current row, reset it
                  resetRow($currentRow);
                });

                updateRowButtons($prev);
                $prev = $that;
              });

      return $prev.slideUp(100, function() {
        resetGroup($prev);
        updateGroupButtons();
      });
    }

    function showNextGroup() {
      $(selectors.groupWidget, root).filter(":hidden:first").slideDown(100, function() {
        updateGroupButtons();
        updateRowButtons($(selectors.group, this));
      });
    }

    function showNextRow(group) {
      //show the next hidden row
      var $nextRow = $(selectors.row, group).filter(":hidden").first();

      return $nextRow.slideDown(100, function() {
        updateRowButtons(group);

        $(selectors.facet, $(this)).focus();

      });
    }

    function removeRow($row, group) {
      var $prev = $row;

      // shuffle row values up
      $row.nextAll(":visible").andSelf().each(
          function() {
            var $that = $(this), valueSelectors = [ selectors.facet, selectors.value,
                selectors.matchValue, selectors.facetValues ];

            //shift facet related fields up
            $(valueSelectors).each(function(index, selector) {

              var $prevElements = $(selector, $prev);
              var $currentElements = $(selector, $that);

              // use the index to copy current values into the corresponding previous row element
              $prevElements.each(function(idx) {
                var $current = $($currentElements[idx]);
                $(this).val($current.val());
                resetFields($current);
              });
            });

            // redisplay the correct input depending on the selected facet
            showFacetValueInput($prev);
            showFacetValueInput($that);

            $prev = $that;
          });

      return $prev.slideUp(100, function() {
        updateRowButtons(group);
      });
    }

    //handle buttons for add/removal of group
    function updateGroupButtons() {
      var groups = $(selectors.groupWidget, root);

      if (groups.filter(":visible").length > 1) {
        $("#initialAddGroupButtonFieldSet").addClass("off");
      } else {
        $("#initialAddGroupButtonFieldSet").removeClass("off");
      }

      if (groups.filter(":hidden").length === 0) {
        $(selectors.addGroupButton, root).hide();
        $(selectors.removeGroupButton, root).show();
        $(selectors.globalOperator, root).show();
      } else if (groups.filter(":visible").length === 1) {
        $(selectors.addGroupButton, root).show();
        $(selectors.removeGroupButton, root).hide();
        $(selectors.globalOperator, root).hide();
      } else {
        $(selectors.removeGroupButton, groups.filter(":visible")).show();
        $(selectors.addGroupButton, root).show();
        $(selectors.globalOperator, root).show();
      }

      //set the focus on the first element of the new search group
      $("select", groups.filter(':visible:last')).first().focus();

    }

    function updateRowButtons(group) {
      var $rows = $(selectors.row, group);
      var $visibleRows = $rows.filter(":visible");

      //hide all the addButtons
      $(selectors.addButton, $visibleRows).hide();

      // only hide remove button if only one left.
      if ($visibleRows.length <= 1) {
        $(selectors.removeButton, $visibleRows).hide();
      } else {
        $(selectors.removeButton, $visibleRows).show();

      }

      // only show add button if there are more rows...
      if ($visibleRows.length === $rows.length) {
        $(selectors.addButton, group).hide();
      } else {
        //show only the addButton from the last line
        $(selectors.addButton, $visibleRows.last()).show();
      }
    }

    //bring group to initial state (show 2 rows, clear values)
    function resetGroup(group) {
      var rows = $(selectors.row, group);
      rows.first().show();
      rows.next().show();
      rows.slice(2).hide();
      updateRowButtons(group);

      var valueSelectors = [ selectors.operator, selectors.facet, selectors.value,
          selectors.matchValue, selectors.facetValues ];
      // clear values in group, reset select boxes
      $(valueSelectors, group).each(function(index, selector) {
        resetFields($(selector, group));
      });

      rows.each(function() {
        resetRow($(this));
      });
    }

    function resetRow($row) {
      // reset facet values
      var valueSelectors = [ selectors.facet, selectors.value, selectors.matchValue,
          selectors.facetValues ];

      $(valueSelectors).each(function(index, selector) {
        resetFields($(selector, $row));
      });

      // redisplay the correct input depending on the selected facet
      showFacetValueInput($row);
    }

    // Helper: Clearing a field in IE9 using using jQuery $field.val(undefined) will blank out the field rather than selecting the first default item.
    // As a workaround, test if a field is a infact a select list and set the selected index to 0 instead.  For every thing else, use .val(undefined)
    function resetFields($fields) {
      $fields.each(function(index, item) {
        if ($(item).is("select")) {
          if (item.selectedIndex !== 0) {
            item.selectedIndex = 0;
          }
        } else if (item.value) {
          item.value = "";
        }
      });
    }

    //FUNCTIONS TO INITIALIZE

    function upgradeNonJsFacetSelectLists() {
      var $textOnlyFacets = $(selectors.facet, root);
      var $allFacets = $(selectors.facetDisabled, root);

      // remove the non-js and prepare the js facet select list
      $allFacets.each(function(index) {
        var textOnlyFacet = $textOnlyFacets[index];

        $(this).attr("class", textOnlyFacet.className).removeAttr("disabled").show();

        // we don't need the text only facets anymore
        $(textOnlyFacet).attr("class", "").attr("disabled", "disabled").hide();
      });
    }

    //
    function initializeGroups() {
      //bind events to change from input field to controlled select-box for search string
      bindFacetChangeEvents();

      //bind events to add/remove group
      bindGroupButtonEvents();

      //formats groups and rows in groups
      setGroupInitialState();

      //handle buttons for add/removal of group
      updateGroupButtons();
    }

    // bind change event for facets to trigger input reflow
    // change from input field to controlled select-box for search string
    function bindFacetChangeEvents() {
      $(selectors.facet, root).change(function() {
        var $row = $(this).closest(selectors.row);
        showFacetValueInput($row);
      });
    }

    function bindGroupButtonEvents() {
      //bind events for add/removal of group
      $(selectors.removeGroupButton, root).click(function() {
        var $groupWidget = $(this).closest(selectors.groupWidget);
        removeGroup($groupWidget);
        return false;
      });

      $(selectors.addGroupButton, root).click(function() {
        showNextGroup();
        return false;
      });
    }

    //formats groups and rows in groups
    function setGroupInitialState() {
      $(selectors.groupWidget, root).each(function(groupIndex) {
        var $group = $(this);

        if (groupIndex === 0 || haveFacetValuesBeenEnteredForGroup($(this))) {
          $(this).show();
        } else {
          // no need to show this, reset it
          resetGroup($group);
          $group.hide();
        }

        initializeRowsInGroup($group);
      });
    }

    function initializeRowsInGroup(group) {
      setRowsInitialState(group);
      bindRowButtonEvents(group);
    }

    function setRowsInitialState(group) {
      var $last;

      //find last row that has some value. usually this is populated by the browser.
      $(selectors.row, group).each(function(index) {
        var $row = $(this);
        if (index <= 1 || hasFacetValueBeenEnteredForRow($row)) {
          $last = $(this);

          // redisplay the correct input depending on the selected facet
          showFacetValueInput($row);
        } else {
          resetRow($row);
          $row.hide();
        }
      });
      updateRowButtons(group);

    }

    function bindRowButtonEvents(group) {
      //bind events for add/removal of row
      $(selectors.addButton, group).click(function() {
        showNextRow(group);
        return false;
      });

      $(selectors.removeButton, group).click(function() {
        var $row = $(this).closest(selectors.row);
        removeRow($row, group);
        return false;
      });
    }

    //handle click of reset button
    function bindFormButtonEvents() {

      $(selectors.resetButton, root).click(function() {
        searchStateCookie.del();

        $(selectors.group, root).each(function() {
          resetFields($(selectors.globalOperator, root));
          resetGroup(this);
        });

        $(selectors.groupWidget, root).first().show().end().slice(1).hide();

        updateGroupButtons();
      });
    }

    //contextual help
    function bindContextualHelp() {
      var fader;

      $(selectors.contextualHelp, root).removeAttr("title").unbind('mouseover').bind('mouseover',
          function() {
            clearTimeout(fader);

            var tooltip = $(selectors.contextualHelpTooltip, root);
            tooltip.fadeIn();
            tooltip.bind("mouseenter", function() {
              clearTimeout(fader);
            }).bind("mouseleave", function() {
              fader = setTimeout(function() {
                tooltip.fadeOut();
              }, 500);
            });
          }).bind('mouseout', function() {
        fader = setTimeout(function() {
          $(selectors.contextualHelpTooltip, root).fadeOut();
        }, 3000);
      });
    }

    function initializeStateStorage() {
      var pageState = searchStateCookie.get();

      if (pageState) {
        var vals = pageState.split('&');
        for ( var i = 0; i < vals.length; i++) {
          var nameValuePair = vals[i].split('=');
          $('[name="' + nameValuePair[0] + '"]').val(unescape(nameValuePair[1]));
        }
      }

      $("form#advanced-search-form", root).bind('submit', function() {
        // store key value params in cookie
        var selectedValues = [];
        // use regex replace to combine selected values
        $(this).serialize().replace(/([^=&]+)=([^=&]+)/ig, function($0, $1, $2) {
          selectedValues.push($1 + "=" + escape(decodeURIComponent(($2).replace(/\+/g, '%20'))));
          return null;
        });

        searchStateCookie.set(selectedValues.join('&'));
      });
    }

    //switch facet select box to the one with facets-selection
    upgradeNonJsFacetSelectLists();

    //initialize cookie for stored last search query
    initializeStateStorage();

    //format adn hide/display correct groups + rows + buttons
    initializeGroups();

    //handle click of reset button
    bindFormButtonEvents();

    //contextual help
    bindContextualHelp();
  };
}(jQuery));

if ($(".advanced-search")) {
  page.init($('.advanced-search').get(0));
}
