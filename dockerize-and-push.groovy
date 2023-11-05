pipeline {
    agent any

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    
                    git branch: 'main', url: 'https://github.com/amrhany20/Simon-Game.git'
                }
                
            }
        }
        stage('Build Dockerfile'){
            steps{
                script {
                    sh 'ls'
                    sh 'echo building ...'
                    sh '''
                    cat <<EOF > Dockerfile
                    # Use an official Nginx base image
                    FROM nginx:alpine

                    # Copy your project files into the Nginx web root directory
                    COPY . /usr/share/nginx/html

                    # Expose port 80 for HTTP traffic (Nginx's default port)
                    EXPOSE 80

                    # Start Nginx when the container runs
                    CMD ["nginx", "-g", "daemon off;"]

                    '''
                    sh 'ls'
                    sh 'cat Dockerfile'
                    sh 'docker build -t simon-app:latest .'
                    sh 'docker tag simon-app:latest us-east1-docker.pkg.dev/iti-final-project-as/iti-project-final-as-repo/simon-app:latest'

                }
            }
        }



        stage('Build and Push Docker Image') {
            steps {
                script {
                    // Authenticate with GCP
                    withCredentials([file(credentialsId: 'gcp-secret', variable: 'SECRET_FILE')]) {
                        sh 'gcloud auth activate-service-account --key-file=$SECRET_FILE'
                        sh 'yes | gcloud auth configure-docker us-east1-docker.pkg.dev'
                        sh 'gcloud config set project iti-final-project-as'
                        sh 'gcloud auth configure-docker'
                        sh 'docker push us-east1-docker.pkg.dev/iti-final-project-as/iti-project-final-as-repo/simon-app:latest'
                    }

                    
                }
            }
        }
        
        
        stage('Clone k8s repo files') {
            steps {
                script {
                    
                    git branch: 'main', url: 'https://github.com/abdelrahman-saad/iti-final-project.git'
                }
                sh 'ls k8s/'
            }
        }
        
        stage('Push K8s Files to GKE') {
            steps {
                script {
                     sh '''
                        sudo apt-get install kubectl
                        echo "kubectl installed ..." 
                        export KUBECONFIG=$HOME/.kube/config
                    '''
                    withCredentials([file(credentialsId: 'gcp-secret', variable: 'SECRET_FILE')]) {
                        sh '''
                            gcloud auth activate-service-account --key-file=$SECRET_FILE
                            gcloud container clusters get-credentials workload-cluster --zone us-central1 --project iti-final-project-as
                            
                        '''
                    }
                    
                    sh 'kubectl get nodes'
                    sh 'kubectl apply -f ./k8s'
                    sh 'kubectl get svc'
                }
            }
        }
    }
}
