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

import java.text.ParseException


/**
 * TimeSpan class.
 * A timespan consists of a from and a till date.
 * Each date is represented by a day, a month and a year.
 *
 * @author boz
 */
class TimeSpan {

    def fromDay = null
    def fromMonth = null
    def fromYear = null

    def tillDay = null
    def tillMonth = null
    def tillYear = null

    /**
     * TimeSpan Constructor with number values
     */
    def TimeSpan(fromDay,fromMonth, fromYear, tillDay, tillMonth, tillYear) {
        this.fromDay = fromDay;
        this.fromMonth = fromMonth;
        this.fromYear = fromYear;
        this.tillDay = tillDay;
        this.tillMonth = tillMonth;
        this.tillYear = tillYear;
    }

    /**
     * TimeSpan Constructor for formatted string values
     *
     * At least one constructor parameter must be set, otherwise an IllegalArgumentException is thrown-
     *
     * If the from parameter is null, set from to the value of till.
     * If the till parameter is null, set till to the value of from.
     *
     * @param from date formated
     * @param till date formated
     */
    def TimeSpan(from, till) {
        if ((from == null) && (till == null)) {
            throw new IllegalArgumentException("At least one constructor parameter must not be null!")
        }

        if (from) {
            setFromDate(from)
        } else {
            setFromDate(till)
        }

        if (till) {
            setTillDate(till)
        } else {
            setTillDate(from)
        }
    }

    /**
     * Sets the from date by an formated string
     * @param from a date formated string
     */
    def setFromDate(String from) {
        Date date = null

        try {
            date = TimeFacetHelper.parseSimpleDateString(from)

            def dateParts = from.split("\\.")

            this.fromDay = Integer.parseInt(dateParts[0]);
            this.fromMonth = Integer.parseInt(dateParts[1]);
            this.fromYear = Integer.parseInt(dateParts[2]);
        } catch (ParseException pe) {
            try {
                this.fromYear = Integer.parseInt(from)
            } catch (NumberFormatException nfe) {
                //log.err "Invalid from date string"
            }
        }
    }

    /**
     * Sets the till date by an formated string
     * @param from a date formated string
     */
    def setTillDate(String till) {
        Date date = null

        try {
            date = TimeFacetHelper.parseSimpleDateString(till)

            def dateParts = till.split("\\.")

            this.tillDay = Integer.parseInt(dateParts[0]);
            this.tillMonth = Integer.parseInt(dateParts[1]);
            this.tillYear = Integer.parseInt(dateParts[2]);
        } catch (ParseException pe) {
            try {
                this.tillYear = Integer.parseInt(till)
            } catch (NumberFormatException nfe) {
                //log.err "Invalid from date string"
            }
        }
    }

    /**
     * A from date needs at least a value for the year.
     * @returns <code>false<code> if no fromYear is set
     */
    def hasFromDate(){
        return this.fromYear != null;
    }

    /**
     * A till date needs at least a value for the year.
     * @returns <code>false<code> if no tillYear is set
     */
    def hasTillDate(){
        return tillYear != null;
    }

    /**
     * At least the year must be existing. The method completes missing fromDay and fromMonth values.
     */
    def completeFromDate(){
        //If no year is set -> return
        if (!hasFromDate()) {
            return;
        }
        //if no day is set fromDay to 1
        if (fromDay == null) {
            fromDay = 1;
        }

        //id no month is set fromMonth to 1
        if (fromMonth == null) {
            fromMonth = 1;
        }
    }

    /**
     * At least the year must be existing. The method complete missing tillDay and tillMonth values.
     */
    def completeTillDate(){
        //If no year is set -> return
        if (!hasTillDate()) {
            return false
        }

        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
        cal.set(Calendar.YEAR, tillYear)

        def maxMonth = 11

        //if no tillMonth is available initialize it with the max month of the year
        if (tillMonth == null) {
            cal.set(tillYear, 1, 1)
            maxMonth = cal.getActualMaximum(Calendar.MONTH)
            tillMonth = maxMonth + 1
        }

        //if no day is set tillDay to ???
        if (tillDay == null) {
            cal.set(tillYear, (tillMonth-1), 1)
            tillDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        return true;
    }

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
    def formatFromDate(){
        def date = null;

        //If no year is set -> return
        if (!hasFromDate()) {
            return;
        }

        completeFromDate();

        if(fromYear >= 0) {
            date = "AD-" + fromYear + "-" + fromMonth + "-" + fromDay;
        }
        else {
            //For BC we can reuse the minus sign of the number as separator in the date.
            date = "BC" + fromYear + "-" + fromMonth + "-" + fromDay;
        }
        return date;

    }

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
    def formatTillDate(){
        def date = null;

        //If no year is set -> return
        if (!hasTillDate()) {
            return;
        }

        completeTillDate();

        if(tillYear >= 0) {
            date = "AD-" + tillYear + "-" + tillMonth + "-" + tillDay;
        }
        else {
            //For BC we can reuse the minus sign of the number as separator in the date.
            date = "BC" + tillYear + "-" + tillMonth + "-" + tillDay;
        }
        return date;

    }

    /**
     * Clear parameter from From Date, because it doesn't find in the URL
     */
    def clearFromDate(){
        this.fromDay = null;
        this.fromMonth = null;
        this.fromYear = null;
    }

    /**
     * Clear parameter from Till Date, because it doesn't find in the URL
     */
    def clearTillDate(){
        this.tillDay = null;
        this.tillMonth = null;
        this.tillYear = null;
    }

    /**
     * Gets a date instance for the from date
     * @return a date instance for the from date
     */
    def getFromDate() {
        def date = null

        if (hasFromDate()) {
            completeFromDate()
            def formatedDate = formatFromDate()
            date = TimeFacetHelper.parseDateString(formatedDate)

        }

        return date
    }

    /**
     * Gets a date instance for the till date
     * @return a date instance for the till date
     */
    def getTillDate() {
        def date = null

        if (hasTillDate()) {
            completeTillDate()
            def formatedDate = formatTillDate()
            date = TimeFacetHelper.parseDateString(formatedDate)

        }

        return date
    }
}
