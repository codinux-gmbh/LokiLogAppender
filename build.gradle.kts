
allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    group = "net.codinux.log"
    version = "1.0.0-SNAPSHOT"


    ext["groupId"] = group
    ext["artifactVersion"] = version

    ext["sourceCodeRepositoryBaseUrl"] = "https://github.com/codinux/LokiLogAppender"

    ext["useNewSonatypeRepo"] = true
    ext["packageGroup"] = "net.codinux"

    ext["projectDescription"] = "Logger implementation to push logs to Loki (Like Prometheus, but for logs)"

    ext["developerId"] = "codinux"
    ext["developerName"] = "codinux GmbH & Co. KG"
    ext["developerMail"] = "git@codinux.net"

    ext["licenseName"] = "The Apache License, Version 2.0"
    ext["licenseUrl"] = "http://www.apache.org/licenses/LICENSE-2.0.txt"
}
