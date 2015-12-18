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
package de.ddb.common.constants


/**
 * Enum for the category facet.
 *
 * Objects in the search index can be sorted to different categories:
 * documents (text, audio, video) or institutions
 *
 * @author boz
 */
public enum CategoryFacetEnum {
    CULTURE("Kultur"),
    INSTITUTION("Institution"),
	DWPI("dwpi"),
	MEMBER_PATENT("dwpi_member"),
	FLD("fld"),
	PUBLICATION("publication")

    /** The facet name as used by the cortex */
    private String name

    /**
     * Constructor
     *
     * @param name name of the facet
     */
    private CategoryFacetEnum(String name) {
        this.name = name
    }

    /**
     * Return the name of the enum
     *
     * @return the name of the enum
     */
    public String getName() {
        return name
    }

    /**
     * Create an object category facet enum.
     *
     * @param name
     *            string value of the category facet enum
     *
     * @return CategoryFacetEnum
     */
    public static Type valueOfName(String name) {
        values().find {it.name == name}
    }
}