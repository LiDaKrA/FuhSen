
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


/**
 * Set of services used in the Pagination Widget
 * 
 * @author ema
 */
class PaginationService {
    def transactional=false

    /**
     * This method gives you back a list containing the pages
     * to show in the pagination widget
     * 
     * @param currentPage the page on which we are actually on
     * @param totalPages the number representing the total available pages
     * @param maxPagesNumberToShow the number representing the number of pages to show in the widget(odd)
     * @return the list of the pages' number available
     */
    def getPagesNumbers(int currentPage, int totalPages, int maxPagesNumberToShow){
        def result = []
        int currentMaximum = (totalPages>maxPagesNumberToShow)?maxPagesNumberToShow:totalPages
        int pivot = Math.ceil(maxPagesNumberToShow/2).toInteger().value
        int leftRightHandSide = pivot-1

        //Case in which we have the same amount of numbers on the left and on the right-hand side.
        if(pageExists(currentPage-leftRightHandSide, totalPages) && pageExists(currentPage+leftRightHandSide, totalPages)){
            result += createListOfIntInRange(currentPage-leftRightHandSide, currentPage)
            result += createListOfIntInRange(currentPage+1, currentPage+leftRightHandSide)
        }
        //Case in which we have more numbers on the right-hand side.
        else if(!pageExists(currentPage-leftRightHandSide, totalPages) && pageExists(currentPage+leftRightHandSide, totalPages)){
            for(int i=leftRightHandSide; i>0; i--){
                if(pageExists(currentPage-i, totalPages)){
                    result.add(currentPage-i)
                }
            }
            result += createListOfIntInRange(currentPage, currentPage+currentMaximum-result.size()-1)

        }
        //Case in which we have more numbers on the left-hand side.
        else if(pageExists(currentPage-leftRightHandSide, totalPages) && !pageExists(currentPage+leftRightHandSide, totalPages)){
            def tmpRightHandSide = []
            for(int i=1; i<=leftRightHandSide; i++){
                if(pageExists(currentPage+i, totalPages)){
                    tmpRightHandSide.add(currentPage+i)
                }
            }
            result += createListOfIntInRange(currentPage-(currentMaximum-tmpRightHandSide.size())+1, currentPage)
            result += tmpRightHandSide

        }
        //Case in which the total pages are less than the maxPagesNumberToShow
        else{
            result = (1..totalPages).toArray()
        }

        return result

    }

    def pageExists(int pageNumberToCheck, int totalPages){
        if(pageNumberToCheck>0 && pageNumberToCheck<=totalPages){
            return true
        } else {
            return false
        }
    }

    def createListOfIntInRange(int begin, int end){
        def result = []
        for(i in (begin..end)){
            result.add(i)
        }
        return result
    }

}
