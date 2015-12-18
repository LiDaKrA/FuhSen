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

class RenderHierarchyItemChildsTagLib {

    static namespace = "ddb"

    /**
     * This tag should only be used by the taglib itself!
     * It triggers the rendering of the childs of the current item.
     *
     * @attr item The current child Item object (of type de.ddb.next.beans.Item) of the recursive call hierarchy
     * @attr mainItem The main Item (of type de.ddb.next.beans.Item), meaning the Item that was initially
     *      given to render the whole hierarchy
     */
    def renderHierarchyItemChilds = { attrs, body ->
        def itemMap = [:]
        def item = attrs.item
        def mainItem = attrs.mainItem

        itemMap["item"] = item
        itemMap["mainItem"] = mainItem
        itemMap["childs"] = item.getChildren()

        out << render(template:"/item/hierarchyItemChilds", model:[item: itemMap])
    }
}
