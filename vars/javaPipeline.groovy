def call(Map pipelineParams) {

            properties([
            buildDiscarder(
                    logRotator(
                            artifactDaysToKeepStr: '',
                            artifactNumToKeepStr: '',
                            daysToKeepStr: '',
                            numToKeepStr: '5')
            ),
    ])

    node('master') {
        try {
            checkout scm
            stage('Compile') {
                withMaven(
                        maven: "Maven 3.5.3",
                        jdk: "jdk1.8.0_171") {
                    sh "mvn clean compile"
                }
            }
            stage('Tests') {
                if (env.BRANCH_NAME == 'master') {
                    withMaven(
                            maven: "Maven 3.5.3",
                            jdk: "jdk1.8.0_171") {
                        sh "mvn test"
                    }
                }
            }
            stage('Sonar') {
                echo 'This is sample of stage which will perform static  inspection of code quality by SonarQube'
            }
            stage('Upload to repo') {
                if (env.BRANCH_NAME == 'master') {
                    withMaven(
                            maven: "Maven 3.5.3",
                            jdk: "jdk1.8.0_171") {
                        sh "mvn install -Dmaven.test.skip=true"
                        // in case of real production bild it will be 'mvn deploy -Dmaven.test.skip=true'
                    }
                } else {
                    withMaven(
                            maven: "Maven 3.5.3",
                            jdk: "jdk1.8.0_171") {
                        sh "mvn install -Dmaven.test.skip=true"

                    }
                }
            }
            stage('Deploy to some environment') {
                echo "This is sample of stage which will deliver build artifact to some testing or staging environment"
            }
            currentBuild.result = 'SUCCESS'
        }
        catch (err) {
            currentBuild.result = "FAILURE"
            echo "This is sample of some action on error. Sending mail for example"
        }
    }
}
