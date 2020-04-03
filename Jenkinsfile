node {
    stage('Checkout') {
        checkout scm
    }
    stage('build') {
        sh './gradlew assemble'
    }
    stage('lint') {
        sh './gradlew lint'
    }
    stage('tests') {
        sh './gradlew test'
        junit '**/test-results/test/*.xml'
    }
}
