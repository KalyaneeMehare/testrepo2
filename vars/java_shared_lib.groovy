def call(String repoUrl,String gitu,String sonarname,String sonarid,String branch) {
def choice=[]
pipeline {
    agent any
    tools {
    	maven 'My_Maven'
	}
    stages {
        stage('Git Checkout') {
            steps {
                git branch: "${branch}",
		credentialsId: "${gitu}",
                url: "${repoUrl}"
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
             withSonarQubeEnv(credentialsId: 'sonar-pro', installationName: 'MySonar'){
		sh 'mvn verify sonar:sonar -Dsonar.projectKey=sonar-pro1 -Dsonar.login=ee1b7f8c900e2ca73ceb9fe326e396fa07b6774c -Dsonar.host.url=https://sonarcloud.io/'
		sh 'sleep 60'
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
	 
	  stage("renaming and-backing-up-the-jar") {
            steps {
        	sh 'sudo mkdir -p /opt/backup'
       	        sh 'sudo mv target/my-app-1.0-SNAPSHOT.jar target/1.0.${BUILD_NUMBER}.jar'
                sh 'sudo cp target/java-jar.${BUILD_NUMBER}.jar /opt/backup/'
           
		}
	     }
	     stage('Deploy To Environment') {
                steps {
                 sh 'echo Deployment Done'
		}
            }
        }
    }
}

