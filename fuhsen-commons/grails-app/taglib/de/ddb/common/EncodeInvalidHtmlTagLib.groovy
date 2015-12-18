package de.ddb.common

//TODO If it change this taglib to ddb-common uninstall plugin html-cleaner
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

/**
 * @author boz
 */
class EncodeInvalidHtmlTagLib {
    static namespace = "ddbcommon"

    /**
     * Html encode all invalid text
     */
    def encodeInvalidHtml = { attrs, body ->
        def text = attrs.text
        def cleaned = text

        if (! Jsoup.isValid(text.toString(), Whitelist.basic())) {
            cleaned = text.encodeAsHTML()
        }

        out << cleaned
    }
}
