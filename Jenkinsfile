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
        stage('test: integration: derby') { 
            steps {
                echo 'Integration Tests...'
                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,derby verify' 
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
                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,hsqldb verify' 
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
                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,h2 verify' 
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
                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,mysql verify' 
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
                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,postgresql verify' 
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
                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,oracle-ojdbc14 verify' 
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
                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,oracle-ojdbc6 verify' 
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
                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,oracle10-ojdbc14 verify' 
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
                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,oracle10-ojdbc6 verify' 
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
                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,mssql41 verify' 
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
                bat 'mvn -f dbunit/pom.xml -B -e -U -Pit-config,db2 verify' 
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
                bat 'mvn -f dbunit/pom.xml -B -e -U -DskipTests deploy'
            }
        }
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
