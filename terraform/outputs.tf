output "alb_dns_name" {
  description = "Public DNS of the Application Load Balancer"
  value       = aws_lb.main.dns_name
}

output "api_base_url" {
  description = "Base URL to access the API"
  value       = "http://${aws_lb.main.dns_name}"
}

output "swagger_ui_url" {
  description = "Swagger UI URL"
  value       = "http://${aws_lb.main.dns_name}/swagger-ui.html"
}

output "ecr_repository_url" {
  description = "ECR repository URL to push Docker images"
  value       = aws_ecr_repository.app.repository_url
}

output "documentdb_endpoint" {
  description = "DocumentDB cluster endpoint"
  value       = aws_docdb_cluster.main.endpoint
  sensitive   = true
}

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = aws_ecs_cluster.main.name
}

output "cloudwatch_log_group" {
  description = "CloudWatch log group for ECS task logs"
  value       = aws_cloudwatch_log_group.app.name
}
