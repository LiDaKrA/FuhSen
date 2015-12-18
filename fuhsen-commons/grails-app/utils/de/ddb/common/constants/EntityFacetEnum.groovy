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
 * Enum for the facets.
 *
 * @author boz
 */
public enum EntityFacetEnum {
    //Main facets
    PERSON_NAME_FCT("person_name_fct", null, true, false),
    PERSON_PLACE_FCT("person_place_fct", null, true, false),
    PERSON_OCCUPATION_FCT("person_occupation_fct", null, true, false),
    PERSON_GENDER_FCT("person_gender_fct", "ddbnext.person_gender_fct_", true, false)


    /** The facet name as used by the cortex */
    private String name

    /** The i18n prefix for this facet  */
    private String i18nPrefix = null

    /** Indicates if this facet is used in the item search */
    private boolean isSearchFacet

    /** Indicates that this facet contains facet values and role values (like the affilate_facet_role)*/
    private boolean isMixedFacet

    /**
     * Constructor
     *
     * @param name name of the facet
     * @param isSearchFacet <code>true</code> if this facet is used in the entity search
     */
    private EntityFacetEnum(String name, String i18nPrefix, boolean isSearchFacet, boolean isMixedFacet) {
        this.name = name
        this.i18nPrefix = i18nPrefix
        this.isSearchFacet = isSearchFacet
        this.isMixedFacet = isMixedFacet
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
     * Indicates if this facet is used for item search
     *
     * @return <code>true</code> if this enum is used for item search
     */
    public boolean isSearchFacet() {
        return isSearchFacet
    }

    /**
     * Indicates if this facet contains facet and role values.
     *
     * @return <code>true</code> if this enum is used as mixed facet
     */
    public boolean isMixedFacet() {
        return isMixedFacet
    }

    /**
     * Gets the i18nPrefix for the values in the resource bundles
     * @return the i18nPrefix for the values in the resource bundles
     */
    public String getI18nPrefix() {
        return i18nPrefix
    }

}
