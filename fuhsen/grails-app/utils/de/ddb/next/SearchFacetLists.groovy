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

import de.ddb.common.constants.EntityFacetEnum
import de.ddb.common.constants.FacetEnum


class SearchFacetLists {

    public static final List<FacetEnum> itemSearchNonJavascriptFacetList = [
        FacetEnum.ASSIGNEE_FCT.getName(),
		FacetEnum.ASSIGNEE_COMBINED_FCT.getName(),
        FacetEnum.INVENTOR_FCT.getName(),
		FacetEnum.KEYWORDS_FCT.getName(),
        FacetEnum.IPC_CLASSIFICATION_FCT.getName(),
		FacetEnum.IPC_CLASSIFICATION_FULL.getName(),
		FacetEnum.MANUAL_CODES_FCT.getName(),
        FacetEnum.DOCUMENTTYPE_FCT.getName(),
		FacetEnum.CATEGORY.getName(),
        FacetEnum.PATENTOFFICE_FCT.getName()
    ]


    public static final List<FacetEnum> itemSearchJavascriptFacetList = [
        FacetEnum.ASSIGNEE_FCT.getName(),
		FacetEnum.ASSIGNEE_COMBINED_FCT.getName(),
        FacetEnum.INVENTOR_FCT.getName(),
		FacetEnum.KEYWORDS_FCT.getName(),
        FacetEnum.IPC_CLASSIFICATION_FCT.getName(),
		FacetEnum.IPC_CLASSIFICATION_FULL.getName(),
		FacetEnum.MANUAL_CODES_FCT.getName(),
        FacetEnum.DOCUMENTTYPE_FCT.getName(),
		FacetEnum.CATEGORY.getName(),
        FacetEnum.PATENTOFFICE_FCT.getName()
    ]

    public static final List<FacetEnum> institutionSearchNonJavascriptFacetList = [
        //FacetEnum.SECTOR_FCT.getName(),
        FacetEnum.STATE_FCT.getName()
    ]

    public static final List<FacetEnum> institutionSearchJavascriptFacetList = [
        //FacetEnum.SECTOR_FCT.getName(),
        FacetEnum.STATE_FCT.getName()
    ]


    public static final List<FacetEnum> entitySearchNonJavascriptFacetList = [
        EntityFacetEnum.PERSON_OCCUPATION_FCT.getName(),
        EntityFacetEnum.PERSON_PLACE_FCT.getName(),
        EntityFacetEnum.PERSON_GENDER_FCT.getName()
    ]

    public static final List<FacetEnum> entitySearchJavascriptFacetList = [
        EntityFacetEnum.PERSON_OCCUPATION_FCT.getName(),
        EntityFacetEnum.PERSON_PLACE_FCT.getName(),
        EntityFacetEnum.PERSON_GENDER_FCT.getName()
    ]
	
	//FuhSen
	public static final List<FacetEnum> personSearchNonJavascriptFacetList = [
		"person_gender_fct",
		"person_birthday_fct",
		"person_occupation_fct",
		"person_livesat_fct",
		"person_worksat_fct",
		"person_studiesat_fct"
	]


	public static final List<FacetEnum> personSearchJavascriptFacetList = [
		"person_gender_fct",
		"person_birthday_fct",
		"person_occupation_fct",
		"person_livesat_fct",
		"person_worksat_fct",
		"person_studiesat_fct"
	]
	
	public static final List<FacetEnum> productSearchNonJavascriptFacetList = [
		"product_price_fct",
		"product_country_fct",
		"product_condition_fct"
	]


	public static final List<FacetEnum> productSearchJavascriptFacetList = [
		"product_price_fct",
		"product_country_fct",
		"product_condition_fct"
	]
	
	public static final List<FacetEnum> organizationSearchNonJavascriptFacetList = [
		"organization_country_fct"
	]


	public static final List<FacetEnum> organizationSearchJavascriptFacetList = [
		"organization_country_fct"
	]
	
}
