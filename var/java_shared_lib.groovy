def call(String repoUrl,String gitu,String sonarname,String sonarid,String branch) {
def choice=[]
node {
    choice = params["build"].split(",")
}
pipeline {
		agent {
		docker {
			image 'maven:3.8.1-jdk-11'
			args '-v /root/.m2:/root/.m2'
			args '-v /opt/:/opt/'
			}
			}

	
//	parameters {
//        choice(
//            choices: ['true' , 'false'],
//            description: '',
//            name: 'REQUESTED_ACTION')
//    }
// 	environment {          
//         def p = 'params.build'
// 		xyz = p.split(',,');
// 	}	
// 	
// 		
	stages {
			stage("Checkout Code") {
               steps {
		       git branch: "${branch}",
	           credentialsId: "${gitu}",
                    url: "${repoUrl}"
               }
           }
			stage('compile') {
// 				String[] str ;
// 				str = env.{params.build}.split(',');
// 				for ( String str in [ 'compile,build,junit-test,integration-test,sonarqube-integration,packing,renaming-the-jar,backup' ]) {
				//for (compile in ['params.build']) {
				//for ( compile in [xyz] ) {
				when {
					expression {'compile' in choice }
				}
				steps {
				       sh 'mvn compile'
				}
		}
			stage('Build') {
				//for (build in [xyz]) {
				//for ( "compile" in ${str}) {
				when  {
					expression { 'build' in choice }
				}
				steps {
				      sh 'mvn clean install'
			}
		}
                        stage('junit-test') {
				when {
					expression { 'junit-test' in choice }
				}
                                steps {
                                       sh 'mvn test'
                        }
        	        post {
 	               always {
                	    junit 'target/*.xml'
         	      	 }
           	 }
 	    }
			stage('integration-testing') {
				when {
					expression { 'integration-test' in choice }
            }
				steps {
				      sh 'mvn test -Dtest=**/*IT.java'
			}
		}
		stage('sonarqube-integration') {
			when {
                expression { 'sonarqube-integration' in choice }
            }
				steps {
					withSonarQubeEnv(credentialsId: 'sonar-pro', installationName: 'MySonar'){
						sh 'java -jar jacococli.jar report target/jacoco.exec --classfiles target/  --xml target/report.xml'
				       sh 'mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=sonar-pro1 -Dsonar.login=ee1b7f8c900e2ca73ceb9fe326e396fa07b6774c -Dsonar.host.url=https://sonarcloud.io/'
				       sh 'sleep 60'
			}
		}
			}
			stage('package')
                             {
				     when {
                expression { 'package' in choice }
            }
                                steps
                                     {
                                       sh 'mvn package'
                                     }
	                     }
			stage('renaming-the-jar') {
				when {
                expression { 'renaming-the-jar' in choice }
            }
				steps {
				      sh 'mkdir -p /opt/backup'
				      sh 'mv target/sampleapp-1.0.0-SNAPSHOT-LOCAL.jar target/java-jar.${BUILD_NUMBER}.jar'
			}
		}
			stage('backing-up-the-jar') {
				when {
                expression { 'backing-up-the-jar' in choice }
            }
				steps {
				      sh 'cp target/java-jar.${BUILD_NUMBER}.jar /opt/backup/'
			}
		}	
	}
}
}
