pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                bat 'mvnw.cmd clean test'
            }
        }

        stage('Build') {
            steps {
                echo 'Building Spring Boot application...'
                bat 'mvnw.cmd package -DskipTests'
            }
        }
    }

    post {

        always {
            junit 'target/surefire-reports/*.xml'
        }

        success {
            echo 'ProjectFin CI pipeline completed successfully.'
        }

        failure {
            echo 'ProjectFin CI pipeline failed.'
        }
    }
}