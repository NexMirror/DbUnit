pipeline {
    agent any 

    stages {
        stage('compile') { 
            steps {
                echo 'Compiling...'
                bat 'mvn -f dbunit/pom.xml -B -e -U clean test-compile' 
            }
        }
        stage('test: unit') { 
            steps {
                echo 'Unit Tests...'
                bat 'mvn -f dbunit/pom.xml -B -e -U test' 
            }
            post {
                always {
                    junit 'dbunit/target/surefire-reports/*.xml' 
                }
            }
        }
        stage('test: integration') { 
            steps {
                echo 'Integration Tests...'
                script {
                    builds = [ 'derby', 'hsqldb', 'h2', 'mysql', 'postgresql', 'oracle-ojdbc8', 'oracle10-ojdbc8', 'mssql41', 'db2'];
                    builds.each {
                        build ->
                            stage('test: integration: ' + build) {
                                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,' + build + ' verify'
                            }
                            post {
                                always {
                                    junit 'dbunit/target/failsafe-reports/*.xml' 
                                }
                            }
                    }
                }
            }
        }
//        stage('deploy: Sonatype OSS') {
//            steps {
//                echo 'Deploying...'
//                bat 'mvn -f dbunit/pom.xml -B -e -U -DskipTests deploy'
//            }
//        }
    }

    post {
        //always {
        //}
        success {
            echo 'Success'
        }
        failure {
            echo 'Failure'
            emailext body: '$DEFAULT_CONTENT', presendScript: '$DEFAULT_PRESEND_SCRIPT', recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider'], [$class: 'FailingTestSuspectsRecipientProvider'], [$class: 'FirstFailingBuildSuspectsRecipientProvider'], [$class: 'UpstreamComitterRecipientProvider']], replyTo: '$DEFAULT_REPLYTO', subject: '$DEFAULT_SUBJECT', to: '$DEFAULT_RECIPIENTS'
        }
        unstable {
            echo 'Unstable'
        }
        changed {
            echo 'Changed'
            emailext body: '$DEFAULT_CONTENT', presendScript: '$DEFAULT_PRESEND_SCRIPT', recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider'], [$class: 'FailingTestSuspectsRecipientProvider'], [$class: 'FirstFailingBuildSuspectsRecipientProvider'], [$class: 'UpstreamComitterRecipientProvider']], replyTo: '$DEFAULT_REPLYTO', subject: '$DEFAULT_SUBJECT', to: '$DEFAULT_RECIPIENTS'
        }
    }
}
