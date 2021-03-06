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
package de.ddb.common.beans

import groovy.transform.ToString

@ToString(includeNames=true)

class SearchQueryTerm {
    final String name
    final Collection<String> values = []

    public SearchQueryTerm(String termString) {
        def termParts = termString.split('=')

        if (termParts.size() > 0) {
            this.name = termParts[0]
            if (termParts.size() > 1) {
                this.values.add(termParts[1])
            }
        }
    }
}
