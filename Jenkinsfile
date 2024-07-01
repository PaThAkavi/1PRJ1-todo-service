pipeline {
    agent any

    tools {
        nodejs "nodejs"  // Ensure this matches the name of your NodeJS installation in Jenkins
        gradle "gradle"  // Ensure this matches the name of your Gradle installation in Jenkins
    }

    environment {
        PATH = "/opt/homebrew/bin:$PATH"
    }

    stages {
        stage('Install Node Modules') {
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

    post {
        always {
            archiveArtifacts artifacts: '**/build/libs/*.jar', allowEmptyArchive: true
            junit 'build/test-results/test/*.xml'
        }
        failure {
            mail to: 'pathakavaneesh@gmail.com',
                 subject: "Build failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Check the Jenkins console output for details: ${env.BUILD_URL}"
        }
    }
}