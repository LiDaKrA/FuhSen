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

import grails.util.Environment
import de.ddb.common.ApiResponse

/**
 * Central controller for handling error situations (404, 500, etc).
 */
class ErrorController {

    def configurationService

    enum Type404 {
        DEFAULT, ITEM_NOT_FOUND, FAVORITELIST_NOT_FOUND, ENTITY_NOT_FOUND, PDF_NOT_FOUND
    }

    def uncaughtException() {
        log.error "uncaughtException(): An uncaught exception occured in the frontend."
        serverError()
    }

    /**
     * Handler method for error 500 situations
     * @return Either the 500_development view or the 500_production view, dependent on the environment
     */
    def serverError() {
        def exceptionMessage = ""

        //Lot of logging to make bugfixing easier
        log.error "serverError(): The user will be redirected to the 500 page."
        if(request?.exception){
            exceptionMessage = request.exception.getMessage()
            try{
                log.error "serverError(): Source of the error is: '"+exceptionMessage+"'"
                def stackElements = request.exception.getStackTrace()
                for(stackElement in stackElements){
                    log.error "Stack: "+stackElement.getClassName()+"."+stackElement.getMethodName()+" ["+stackElement.getLineNumber()+"]"
                }

            }catch(Throwable ex){
                log.error "serverError(): Grails did not provide a valid error object", ex
            }
        }else{
            log.error "serverError(): Grails did not provide an error object. No stacktrace available. Most probably an earlier try-catch already consumed it. Check your log."
        }

        response.status = response.SC_INTERNAL_SERVER_ERROR // Return response code 500
        response.setHeader("Error-Message", exceptionMessage)

        // The content type and encoding of the error page (should be explicitly set, otherwise the mime
        // could be text/json if an API was called and the layout would be messed up
        def contentTypeFromConfig = configurationService.getMimeTypeHtml()
        def encodingFromConfig = configurationService.getEncoding()

        // Return the view dependent on the configured environment (PROD vs DEV)
        if ( Environment.PRODUCTION == Environment.getCurrent() ) {

            // Production: show a nice error message
            log.error "serverError(): Return view '500_production'"
            return render(view:'500_production', contentType: contentTypeFromConfig, encoding: encodingFromConfig)
        } else {
            // Not it production? show an ugly, developer-focused error message
            log.error "serverError(): Return view '500_development'"
            return render(view:'500_development', contentType: contentTypeFromConfig, encoding: encodingFromConfig)
        }
    }

    /**
     * Handler method for error 409 situations
     * @return The conflict view
     */
    def conflict() {

        // Here we have the possibility to add further logging to identify if some 409 urls were called

        def exceptionMessage = ""
        def apiResponse

        // Does it come from a automatically handled backend request?
        if(request?.exception){
            exceptionMessage = request.exception.getMessage()
        }

        apiResponse = request.getAttribute(ApiResponse.REQUEST_ATTRIBUTE_APIRESPONSE)

        response.status = response.SC_CONFLICT // Return response code 409
        response.setHeader("Error-Message", exceptionMessage)

        // The content type and encoding of the error page (should be explicitly set, otherwise the mime
        // could be text/json if an API was called and the layout would be messed up
        def contentTypeFromConfig = configurationService.getMimeTypeHtml()
        def encodingFromConfig = configurationService.getEncoding()

        // Return the view dependent on the configured environment (PROD vs DEV)
        if ( Environment.PRODUCTION == Environment.getCurrent() ) {

            // Return the 409 view
            log.error "notFound(): Return view '409'"
            return render(view:'409_production', contentType: contentTypeFromConfig, encoding: encodingFromConfig)

        } else {

            // Not it production? show an ugly, developer-focused error message
            log.error "notFound(): Return view '409_development'"
            return render(view:'409_development', model: ["error_message": exceptionMessage, "apiResponse": apiResponse], contentType: contentTypeFromConfig, encoding: encodingFromConfig)

        }

    }

    /**
     * Handler method for error 404 situations
     * @return The notfound view
     */
    private def notFound(Type404 type) {

        // Here we have the possibility to add further logging to identify if some 404 urls were called

        def exceptionMessage = ""
        def apiResponse

        // Does it come from a automatically handled backend request?
        if(request?.exception){
            exceptionMessage = request.exception.getMessage()
        }

        apiResponse = request.getAttribute(ApiResponse.REQUEST_ATTRIBUTE_APIRESPONSE)

        response.status = response.SC_NOT_FOUND // Return response code 404
        response.setHeader("Error-Message", exceptionMessage)

        // The content type and encoding of the error page (should be explicitly set, otherwise the mime
        // could be text/json if an API was called and the layout would be messed up
        def contentTypeFromConfig = configurationService.getMimeTypeHtml()
        def encodingFromConfig = configurationService.getEncoding()

        // Return the view dependent on the configured environment (PROD vs DEV)
        if ( Environment.PRODUCTION == Environment.getCurrent() ) {
            // Return the 404 view
            return render(view:'404_production', contentType: contentTypeFromConfig, encoding: encodingFromConfig, model: [type: type])
        } else {
            // Not it production? show an ugly, developer-focused error message
            return render(view:'404_development', model: ["error_message": exceptionMessage, "apiResponse": apiResponse, type: type], contentType: contentTypeFromConfig, encoding: encodingFromConfig)
        }
    }

    def defaultNotFound() {
        notFound(Type404.DEFAULT)
    }

    def favoritelistNotFound() {
        notFound(Type404.FAVORITELIST_NOT_FOUND)
    }

    def itemNotFound() {
        notFound(Type404.ITEM_NOT_FOUND)
    }

    def entityNotFound() {
        notFound(Type404.ENTITY_NOT_FOUND)
    }

    def pdfNotFound() {
        notFound(Type404.PDF_NOT_FOUND)
    }

    /**
     * Handler method for error 401 situations
     * @return The auth view
     */
    def auth() {

        def exceptionMessage = ""
        def apiResponse

        // Does it come from a automatically handled backend request?
        if(request?.exception){
            exceptionMessage = request.exception.getMessage()
        }

        apiResponse = request.getAttribute(ApiResponse.REQUEST_ATTRIBUTE_APIRESPONSE)

        response.status = response.SC_UNAUTHORIZED // Return response code 401
        response.setHeader("Error-Message", exceptionMessage)

        // The content type and encoding of the error page (should be explicitly set, otherwise the mime
        // could be text/json if an API was called and the layout would be messed up
        def contentTypeFromConfig = configurationService.getMimeTypeHtml()
        def encodingFromConfig = configurationService.getEncoding()

        // Return the view dependent on the configured environment (PROD vs DEV)
        if ( Environment.PRODUCTION == Environment.getCurrent() ) {

            // Return the 404 view
            log.error "auth(): Return view '401'"
            return render(view:'401_production', contentType: contentTypeFromConfig, encoding: encodingFromConfig)

        } else {

            // Not it production? show an ugly, developer-focused error message
            log.error "auth(): Return view '401_development'"
            return render(view:'401_development', model: ["error_message": exceptionMessage, "apiResponse": apiResponse], contentType: contentTypeFromConfig, encoding: encodingFromConfig)

        }

    }

    /**
     * Handler method for error 400 situations
     * @return The bad-request view
     */
    def badRequest() {

        def exceptionMessage = ""
        def apiResponse

        // Does it come from a automatically handled backend request?
        if(request?.exception){
            exceptionMessage = request.exception.getMessage()
        }

        apiResponse = request.getAttribute(ApiResponse.REQUEST_ATTRIBUTE_APIRESPONSE)

        response.status = response.SC_BAD_REQUEST // Return response code 400
        response.setHeader("Error-Message", exceptionMessage)

        // The content type and encoding of the error page (should be explicitly set, otherwise the mime
        // could be text/json if an API was called and the layout would be messed up
        def contentTypeFromConfig = configurationService.getMimeTypeHtml()
        def encodingFromConfig = configurationService.getEncoding()

        // Return the view dependent on the configured environment (PROD vs DEV)
        if ( Environment.PRODUCTION == Environment.getCurrent() ) {

            // Return the 400 view
            log.error "badRequest(): Return view '400'"
            return render(view:'400_production', contentType: contentTypeFromConfig, encoding: encodingFromConfig)

        } else {

            // Not it production? show an ugly, developer-focused error message
            log.error "auth(): Return view '400_development'"
            return render(view:'400_development', model: ["error_message": exceptionMessage, "apiResponse": apiResponse], contentType: contentTypeFromConfig, encoding: encodingFromConfig)

        }

    }

}
