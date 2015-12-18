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

import grails.util.Environment

grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

def environment = Environment.getCurrent()

def localDdbCommonFound = false
if (environment == Environment.DEVELOPMENT) {

    if (!new File("../fuhsen-commons").exists()){
        println "-> Local version of fuhsen-common not found under path ../fuhsen-commons\n\r A "
    }else{
        println "| Using local version of fuhsen common plugin"
        localDdbCommonFound=true
    }

    grails.plugin.location.'ddb-common' = "../fuhsen-commons"
}

grails.project.fork = [
    // configure settings for the test-app JVM, uses the daemon by default
    test: false,
    // configure settings for the run-app JVM
    //run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    run: false,
    // configure settings for the run-war JVM
    //war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    war: false,
    // configure settings for the Console UI JVM
    //console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256],
    console: false,
]

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenLocal()
        mavenCentral()

        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        // mavenRepo "http://snapshots.repository.codehaus.org"
        // mavenRepo "http://repository.codehaus.org"
        // mavenRepo "http://download.java.net/maven/2/"
        // mavenRepo "http://repository.jboss.com/maven2/"

        // This are the geotools repositories required for coordinate transformation
        mavenRepo "http://repo.boundlessgeo.com/main/"
        mavenRepo "http://download.osgeo.org/webdav/geotools/"

        mavenRepo "https://www.escidoc.org/artifactory/repo/"
    }

    dependencies {
        runtime 'org.ccil.cowan.tagsoup:tagsoup:1.2.1'
        runtime 'org.openid4java:openid4java:0.9.8'
        //runtime 'org.springframework:spring-test:3.2.8.RELEASE'
        runtime ('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') { excludes "groovy" }

        // This are the geotools dependencies required for coordinate transformation
        //runtime 'org.geotools:gt-referencing:10.2'
        //runtime 'org.geotools:gt-geometry:10.2'
        //runtime 'org.geotools:gt-epsg-hsql:10.2'
        //runtime 'org.geotools:gt-epsg-extension:10.2'
		
		compile ("org.apache.jena:jena-core:2.12.1") {
			excludes 'slf4j-api', 'xercesImpl'
		}
		compile ("org.apache.jena:jena-arq:2.12.1")
		
    }

    plugins {
        compile ':cache:1.1.7'
        compile ':cache-ehcache:1.0.1'
        compile ':hibernate:3.6.10.12'
        compile ":html-cleaner:0.2"
        compile ":jawr:3.3.3"
        compile ":rendering:0.4.4"
        compile ":cache-headers:1.1.5"
        //compile ":mail:1.0.1"
        compile ":rest:0.7"
        compile ":message-digest:1.1"
		
		//compile ("org.apache.jena:apache-jena:2.12.1") //{
		//	excludes "xml-apis", "commons-logging", "slf4j-log4j12"
		//}
		
		//compile ("org.apache.jena:jena-core:2.12.1")
		//compile ("org.apache.jena:jena-arq:2.12.1")
		

        build ":tomcat:7.0.52.1"
        runtime ":resources:1.2.7"
        runtime ":zipped-resources:1.0"
        runtime ":cached-resources:1.0"
        runtime ":compress:0.4"

        //if ((environment != Environment.DEVELOPMENT)|| (!localDdbCommonFound))  {
        //    println "Using maven repo for common plugin"
        //    compile "de.ddb:ddb-common:0.15-SNAPSHOT"
        //}
    }

	/*
    // don't put Selenium tests into war file
    grails.war.resources = {stagingDir, args ->
        delete(dir: "${stagingDir}/selenium")
    }*/

}
