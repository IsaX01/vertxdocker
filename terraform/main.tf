provider "google" {
    project = var.project
    region = var.region
    zone = var.zone
}

resource "google_container_cluster" "primary" {
  name     = "cluster-2"
  location = var.zone
  deletion_protection = false

  initial_node_count = 1

  node_config {
    machine_type = "e2-micro"
  }
}

data "google_client_config" "default" {}

# Configurar el proveedor de Kubernetes
provider "kubernetes" {
  host                   = google_container_cluster.primary.endpoint
  token                  = data.google_client_config.default.access_token
  cluster_ca_certificate = base64decode(google_container_cluster.primary.master_auth.0.cluster_ca_certificate)
}

# Desplegar la aplicación en Kubernetes
resource "kubernetes_deployment" "app" {
  metadata {
    name = "app-deployment"
  }
  spec {
    replicas = 2
    selector {
      match_labels = {
        app = "my-app"
      }
    }
    template {
      metadata {
        labels = {
          app = "my-app"
        }
      }
      spec {
        container {
          image = "gcr.io/${var.project}/app:latest"
          name  = "app-container"
          port {
            container_port = 8080
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "app_service" {
  metadata {
    name = "app-service"
  }
  spec {
    selector = {
      app = "my-app"
    }
    type = "LoadBalancer"
    port {
      port        = 80
      target_port = 8080
    }
  }
}

terraform {
  backend "gcs" {
    bucket  = "terraform-state-isax01"
    prefix  = "terraform/state"
  }
}