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
	 stage('Building') {
            steps {
                sh 'mvn clean install'
            }
        } 
     
	stage('Sonar Analysis') {
		environment {
		SCANNER_HOME = tool 'Mysonar'
		PROJECT_NAME = "sonar-pro"
		}
	    steps {
	       withSonarQubeEnv('Mysonar') {
		sh '''$SCANNER_HOME/bin/sonar-scanner  -Dsonar.java.binaries=build/classes/java/ -Dsonar.projectKey=$PROJECT_NAME -Dsonar.sources=.'''
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
	
