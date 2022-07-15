pipeline {
    agent any
    tools {
    	maven 'My_Maven'
	}
    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main',
		credentialsId: '6206ef0d-ec37-41d9-9390-2fe8ac30d231',
		url: 'https://github.com/KalyaneeMehare/testrepo.git'
            }
        }    
        stage('Compile') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }
        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
        }
	stage("build & SonarQube analysis") {
            steps {
              withSonarQubeEnv('MySonar'){
                sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar'
		sh 'mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=sonar-pro -Dsonar.login=3223a9e4899bd80cac62129a6627254edb3cc046'
		}
            }
          }
	   stage("Quality Gate") {
            steps {
              timeout(time: 1, unit: 'HOURS') {
                waitForQualityGate abortPipeline: true
              }
            }
          }
	  stage('Deploy To Environment') {
            steps {
                sh 'echo Deployment Done'
            }
        }
    }
}
	
