
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

        stage('Docker Hub Publish') {
            steps {
                script {
                    def commitTag = bat(
                        returnStdout: true,
                        script: '@git rev-parse --short=7 HEAD'
                    ).trim()

                    withCredentials([
                        usernamePassword(
                            credentialsId: 'dockerhub-creds',
                            usernameVariable: 'DOCKERHUB_USER',
                            passwordVariable: 'DOCKERHUB_TOKEN'
                        )
                    ]) {
                        try {
                            echo "Publishing image with tag: ${commitTag}"

                            bat '''@powershell -NoProfile -NonInteractive -ExecutionPolicy Bypass -Command "$env:DOCKERHUB_TOKEN | docker login --username $env:DOCKERHUB_USER --password-stdin"'''

                            bat "docker tag projectfin:1.0 %DOCKERHUB_USER%/projectfin:${commitTag}"

                            bat "docker push %DOCKERHUB_USER%/projectfin:${commitTag}"
                        } finally {
                            bat 'docker logout || exit /b 0'
                        }
                    }
                }
            }
        }

        stage('Docker Compose Deploy') {
            steps {
                echo 'Starting ProjectFin with Docker Compose...'
                bat 'docker-compose up -d'
            }
        }

        stage('Verify Deployment') {
            steps {
                echo 'Checking Docker container status...'

                bat 'docker ps'

                bat 'docker inspect -f "{{.State.Status}}" projectfin-app | findstr /I "running"'

                echo 'Checking HTTP response from ProjectFin...'

                bat '''@powershell -NoProfile -NonInteractive -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; for ($i = 1; $i -le 12; $i++) { try { Invoke-WebRequest -Uri 'http://localhost:8081/products' -UseBasicParsing -TimeoutSec 5 | Out-Null; Write-Host 'HTTP check passed'; exit 0 } catch { Write-Host ('HTTP attempt ' + $i + ' failed; retrying'); Start-Sleep -Seconds 5 } }; Write-Error 'HTTP verification failed after 12 attempts'; exit 1"'''
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
