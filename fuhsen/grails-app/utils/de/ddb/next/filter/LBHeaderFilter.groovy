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
package de.ddb.next.filter

import grails.util.GrailsWebUtil

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LBHeaderFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request
            HttpServletResponse httpResponse = (HttpServletResponse) response

            def grailsApplication = GrailsWebUtil.lookupApplication(httpRequest.getServletContext())
            def headerName = grailsApplication.config.ddb.loadbalancer.header.name
            def headerValue = grailsApplication.config.ddb.loadbalancer.header.value

            httpResponse.addHeader(headerName, headerValue)
        }catch(Throwable t){
            // Never let any exception pass in a filter, or the application will run into an infinite loop:
            // because the error-page will be called, which causes this filter to be called, which causes
            // this Exception to be thrown, which causes the error-page to be called, ....
            log.error "doFilter(): Critical exception occured in filter", t
        }

        chain.doFilter(request, response)
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }
}
