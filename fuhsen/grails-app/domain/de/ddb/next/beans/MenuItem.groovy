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
package de.ddb.next.beans

import groovy.transform.ToString

/**
 * Domain object representing a menu item from the main menu.
 *
 * @author sche
 */
@ToString(includeNames=true)
class MenuItem {
    final def label = [:]
    final MenuItem[] subMenuItems
    final String uri

    MenuItem(String deLabel, String enLabel, String etLabel, String ruLabel, String uri, Object subMenu) {
        this.label["de"] = deLabel
        this.label["en"] = enLabel
        this.label["et"] = etLabel
        this.label["ru"] = ruLabel
        this.uri = uri
        if (subMenu) {
            subMenuItems = new MenuItem[subMenu.size()]
            subMenu.each {menuItem ->
                subMenuItems[menuItem.position - 1] =
                        new MenuItem(menuItem.deValue, menuItem.enValue, menuItem.etValue, menuItem.ruValue, menuItem.ref, menuItem.submenu)
            }
        }
    }
}