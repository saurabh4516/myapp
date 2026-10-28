pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk   'JDK17'
    }

    triggers { githubPush() }

    environment {
        APP_USER = 'ec2-user'
        STAGING_HOST = '<STAGING_EC2_PUBLIC_IP>'
        PROD_HOST    = '<PROD_EC2_PUBLIC_IP>'
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build') {
            steps { sh 'mvn -B clean compile' }
        }

        stage('Test') {
            steps { sh 'mvn -B test' }
            post {
                always { junit 'target/surefire-reports/*.xml' }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy to Staging') {
            when { branch 'develop' }
            steps {
                sshagent(['ec2-ssh-key']) {
                    sh '''
                      scp -o StrictHostKeyChecking=no target/*.jar $APP_USER@$STAGING_HOST:/home/ec2-user/app.jar
                      ssh -o StrictHostKeyChecking=no $APP_USER@$STAGING_HOST "sudo systemctl restart myapp"
                    '''
                }
            }
        }

        stage('Approve Production') {
            when { branch 'main' }
            steps {
                input message: 'Deploy to production?', ok: 'Deploy'
            }
        }

        stage('Deploy to Production') {
            when { branch 'main' }
            steps {
                sshagent(['ec2-ssh-key']) {
                    sh '''
                      scp -o StrictHostKeyChecking=no target/*.jar $APP_USER@$PROD_HOST:/home/ec2-user/app.jar
                      ssh -o StrictHostKeyChecking=no $APP_USER@$PROD_HOST "sudo systemctl restart myapp"
                    '''
                }
            }
        }
    }

    post {
        success { echo 'Pipeline succeeded 🎉' }
        failure { echo 'Pipeline failed. Check the test report and console output.' }
    }
}
