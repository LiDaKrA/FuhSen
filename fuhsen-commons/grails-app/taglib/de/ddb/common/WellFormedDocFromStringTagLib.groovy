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

import groovy.xml.XmlUtil

/**
 * This taglib is used mainly in PDF generation so it's goal is to ensure a string doesn't contain invalid XML data.
 */
class WellFormedDocFromStringTagLib {
    static namespace = "ddbcommon"

    def wellFormedDocFromString = {attrs, body ->
        out << XmlUtil.escapeXml(attrs.text.toString())
    }
}
