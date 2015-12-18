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
package de.ddb.common

import static groovyx.net.http.ContentType.*
import groovy.json.*
import groovyx.net.http.ContentType

import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.util.WebUtils
import org.codehaus.jackson.map.ObjectMapper

import de.ddb.common.beans.aas.AasCredential
import de.ddb.common.beans.aas.Organization
import de.ddb.common.beans.aas.OrganizationTreeObject
import de.ddb.common.beans.aas.Person
import de.ddb.common.beans.aas.Privilege

/**
 * Set of Methods that encapsulate REST-calls to the Organizations endpoint of AASWebService
 *
 * @author boz
 */
class AasOrganizationService {

    def configurationService
    def aasPersonService
    def transactional = false

    private static final log = LogFactory.getLog(this)

    private static final String ORGANIZATION_PATH = "organizations/"
    private static final String PERSON_PATH = "persons/"
    private static final String HIERARCHY_PATH = "hierarchy/"
    private static final String PRIVILEGE_PATH = "privileges/"

    /**
     * Retrieves a Organization with the given id
     * @param id the id of a Organization
     * @param Credentials object of the logged user
     * 
     * @return a Organization with the given id
     */
    public Organization getOrganization(String id, AasCredential cred) {
        Organization organization
        ObjectMapper mapper = new ObjectMapper()

        def optionalHeaders = aasPersonService.getTextAndAuthHeader(cred)

        def apiResponse = ApiConsumer.getText(configurationService.getAasUrl(), ORGANIZATION_PATH + id, false, [:],
        optionalHeaders, false, false, false)
        if (apiResponse.isOk()) {
            def textResponse = apiResponse.getResponse()
            organization = mapper.readValue(textResponse, de.ddb.common.beans.aas.Organization.class)
        }

        return organization
    }

    /**
     * Retrieves all organizations from the aas
     * @param searchQuery
     *
     * @return a OrganizationTreeObject
     */
    public OrganizationTreeObject searchOrganizations(searchQuery=[:], AasCredential cred) {
        return getOrganizationTreeObject(cred, ORGANIZATION_PATH, searchQuery)
    }

    /**
     * Approves an organization with the given organization id
     * @param id the id of the organization
     * @param cred credentials object of the logged in user
     */
    void approveOrganization(String id, AasCredential cred) {
        def apiResponse = ApiConsumer.putJson(
                configurationService.getAasUrl(),
                ORGANIZATION_PATH + id + "/approve",
                false,
                "",
                [:],
                aasPersonService.getUserBasicAuth(cred.getId(), cred.getPassword()))

        if(!apiResponse.isOk()){
            log.error "cannot approve " + id
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
    }

    /**
     * Change the parent organization of the given organization.
     *
     * @param organizationId id of the organization to be changed
     * @param cred credentials object of the logged in user
     * @param parentId id of the new parent organization, may be empty string to only remove the old parent
     */
    void changeParent(String organizationId, AasCredential cred, String parentId) {
        def headers = aasPersonService.getUserBasicAuth(cred.getId(), cred.getPassword())

        headers.put("Accept", ContentType.JSON)

        def apiResponse = ApiConsumer.putAny(configurationService.getAasUrl(), ORGANIZATION_PATH + organizationId +
                "/parent", false, [parentId: parentId], [:], headers)

        if (!apiResponse.isOk()) {
            log.error "cannot change parent to \"" + parentId + "\""
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
    }

    /**
     * Creates a Organization with the given Organization id
     * @param id the id of a Organization
     * @param Credentials object of the logged user
     */
    public void createOrganization(Organization organization, AasCredential cred) {
        ObjectMapper mapper = new ObjectMapper()
        String organizationJSON = mapper.writeValueAsString(organization)
        def apiResponse = ApiConsumer.postJson(configurationService.getAasUrl(), ORGANIZATION_PATH, false,
                new JSONObject(organizationJSON), [:], aasPersonService.getUserBasicAuth(cred.getId(),
                cred.getPassword()))

        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }

        //Get the autogenerated id and determined location from the JSON result
        organization.setId(apiResponse.getResponse().id)
        organization.address.latitude = apiResponse.getResponse().address.latitude
        organization.address.longitude = apiResponse.getResponse().address.longitude
    }

    /**
     * Updates a Organization with the given Organization id
     * @param id the id of a Organization
     * @param Credentials object of the logged user
     */
    public void updateOrganization(Organization organization, AasCredential cred) {
        if (organization) {
            ObjectMapper mapper = new ObjectMapper()
            String organizationJSON = mapper.writeValueAsString(organization)
            def apiResponse = ApiConsumer.putJson(configurationService.getAasUrl(), ORGANIZATION_PATH + organization.id,
                    false, organizationJSON, [:], aasPersonService.getUserBasicAuth(cred.getId(), cred.getPassword()))
            if(!apiResponse.isOk()){
                log.error "Json: Json file was not found"
                apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            }
            organization.address.latitude = apiResponse.getResponse()?.address?.latitude
            organization.address.longitude = apiResponse.getResponse()?.address?.longitude
        }
    }

    /**
     * Deletes a Organization with the given Organization id
     * @param id the id of a Organization
     * @param Credentials object of the logged user
     */
    public void deleteOrganization(String id, AasCredential cred) {
        def apiResponse = ApiConsumer.deleteJson(configurationService.getAasUrl(), ORGANIZATION_PATH + id, false, [:],
        aasPersonService.getUserBasicAuth(cred.getId(), cred.getPassword()))
        if(!apiResponse.isOk()){
            log.error "Json: Json file was not found"
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
    }

    /**
     * Retrieves all organizations as Admin from the aas
     * @param searchQuery
     *
     * @return a  OrganizationTreeObject
     */
    public OrganizationTreeObject searchOrganizationsAsAdmin(searchQuery=[:]) {
        return getOrganizationTreeObject(aasPersonService.getAdminUser(), ORGANIZATION_PATH, searchQuery)
    }

    /**
     * Retrieves a Organization as Admin with the given Organization id
     * @param id the id of a Organization
     *
     * @return a Organization with the given id
     */
    public Organization getOrganizationAsAdmin(String id) {
        return getOrganization(id, aasPersonService.getAdminUser())
    }

    /**
     * Creates a Organization as Admin with the given Organization id
     * @param id the id of a Organization
     */
    public void createOrganizationAsAdmin(Organization organization) {
        createOrganization(organization, aasPersonService.getAdminUser())
    }

    /**
     * Updates a Organization as Admin with the given Organization id
     * @param id the id of a Organization
     */
    public void updateOrganizationAsAdmin(Organization organization) {
        updateOrganization(organization, aasPersonService.getAdminUser())
    }

    /**
     * Withdraws an organization with the given organization id
     * @param id the id of the organization
     * @param cred credentials object of the logged in user
     */
    void withdrawOrganization(String id, AasCredential cred) {
        def apiResponse = ApiConsumer.putJson(
                configurationService.getAasUrl(),
                ORGANIZATION_PATH + id + "/withdraw",
                false,
                "",
                [:],
                aasPersonService.getUserBasicAuth(cred.getId(), cred.getPassword()))

        if(!apiResponse.isOk()){
            log.error "cannot approve " + id
            apiResponse.throwException(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        }
    }

    /**
     * Deletes a Organization as Admin with the given Organization id
     * @param id the id of a Organization
     */
    public void deleteOrganizationAsAdmin(String id) {
        deleteOrganization(id, aasPersonService.getAdminUser())
    }

    /**
     * Puts the parent value on one org.
     * @param id the id of the parent
     * @param id the id of the children
     * @param credentials
     */
    public void setParent(String parentId, String childrenId, AasCredential cred) {
        Organization organization =  getOrganization(childrenId, cred)
        organization.setOrgParent(parentId)
        updateOrganization(organization, cred)
    }

    /**
     * /organizations/<id>/privileges
     * read privileges set of organization
     * @param id the id of the org
     * @param credentials
     *
     * @return a Organization list with the children of the parent
     */

    public List<Privilege> getOrganizationPrivileges(String organizationId, AasCredential cred) {
        ObjectMapper mapper = new ObjectMapper()
        Privilege[] privilegeList

        def optionalHeaders = aasPersonService.getTextAndAuthHeader(cred)

        def apiResponse = ApiConsumer.getText(configurationService.getAasUrl(), ORGANIZATION_PATH + organizationId +
                "/" + PRIVILEGE_PATH , false, [:], optionalHeaders, false, false)
        if(apiResponse.isOk()){
            privilegeList = mapper.readValue(apiResponse.getResponse(), de.ddb.common.beans.aas.Privilege[].class)
        }
        return privilegeList
    }


    /**
     * /organizations/<id>/persons
     * read privileges set of organization
     * @param id the id of the org
     * @param credentials
     *
     * @return a Organization list with the children of the parent
     */
    public List<Person> getOrganizationPersons(String organizationId, AasCredential cred) {
        List<Person> resultList = new ArrayList<Person>()
        ObjectMapper mapper = new ObjectMapper()
        def apiResponse = ApiConsumer.getJson(configurationService.getAasUrl(), ORGANIZATION_PATH + organizationId +
                "/" + PERSON_PATH , false, [:], aasPersonService.getUserBasicAuth(cred.getId(), cred.getPassword()),
                false, false)

        if (apiResponse.isOk()) {
            JSONObject jsonObject = apiResponse.getResponse()
            JSONObject results = jsonObject["results"]

            def persons = results["person"]
            persons.each {
                def personJSON = new JSONObject(it)
                Person person = mapper.readValue(personJSON.toString(), de.ddb.common.beans.aas.Person.class)
                resultList.add(person)
            }
        }

        return resultList
    }

    /**
     * /organizations/<id>/hierarchy
     * read listing of organizations which are parents and children of organization (whole tree)
     * @param id the id of the organization
     * @param credentials
     *
     * @return a Organization tree with the organization hierarchy
     */
    public OrganizationTreeObject getOrganizationHierarchy(String parentId, AasCredential cred) {
        return getOrganizationTreeObject(cred, ORGANIZATION_PATH + parentId + "/" + HIERARCHY_PATH)
    }

    /**
     * parses a tree structure received from the backend
     * @param path of the rest api to get the tree
     * @param credentials
     *
     * @return a Organization tree
     */
    public OrganizationTreeObject getOrganizationTreeObject(AasCredential cred, String path = ORGANIZATION_PATH,
            searchQuery=[:]) {
        ObjectMapper mapper = new ObjectMapper()
        OrganizationTreeObject organizationTreeObject
        def optionalHeaders = aasPersonService.getTextAndAuthHeader(cred)

        def apiResponse = ApiConsumer.getText(configurationService.getAasUrl(), path , false, searchQuery,
                optionalHeaders, false, false, false)

        if (apiResponse.isOk()) {
            def textResponse = apiResponse.getResponse()
            organizationTreeObject = mapper.readValue(textResponse,
                    de.ddb.common.beans.aas.OrganizationTreeObject.class)
        }
        return organizationTreeObject
    }
}