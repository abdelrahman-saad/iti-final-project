provider "google" {
  credentials = file("../secrets/iti-final-project-as.json")
  project     = var.project_id
}