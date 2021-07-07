pipeline {
    agent any
    
    tools { 
        maven 'Maven 3.6.3' 
        jdk 'jdk8' 
    }
    
    stages {
        stage ('Clone') {
            steps {
                git branch: 'master', url: "https://github.com/jfrog/project-examples.git"
            }
        }
        
        stage ('Maven') {
            steps {
                 sh 'mvn -DskipTests=true install' 
            }
        }
    }
}
