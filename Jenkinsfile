pipeline {

    agent any
    
    tools { 
        maven 'Maven 3.6.3' 
        jdk 'jdk8' 
    }
    
    environment {
        // registry.gitlab.com/banrep-openshift/springboot-api-example:0.0.3
        imagename = "banrep-openshift/springboot-api-example"
    }
    
    stages {
        stage ('Clone Git') {
             steps {
                checkout scm
             }
        }
        
        stage ('Maven') {
            steps {
                 sh 'mvn -q -DskipTests=true install' 
            }
        }
        
        
        stage('Build Image') {
            steps {
                echo "Docker imagename: ${imagename}:${env.BUILD_ID}, ${env.imagename}:${env.BUILD_ID}"
                
               script {
                   
                   docker.withRegistry('https://registry.gitlab.com', 'gitlab') {

                        def customImage = docker.build("${imagename}:${env.BUILD_ID}")

                        /* Push the container to the custom Registry */
                        customImage.push()
                    }
                    // Build de Image
                    //def customImage = docker.build("${imagename}:${env.BUILD_ID}")
                    
                   // Push image to private repository
                   //
                }
            }
        }
    }
}
