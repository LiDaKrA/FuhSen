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

class RenderHierarchyItemTagLib {

    static namespace = "ddb"

    /**
     * This tag should only be used by the taglib itself!
     * It renders a single item entry and triggers the recursive render call to its childs (if it has any).
     *
     * @attr item The current Item object (of type de.ddb.next.beans.Item) of the recursive call hierarchy
     * @attr mainItem The main Item (of type de.ddb.next.beans.Item), meaning the Item that was initially
     *      given to render the whole hierarchy
     *
     */
    def renderHierarchyItem = { attrs, body ->
        def itemMap = [:]
        def item = attrs.item
        def mainItem = attrs.mainItem

        itemMap["label"] = item.label
        itemMap["isMainItem"] = (item.id == mainItem.id)
        itemMap["item"] = item
        itemMap["mainItem"] = mainItem
        if(item.hasChildren()){
            itemMap["hasChildren"] = true
        }else{
            itemMap["hasChildren"] = false
        }

        out << render(template:"/item/hierarchyItem", model:[item: itemMap])
    }
}
