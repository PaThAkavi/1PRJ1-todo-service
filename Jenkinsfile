pipeline {
    agent any

    tools {
        gradle "gradle"  // Ensure this matches the name of your Gradle installation in Jenkins
    }

    stages {

        stage('Build') {
            steps {
                sh './gradlew clean build'  // Use './gradlew' if Gradle Wrapper is included
            }
        }

    }
}