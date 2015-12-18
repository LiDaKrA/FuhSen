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

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

import de.ddb.common.exception.InvalidUrlException
import de.ddb.common.filter.SecurityHelper
import de.ddb.common.filter.ServletRequestWrapper


/**
 * A request filter that ensures that no malicious code (script-tags, etc) comes in over request parameters or cookies.
 * 
 * @author hla
 */
class SecurityFilter implements Filter {

    private SecurityHelper ddbCommonSecurityHelper

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            ServletRequestWrapper requestWrapper = new ServletRequestWrapper(request)
            HttpServletResponse httpResponse = (HttpServletResponse)response

            // Skip check of this static content
            String contextPath = requestWrapper.getContextPath()
            String requestedUri = requestWrapper.getRequestURI()
            if(requestedUri.startsWith(contextPath + "/appStatic") ||
            requestedUri.startsWith(contextPath + "/apis/") ||
            requestedUri.endsWith(".png") ||
            requestedUri.endsWith(".jpg") ||
            requestedUri.endsWith(".jpeg") ||
            requestedUri.endsWith(".gif") ||
            requestedUri.endsWith(".css")){
                chain.doFilter(request, response)
                return
            }

            ddbCommonSecurityHelper.performSecurityTasks(requestWrapper, httpResponse)
            chain.doFilter(requestWrapper, response)
            return
        }catch(InvalidUrlException i){
            response.sendRedirect(request.getContextPath())
            return
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
        log.info "Init SecurityFilter"
        ddbCommonSecurityHelper = new SecurityHelper()
    }
}
