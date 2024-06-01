pipeline {
    agent any

    environment {
        registry = "mirasap/post-app:0.0.1"
        registryCredential = 'dockerHub'
        dockerImage = ''
    }

    stages {
        stage('Build and Test') {
            steps {
                script {
                    sh './gradlew clean build'
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                   dockerImage = docker.build registry
                }
            }
        }
        stage('Push image to Docker Hub') {
            steps {
                script {
                    dockerImage.push()
                }
            }
        }
    }
    post {
        success {
            echo 'Successfully built and pushed the Docker image'
        }
    }
}