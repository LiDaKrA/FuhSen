package de.ddb.common

import org.jsoup.safety.Whitelist
import org.jsoup.Jsoup
/**
 * 
 * @author arb
 * Clean all the nondesired tags from a string! 
 * Inspired by strip_tags function of PHP.
 * usage: <ddbcommon:stripTags text="${text}" allowedTags="p,a,body,title,img" />
 *
 */
class StrStripTagsTagLib {
    static namespace = "ddbcommon"

    def stripTags = { attrs, body ->
        def text = attrs.text
        def allowedTags= attrs.allowedTags
        Whitelist whitelist = new Whitelist().none()
        if (allowedTags) {
            String[] tag_list = allowedTags.split(",")
            tag_list.each {
                whitelist.addTags(it)
            }
        }
       out << Jsoup.clean(text,whitelist)
    }
}
