output "kubernetes_cluster_name" {
  description = "cluster-1"
  value       = google_container_cluster.primary.name
}
