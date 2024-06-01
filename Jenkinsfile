pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                script {
                    checkout scm
                }
            }
        }
        stage('Build and Test') {
            steps {
                script {
                    sh '/gradlew clean build'
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build --platform=linux/amd64 -t mirasap/post-app:latest .'
                }
            }
        }
        stage('Push image to Docker Hub') {
            steps {
                script {
                    sh 'docker push mirasap/post-app:latest'
                }
            }
        }
    }
}