/*
 * Copyright (C) 2013 FIZ Karlsruhe
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
package de.ddb.common.constants


/**
 * @author chh
 *
 */
public enum Type {
	
    CULTURAL_ITEM("item"), INSTITUTION("institution"), ENTITY("entity"), PERSON("person"), PRODUCT("product"), ORGANIZATION("organization")

    private String name

    private Type(String name) {
        this.name = name
    }

    public String getName() {
        return name
    }

    /**
     * Create an object type enum.
     * 
     * @param name
     *            string value of the object type enum
     * 
     * @return Type
     */
    public static Type valueOfName(String name) {
        values().find {it.name == name}
    }
}