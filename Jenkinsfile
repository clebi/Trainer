node {
    stage('Checkout') {
        checkout scm
    }
    stage('Clean') {
            sh './gradlew clean'
        }
    stage('Build') {
        sh './gradlew assemble'
        def java = scanForIssues tool: java()
        def kotlin = scanForIssues tool: kotlin()

        publishIssues issues: [java, kotlin], filters: [includePackage('com.clebi.trainer.*')]
    }
    stage('Lint') {
        sh './gradlew lint'
        sh './gradlew ktlintCheck'
        def android = scanForIssues tool: androidLintParser(pattern: '**/app/build/reports/lint-results.xml')
        def ktlint = scanForIssues tool: ktLint(pattern: '**/app/build/reports/ktlint/**/*.xml')

        publishIssues issues: [android, ktlint]
    }
    stage('Tests') {
        sh './gradlew test'
        junit 'app/build/test-results/testReleaseUnitTest/*.xml'
    }
}
