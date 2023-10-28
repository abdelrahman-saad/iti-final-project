resource "google_artifact_registry_repository" "my-repo" {
  location      = var.used_region_1
  repository_id = "iti-project-final-as-repo"
  description   = "Holds docker image pushed from repo"
  format        = "DOCKER"
}