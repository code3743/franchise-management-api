# ── General ───────────────────────────────────────────────────────────────────

variable "aws_region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Deployment environment (dev, staging, prod)"
  type        = string
  default     = "dev"

  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "environment must be one of: dev, staging, prod."
  }
}

variable "app_name" {
  description = "Application name used as prefix for all resources"
  type        = string
  default     = "franchise-api"
}

# ── Network ───────────────────────────────────────────────────────────────────

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets (one per AZ)"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets (one per AZ)"
  type        = list(string)
  default     = ["10.0.11.0/24", "10.0.12.0/24"]
}

variable "availability_zones" {
  description = "Availability zones to use"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

# ── ECS / Application ─────────────────────────────────────────────────────────

variable "app_port" {
  description = "Port exposed by the application container"
  type        = number
  default     = 8080
}

variable "app_cpu" {
  description = "CPU units for the ECS task (256 = 0.25 vCPU)"
  type        = number
  default     = 512
}

variable "app_memory" {
  description = "Memory (MB) for the ECS task"
  type        = number
  default     = 1024
}

variable "app_desired_count" {
  description = "Number of ECS tasks to run"
  type        = number
  default     = 1
}

# ── DocumentDB (MongoDB-compatible) ───────────────────────────────────────────

variable "docdb_instance_class" {
  description = "DocumentDB instance class"
  type        = string
  default     = "db.t3.medium"
}

variable "docdb_username" {
  description = "DocumentDB master username"
  type        = string
  default     = "franchiseadmin"
  sensitive   = true
}

variable "docdb_password" {
  description = "DocumentDB master password (min 8 chars)"
  type        = string
  sensitive   = true
}
