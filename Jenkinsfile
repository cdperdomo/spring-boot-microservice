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
				def raw  = openshift.raw("import-image ${imagename}:${env.BUILD_ID} --from=${finalImageName} --confirm ")
				echo "Import Image Status: ${raw.status} "
				
				// tag image
				def tag = openshift.tag("${appName}:${env.BUILD_ID}", "${appName}:latest")
				echo " Tag Status: ${tag.status} "
				
				// Finding DeploymentConfig
				def dcExists = openshift.selector("dc", "${appName}").exists() 
				if (dcExists) {
					echo "The app ${appName} exists"
				} else {
					createNewApp(${appName})
			        }
				
				log(${appName})
			}
		    }
                }
            }
        }
    }
}

def log(String appName) {
	echos "LOg............................ " + appName
}

def createNewApp(String appName) {
	// Since the application does not exist we need to create a new app
	echo "The app ${appName} doesn't exist!"

	def dcs = openshift.newApp("--name=${appName}", "--image-stream=${appName}:latest", "--as-deployment-config").narrow('dc')
	def dc = dcs.object()

	// dc is not a Selector -- It is a Groovy Map which models the content of the DC
	// new-app created at the time object() was called. Changes to the model are not
	// reflected back to the API server, but the DC's content is at our fingertips.
	echo "new-app created a ${dc.kind} with name ${dc.metadata.name}"
	echo "The object has labels: ${dc.metadata.labels}"
}
