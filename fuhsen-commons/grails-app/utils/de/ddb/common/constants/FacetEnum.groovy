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
public enum FacetEnum {

    SEARCH_ALL("search_all", null, false, false,"z", null, false),
    TITLE("title", null, false, false,"z", null, false),
    DESCRIPTION("description", null, false, false,"z", null, false),
    PLACE_FCT("place_fct", null, true, false,"z", null, false),
    PLACE("place", null, false, false,"z", null, false),
    AFFILIATE_FCT("affiliate_fct", null, true, false,"z", null, false),
    AFFILIATE("affiliate", null, false, false,"z", null, false),
    AFFILIATE_FCT_ROLE("affiliate_fct_role", null, true, true,"z", null, false),
    AFFILIATE_FCT_ROLE_NORMDATA("affiliate_fct_role_normdata", null, false, false,"z", null, false),
    KEYWORDS_FCT("keywords_fct", null, true, false,"bh", null, false),
    KEYWORDS("keywords", null, false, false,"bh", null, false),
    LANGUAGE_FCT("language_fct", "ddbnext.language_fct_", true, false,"z", null, false),
    LANGUAGE("language", "ddbnext.language_fct_", false, false,"z", null, false),
    TYPE_FCT("type_fct", "ddbnext.type_fct_", true, false,"z", null, false),
    TYPE("type", "ddbnext.type_fct_", true, false,"z", null, false),
    SECTOR_FCT("sector_fct", "ddbnext.sector_fct_", true, false,"z", null, false),
    SECTOR("sector", "ddbnext.sector_fct_", false, false,"z", null, false),
    PROVIDER_FCT("provider_fct", null, true, false,"z", null, false),
    PROVIDER("provider", null, false, false,"z", null, false),
    BEGIN_TIME("begin_time", null, true, false,"z", null, false),
    END_TIME("end_time", null, true, false,"z", null, false),
    PROVIDER_ID("provider_id", null, false, false,"z", null, false),
    LICENSE("license", "ddbnext.license.text.", true, false,"z", null, false),
    LICENSE_GROUP("license_group", "ddbnext.license_group_", true, false,"z", null, false),
    STATE_FCT("state_fct", null, true, false,"z", null, false),
    CATEGORY("category", null, false, false,"z", null, false),
    APD_REFERENCE_NUMBER_FCT("apd_reference_number_fct",null, true, false,"z", null, false),
    APD_REFERENCE_NUMBER("apd_reference_number",null, false, false,"z", null, false),
    APD_DOCUMENT_TYPE_FCT("apd_document_type_fct",null, true, false,"z", null, false),
    APD_DOCUMENT_TYPE("apd_document_type",null, false, false,"z", null, false),
    APD_MATERIAL_FCT("apd_material_fct",null, true, false,"z", null, false),
    APD_MATERIAL("apd_material",null, false, false,"z", null, false),
    APD_LEVEL_OF_DESCRIPTION_FCT("apd_level_of_description_fct", "apd.apd_level_of_description_", true, false,"z", null, false),
    APD_LEVEL_OF_DESCRIPTION("apd_level_of_description", "apd.apd_level_of_description_", true, false,"z", null, false),
    APD_PROVENANCE_FCT("apd_provenance_fct",null, true, false,"z", null, false),
    APD_PROVENANCE("apd_provenance",null, false, false,"z", null, false),
    APD_KEYWORDS_FCT("apd_keywords_fct",null, true, false,"z", null, false),
    DIGITALISAT("digitalisat",null, true, false,"z", null, false),
	//New Facets VWPATENT project
	APPLICATIONYEAR_FCT("applicationYear_fct", null, true, false,"bc", null, true),
	PUBLICATIONYEAR_FCT("publicationYear_fct", null, true, false,"bc", null, true),
	PATENTNUMBER("patentnumber", null, true, false,"b", null, false),
	APPLICATIONYEAR_YEAR("applicationYear_Year", null, true, false,"bc", "alpha_desc", false),
	ASSIGNEE_COMBINED_FCT("assignee_combined_fct", null, true, false,"z", null, false),
	ASSIGNEE_FCT("assignee_fct", null, true, false,"ba", null, true),
	ASSIGNEE("assignee", null, false, false,"ba", null, false),
	CLASSIFICATION_FCT("classification_fct", null, true, false,"z", null, false),
	CLASSIFICATION("classification", null, false, false,"z", null, false),
	IPC_CLASSIFICATION_FCT("ipc_classification_fct", null, true, false,"bd", null, true),
	IPC_CLASSIFICATION("ipc_classification", null, false, false,"bd", null, false),
	IPC_CLASSIFICATION_FULL("ipc_classificationFull", null, false, false,"bd", null, false),
	IPC_CLASSIFICATION_FULL_FCT("ipc_classificationFull_Fct", null, false, false,"bd", null, false),
	PATENTOFFICE_FCT("patentOffice_fct", null, true, false,"bg", null, true),
	PATENTOFFICE("patentOffice", null, false, false,"bg", null, false),
	INVENTOR_FCT("inventor_fct", null, true, false,"bb", null, true),
	INVENTOR("inventor", null, false, false,"bb", null, false),
	MANUAL_CODES_FCT("manual_codes_fct", null, true, false,"be", null, true),
	MANUAL_CODES("manual_codes", null, false, false,"be", null, false),
	DOCUMENTTYPE_FCT("documentType_fct", null, false, false,"bf", null, false),
	DOCUMENTTYPE("documentType", null, false, false,"bf", null, false),
    //TODO Review if context facet has rights parameters
    CONTEXT("context",null,false,true, "z", null, false)

    /** The facet name as used by the cortex */
    private String name

    /** The i18n prefix for this facet  */
    private String i18nPrefix = null

    /** Indicates if this facet is used in the item search */
    private boolean isSearchFacet

    /** Indicates that this facet contains facet values and role values (like the affilate_facet_role)*/
    private boolean isMixedFacet

	/** String used to sort facets for a global display ranking **/
	private String sortIndex;
	
	/** Sets the sort criteria (default: count_desc)**/
	private String sortBy;
	
	/** Triggers display in Infobox of searchresultlist **/
	private boolean showInInfobox;
	
    /**
     * Constructor
     *
     * @param name name of the facet
     * @param isSearchFacet <code>true</code> if this facet is used in the item search
     */
    private FacetEnum(String name, String i18nPrefix, boolean isSearchFacet, boolean isMixedFacet, String sortIndex, String sortBy, boolean showInInfoBox) {
        this.name = name
        this.i18nPrefix = i18nPrefix
        this.isSearchFacet = isSearchFacet
        this.isMixedFacet = isMixedFacet
		this.sortIndex = sortIndex
		this.sortBy = "count_desc"
		if (sortBy != null) {
			this.sortBy = sortBy
		}
		this.showInInfobox = showInInfoBox
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

	public String getSortIndex() { 
		return sortIndex
	}
	
	public String getSortBy() {
		return sortBy
	}
	
	public String isShowInInfoBox() {
		return showInInfobox
	}
	
    /**
     * Create a facet enum.
     *
     * @param name
     *            string value of the facet enum
     *
     * @return FacetEnum
     */
    public static FacetEnum valueOfName(String name) {
        def facet = values().find {it.name == name}
        return facet
    }

    /**
     * Check if the values of an facet must be retrieved via i18n.
     * @param facetName Name of the facet
     * @return <code>true</code> if the values of an facet value must be retrieved via i18n
     */
    public boolean isI18nFacet() {
        return (i18nPrefix != null)
    }
	
	/**
	 * Returns values of enum sorted by Sort index (alphabetically ascending).
	 *  
	 * @return Array of values.
	 */
	public static FacetEnum[] valuesSorted() {
		return values().sort { it.sortIndex }
	} 
}
