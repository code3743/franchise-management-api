# Franchise Management API

REST API reactiva para gestión de franquicias, sucursales y productos.

## Stack

| Tecnología | Uso |
|---|---|
| Java 17 + Spring Boot 3 | Framework base |
| Spring WebFlux + Project Reactor | Programación reactiva no bloqueante |
| MongoDB Reactivo | Persistencia |
| MapStruct + Lombok | Mapeo y reducción de boilerplate |
| SpringDoc OpenAPI | Documentación automática |
| Docker + Docker Compose | Empaquetado y ejecución local |
| Terraform | Infraestructura como código (AWS) |

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=code3743_franchise-management-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=code3743_franchise-management-api)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=code3743_franchise-management-api&metric=coverage)](https://sonarcloud.io/summary/new_code?id=code3743_franchise-management-api)

## Arquitectura

El proyecto sigue **Clean Architecture** con tres capas:

```
domain/          → modelos puros, excepciones, contratos (ports)
application/     → use cases, DTOs, servicios
infrastructure/  → controllers, MongoDB, mappers, configuración
```

La regla de dependencia se respeta estrictamente: cada capa solo conoce las capas interiores, nunca las exteriores.

```
Infrastructure → Application → Domain
```

El dominio no tiene ninguna dependencia de Spring ni de MongoDB.

## Decisiones de diseño

### Recursos planos vs. recursos anidados

Los endpoints de productos usan recursos planos (`/api/products/{id}`) para simplificar el alcance de esta prueba técnica. Los IDs de MongoDB son globalmente únicos, por lo que el contexto padre no es necesario para identificar un recurso.

En un proyecto real, la decisión depende del contexto: si el sistema requiere aislamiento por franquicia o admins con acceso restringido, los recursos anidados (`/franchises/{fId}/branches/{bId}/products/{id}`) son la mejor opción porque el ownership queda explícito en la URL y facilita la validación de autorización.

### PATCH vs. PUT para actualizaciones de nombre y stock

Se usa `PATCH` porque las operaciones modifican **un campo específico**, no el recurso completo. `PUT` semánticamente implica reemplazar la representación entera del recurso, lo cual no corresponde a la intención de estos endpoints (RFC 5789).

La implementación usa `ReactiveMongoTemplate` con `$set` para que la operación en base de datos también sea parcial, evitando sobrescribir campos no relacionados y reduciendo el riesgo de race conditions en escrituras concurrentes.

## Endpoints

| Método | URL | Descripción |
|---|---|---|
| `POST` | `/api/franchises` | Crear franquicia |
| `PATCH` | `/api/franchises/{id}/name` | Actualizar nombre de franquicia |
| `POST` | `/api/branches` | Agregar sucursal a franquicia |
| `PATCH` | `/api/branches/{id}/name` | Actualizar nombre de sucursal |
| `POST` | `/api/products` | Agregar producto a sucursal |
| `DELETE` | `/api/products/{id}` | Eliminar producto |
| `PATCH` | `/api/products/{id}/stock` | Modificar stock |
| `PATCH` | `/api/products/{id}/name` | Actualizar nombre de producto |
| `GET` | `/api/products/top-stock/franchise/{franchiseId}` | Producto con mayor stock por sucursal para una franquicia |

Documentación interactiva disponible en `/swagger-ui.html` una vez levantada la aplicación.

## Ejecución local

### Requisitos
- Docker y Docker Compose

### Levantar todo con un comando

```bash
docker compose up --build
```

Esto levanta MongoDB y la API. La aplicación queda disponible en `http://localhost:8080`.

| URL | Descripción |
|---|---|
| `http://localhost:8080/swagger-ui.html` | Swagger UI |
| `http://localhost:8080/api-docs` | OpenAPI JSON |
| `http://localhost:8080/actuator/health` | Health check |

### Detener

```bash
docker compose down
```

Para eliminar también los datos de MongoDB:

```bash
docker compose down -v
```

## Ejecución con Maven (sin Docker)

Requiere MongoDB corriendo en `localhost:27017`.

```bash
./mvnw spring-boot:run
```

## Tests

```bash
./mvnw test
```

Los tests cubren:
- **Service tests:** lógica de negocio con `StepVerifier` y mocks de repositorios
- **Controller tests:** endpoints HTTP con `WebTestClient` y mocks de use cases

## Infraestructura (Terraform)

El directorio `terraform/` provisiona la infraestructura en AWS:

| Recurso | Descripción |
|---|---|
| VPC + subnets | Red pública (ALB, ECS) y privada (DocumentDB) |
| ECR | Repositorio de imágenes Docker |
| ECS Fargate | Ejecución del contenedor sin gestión de servidores |
| Application Load Balancer | Entrada HTTP pública |
| DocumentDB | Base de datos MongoDB-compatible en subnets privadas |
| CloudWatch | Logs de la aplicación |

### Despliegue

```bash
cd terraform
cp terraform.tfvars.example terraform.tfvars
# Editar terraform.tfvars con las credenciales reales

terraform init
terraform plan
terraform apply
```

Después del primer `apply`, hacer push de la imagen al ECR:

```bash
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin <ecr_url>

docker build -t franchise-api .
docker tag franchise-api:latest <ecr_url>:latest
docker push <ecr_url>:latest
```

La URL pública del API queda en el output `api_base_url`.

## Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/franchise_db` | URI de conexión a MongoDB |
| `SERVER_PORT` | `8080` | Puerto del servidor |
