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
    stage('Analysis') {
        def java = scanForIssues tool: java()
        def kotlin = scanForIssues tool: kotlin()
        def android = scanForIssues tool: androidLintParser()

        publishIssues issues: [java, kotlin, android], filters: [includePackage('com.clebi.trainer.*')]
    }
}
