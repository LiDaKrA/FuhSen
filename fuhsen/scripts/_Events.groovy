import org.apache.log4j.Logger;

import groovy.util.logging.Log4j;
import groovy.xml.StreamingMarkupBuilder


eventCreateWarStart = { warName, stagingDir ->
    println ""
    println "| Hook WAR creation"
    
    try{
        println "| " + warName    
        println "| " + stagingDir

        System.getProperties().each {
            println "| System.property: " + it
        }
                
        def buildNumber = System.getProperty("build.number", "eclipse")
        def buildId = System.getProperty("build.id", "eclipse")
        def buildUrl = System.getProperty("build.url", "eclipse")
        def buildGitCommit = System.getProperty("build.git.commit", "eclipse")
        def buildGitBranch = System.getProperty("build.git.branch", "eclipse")
        
        println "| buildNumber = " + buildNumber
        println "| buildId = " + buildId
        println "| buildUrl = " + buildUrl
        println "| buildGitCommit = " + buildGitCommit
        println "| buildGitBranch = " + buildGitBranch
        
        ant.propertyfile(file: "${stagingDir}/WEB-INF/classes/application.properties") {
            entry(key:"build.number", value:buildNumber)
            entry(key:"build.id", value:buildId)
            entry(key:"build.url", value:buildUrl)
            entry(key:"build.git.commit", value:buildGitCommit)
            entry(key:"build.git.branch", value:buildGitBranch)
        }
        
    }catch(Exception e){
        e.printStackTrace()
    }
    
    println "| Hook WAR creation finished"
}

eventWebXmlEnd = {String tmpfile ->
    def log = Logger.getLogger(this.getClass());
    
    log.info "Dynamically adjusting web.xml in /scripts/_Events.groovy"
    
    def root = new XmlSlurper().parse(webXmlFile)
    
    log.info "Adding security filter (de.ddb.next.filter.SecurityFilter) to web.xml"
    
    root.appendNode {
        'filter' {
            'filter-name' ('SecurityFilter')
            'filter-class' ('de.ddb.next.filter.SecurityFilter')
        }
    }
    root.appendNode {
        'filter-mapping' {
            'filter-name' ('SecurityFilter')
            'url-pattern' ('/*')
        }
    }

    log.info "Adding Loadbalancer response header filter (de.ddb.next.filter.LBHeaderFilter) to web.xml"
    
    root.appendNode {
        'filter' {
            'filter-name' ('LBHeaderFilter')
            'filter-class' ('de.ddb.next.filter.LBHeaderFilter')
        }
    }
    root.appendNode {
        'filter-mapping' {
            'filter-name' ('LBHeaderFilter')
            'url-pattern' ('/*')
        }
    }

    log.info "Adding session listener (de.ddb.common.listener.SessionListener) to web.xml"
    
    root.appendNode {
        'listener' {
            'listener-class' (
            'de.ddb.common.listener.SessionListener'
            )
        }
    }
    
    /*log.info "Adding mime-mapping (woff -> application/x-font-woff) to web.xml"
    
    root.appendNode {
        'mime-mapping' {
            'extension' (
                'woff'
            )
            'mime-type' (
                'application/x-font-woff'
            )
        }
    }*/
        
    webXmlFile.text = new StreamingMarkupBuilder().bind {
        mkp.declareNamespace("": "http://java.sun.com/xml/ns/javaee")
        mkp.yield(root)
    }
}
