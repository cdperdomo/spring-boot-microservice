pipeline {

    agent any
    
    tools { 
        maven 'Maven 3.6.3' 
        jdk 'jdk8' 
    }
    
    environment {
        // registry.gitlab.com/banrep-openshift/springboot-api-example:0.0.3
        imagename = "banrep-openshift/springboot-api-example"
        finalImageName = '';
        OCP_CREDENTIALS = credentials('OCP_TOKEN')
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
        
        
        stage('Build and Push Image') {
            steps {
                echo "Docker imagename: ${imagename}:${env.BUILD_ID}, ${env.imagename}:${env.BUILD_ID}"
                
               script {
                   
                   docker.withRegistry('https://registry.gitlab.com', 'gitlab') {
                        // Build de Image
                        def customImage = docker.build("${imagename}:${env.BUILD_ID}")

                        // Push the image to a private repository
                        customImage.push()
                        
                        // Getting image name
                        finalImageName = customImage.imageName()
                    }
                }
            }
        }
        
        stage('Deploy Openshift') {
            steps {
                echo "Deploying image: ${finalImageName}"
                echo "Credentials: ${OCP_CREDENTIALS_PSW}"
                sh '''
                       oc login --token="${OCP_CREDENTIALS_PSW}" --server=https://api.sandbox-m2.ll9k.p1.openshiftapps.com:6443
                   '''
                script {
                    def replicas = sh(returnStdout: true, script: "oc get rc/springboot-api-example-4 -o yaml  | grep -A 5  'status:' |grep 'replicas:' | cut -d ':' -f 2 | sed -n '2p'").trim()
                    echo "#Replicas: ${replicas}"
                }
            
            }
        }
    }
}
