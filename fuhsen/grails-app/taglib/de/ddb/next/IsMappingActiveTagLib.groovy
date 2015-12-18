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
package de.ddb.next


/**
 * Taglib for checking if the currently displayed page fits a controller/action/dir combination
 * given as params to the tag. If the param-combination matches the the current page, the body
 * of the tag is displayed. Otherwise not.
 * @author hla
 */
class IsMappingActiveTagLib {

    static namespace = "ddb"

    /**
     * Renders the body of the tag if the given controller/action/dir combination fits the currently 
     * displayed page. The following combinations are possible parameters for the tag:
     * 1) controller
     * 2) controller/action
     * 3) controller/dir
     *
     * @attr context The current page context, normally just ${params}
     * @attr testif A list of maps, with each map defining a controller/action/dir combination that 
     *  should be tested against the currently displayed page.
     */
    def isMappingActive = {attrs, body ->
        def controller = attrs?.context?.controller
        def action = attrs?.context?.action
        def dir = attrs?.context?.dir
        def testForMappings = attrs?.testif

        boolean doesPageMatch = false

        if(controller && action && testForMappings){
            for(it in testForMappings){
                // controller/action combination
                if(it.controller && it.action){
                    if(controller == it.controller && action == it.action){
                        doesPageMatch = true
                        break
                    }
                    // controller/dir combination
                }else if(it.controller && it.dir){
                    if(controller == it.controller && dir == it.dir){
                        doesPageMatch = true
                        break
                    }
                    // controller combination
                }else if(it.controller){
                    if(controller == it.controller){
                        doesPageMatch = true
                        break
                    }
                }
            }
        }

        if(doesPageMatch){
            out << body()
        }else{
            out << ""
        }
    }
}
