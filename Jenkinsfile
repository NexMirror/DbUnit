pipeline {
    agent any 

    environment { 
        MVN_CMD = 'mvn -f dbunit/pom.xml -B -e -U'
    }

    stages {
        stage('compile') { 
            steps {
                echo 'Compiling...'
                bat ${MVN_CMD} 'clean test-compile' 
            }
        }
        stage('test: unit') { 
            steps {
                echo 'Unit Tests...'
                bat ${MVN_CMD} 'test' 
            }
            post {
                always {
                    junit 'dbunit/target/surefire-reports/*.xml' 
                }
            }
        }
        stage('test: integration: derby') { 
            steps {
                echo 'Integration Tests...'
                bat ${MVN_CMD} '-Pit-config,derby verify' 
            }
            post {
                always {
                    junit 'dbunit/target/failsafe-reports/*.xml' 
                }
            }
        }
        stage('test: integration: hsqldb') { 
            steps {
                echo 'Integration Tests...'
                bat ${MVN_CMD} '-Pit-config,hsqldb verify' 
            }
            post {
                always {
                    junit 'dbunit/target/failsafe-reports/*.xml' 
                }
            }
        }
        stage('test: integration: h2') { 
            steps {
                echo 'Integration Tests...'
                bat ${MVN_CMD} '-Pit-config,h2 verify' 
            }
            post {
                always {
                    junit 'dbunit/target/failsafe-reports/*.xml' 
                }
            }
        }
        stage('test: integration: mysql') { 
            steps {
                echo 'Integration Tests...'
                bat ${MVN_CMD} '-Pit-config,mysql verify' 
            }
            post {
                always {
                    junit 'dbunit/target/failsafe-reports/*.xml' 
                }
            }
        }
        stage('test: integration: postgresql') { 
            steps {
                echo 'Integration Tests...'
                bat ${MVN_CMD} '-Pit-config,postgresql verify' 
            }
            post {
                always {
                    junit 'dbunit/target/failsafe-reports/*.xml' 
                }
            }
        }
        stage('test: integration: oracle-ojdbc14') { 
            steps {
                echo 'Integration Tests...'
                bat ${MVN_CMD} '-Pit-config,oracle-ojdbc14 verify' 
            }
            post {
                always {
                    junit 'dbunit/target/failsafe-reports/*.xml' 
                }
            }
        }
        stage('test: integration: oracle-ojdbc6') { 
            steps {
                echo 'Integration Tests...'
                bat ${MVN_CMD} '-Pit-config,oracle-ojdbc6 verify' 
            }
            post {
                always {
                    junit 'dbunit/target/failsafe-reports/*.xml' 
                }
            }
        }
        stage('test: integration: oracle10-ojdbc14') { 
            steps {
                echo 'Integration Tests...'
                bat ${MVN_CMD} '-Pit-config,oracle10-ojdbc14 verify' 
            }
            post {
                always {
                    junit 'dbunit/target/failsafe-reports/*.xml' 
                }
            }
        }
        stage('test: integration: oracle10-ojdbc6') { 
            steps {
                echo 'Integration Tests...'
                bat ${MVN_CMD} '-Pit-config,oracle10-ojdbc6 verify' 
            }
            post {
                always {
                    junit 'dbunit/target/failsafe-reports/*.xml' 
                }
            }
        }
        stage('test: integration: mssql41') { 
            steps {
                echo 'Integration Tests...'
                bat ${MVN_CMD} '-Pit-config,mssql41 verify' 
            }
            post {
                always {
                    junit 'dbunit/target/failsafe-reports/*.xml' 
                }
            }
        }
        stage('test: integration: db2') { 
            steps {
                echo 'Integration Tests...'
                bat ${MVN_CMD} '-Pit-config,db2 verify' 
            }
            post {
                always {
                    junit 'dbunit/target/failsafe-reports/*.xml' 
                }
            }
        }
        stage('deploy: Sonatype OSS') {
            steps {
                echo 'Deploying...'
                bat ${MVN_CMD} '-DskipTests deploy'
            }
        }
    }

    post {
        always {
        }
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
