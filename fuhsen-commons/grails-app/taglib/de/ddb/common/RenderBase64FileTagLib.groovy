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
import org.apache.commons.codec.binary.Base64
/**
 *  @author arb
 * To be used if a string which might contain html chars need to be parsed to a dom to generate well formed html
 * It will get rid of some content and guarantee the output is well formed,
 * The method parses the input HTML into a new Document and out of the new document we retireve only the html information needed  
 *
 */
class RenderBase64FileTagLib {
    static namespace = "ddbcommon"

    def renderBase64File = { attrs ->

        def mimeType = attrs.remove('mimeType')
        if (mimeType == null) {
            throwTagError("'mimeType' is required")
        }

        def bytes = attrs.remove('bytes')
        if (bytes == null) {
            throwTagError("'bytes' is required")
        }

        out << 'data:'
        out << mimeType
        out << ';base64,'
        out << new String(new Base64().encode(bytes), "UTF-8")
    }
}
