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
 * Enum for the role facets.
 *
 * Roles have the following structure:
 * <Stufe der Hierarchie der Rolle>_<Name der Facette>_<Name der ersten Rollen-Ebene>_..._<Name der n-ten Rollen-Ebene>
 *
 * Examples:
 * "Goethe, Johann Wolfgang von_1_affiliate_fct_involved"
 * "Schiller, Friedrich_1_affiliate_fct_involved"
 *
 *
 * @author boz
 */
public enum RoleFacetEnum {
    AFFILIATE_INVOLVED("affiliate_fct_involved", 1),
    AFFILIATE_SUBJECT("affiliate_fct_subject", 1),
    AFFILIATE_INVOLVED_NORMDATA("affiliate_fct_involved_normdata", 1),
    AFFILIATE_SUBJECT_NORMDATA("affiliate_fct_subject_normdata", 1),

    /** The facet name as used by the cortex */
    private String name

    /** The hierarchical level of the role */
    private int level

    /**
     * Constructor
     *
     * @param name name of the facet
     */
    private RoleFacetEnum(String name, int level) {
        this.name = name
        this.level = level
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
     * Return the hierarchical name of the enum
     *
     * @return the hierarchical name of the enum
     */
    public String getHierarchicalName() {
        return "_" + level + "_" + name
    }

    /**
     * Return the hierarchical level of the enum
     *
     * @return the hierarchical level of the enum
     */
    public int getLevel() {
        return level
    }
}