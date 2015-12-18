package de.ddb.next
import org.apache.commons.lang.StringEscapeUtils

class DoHtmlEncodeTagLib {
    static namespace = "ddb"
    /**
     * @author arb
     * Normally the Grails encodeAsHTML() should be used but! for some reason that did not work for me.
     * So here is an alternative taglib to encode HTML
     */
    def doHtmlEncode = { attrs, body ->
        def url = attrs.url
        //log.console (StringEscapeUtils.escapeHtml(url).toString())
        out << StringEscapeUtils.escapeHtml(url).toString()
    }
}

