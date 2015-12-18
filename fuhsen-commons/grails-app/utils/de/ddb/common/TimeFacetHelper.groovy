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
package de.ddb.common

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat



/**
 * All calculations are based on this formula
 * (<Wert> - 719164(Time from 0 to 01.01.1970 in Days)) * 86400000(Milliseconds of a day) = Time in Milliseconds since 01.01.1970
 */
class TimeFacetHelper {

    final static DateFormat dateFormat = new SimpleDateFormat("G-yyyy-MM-dd", Locale.ENGLISH)

    final static DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy")

    final static def MILLISECONDS_DAY = 86400000

    final static def DAYS_FROM_YEAR_0_TO_1970 = 719164


    /**
     * Returns a Date instance for a given formatted string with the form <code>G-yyyy-MM-dd</code>
     * <ul>
     *   <li>G: is the era BC or AD</li>
     *   <li>y: a year jdigit</li>
     *   <li>M: a month digit</li>
     *   <li>d: a day digit</li>
     * </ul>
     * @param date the sting
     * @return a Date instance for a given formatted string
     */
    def static parseDateString(String date) {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

        return dateFormat.parse(date)
    }

    /**
     * Returns a Date instance for a given formatted string with the form <code>dd.MM.yyyy</code>
     * <ul>
     *   <li>y: a year jdigit</li>
     *   <li>M: a month digit</li>
     *   <li>d: a day digit</li>
     * </ul>
     * @param date the sting
     * @return a Date instance for a given formatted string
     */
    def static parseSimpleDateString(String date) {
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

        return simpleDateFormat.parse(date)
    }

    /**
     * Calculate the time facet Days representation for a given formatted string with the form <code>G-yyyy-MM-dd</code>
     * <ul>
     *   <li>G: is the era BC or AD</li>
     *   <li>y: a year jdigit</li>
     *   <li>M: a month digit</li>
     *   <li>d: a day digit</li>
     * </ul> 
     * @param dateString the string for which to calculate the days
     * @return the time facet Days representation for a given date
     */
    def static calculateDaysForTimeFacet(String dateString) {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
        Date date = dateFormat.parse(dateString)

        return calculateDaysForTimeFacet(date)
    }

    /**
     * Returns a formated string for a given date
     * 
     * @param date the date to format
     * @return a formated string for a given date
     */
    def static formatDate(Date date) {
        if (date) {
            return dateFormat.format(date)
        }

        return ""
    }

    /**
     * Returns a formated string for a given time.
     * A Calendar instance is used to handle the right era (BC or AD) 
     *
     * @param date the time to format
     * @return a formated string for a given time
     */
    def static formatMillis(def millis) {
        Calendar cal = Calendar.getInstance()
        cal.setTimeInMillis(millis)

        return dateFormat.format(cal.getTime())
    }

    /**
     * Calculate the time facet Days representation for a given date
     * 
     * @param dateString the date instance for which to calculate the Days
     * @return the time facet Day representation for a given date
     */
    def static calculateDaysForTimeFacet(Date date) {
        def timeSince1970 = date.getTime()

        def days = (timeSince1970 / MILLISECONDS_DAY) + DAYS_FROM_YEAR_0_TO_1970

        return days
    }

    /**
     * Calculate the time for a given time facet Day value
     * 
     * @param day the Day value to convert
     * @return the time for a given time facet Day value
     */
    def static calculateTimeFromTimeFacetDays(days) {
        def time = null

        if (days != null) {
            time = (days.toLong() - DAYS_FROM_YEAR_0_TO_1970) * MILLISECONDS_DAY
        }
        return time
    }

    /**
     * Convert the given days into a human readable format.
     *
     * @param daysString days since 1.1.1970
     * @param exact if true then return date including day and month, otherwise return only the year
     *
     * @return human readable date string
     */
    public static String convertDaysToDateString(String daysString, boolean exact) {
        String result

        if (daysString) {
            Date date = new Date(TimeFacetHelper.calculateTimeFromTimeFacetDays(Integer.parseInt(daysString)))

            if (exact) {
                result = TimeFacetHelper.simpleDateFormat.format(date)
            }
            else {
                Calendar calendar = Calendar.instance

                calendar.setTime(date)
                result = calendar.get(Calendar.YEAR) as String
            }
        }
        return result
    }

    /**
     * Do some magic things to convert the given time span into a human readable format.
     *
     * @param beginTime
     * @param endTime
     *
     * @return human readable time span string
     */
    def static convertTimeFacetValues(String beginTime, String endTime) {
        String result = ""
        String beginDays = null
        String endDays = null
        boolean exact = false

        if (beginTime) {
            String[] split = beginTime.substring(1, beginTime.length() - 1).split(" ")

            // Fuzzy
            if (split[0] == "*") {
                endDays = split[2]
            }
            else {
                // Exactly
                beginDays = split[0]
                exact = true
            }
        }
        if (endTime) {
            String[] split = endTime.substring(1, endTime.length() - 1).split(" ")

            // Fuzzy
            if (split[2] == "*") {
                beginDays = split[0]
            }
            else {
                // Exactly
                endDays = split[2]
                exact = true
            }
        }
        if (! exact) {
            result += "~ "
        }
        if (beginDays) {
            result += convertDaysToDateString(beginDays, exact)
        }
        result += " - "
        if (endDays) {
            result += convertDaysToDateString(endDays, exact)
        }
        return result
    }

    /**
     * Validate the lifetime strings from the request
     * If both values are empty or one of the values is invalid, the method return <code>false</code>
     * 
     * @param from a date as string
     * @param to a date as string
     * 
     * @return <code>true</code> if the user input is valid
     */
    def static validateLifetimeUserInput(def from, def to) {
        if (!from && !to) {
            return false
        }

        //Check if the user input for the from date is valid
        if (from) {
            if (!isValidLifetimeString(from)) {
                return false
            }
        }

        //Check if the user input for the to date is valid
        if (to) {
            if (!isValidLifetimeString(to)) {
                return false
            }
        }

        return true
    }

    /**
     * Validates a lifetime String
     * A valid date string has either the form yyyy or dd.MM.yyyy
     * 
     * @param dateString the string to validate
     * @return <code>true</true> if the dateString is valid
     */
    def static isValidLifetimeString(String dateString) {
        boolean isValid = false

        //FIXME Validation should be replaced by a regex
        Date date = null

        try {
            date = TimeFacetHelper.parseSimpleDateString(dateString)
            isValid = true
        } catch (ParseException pe) {
            try {
                Integer.parseInt(dateString)
                isValid = true
            } catch (NumberFormatException nfe) {
                isValid = false
            }
        }

        return isValid
    }


    /**
     * Creates the time facet url parameter values
     *
     * @param exact if the time range should be exact
     * @return a list with time facet values
     */
    def static getTimespanQuery(def dateFrom , def dateTill , boolean exact) {
        def queryPart = ""
        def timespanValues = []

        def daysFrom = '*'
        def daysTill = '*'

        if (!dateFrom && !dateTill) {
            return null
        }

        if(dateFrom) {
            daysFrom = calculateDaysForTimeFacet(dateFrom)
        }

        if(dateTill) {
            daysTill = calculateDaysForTimeFacet(dateTill)
        }

        if(exact) {
            if(daysFrom != '*') {
                timespanValues.add('begin_time:([' + daysFrom + ' ' + daysTill + '])')
            }
            if(daysTill != '*') {
                timespanValues.add('end_time:([' + daysFrom + ' ' + daysTill + '])')
            }
        }else{
            //Unscharf
            if(daysTill != '*') {
                timespanValues.add('begin_time:([* '+ daysTill + '])')
            }
            if(daysFrom != '*') {
                timespanValues.add('end_time:([' + daysFrom + ' *])')
            }
        }

        //Only one value
        if (timespanValues.size() == 1) {
            queryPart = timespanValues[0]
        }

        //Two values concate with AND
        if (timespanValues.size() == 2) {
            queryPart = timespanValues[0] + " AND " + timespanValues[1]
        }

        return queryPart
    }

}
