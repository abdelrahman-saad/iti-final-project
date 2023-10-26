gcloud container clusters get-credentials workload-cluster --zone us-central1 --project iti-final-project-as --internal-ip
gcloud compute ssh management-vm --tunnel-through-iap --project=iti-final-project-as --zone=us-central1 --ssh-flag="-4 -L8888:localhost:8888 -N -q -f"
export HTTPS_PROXY=localhost:8888