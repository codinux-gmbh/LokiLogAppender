import org.gradle.api.publish.PublishingExtension

apply(plugin = "maven-publish")
apply(plugin = "signing")

// defined in user's global gradle.properties
val ossrhUsername: String? by project
val ossrhPassword: String? by project

// defined in project's gradle.properties
val groupId: String by project
val licenseName: String by project
val licenseUrl: String by project
// optional properties
val orgId: String? by project
val orgName: String? by project
val orgUrl: String? by project
val developerName: String? by project
val developerId: String? by project

val artifactId: String by extra
val artifactVersion: String by extra
val libraryName: String by extra
val projectDescription: String by extra
val sourceCodeRepositoryBaseUrl: String by extra

project.group = groupId
project.version = artifactVersion


println("groupId = $groupId, artifactId = $artifactId") // TODO: remove again


tasks {
    create<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
//        dependsOn(dokkaHtml)
//        from(dokkaHtml.get().outputDirectory)
    }
}

configure<PublishingExtension> {
  val publishing = this
  publications {
    withType<MavenPublication> {
      groupId = groupId
      artifactId = artifactId
      version = artifactVersion

      if (name == "jvm") {
        artifact(tasks["javadocJar"])
//        artifact(tasks["jvmSourcesJar"])
      }

      println("Publishing ($name, $libraryName) to ${groupId}:${artifactId}:$artifactVersion")

      pom {
        name.set(libraryName)
        description.set(projectDescription)
        url.set(sourceCodeRepositoryBaseUrl)

        licenses {
          license {
            name.set(licenseName)
            url.set(licenseUrl)
          }
        }
        scm {
          url.set(sourceCodeRepositoryBaseUrl)
        }
        developers {
          if (!developerId.isNullOrEmpty()) {
            developer {
              id.set(developerId)
              name.set(developerName)
            }
          }
          if (!orgId.isNullOrEmpty()) {
            developer {
              id.set(orgId)
              name.set(orgName)
              organization.set(orgName)
              organizationUrl.set(orgUrl)
            }
          }
        }
        if (!orgName.isNullOrEmpty()) {
          organization {
            name.set(orgName)
            if (!orgUrl.isNullOrEmpty())
              url.set(orgUrl)
          }
        }
      }
    }
  }

  repositories {
    maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
      credentials {
        username = ossrhUsername
        password = ossrhPassword
      }
    }
  }

  configure<SigningExtension> {
    sign(publishing.publications)
  }
}
