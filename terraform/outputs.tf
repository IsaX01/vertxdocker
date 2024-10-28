output "kubernetes_cluster_name" {
  description = "cluster-2"
  value       = google_container_cluster.primary.name
}
