grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.war.file = "target/${appName}.war"
grails.server.port.http = 8082
grails.project.fork = [
        // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
        //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

        // configure settings for the test-app JVM, uses the daemon by default
        //test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
        // configure settings for the run-app JVM
        //run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
        // configure settings for the run-war JVM
        //war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
        // configure settings for the Console UI JVM
        //console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
//        excludes 'grails-docs'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        runtime 'mysql:mysql-connector-java:5.1.29'
        compile 'org.imgscalr:imgscalr-lib:4.2'
        compile 'net.sf.uadetector:uadetector-resources:2014.10'
        build "com.lowagie:itext:2.1.7"
        runtime 'org.apache.poi:poi-scratchpad:3.10-FINAL'
        runtime 'org.apache.poi:ooxml-schemas:1.1'
        runtime 'org.apache.poi:poi-ooxml:3.10-FINAL'
        runtime 'com.itextpdf:itextpdf:5.5.4'   // for pdf parsing/generation
        runtime 'com.itextpdf.tool:xmlworker:5.5.5'  // for xhtml to pdf generation
        runtime 'org.jsoup:jsoup:1.7.2'
        runtime 'javax.mail:javax.mail-api:1.5.1'
        runtime 'com.sun.mail:javax.mail:1.5.1'
        runtime 'com.jcraft:jsch:0.1.52'
        compile group: 'com.google.guava', name: 'guava', version: '19.0'
    }

    plugins {
        compile ':spring-security-core:2.0-RC4'
        compile(':jasper:1.10.0')
        compile ':quartz:1.0.2'
        build   ":tomcat:7.0.55"
        compile ":hibernate:3.6.10.18" // or ":hibernate:3.6.10.18"
        runtime ":jquery:1.11.1"
        compile ":mail:1.0.7"
        compile ":rest-client-builder:2.1.1"
        compile ":ic-alendar:0.4.6"
    }
}
