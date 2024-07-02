pipeline {
    agent any

    tools {
        nodejs "nodejs"  // Ensure this matches the name of your NodeJS installation in Jenkins
        gradle "gradle"  // Ensure this matches the name of your Gradle installation in Jenkins
    }

    // This is to add npm to the jenkins environment path
    environment {
        PATH = "/opt/homebrew/bin:$PATH"
    }

    stages {

        stage('Install npm packages') {
            steps {
                dir('todo-service-web') {
                    script {
                        // Ensure npm is installed and the node modules are installed
                        sh 'npm install'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                // Run the Gradle build
                sh './gradlew clean build'
            }
        }
    }

}