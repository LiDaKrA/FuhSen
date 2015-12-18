/*
 * Copyright (C) 2013 FIZ Karlsruhe
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
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    checksums true

    repositories {
        inherits false // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()
    }

    dependencies {
        compile 'org.codehaus.jackson:jackson-core-asl:1.9.13'
        compile 'org.codehaus.jackson:jackson-mapper-asl:1.9.13'
        compile 'commons-lang:commons-lang:2.3'
        compile 'org.ccil.cowan.tagsoup:tagsoup:1.2.1'
        runtime 'org.openid4java:openid4java:0.9.8'
        compile ('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') { excludes "groovy" }
		
		compile ("org.apache.jena:jena-core:2.12.1") {
			excludes 'slf4j-api', 'xercesImpl'
		}
		compile ("org.apache.jena:jena-arq:2.12.1")
		
    }

    plugins {
        compile ":html-cleaner:0.2"
        build(":tomcat:7.0.52.1",
                ":release:2.2.1",
                ":rest:0.8",
                ":rest-client-builder:1.0.3") { export = false }
    }
}
