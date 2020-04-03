node {
    stage('Checkout') {
        checkout scm
    }
    stage('Clean') {
            sh './gradlew clean'
        }
    stage('Build') {
        sh './gradlew assemble'
    }
    stage('Lint') {
        sh './gradlew lint'
    }
    stage('Tests') {
        sh './gradlew test'
        junit 'app/build/test-results/testReleaseUnitTest/*.xml'
    }
}
