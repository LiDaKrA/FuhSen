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

import groovy.util.slurpersupport.NodeChild
import groovy.xml.StreamingMarkupBuilder

class BootStrap {

    def configurationService

    def init = { servletContext ->
        try {
            configurationService.logConfigurationSettings()
        }
        catch (UnsupportedOperationException e) {
        }

        NodeChild.metaClass.toXmlString = {
            def self = delegate
            new StreamingMarkupBuilder().bind {
                delegate.mkp.xmlDeclaration() // Use this if you want an XML declaration
                delegate.out << self
            }.toString()
        }
    }

    def destroy = {
    }
}