package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.Ontrack

class BuildProperties extends ProjectEntityProperties {

    BuildProperties(Ontrack ontrack, Build build) {
        super(ontrack, build)
    }

    /**
     * Sets the Jenkins build property on a build
     */
    def jenkinsBuild(String configuration, String job, int buildNumber) {
        property(
                'net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType',
                [
                        configuration: configuration,
                        job          : job,
                        build        : buildNumber
                ]
        )
    }

    /**
     * Gets the Jenkins build property
     */
    def getJenkinsBuild() {
        property('net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType')
    }

}