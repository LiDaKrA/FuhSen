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

package de.ddb.common.filter

import java.security.Principal

import javax.servlet.AsyncContext
import javax.servlet.DispatcherType
import javax.servlet.RequestDispatcher
import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.ServletInputStream
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.servlet.http.Part
/**
 * Wrapper class for the HttpServletRequest, because the original request class object does not allow
 * manipulation of the request parameters.
 * 
 * @author hla
 */
class ServletRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> sanitizedParameterMap = new LinkedHashMap<String, String[]>()
    private Map<String, String> sanitizedHeadersMap = new LinkedHashMap<String, String>()
    private HttpServletRequest request

    public ServletRequestWrapper(ServletRequest request) {
        super((HttpServletRequest) request)
        this.request = (HttpServletRequest) request
        this.setParameterMap()
        this.setHeaders()
    }

    private void setParameterMap(){
        Map<String, String[]> parameterMap = this.request.getParameterMap()
        parameterMap.each {
            // https://jira.deutsche-digitale-bibliothek.de/browse/DDBNEXT-1989
            // "lang=" sends Grails into a StackOverflowError
            if (!(it.key == "lang" && it.value?.size() == 1 && it.value[0].empty)) {
                sanitizedParameterMap.put(it.key, it.value)
            }
        }
    }

    private void setHeaders(){
        this.request.getHeaderNames().each {
            sanitizedHeadersMap.put(it, this.request.getHeader(it))
        }
    }

    public Map<String, String[]> getParameterMap() {
        return sanitizedParameterMap
    }

    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(sanitizedParameterMap.keySet())
    }

    public String getParameter(String paramName) {
        return sanitizedParameterMap.get(paramName)
    }

    public String[] getParameterValues(String paramName) {
        return sanitizedParameterMap.values().collect()
    }

    public Cookie[] getCookies() {
        return this.request.getCookies()
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return this.request.authenticate(response)
    }

    @Override
    public String getAuthType() {
        return this.request.getAuthType()
    }

    @Override
    public String getContextPath() {
        return this.request.getContextPath()
    }

    @Override
    public long getDateHeader(String name) {
        return this.request.getDateHeader(name)
    }

    @Override
    public String getHeader(String name) {
        return this.sanitizedHeadersMap.get(name)
    }

    public void setHeader(String name, String value){
        this.sanitizedHeadersMap.put(name, value)
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(this.sanitizedHeadersMap.keySet())
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String lowercaseName = name.toLowerCase()
        ArrayList<String> headers = new ArrayList<String>()
        this.sanitizedHeadersMap.keySet().each {
            if(it.toLowerCase() == lowercaseName){
                headers.add(this.sanitizedHeadersMap.get(name))
            }
        }
        return Collections.enumeration(headers)
    }

    @Override
    public int getIntHeader(String name) {
        return this.request.getIntHeader(name)
    }

    @Override
    public String getMethod() {
        return this.request.getMethod()
    }

    @Override
    public Part getPart(String name) throws IllegalStateException, IOException, ServletException {
        return this.request.getPart(name)
    }

    @Override
    public Collection<Part> getParts() throws IllegalStateException, IOException, ServletException {
        return this.request.getParts()
    }

    @Override
    public String getPathInfo() {
        return this.request.getPathInfo()
    }

    @Override
    public String getPathTranslated() {
        return this.request.getPathTranslated()
    }

    @Override
    public String getQueryString() {
        return this.request.getQueryString()
    }

    @Override
    public String getRemoteUser() {
        return this.request.getRemoteUser()
    }

    @Override
    public String getRequestURI() {
        return this.request.getRequestURI()
    }

    @Override
    public StringBuffer getRequestURL() {
        return this.request.getRequestURL()
    }

    @Override
    public String getRequestedSessionId() {
        return this.request.getRequestedSessionId()
    }

    @Override
    public String getServletPath() {
        return this.request.getServletPath()
    }

    @Override
    public HttpSession getSession() {
        return this.request.getSession()
    }

    @Override
    public HttpSession getSession(boolean create) {
        return this.request.getSession(create)
    }

    @Override
    public Principal getUserPrincipal() {
        return this.request.getUserPrincipal()
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return this.request.isRequestedSessionIdFromCookie()
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return this.request.isRequestedSessionIdFromURL()
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return this.request.isRequestedSessionIdFromUrl()
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return this.request.isRequestedSessionIdValid()
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.request.isUserInRole(role)
    }

    @Override
    public void login(String username, String password) throws ServletException {
        this.request.login(username, password)
    }

    @Override
    public void logout() throws ServletException {
        this.request.logout()
    }

    @Override
    public AsyncContext getAsyncContext() {
        return this.request.getAsyncContext()
    }

    @Override
    public Object getAttribute(String name) {
        return this.request.getAttribute(name)
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return this.request.getAttributeNames()
    }

    @Override
    public String getCharacterEncoding() {
        return this.request.getCharacterEncoding()
    }

    @Override
    public int getContentLength() {
        return this.request.getContentLength()
    }

    @Override
    public String getContentType() {
        return this.request.getContentType()
    }

    @Override
    public DispatcherType getDispatcherType() {
        return this.request.getDispatcherType()
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.request.getInputStream()
    }

    @Override
    public String getLocalAddr() {
        return this.request.getLocalAddr()
    }

    @Override
    public String getLocalName() {
        return this.request.getLocalName()
    }

    @Override
    public int getLocalPort() {
        return this.request.getLocalPort()
    }

    @Override
    public Locale getLocale() {
        return this.request.getLocale()
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return this.request.getLocales()
    }

    @Override
    public String getProtocol() {
        return this.request.getProtocol()
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return this.request.getReader()
    }

    @Override
    public String getRealPath(String path) {
        return this.request.getRealPath(path)
    }

    @Override
    public String getRemoteAddr() {
        return this.request.getRemoteAddr()
    }

    @Override
    public String getRemoteHost() {
        return this.request.getRemoteHost()
    }

    @Override
    public int getRemotePort() {
        return this.request.getRemotePort()
    }

    @Override
    public ServletRequest getRequest() {
        return this.request
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return this.request.getRequestDispatcher(path)
    }

    @Override
    public String getScheme() {
        return this.request.getScheme()
    }

    @Override
    public String getServerName() {
        return this.request.getServerName()
    }

    @Override
    public int getServerPort() {
        return this.request.getServerPort()
    }

    @Override
    public ServletContext getServletContext() {
        return this.request.getServletContext()
    }

    @Override
    public boolean isAsyncStarted() {
        return this.request.isAsyncStarted()
    }

    @Override
    public boolean isAsyncSupported() {
        return this.request.isAsyncSupported()
    }

    @Override
    public boolean isSecure() {
        return this.request.isSecure()
    }

    @Override
    public boolean isWrapperFor(Class wrappedType) {
        return this.request.class == wrappedType
    }

    @Override
    public boolean isWrapperFor(ServletRequest wrapped) {
        return this.request == wrapped
    }

    @Override
    public void removeAttribute(String name) {
        this.request.removeAttribute(name)
    }

    @Override
    public void setAttribute(String name, Object o) {
        this.request.setAttribute(name, o)
    }

    @Override
    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        this.request.setCharacterEncoding(enc)
    }

    @Override
    public void setRequest(ServletRequest request) {
        this.request = request
    }

    @Override
    public AsyncContext startAsync() {
        return this.request.startAsync()
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
    throws IllegalStateException {
        return this.request.startAsync(servletRequest, servletResponse)
    }
}
