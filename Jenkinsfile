pipeline {
    agent any
    
    tools { 
        maven 'Maven 3.6.3' 
        jdk 'jdk8' 
    }
    
    stages {
        stage ('Clone') {
            steps {
                            
               
                sh 'printenv'
                echo "Branch: ${env.GIT_BRANCH}, Git: ${env.GIT_URL}"
                git branch: "${env.GIT_BRANCH}", url: "${env.GIT_URL}"
            }
        }
        
        stage ('Maven') {
            steps {
                 sh 'mvn -DskipTests=true install' 
            }
        }
    }
}
