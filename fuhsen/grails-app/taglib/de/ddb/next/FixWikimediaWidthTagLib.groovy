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

class FixWikimediaWidthTagLib {
    static namespace = "ddb"

    /**
     * Wikimedia can have images delivered with a fixed width. 
     * This will change the width to a default with
     * Takes 2 attributes: attrs.thumbnail and attrs.desiredWidth (ex: 130px)
     * Take a string like:  http://upload.wikimedia.org/wikipedia/commons/thumb/7/71/Karl_IV._(HRR).jpg/270px-Karl_IV._(HRR).jpg
     * Return it to: http://upload.wikimedia.org/wikipedia/commons/thumb/7/71/Karl_IV._(HRR).jpg/150px-Karl_IV._(HRR).jpg
     */
    def fixWikimediaImageWidth={attrs, body ->
        def thumbnail =attrs.thumbnail.toString()
        out << thumbnail.replaceAll(/(\d+)px(.*)\.([a-zA-Z]{3,4})/,attrs.desiredWidth+'$2.$3')
    }
}
