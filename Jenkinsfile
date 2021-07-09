pipeline {

    agent any
    
    tools { 
        maven 'Maven 3.6.3' 
        jdk 'jdk8' 
    }
    
    environment {
        // registry.gitlab.com/banrep-openshift/springboot-api-example:0.0.3
        appName = "springboot-api-example"
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
                    // Validate if application exists
                    def dc = sh(returnStdout: true, script: "oc get dc -o=jsonpath='{.items[].metadata.name}' | grep  ${appName} > /dev/null && echo ${appName} || echo null").trim()
                     echo "#DeploymentConfig: ${dc}"
                    
                    if (dc.contains("${appName}")) {
                         echo "The app ${appName} already exists"
                        /*
                        sh '''
                              oc import-image
                              oc tag
                             
                           ''' */
                    } else {
                        echo "The app does not exists .........................."
                        throw new Exception("The app ${appName}  does not exists!")
                         /*sh '''
                              oc import-image
                              oc tag
                              oc new-app
                           ''' */
                    }
                
                    // validate number of replicas
                    /*
                    def replicas = sh(returnStdout: true, script: "oc get rc/springboot-api-example-6 -o yaml  | grep -A 5  'status:' |grep 'replicas:' | cut -d ':' -f 2 | sed -n '2p'").trim()
                    echo "#Replicas: ${replicas}"
                    
                    if(replicas.contains("0")) {
                         echo "No hay replicas !!!!!!!"
                        
                    } else {
                       echo "#Replicas mayor a 0"
                    }
                    */
                }
            
            }
        }
    }
    
    post {
        success {
            script {
                echo "success"
            }
        }
        failure {
            script {
                echo "failure"
            }
        }
    }     
}
