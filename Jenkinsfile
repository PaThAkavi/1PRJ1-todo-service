pipeline {
    agent any

    tools {
        gradle "gradle"  // Ensure this matches the name of your Gradle installation in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/PaThAkavi/1PRJ1-todo-service.git'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'  // Use './gradlew' if Gradle Wrapper is included
            }
        }
    }
}