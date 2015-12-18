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


//#############################################################################################
//TimeSpan Object
//#############################################################################################
/**
 * TimeSpan Constructor Function
 */
de.ddb.next.search.TimeSpan = function(fromDay,fromMonth, fromYear, tillDay, tillMonth, tillYear) {
  this.fromDay = fromDay;
  this.fromMonth = fromMonth;
  this.fromYear = fromYear;
  this.tillDay = tillDay;
  this.tillMonth = tillMonth;
  this.tillYear = tillYear;
};

$.extend(de.ddb.next.search.TimeSpan.prototype, {
  print : function() {
    var currObjInstance = this;

    console.log("fromDay: " + currObjInstance.fromDay);
    console.log("from-month: " + currObjInstance.fromMonth);
    console.log("fromYear: " + currObjInstance.fromYear);
    console.log("tillDay: " + currObjInstance.tillDay);
    console.log("tillMonth: " + currObjInstance.tillMonth);
    console.log("tillYear: " + currObjInstance.tillYear);
  },

  /**
   * A from date needs at least a value for the year.
   * @returns <code>false<code> if no fromYear is set
   */
  hasFromDate: function(){
    var currObjInstance = this;
    return currObjInstance.fromYear !== null;
  },

  /**
   * A till date needs at least a value for the year.
   * @returns <code>false<code> if no tillYear is set
   */
  hasTillDate: function(){
    var currObjInstance = this;
    return currObjInstance.tillYear !== null;
  },

  /**
   * At least the year must be existing. The method completes missing fromDay and fromMonth values.
   */
  completeFromDate: function(){
    var currObjInstance = this;

    //If no year is set -> return
    if (!currObjInstance.hasFromDate()) {
      return;
    }
    //if no day is set fromDay to 1
    if (currObjInstance.fromDay === null) {
      currObjInstance.fromDay = 1;
    }

    //id no month is set fromMonth to 1
    if (currObjInstance.fromMonth === null) {
      currObjInstance.fromMonth = 1;
    }
  },

  /**
   * At least the year must be existing. The method complete missing tillDay and tillMonth values.
   */
  completeTillDate: function(){
    var currObjInstance = this;

    //If no year is set -> return
    if (!currObjInstance.hasTillDate()) {
      return;
    }

    //id no month is set tillMonth to 12
    if (currObjInstance.tillMonth === null) {
      currObjInstance.tillMonth = 12;
    }

    //if no day is set tillDay to ???
    if (currObjInstance.tillDay === null) {
      var days_in_month = [31,28,31,30,31,30,31,31,30,31,30,31];

      if(currObjInstance.tillYear %4 === 0 && currObjInstance.tillYear !== 1900)
      {
         days_in_month[1]=29;
      }
      currObjInstance.tillDay = days_in_month[currObjInstance.tillMonth-1];
    }

    return true;
  },

  /**
   * Formats the from date in this form:
   * <code>G-yyyy-MM-dd</code>
   *
   * <ul>
   *   <li>G: is the era BC or AD</li>
   *   <li>y: a year digit</li>
   *   <li>M: a month digit</li>
   *   <li>d: a day digit</li>
   * </ul>
   */
  formatFromDate: function(){
    var currObjInstance = this;
    var date = null;

    //If no year is set -> return
    if (!currObjInstance.hasFromDate()) {
      return;
    }

    currObjInstance.completeFromDate();

    if(currObjInstance.fromYear >= 0) {
     date = "AD-" + currObjInstance.fromYear + "-" + currObjInstance.fromMonth + "-" + currObjInstance.fromDay;
    }
    else {
      //For BC we can reuse the minus sign of the number as separator in the date.
      date = "BC" + currObjInstance.fromYear + "-" + currObjInstance.fromMonth + "-" + currObjInstance.fromDay;
    }
    return date;

  },

  /**
   * Formats the till date in this form:
   * <code>G-yyyy-MM-dd</code>
   *
   * <ul>
   *   <li>G: is the era BC or AD</li>
   *   <li>y: a year digit</li>
   *   <li>M: a month digit</li>
   *   <li>d: a day digit</li>
   * </ul>
   */
  formatTillDate: function(){
    var currObjInstance = this;
    var date = null;

    //If no year is set -> return
    if (!currObjInstance.hasTillDate()) {
      return;
    }

    currObjInstance.completeTillDate();

    if(currObjInstance.tillYear >= 0) {
      date = "AD-" + currObjInstance.tillYear + "-" + currObjInstance.tillMonth + "-" + currObjInstance.tillDay;
     }
     else {
       //For BC we can reuse the minus sign of the number as separator in the date.
       date = "BC" + currObjInstance.tillYear + "-" + currObjInstance.tillMonth + "-" + currObjInstance.tillDay;
     }
    return date;

  },

  /**
   * Setter for the from Date
   */
  setFromDate: function(date){
    var currObjInstance = this;

    currObjInstance.fromDay =  date.getDate();
    currObjInstance.fromMonth = date.getMonth() + 1;
    currObjInstance.fromYear = date.getFullYear();
  },

  /**
   * Setter for the from Date
   */
  setTillDate: function(date){
    var currObjInstance = this;

    currObjInstance.tillDay = date.getDate();
    currObjInstance.tillMonth = date.getMonth() + 1;
    currObjInstance.tillYear = date.getFullYear();
  },

  /**
   * Clear parameter from From Date, because it doesn't find in the URL
   */
  clearFromDate: function(){
    this.fromDay = null;
    this.fromMonth = null;
    this.fromYear = null;
  },

  /**
   * Clear parameter from Till Date, because it doesn't find in the URL
   */
  clearTillDate: function(){
    this.tillDay = null;
    this.tillMonth = null;
    this.tillYear = null;
  }

});


//#############################################################################################
//TimeFacet Object
//#############################################################################################
/**
 * TimeFacet Constructor Function
 */
de.ddb.next.search.TimeFacet = function(facetsManager) {
  this.init(facetsManager);
};

/**
 * TimeFacet prototype extension with JQuery
 */
$.extend(de.ddb.next.search.TimeFacet.prototype, {
  /* TimeFacet attributes  */
  facetsManager: null,
  opened: false,
  added: false,
  selectedTimeSpan: null,
  localisation : "unscharf",

  /**
   * Initialize the TimeFacet object
   */
  init : function(facetsManager) {
    var currObjInstance = this;

    currObjInstance.facetsManager = facetsManager;
    currObjInstance.selectedTimeSpan = new de.ddb.next.search.TimeSpan();

    //Remove the off class for Non Javascript
    $(".time-facet").removeClass("off");

    //During initialisation hide the timespan form and disable the form elements
    $("#timespan-form").hide();

    // Click handler for Opening|Closing the time facet
    $(".time-facet a.h3").click(function(event) {
      event.preventDefault();
      currObjInstance.toggleForm();
    });

    // Click handler for adding a new TimeSpan
    $("#add-timespan").click(function(event) {
      event.preventDefault();
      if(!$(this).hasClass('disabled')){
        currObjInstance.assignTimeSpan(true);
        // buttons unsichtbar machen
        $('#buttons_FacetTime').hide();
      }
    });

    // Click handler for reseting the time facet
    $("#reset-timefacet").click(function(event) {
      event.preventDefault();
      if(!$(this).hasClass('disabled')){
        currObjInstance.reset();
        // buttons unsichtbar machen
        $('#buttons_FacetTime').hide();
      }
    });

    $("#from-year").change(function(){
      if ($("#from-year").val()) {
    	  $('#buttons_FacetTime').show();
        $("#add-timespan").removeClass('disabled');
        $('#reset-timefacet').removeClass('disabled');
        $('#reset-timefacet').addClass('grey');
      }
    });

    $("#till-year").change(function(){
      if ($("#till-year").val()) {
    	  $('#buttons_FacetTime').show();
        $("#add-timespan").removeClass('disabled');
        $('#reset-timefacet').removeClass('disabled');
        $('#reset-timefacet').addClass('grey');
      }
    });
    
    $(".time-facet").focusin(function(){
  	  if (currObjInstance.added) {  
  		  $('#label_timeFrom').show();
  		  $('#buttons_FacetTime').show();
  		$("#add-timespan").removeClass('disabled');
        $('#reset-timefacet').removeClass('disabled');
  		  //$('#from-day').removeClass('disabled');
  		  //$('#from-month').removeClass('disabled');
  		  //$('#from-year').removeClass('disabled');
  		  $('#from-day').css({'background-color' : 'white'});
  		  $('#from-month').css({'background-color' : 'white'});
  		  $('#from-year').css({'background-color' : 'white'});
  		  $('#till-day').css({'background-color' : 'white'});
  		  $('#till-month').css({'background-color' : 'white'});
  		  $('#till-year').css({'background-color' : 'white'});
  	  }
    });
    
    $(".time-facet").focusout(function(){
  	  if (currObjInstance.added) {      
  		  $('#label_timeFrom').hide();
  		  $('#buttons_FacetTime').hide();	
  		  //$('#from-day').addClass('disabled');
  		  //$('#from-month').addClass('disabled');
  		  //$('#from-year').addClass('disabled');
  		  $('#from-day').css({'background-color' : '#DFDEDD'});
  		  $('#from-month').css({'background-color' : '#DFDEDD'});
  		  $('#from-year').css({'background-color' : '#DFDEDD'});
  		  $('#till-day').css({'background-color' : '#DFDEDD'});
  		  $('#till-month').css({'background-color' : '#DFDEDD'});
  		  $('#till-year').css({'background-color' : '#DFDEDD'});
  	  }
    });
    
  },

  /**
   * This method initialize the TimeFacet widget based on the window url.
   * It search for facetValues[] 'begin_time' and 'end_time'. Contained values will be set into the form.
   */
  initOnLoad: function() {
    var currObjInstance = this;

    //Asynchronously calculate the dates for the form! Continue the initialization in the success part
    currObjInstance.calculateFacetDates();
    
  },

  /**
   * Converts a string of the form <code>G-yyyy-MM-dd</code> to a Javascript Date object.
   */
  convertServerDateToJsDate: function(serverDate) {
    var date = null;

    if(serverDate) {
      var year = null;
      var dateArray = serverDate.split("-");

      if(dateArray[0].indexOf("BC") !== -1){
        year = "-" + dateArray[1];
      }
      else {
        year = dateArray[1];
      }

      //Months starts in Javascript with 0!
      //To work with years between 1-99
      date = new Date();
      date.setFullYear(year,dateArray[2] -1,dateArray[3]);
    }
    return date;

  },

  /**
  * This method initialize the TimeFacet form based on the given beginDate, endDate and calculation method (excact/fuzzy)
  */
  initFormOnLoad: function(beginDateStr, endDateStr, exact) {
    var currObjInstance = this;

    var beginDate = currObjInstance.convertServerDateToJsDate(beginDateStr);
    var endDate = currObjInstance.convertServerDateToJsDate(endDateStr);

    $(".time-facet").removeClass("off");

    if(exact) {
      $("#limitation-exact").prop("checked", true);
    }
    else {
      $("#limitation-fuzzy").prop("checked", true);
    }

    if (beginDate) {
      currObjInstance.selectedTimeSpan.setFromDate(beginDate);
    } else {
      currObjInstance.selectedTimeSpan.clearFromDate();
    }

    if (endDate) {
      currObjInstance.selectedTimeSpan.setTillDate(endDate);
    } else {
      currObjInstance.selectedTimeSpan.clearTillDate();
    }

    //Initialize the form
    if (beginDate || endDate) {
      currObjInstance.updateTimeSpanForm();
      currObjInstance.openForm();
      $("#add-timespan").removeClass('disabled');
      $('#reset-timefacet').removeClass('disabled');
      $('#reset-timefacet').addClass('grey');
    } else {
      //Close the form if no values has been found.
      currObjInstance.closeForm();
    }
  },

  /**
   * Parses the browser url for time facet values.
   */
   parseWindowsUrl: function() {
     var dividerPattern = /\-?[0-9]+/;
     var beginDays = null;
     var endDays = null;
     var exact = null;

     // Search for time facetValues[] in the window url
     var facetValuesFromUrl = de.ddb.common.search.getFacetValuesFromUrl();

     if (facetValuesFromUrl) {
       $.each(facetValuesFromUrl, function(key) {

         var decodedValue = decodeURIComponent(facetValuesFromUrl[key]);
         if ((facetValuesFromUrl[key].indexOf("applicationYear_fct") === 0)) {
           

           //Unscharf/Fuzzy
           if(decodedValue.indexOf('*+TO') !== -1){
        	 var split = dividerPattern.exec(decodedValue);
        	 if (split != null) {
        		 endDays = split[0];
        	 } 
             exact = false;
           }else {//Genau/Exactly
        	 var split = dividerPattern.exec(decodedValue);
          	 if (split != null) {
        	   beginDays = split[0];
          	 } 
             exact = true;
           }
         }
         
         if ((facetValuesFromUrl[key].indexOf("applicationYear_fct") === 0)) {

           //Unscharf/Fuzzy
           if(decodedValue.indexOf('TO+*') !== -1){
             split = dividerPattern.exec(decodedValue);
             if (split != null) {
            	 beginDays = split[0];
             } 
             exact = false;
           }else {//Genau/Exactly
             var indexOfTo = decodedValue.indexOf('TO');
             var endSubstring = decodedValue.substr(indexOfTo);
             split = dividerPattern.exec(endSubstring);
             if (split != null) {
            	 endDays = split[0];
             } 
             exact = true;
       		}
     	}
       });
     }

     //Return an object containing the parsed information
     return {
       beginDays:beginDays,
       endDays:endDays,
       exact:exact
       };
   },

  /**
   * This method toggles between the open and closed state of the timefacet form
   */
  toggleForm : function() {
    var currObjInstance = this;

    if (!currObjInstance.opened) {
      currObjInstance.openForm();
    } else {
      //Prevent from closing if a timespan has been selected
      if (!currObjInstance.added) {
        currObjInstance.closeForm();
      }
    }
  },

  /**
   * This method opens the timefacet form
   */
  openForm : function() {
    var currObjInstance = this;
    var timespanFormDiv = $("#timespan-form");
    var timeFacetDiv = $(".time-facet");

    currObjInstance.opened = true;
    timespanFormDiv.fadeIn('fast');
    timeFacetDiv.addClass('active');
  },
  
  /**
   * This method closes the timefacet form if it is empty
   */
  closeFormIfEmpty : function() {
	  var currObjInstance = this;
	  
	  if (!currObjInstance.added) {
	        currObjInstance.closeForm();
	      }
  },

  /**
   * This method closes the timefacet form
   */
  closeForm : function() {
    var currObjInstance = this;
    var timespanFormDiv = $("#timespan-form");
    var timeFacetDiv = $(".time-facet");

    currObjInstance.opened = false;
    timespanFormDiv.fadeOut('fast');
    timeFacetDiv.removeClass('active');
  },

  /**
   * Close the time facet form when the user clicks outside of it.
   */
  manageOutsideClicks : function(thisInstance) {
    $(document).mouseup(function(e) {
      var container = $(".time-facet");
      if(!$(e.target).parents(container).is(container) && thisInstance.opened) {
        thisInstance.closeForm();
      }
    });
  },

  /**
   * Checks the values of the form and adds a new timespan.
   */
  assignTimeSpan : function(checkYears) {
    var currObjInstance = this;

    de.ddb.next.search.hideError();

    //Retrieve the values from the timespan form
    var fromDayValue = $("#from-day").val() !== "" ? $("#from-day").val() : null;
    var fromMonthValue = $("#from-month").val() !== "" ? $("#from-month").val() : null;
    var fromYearValue = $("#from-year").val() !== "" ? $("#from-year").val() : null;

    var tillDayValue = $("#till-day").val() !== "" ? $("#till-day").val() : null;
    var tillMonthValue = $("#till-month").val() !== "" ? $("#till-month").val() : null;
    var tillYearValue = $("#till-year").val() !== "" ? $("#till-year").val() : null;

    if (checkYears && fromYearValue === null && tillYearValue === null) {
      de.ddb.next.search.showError("Bitte geben Sie in eines der Zeit-Eingabefelder 'Von' oder 'Bis' eine Jahreszahl ein.");
      return;
    }

    var newTimeSpan = new de.ddb.next.search.TimeSpan(fromDayValue, fromMonthValue, fromYearValue, tillDayValue, tillMonthValue, tillYearValue);
    currObjInstance.selectedTimeSpan = newTimeSpan;
    currObjInstance.added = true;

    currObjInstance.selectedTimeSpan.completeFromDate();
    currObjInstance.selectedTimeSpan.completeTillDate();

    currObjInstance.updateTimeSpanForm();

    currObjInstance.calculateFacetDays();
  },

  /**
   * Resets the input elements of the form.
   * The window URL is reseted by calling assignTimeSpan() which does this implicitly
   */
  reset : function() {
    var currObjInstance = this;

    //Hide error if available
    de.ddb.next.search.hideError();

    //Set an empty TimeSpan
    var newTimeSpan = new de.ddb.next.search.TimeSpan();
    currObjInstance.selectedTimeSpan = newTimeSpan;
    
    //reset the GUI
    currObjInstance.updateTimeSpanForm();

    //reset button Apply
    $("#add-timespan").addClass('disabled');
    $('#reset-timefacet').removeClass('grey');
    $('#reset-timefacet').addClass('disabled');

    //asign the timeSpan to reset also the window url etc!
    currObjInstance.assignTimeSpan(false);

    currObjInstance.added = false;
  },

  /**
   * Updates the form fields
   */
  updateTimeSpanForm: function() {
    var currObjInstance = this;

    $("#from-year").val(currObjInstance.selectedTimeSpan.fromYear);
    if($("#from-year").val()) {
      $("#from-day").val(currObjInstance.selectedTimeSpan.fromDay);
      $("#from-month").val(currObjInstance.selectedTimeSpan.fromMonth);
    } else {
      $("#from-day").val(null);
      $("#from-month").val(null);
    }

    $("#till-year").val(currObjInstance.selectedTimeSpan.tillYear);
    if($("#till-year").val()) {
      $("#till-day").val(currObjInstance.selectedTimeSpan.tillDay);
      $("#till-month").val(currObjInstance.selectedTimeSpan.tillMonth);
    } else {
      $("#till-day").val(null);
      $("#till-month").val(null);
    }
  },

  /**
   * Updates the browser URL and performs a new search with the given time facet values.
   */
  updateWindowUrl: function(daysFrom, daysTill) {
    var paramsArray = null;
    var selectedFacetValues = [];

    daysFrom = daysFrom || '*';
    daysTill = daysTill || '*';

    // Update Url (We want to keep the already selected facet values, but throw away the offset etc.)
    var facetValuesFromUrl = de.ddb.common.search.getFacetValuesFromUrl();

    if (facetValuesFromUrl) {
      $.each(facetValuesFromUrl, function(key, value) {
        ///Only add facetValues that do not start with "applicationYear_fct"
          if ((facetValuesFromUrl[key].indexOf("applicationYear_fct") === -1)) {
          selectedFacetValues.push(decodeURIComponent(value.replace(/\+/g, '%20')));
        }
      });
    }

    if (daysFrom != '*' || daysTill != '*') {
    	selectedFacetValues.push('applicationYear_fct=[' + daysFrom + ' TO ' + daysTill + ']');
    }

    //The facet values will be stored in a two dimensional Array ["facetValues[]",['type_fctyDmediatype_003','time_begin_fct=1014', 'time_end_fct=2014',]]
    paramsArray = [['facetValues[]', selectedFacetValues]];

    //Perform the search with offset 0
    paramsArray.push(['offset', 0]);

    var newUrl = $.addParamToCurrentUrl(paramsArray);

    de.ddb.next.search.fetchResultsList(newUrl, function() {});
  },


  /**
   * Converts the Date representation of the time facet values to a backend related Day represenation.
   * The method works with the formated values of the fromDate and tillDate from the inner model.
   *
   * The calculation itself is performed on the frontend server and triggered by an AJAX request:
   * http://localhost:8080/ddb-next/facets/calculateTimeFacetDays?dateFrom=AD-1000-1-1&dateTill=AD-2000-12-31
   *
   * After a successful response the window url is updated and a new search is performed.
   *
   * The AJAX call must be async, otherwise the browser might freeze!
   */
  calculateFacetDays: function() {
    var currObjInstance = this;

    var url = jsContextPath + '/facets/calculateTimeFacetDays' + '?';
    if (currObjInstance.selectedTimeSpan.hasFromDate()) {
      url += 'dateFrom='+ currObjInstance.selectedTimeSpan.formatFromDate();
    }

    if (currObjInstance.selectedTimeSpan.hasTillDate()) {
      if (url.indexOf("dateFrom") !== -1) {
        url += "&";
      }

      url += 'dateTill=' + currObjInstance.selectedTimeSpan.formatTillDate();
    }
    $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : url,
      complete : function(data) {
        var parsedResponse = jQuery.parseJSON(data.responseText);
        currObjInstance.updateWindowUrl(parsedResponse.daysFrom, parsedResponse.daysTill);
      }
    });
  },

  /**
   * Converts the Day representation of the time facet values (stored in the window url) to Date formates values which has the form:
   * <code>G-yyyy-MM-dd</code>
   *
   * <ul>
   *   <li>G: is the era BC or AD</li>
   *   <li>y: a year jdigit</li>
   *   <li>M: a month digit</li>
   *   <li>d: a day digit</li>
   * </ul>
   *
   * The calculation itself is done on the frontsend server and performed with an AJAX request which looks like the following:
   * http://localhost:8080/ddb-next/facets/calculateTimeFacetDates?beginDays=364884&endDays=730486
   *
   * After a successful response, the time facet form is updated with the response values.
   *
   * The AJAX call must be async, otherwise the browser might freeze!
   */
  calculateFacetDates: function() {
    var currObjInstance = this;

    // Search for time facet parameter in the window url
    var urlValues = currObjInstance.parseWindowsUrl();

    var url = jsContextPath + '/facets/calculateTimeFacetDates' + '?';
    if (urlValues.beginDays) {
      url += 'beginDays='+ urlValues.beginDays;
    }

    if (urlValues.endDays) {
      if (url.indexOf("beginDays") !== -1) {
        url += "&";
      }

      url += 'endDays=' + urlValues.endDays;
    }

    $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : url,
      complete : function(data) {
        var parsedResponse = jQuery.parseJSON(data.responseText);

        //Continue with initializing the form
        currObjInstance.initFormOnLoad(parsedResponse.dateFrom, parsedResponse.dateTill, urlValues.exact);
      }
    });
  }

});// End extend TimeFacet prototype