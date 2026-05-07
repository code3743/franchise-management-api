terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # Descomenta para usar S3 como backend remoto en producción
  # backend "s3" {
  #   bucket         = "franchise-api-tfstate"
  #   key            = "franchise-management-api/terraform.tfstate"
  #   region         = "us-east-1"
  #   dynamodb_table = "franchise-api-tfstate-lock"
  #   encrypt        = true
  # }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.app_name
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

locals {
  name_prefix = "${var.app_name}-${var.environment}"
}
