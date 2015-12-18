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

/* Search namespace  */
de.ddb.next.search = de.ddb.next.search || {};

de.ddb.next.search.getLocalizedFacetValue = function(facetField, facetValue) {

  if (facetField === 'affiliate_fct_role' || facetField === 'keywords_fct' || facetField === 'place_fct' || facetField === 'provider_fct' || facetField === 'state_fct') {
    return facetValue.toString();
  }
  else if (facetField === 'person_name_fct' || facetField === 'person_place_fct' || facetField === 'person_occupation_fct') {
    return facetValue.toString();
  }
  else if (facetField === 'person_gender_fct') {
    return messages.ddbnext['person_gender_fct_' + facetValue];
  }
  else if (facetField === 'type_fct') {
    return messages.ddbnext['type_fct_' + facetValue];
  }
  else if (facetField === 'language_fct') {
    return messages.ddbnext['language_fct_' + facetValue];
  }
  else if (facetField === 'sector_fct') {
    return messages.ddbnext['sector_fct_' + facetValue];
  }
  else if (facetField === 'license_group') {
    return messages.ddbnext['license_group_' + facetValue];
  }
  else if (facetField === 'license') {
    //As the language keys for licenses are urls they must be also transformable to valid javascript variable names. So the license keys are url encoded
    var encodedFacetValue = encodeURIComponent(facetValue);
    encodedFacetValue = encodedFacetValue.replace(/\./g,'%2E');//the dot character must be manually URL encoded!
    return messages.ddbnext['license_' + encodedFacetValue];
  }
  return facetValue.toString();
};

de.ddb.next.search.getLocalizedFacetField = function(facetField) {
  return messages.ddbnext['facet_' + facetField];
};