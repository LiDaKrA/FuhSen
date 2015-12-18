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


class GetWithoutTagsTagLib {

    static namespace = "ddb"

    /**
     * It parses the body of the tag for further tags and removes them.
     * This is particularly useful, if the body of the tag is dynamically rendered from backend data but you want to
     * ensure there is no html code contained.
     */
    def getWithoutTags = { attrs, body ->
        def inputString = body()
        def outputString = ""
        if(inputString){
            outputString = inputString.replaceAll(/<!--.*?-->/, '').replaceAll(/<.*?>/, '')
        }
        out << outputString.encodeAsHTML()
    }
}
