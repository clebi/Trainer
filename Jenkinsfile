node {
    stage('Checkout') {
        checkout scm
    }
    stage('build') {
        sh './gradlew build'
    }
    stage('lint') {
        sh './gradlew lint'
    }
    stage('tests') {
        sh './gradlew test'
    }
}
