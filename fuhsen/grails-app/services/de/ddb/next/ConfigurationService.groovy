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

import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import de.ddb.common.CommonConfigurationService

/**
 * Service for accessing the configuration.
 *
 * @author hla
 */
class ConfigurationService extends CommonConfigurationService {

    def grailsApplication
    def LinkGenerator grailsLinkGenerator

    def transactional=false

    /*
     * Abstract methods from super class
     */

    public String getContextPath() {
        return grailsLinkGenerator.contextPath
    }

    /**
     * Return the application base URL with context path and without trailing slash.
     */
    public String getContextUrl(){
        return grailsLinkGenerator.serverBaseURL
    }

    protected def getValueFromConfig(String key) {
        def value = grailsApplication.config
        for (String keyPart : key.split("\\.")) {
            if (!(value instanceof ConfigObject)) {
                value = null
                break
            }
            value = value[keyPart]
        }
        try {
            if (value?.isEmpty()) {
                value = null
            }
        }
        catch (MissingMethodException e) {
        }
        return value
    }

    /*
     * Public methods
     */

    public String getApiKeyDocUrl(){
        return getConfigValue("ddb.apikey.doc.url")
    }

    public String getApiKeyTermsUrl(){
        return getConfigValue("ddb.apikey.terms.url")
    }

    public String getCulturegraphUrl(){
        return getConfigValue("ddb.culturegraph.url")
    }

    public String getDomainCanonic(){
        return getConfigValue("ddb.domain.canonic")
    }

    public String getFooterMenu() {
        return getConfigValue("ddb.footerMenu")
    }

    public String getGrailsMailHost(){
        return getConfigValue("grails.mail.host")
    }

    public int getGrailsMailPort() {
        return getIntegerConfigValue("grails.mail.port")
    }

    public String getLoadbalancerHeaderName(){
        return getConfigValue("ddb.loadbalancer.header.name")
    }

    public String getLoadbalancerHeaderValue(){
        return getConfigValue("ddb.loadbalancer.header.value")
    }

    public String getMimeTypeHtml(){
        return getConfigValue("grails.mime.types['html'][0]", String, grailsApplication.config.grails?.mime?.types["html"][0])
    }

    public String getMainMenu(){
        return getConfigValue("ddb.mainMenu")
    }

    public getSocialIconsFacebookUrl(){
        return getConfigValue("ddb.socialIcons.url.facebook")
    }

    public getSocialIconsTwitterUrl(){
        return getConfigValue("ddb.socialIcons.url.twitter")
    }

    public boolean isCulturegraphFeaturesEnabled() {
        //FIXME dev.escidoc.org and dev.escidoc.org/current shares the same ddb-next.property file
        //      Because we will show the entity features on dev.escidoc.org/current and not on dev.escidoc.org
        //      we will return always true in this method of the develop branch.
        //      Later versions of develop might reactivate the code.

        //        def value = getExistingConfigValue("ddb.culturegraph.features.enabled")
        //        return Boolean.parseBoolean(value.toString())

        return true
    }

    public boolean isEntitySearchFeaturesEnabled() {
        def value = getExistingConfigValue("ddb.search.entities.features.enabled")
        return Boolean.parseBoolean(value.toString())
    }

    public boolean isExhibitionsFeaturesEnabled() {
        def value = getExistingConfigValue("ddb.exhibitions.features.enabled")
        return Boolean.parseBoolean(value.toString())
    }

    public boolean isInstitutionSearchFeaturesEnabled() {
        def value = getExistingConfigValue("ddb.search.institutions.features.enabled")
        return Boolean.parseBoolean(value.toString())
    }

    public boolean isRightsFacetFeaturesEnabled() {
        def value = getExistingConfigValue("ddb.rights.facet.features.enabled")
        return Boolean.parseBoolean(value.toString())
    }

    public def logConfigurationSettings() {
        //Call Common Configuration
        super.logConfigurationSettings(grailsApplication)

        log.info "------------- ddb-next.properties ---------------------"
        log.info "ddb.apikey.doc.url = " + getApiKeyDocUrl()
        log.info "ddb.apikey.terms.url = " + getApiKeyTermsUrl()
        log.info "ddb.culturegraph.url = " + getCulturegraphUrl()
        log.info "ddb.domain.canonic = " + getDomainCanonic()
        log.info "grails.mail.host = " + getGrailsMailHost()
        log.info "grails.mail.port = " + getGrailsMailPort()
        log.info "ddb.loadbalancer.header.name = " + getLoadbalancerHeaderName()
        log.info "ddb.loadbalancer.header.value = " + getLoadbalancerHeaderValue()
        log.info "grails.mime.types['html'][0] = " + getMimeTypeHtml()
        log.info "ddb.culturegraph.features.enabled = " + isCulturegraphFeaturesEnabled()
        log.info "ddb.entities.features.enabled = " + isEntitySearchFeaturesEnabled()
        log.info "ddb.exhibitions.features.enabled = " + isExhibitionsFeaturesEnabled()
        log.info "ddb.footerMenu = " + getFooterMenu()
        log.info "ddb.institutions.features.enabled = " + isInstitutionSearchFeaturesEnabled()
        log.info "ddb.mainMenu = " + getMainMenu()
        log.info "ddb.rights.facet.features.enabled = " + isRightsFacetFeaturesEnabled()

        log.info "-------------------------------------------------------"
    }
}
