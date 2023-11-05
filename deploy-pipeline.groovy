pipeline {
    agent any // You can specify a specific agent label or node here
    parameters {
        choice(name: 'ACTION', choices: ['deploy', 'delete'], description: 'Choose whether to deploy or delete')
    }
    stages {
        
        stage('Fetch from GitHub') {
            steps {
                script {
                    
                    git branch: 'main', url: 'https://github.com/abdelrahman-saad/iti-final-project.git'
                }
            }
        }
        stage('Deploy Terraform Infra') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'gcp-secret', variable: 'SECRET_FILE')]) {
                    def action = params.ACTION
                    if (action == "deploy"){
                        sh '''
                            ls
                            mkdir -p secrets
                            cat $SECRET_FILE > secrets/iti-final-project-as.json
                            echo "nth to see"
                            cd Terraform
                            terraform init
                            terraform apply -auto-approve
                            
                        '''
                    } else if (action == 'delete') {
                        sh '''
                            cd Terraform
                            terraform destroy -auto-approve
                        '''
                    }
                }
                }
            }
        }
    }
    post {
        success {
            echo 'infrastrucutre deployed and need the other pipeline'
            build(job: 'build-and-push-image', propagate: false)
            
        }
        failure {
            echo 'The pipeline run failed'
        }
    }

}
