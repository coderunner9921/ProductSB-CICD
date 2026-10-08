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

        stage('Docker Build') {

            steps {

                echo 'Building Docker image...'

                bat 'docker build -t projectfin:1.0 .'

            }

        }

        stage('Docker Compose Deploy') {

            steps {

                echo 'Starting ProjectFin with Docker Compose...'

                bat 'docker compose up -d'

            }

        }

    }

    post {

        always {

            junit 'target/surefire-reports/*.xml'

        }

        success {

            echo 'ProjectFin CI/CD pipeline completed successfully.'

        }

        failure {

            echo 'ProjectFin CI/CD pipeline failed.'

        }

    }
}