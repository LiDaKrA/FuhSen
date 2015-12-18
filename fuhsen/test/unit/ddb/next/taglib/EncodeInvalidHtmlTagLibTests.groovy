package ddb.next.taglib

import grails.test.mixin.TestFor

import org.junit.Test

import de.ddb.common.EncodeInvalidHtmlTagLib

@TestFor(EncodeInvalidHtmlTagLib)
class EncodeInvalidHtmlTagLibTests {

    @Test
    void textShouldBeEncoded() {
        String text = "before<br /><Izchak><br />after"
        String result = applyTemplate('<ddbcommon:encodeInvalidHtml text="${text}"/>', [text : text]).trim()

        assert result.equals(text.encodeAsHTML())
    }
}
