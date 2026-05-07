# ── Subnet Group ──────────────────────────────────────────────────────────────

resource "aws_docdb_subnet_group" "main" {
  name       = "${local.name_prefix}-docdb-subnet-group"
  subnet_ids = aws_subnet.private[*].id

  tags = { Name = "${local.name_prefix}-docdb-subnet-group" }
}

# ── Cluster ───────────────────────────────────────────────────────────────────

resource "aws_docdb_cluster" "main" {
  cluster_identifier      = "${local.name_prefix}-docdb"
  engine                  = "docdb"
  master_username         = var.docdb_username
  master_password         = var.docdb_password
  db_subnet_group_name    = aws_docdb_subnet_group.main.name
  vpc_security_group_ids  = [aws_security_group.documentdb.id]
  skip_final_snapshot     = var.environment != "prod"
  deletion_protection     = var.environment == "prod"
  storage_encrypted       = true

  tags = { Name = "${local.name_prefix}-docdb" }
}

# ── Instance ──────────────────────────────────────────────────────────────────

resource "aws_docdb_cluster_instance" "main" {
  count              = var.environment == "prod" ? 2 : 1
  identifier         = "${local.name_prefix}-docdb-${count.index + 1}"
  cluster_identifier = aws_docdb_cluster.main.id
  instance_class     = var.docdb_instance_class
}
