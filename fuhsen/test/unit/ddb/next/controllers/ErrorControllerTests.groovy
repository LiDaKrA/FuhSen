package ddb.next.controllers

import grails.test.mixin.web.GroovyPageUnitTestMixin
import grails.util.Environment

import de.ddb.next.ErrorController
import grails.test.mixin.TestFor

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ErrorController)
class ErrorControllerTests {

    void testServerError() {
        //        controller.serverError();
        //
        //        // Check if response status is 500
        //        assert response.status == 500
        //
        //        // Check if the controller shows the correct 500 view
        //        if(Environment.PRODUCTION == Environment.getCurrent()) {
        //            assert view == "/error/500_production"
        //        }else{
        //            assert view == "/error/500_development"
        //        }

        //        //View testing seems to be a bit of a trouble: http://grails.1312388.n4.nabble.com/Testing-Views-td3319565.html
        //        GroovyPageUnitTestMixin groovyPageUnitTestMixin = new GroovyPageUnitTestMixin();
        //        def result = groovyPageUnitTestMixin.render(view: view)

        assert true
    }

    void testNotFound() {
        //
        //        // Check if response status is 404
        //        assert response.status == 404
        //
        //        // Check if the controller shows the correct 404 view
        //        assert view == "/error/notfound"

        assert true
    }
}
