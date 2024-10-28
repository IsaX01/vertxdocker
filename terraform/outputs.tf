output "kubernetes_cluster_name" {
  description = "cluster-1"
  value       = google_container_cluster.primary.name
}

output "app_service_ip" {
  value = kubernetes_service.app_service.status.load_balancer.ingress[0].ip
}