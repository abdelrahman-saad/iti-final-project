#!/bin/bash

sudo apt-get update -y
sudo apt-get upgrade -y


###################################
# Authenticate with service account
###################################

cd /tmp

wget --header="Metadata-Flavor: Google" -O key.json http://metadata.google.internal/computeMetadata/v1/instance/attributes/service-account-key

#decrypt the key to its json format
cat key.json | base64 -d > sa.json

#Activate service account with key

gcloud auth activate-service-account --key-file=sa.json



#Authenticate to Docker
echo "y" | gcloud auth configure-docker us-east1-docker.pkg.dev


# Login
cat key.json | docker login -u _json_key_base64 --password-stdin \
https://us-eastl-docker.pkg.dev




##########################################
# install tinyproxy to control the cluster
# from localhost & kubectl installation
##########################################

# Deploy the proxy 
sudo apt install tinyproxy -y

# Open the Tinyproxy configuration file with sudo and append 'Allow localhost' to it
sudo sh -c "echo 'Allow localhost' >> /etc/tinyproxy/tinyproxy.conf"

# Restart tinyproxy
sudo service tinyproxy restart

# Install the kubernetes commandline client
sudo apt update

sudo apt-get install kubectl 
sudo apt-get install google-cloud-sdk-gke-gcloud-auth-plugin 

export KUBECONFIG=$HOME/.kube/config
# Get cluster credentials and set kubectl to use internal ip
gcloud container clusters get-credentials workload-cluster --zone us-central1 --project iti-final-project-as --internal-ip

if [[ $? -eq 0 ]]; then
    echo 'Authentication run successfully'
else

    echo "authentication failed"
fi

# Enabling control plane private endpoint global access
gcloud container clusters update workload-cluster --zone us-central1 --enable-master-global-access