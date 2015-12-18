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

import de.ddb.common.constants.CortexConstants
import de.ddb.next.beans.Item

class GetItemHierarchyTagLib {

    static namespace = "ddb"

    def ddbItemService
    def itemService
    
    /**
     * This tag renders the hierarchy of a given item-id. The hierarchy of the given item-id is
     * dynamically created via two calls to the backend.
     *
     * @attr item The item-ID (e.g. AYKQ6FKHP6A7KFKCK2K3DP6HCVNZQEQC).
     */
    def getItemHierarchy = { attrs, body ->
        def itemId = attrs.item

        // Build the hierarchy from the item to the root element. The root element is kept.
        def parentList = ddbItemService.getParent(itemId)

        // No parentList -> No hierarchy
        if(!Item.doesParentListContainHierarchy(itemId, parentList)) {
            out << ""
            return
        }

        def rootItem = Item.buildHierarchy(parentList)

        // Get all the sibling nodes
        def parentNode = rootItem.getItemFromHierarchy(itemId).getParentItem()
        def childListJson = itemService.getChildren(parentNode.id)
        // Remove the starting item from the hierarchy, it will come again with the sibling list
        rootItem.removeItemFromHierarchy(itemId)
        // Cut list after 501 items
        if(childListJson.size() > CortexConstants.MAX_HIERARCHY_SEARCH_RESULTS){
            childListJson = childListJson.subList(0, CortexConstants.MAX_HIERARCHY_SEARCH_RESULTS)
        }
        // Add them to the hierarchie tree
        rootItem.addItemsToHierarchy(childListJson)

        // Get the mainItem
        Item mainItem = rootItem.getItemFromHierarchy(itemId)

        Item emptyStartItem = new Item()
        emptyStartItem.getChildren().add(rootItem)

        attrs["item"] = emptyStartItem
        attrs["mainItem"] = mainItem

        // Start building the html code
        out << ddb.renderHierarchyItemChilds(attrs, body)
    }


}
