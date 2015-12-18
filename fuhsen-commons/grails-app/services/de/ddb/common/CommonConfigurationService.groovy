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

import org.codehaus.groovy.grails.commons.GrailsApplication

import de.ddb.common.exception.ConfigurationException

/**
 * Service for accessing the configuration.
 *
 * @author hla
 */
abstract class CommonConfigurationService {

    /*
     * Methods which have to be defined in derived class
     */

    public abstract String getContextPath()

    /**
     * Return the application base URL with context path and without trailing slash.
     */
    public abstract String getContextUrl()

    protected abstract def getValueFromConfig(String key)

    /*
     * Public methods
     */

    public String getAasAdminPassword() {
        return getConfigValue("ddb.aas.admin.password")
    }

    public String getAasAdminUserId() {
        return getConfigValue("ddb.aas.admin.userid")
    }

    public String getAasUrl() {
        return getOptionalConfigValue("ddb.aas.url")
    }

    public String getAccountPrivacyUrl() {
        return getConfigValue("ddb.account.privacy.url")
    }

    public String getAccountTermsUrl() {
        return getConfigValue("ddb.account.terms.url")
    }

    public String getApisUrl() {
        return getConfigValue("ddb.apis.url")
    }

    /**
     * Get the authorization key to access restricted API calls.
     *
     * This property is optional. Leave it blank if you do not want to set an API key.
     *
     * @return the authorization key
     */
    public String getBackendApikey() {
        return getProperlyTypedConfigValue("ddb.backend.apikey")
    }

    public String getBackendUrl() {
        return getConfigValue("ddb.backend.url")
    }

    public String getBinaryUrl() {
        return getConfigValue("ddb.binary.url")
    }

    public String getConfirmBase() {
        return getContextUrl() + getUserConfirmationBase()
    }

    public String getCreateConfirmationLink() {
        return getConfirmBase() + "?type=create"
    }

    public String getDefaultStaticPage() {
        return getConfigValue("ddb.default.staticPage")
    }

    public String getElasticSearchUrl() {
        return getOptionalConfigValue("ddb.elasticsearch.url")
    }

    public String getEmailUpdateConfirmationLink() {
        return getConfirmBase() + "?type=emailupdate"
    }

    public String getEncoding() {
        return getConfigValue("grails.views.gsp.encoding")
    }

    public List getFacetsFilter() {
        return getConfigValue("ddb.backend.facets.filter", List)
    }

    /*public String getFavoritesReportMailTo() {
        return getConfigValue("ddb.favorites.reportMailTo")
    }*/

    public String getFavoritesSendMailFrom() {
        return getConfigValue("ddb.favorites.sendmailfrom")
    }

    public String getLoggingFolder() {
        return getConfigValue("ddb.logging.folder")
    }

    public String getNonProxyHosts() {
        return getSystemProperty("http.nonProxyHosts")
    }

    public String getPasswordResetConfirmationLink() {
        return getConfirmBase() + "?type=passwordreset"
    }

    public String getPiwikTrackingFile() {
        return getExistingConfigValue("ddb.tracking.piwikfile")
    }

    public String getProxyHost() {
        return getSystemProperty("http.proxyHost")
    }

    public String getProxyPort() {
        return getSystemProperty("http.proxyPort")
    }

    public String getPublicUrl() {
        return getConfigValue("ddb.public.url")
    }

    public String getRegistrationInfoUrl() {
        return getConfigValue("ddb.registration.info.url")
    }

    public int getSearchFieldCount() {
        return getIntegerConfigValue("ddb.advancedSearch.searchFieldCount")
    }

    public int getSearchGroupCount() {
        return getIntegerConfigValue("ddb.advancedSearch.searchGroupCount")
    }

    public int getSearchOffset() {
        return getIntegerConfigValue("ddb.advancedSearch.defaultOffset")
    }

    public int getSearchRows() {
        return getIntegerConfigValue("ddb.advancedSearch.defaultRows")
    }

    public String getUserConfirmationBase() {
        return getConfigValue("ddb.user.confirmationbase")
    }

    public List getSupportedLocales() {
        List retVal = []
        List configLocales = getOptionalConfigValue("ddb.supportedLocales", List)

        configLocales.each {
            def languageVariant = it.split("_")
            retVal.add(new Locale(languageVariant[0], languageVariant[1]))
        }

        if (!retVal) {
            retVal = [Locale.US]
        }

        return retVal
    }

    public Locale getDefaultLanguage() {
        def language = getConfigValue("ddb.defaultLanguage")
        return new Locale(language)
    }

    /**
     * Return the application base URL without context path and without trailing slash.
     */
    public String getSelfBaseUrl() {
        def result = getContextUrl()
        def contextPath = getContextPath()
        if (contextPath?.length() > 0) {
            result = result.substring(0, result.length() - contextPath.length())
        }
        return result
    }

    public int getSessionTimeout() {
        return getIntegerConfigValue("ddb.session.timeout")
    }

    public String getStaticUrl() {
        return getConfigValue("ddb.static.url")
    }

    /**
     * User related services needs the configuration of AAS and Elastic Serach
     * This method checks if these values have been configured.
     *
     * @return <code>true</code> if both urls have been configured
     */
    public boolean isUserServiceConfigured() {
        def retVal = false

        if (getAasUrl() != null && getElasticSearchUrl() != null) {
            retVal = true
        }
        else {
            log.warn "No properties available for user related services"
        }
        return retVal
    }

    public def logConfigurationSettings(GrailsApplication grailsApplication) {
        log.info "------------- application.properties ------------------"
        log.info "app.grails.version = "+grailsApplication.metadata["app.grails.version"]
        log.info "app.name = "+grailsApplication.metadata["app.name"]
        log.info "app.version = "+grailsApplication.metadata["app.version"]
        log.info "build.number = "+grailsApplication.metadata["build.number"]
        log.info "build.id = "+grailsApplication.metadata["build.id"]
        log.info "build.url = "+grailsApplication.metadata["build.url"]
        log.info "build.git.commit = "+grailsApplication.metadata["build.git.commit"]
        log.info "build.bit.branch = "+grailsApplication.metadata["build.bit.branch"]

        log.info "------------- System.properties -----------------------"
        log.info "proxyHost = " + getProxyHost()
        log.info "proxyPort = " + getProxyPort()
        log.info "nonProxyHosts = " + getNonProxyHosts()
        log.info "------------- common properties ---------------------"
        log.info "ddb.aas.admin.userid = " + getAasAdminUserId()
        log.info "ddb.aas.url = " + getAasUrl()
        log.info "ddb.account.privacy.url = " + getAccountPrivacyUrl()
        log.info "ddb.account.terms.url = " + getAccountTermsUrl()
        log.info "ddb.apis.url = " + getApisUrl()
        log.info "ddb.backend.apikey = " + getBackendApikey().substring(0, 5) + "..."
        log.info "ddb.backend.url = " + getBackendUrl()
        log.info "ddb.binary.url = " + getBinaryUrl()
        log.info "getConfirmBase = " + getConfirmBase()
        log.info "getContextUrl = " + getContextUrl()
        log.info "getCreateConfirmationLink = " + getCreateConfirmationLink()
        log.info "ddb.default.staticPage = " + getDefaultStaticPage()
        log.info "ddb.elasticsearch.url = " + getElasticSearchUrl()
        log.info "getEmailUpdateConfirmationLink = " + getEmailUpdateConfirmationLink()
        log.info "grails.views.gsp.encoding = " + getEncoding()
        log.info "ddb.backend.facets.filter = " + getFacetsFilter()
        //log.info "ddb.favorites.reportMailTo = " + getFavoritesReportMailTo()
        log.info "ddb.favorites.sendmailfrom = " + getFavoritesSendMailFrom()
        log.info "ddb.logging.folder = " + getLoggingFolder()
        log.info "getPasswordResetConfirmationLink = " + getPasswordResetConfirmationLink()
        log.info "ddb.public.url = " + getPublicUrl()
        log.info "ddb.registration.info.url = " + getRegistrationInfoUrl()
        log.info "ddb.advancedSearch.searchFieldCount = " + getSearchFieldCount()
        log.info "ddb.advancedSearch.searchGroupCount = " + getSearchGroupCount()
        log.info "ddb.advancedSearch.defaultOffset = " + getSearchOffset()
        log.info "ddb.advancedSearch.defaultRows = " + getSearchRows()
        log.info "getSelfBaseUrl = " + getSelfBaseUrl()
        log.info "ddb.session.timeout = " + getSessionTimeout()
        log.info "ddb.static.url = " + getStaticUrl()
        log.info "ddb.tracking.piwikfile = " + getPiwikTrackingFile()
        log.info "ddb.supportedLocales = " + getSupportedLocales()
        log.info "ddb.defaultLanguage = " + getDefaultLanguage()
    }

    protected def getOptionalConfigValue(String key, Class expectedClass = String, def value = getValueFromConfig(key)) {
        getExistingConfigValue(key, value, false)
        return getProperlyTypedConfigValue(key, expectedClass, value, false)
    }

    protected def getConfigValue(String key, Class expectedClass = String, def value = getValueFromConfig(key)) {
        getExistingConfigValue(key, value, true)
        return getProperlyTypedConfigValue(key, expectedClass, value, true)
    }

    protected def getExistingConfigValue(String key, def value = getValueFromConfig(key), throwException = true) {
        if (value == null && throwException) {
            throw new ConfigurationException("Configuration entry does not exist -> " + key)
        }
        else if (value == null) {
            log.warn "Configuration entry does not exist -> " + key
        }
        return value
    }

    protected Integer getIntegerConfigValue(String key, def value = getValueFromConfig(key)) {
        def searchGroupCount = getExistingConfigValue(key, value)
        return parseIntegerValue(key, searchGroupCount)
    }

    protected def getProperlyTypedConfigValue(String key, Class expectedClass = String,
            def value = getValueFromConfig(key), boolean throwException = true) {
        if (!expectedClass.isAssignableFrom(value.getClass()) && throwException) {
            throw new ConfigurationException(key + " is not a " + expectedClass.getSimpleName())
        }
        else if (!expectedClass.isAssignableFrom(value.getClass())) {
            log.warn key + " is not a " + expectedClass.getSimpleName()
        }
        return value
    }

    protected String getSystemProperty(String key) {
        String propertyValue = System.getProperty(key)
        if (!propertyValue) {
            log.warn "No " + key + " configured -> System.getProperty('" + key +
                    "'). This will most likely lead to problems."
        }
        return propertyValue
    }

    private Integer parseIntegerValue(String key, def value) {
        try {
            return Integer.parseInt(value.toString())
        }
        catch (NumberFormatException e) {
            throw new ConfigurationException(key + " is not an Integer")
        }
    }
}
