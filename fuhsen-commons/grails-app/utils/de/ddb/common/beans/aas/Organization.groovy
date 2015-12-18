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
package de.ddb.common.beans.aas

import groovy.transform.ToString
import de.ddb.common.constants.aas.OrganizationStatus

@ToString(includeNames=true)

class Organization {
    String id
    String sector
    List<String> subSector
    String fundingAgency
    String description
    String displayName
    String email
    String telephone
    String fax
    String abbreviation
    String legalStatus
    List<String> url
    OrganizationStatus status
    String pid
    String logo
    String created
    String modified

    String orgParent

    @ToString(includeNames=true)
    class Address {
        String street
        String houseIdentifier
        String addressSupplement
        String postalCode
        String city
        String state
        String country
        Double latitude
        Double longitude
        String locationDisplayName
    }

    Address address = new Address()

    @Override
    public int hashCode() {
        final int prime = 31
        int result = 1
        result = prime * result + ((id == null) ? 0 : id.hashCode())
        return result
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true
        if (obj == null)
            return false
        if (getClass() != obj.getClass())
            return false
        Organization other = (Organization) obj
        if (id == null) {
            if (other.id != null)
                return false
        } else if (!id.equals(other.id))
            return false
        return true
    }
}
