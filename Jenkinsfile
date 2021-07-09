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
                script {
                    openshift.withCluster("openshift") {
                        openshift.withProject() {
				// Import image
				echo " Importing Image: oc import-image ${imagename}:${env.BUILD_ID} --from=${finalImageName} --confirm"
				//sh " oc import-image ${imagename}:${env.BUILD_ID} --from=${finalImageName} --confirm "
				def raw  = openshift.raw("import-image ${imagename}:${env.BUILD_ID} --from=${finalImageName} --confirm ")
				echo " Tag: ${raw} "
				
				// tag image
				def tag = openshift.tag("${appName}:${env.BUILD_ID}", "${appName}:latest")
				echo " Tag: ${tag.status} "
				
				def dcExists = openshift.selector("dc", "${appName}").exists() 
				if (dcExists) {
					echo "The app ${appName} exists"
				} else {
					echo "Eror: the app ${appName} doesn't exist!"
					def newApp = openshift.newApp("${appName}:${env.BUILD_ID}", "${appName}:latest")
				}
			}
		    }
                }
            }
        }
    }
}
