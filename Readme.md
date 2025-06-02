# Xopix E-commerce: Product Service

The **Xopix Product Service** is a core microservice responsible for managing the entire product catalog for the Xopix E-commerce platform. It provides APIs for creating, retrieving (individually and in lists), updating, and deleting product information. This service is designed for both public Browse (listing products) and administrative product management.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [API Endpoints](#api-endpoints)
- [Local Development Setup](#local-development-setup)
- [Database Migrations (Flyway)](#database-migrations-flyway)
- [Contributing](#contributing)
- [License](#license)

## Features

- Product creation with unique SKU
- Product retrieval by ID
- Paginated listing of all products with sorting options
- Product details update
- Product deletion
- Secure product management endpoints (requires Admin role)
- Robust error handling (e.g., product not found, SKU already exists)

## Architecture

This service is part of the larger Xopix E-commerce microservices architecture.

- **Data Ownership:** Owns its data in a dedicated MySQL database (`xopix_product_db`).
- **API Gateway:** Exposed via the Kong API Gateway (e.g., `api.xopix.com/products`).
- **Authentication/Authorization:** Relies on Auth0 (via Kong API Gateway) for user authentication. Admin-specific endpoints are secured using Spring Security's `@PreAuthorize`, assuming role information is passed securely by the API Gateway.
- **Service Mesh:** Operates within the Service Mesh (Envoy), benefiting from mTLS, advanced traffic management, and enhanced observability for inter-service communication.
- **Future Integration:** In later phases, this service will publish product lifecycle events (e.g., `ProductCreated`, `ProductUpdated`) to Kafka for consumption by services like the Product Search Service.

For a detailed overview of the entire Xopix architecture, please refer to the main [Xopix E-commerce: Architectural Goals & Implementation Plan](../docs/ARCHITECTURE_PLAN.md) (adjust path if your docs are in a different repo/folder).

## Technologies

- **Language:** Java 17+
- **Framework:** Spring Boot 3.2.x
- **Database:** MySQL
- **ORM:** Spring Data JPA / Hibernate
- **Database Migrations:** Flyway
- **Build Tool:** Maven
- **Dependency Management:** Lombok
- **Security:** Spring Security (for role-based method authorization)

## API Endpoints

The Product Service exposes RESTful APIs. For the full API specification, refer to `openapi.yaml` in the `docs/api` folder of this repository.

| Method   | Endpoint                          | Description                                      | Authentication |
| -------- | --------------------------------- | ------------------------------------------------ | -------------- |
| `GET`    | `/api/products`                   | Get a paginated list of all products.            | None           |
| `GET`    | `/api/products/{productId}`       | Retrieve product details by ID.                  | None           |
| `POST`   | `/api/products`                   | Create a new product.                            | JWT (Admin)    |
| `PUT`    | `/api/products/{productId}`       | Update an existing product's details.            | JWT (Admin)    |
| `DELETE` | `/api/products/{productId}`       | Delete a product by ID.                          | JWT (Admin)    |

### Authentication/Authorization Note:
Public `GET` endpoints are unsecured. For `POST`, `PUT`, `DELETE` operations, the service expects an authenticated request with the "ADMIN" role. The primary JWT validation and role extraction happen at the **Kong API Gateway** (integrated with Auth0), which then securely passes role information to the Product Service.

## Local Development Setup

To run the Product Service locally:

1.  **Prerequisites:**
    * Java 17+ SDK installed.
    * Maven 3.x installed.
    * MySQL Server (8.x recommended) running locally.
    * (Optional but Recommended) Docker and Docker Compose for easy MySQL setup.

2.  **Database Setup (using Docker Compose for MySQL):**
    If you're using a shared `docker-compose.yml` (recommended for Xopix microservices), add a new service for `mysql_product_db`:
    ```yaml
    version: '3.8'
    services:
      mysql_product_db:
        image: mysql:8.0
        environment:
          MYSQL_ROOT_PASSWORD: password # Change to a strong password in prod
          MYSQL_DATABASE: xopix_product_db
        ports:
          - "3307:3306" # Use a different host port if 3306 is taken by user-service's DB
        volumes:
          - mysql_product_data:/var/lib/mysql
        healthcheck:
          test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
          timeout: 20s
          retries: 10
    volumes:
      mysql_product_data:
    ```
    Start the MySQL container (if not already running other services' DBs):
    ```bash
    docker-compose up -d mysql_product_db
    ```
    **Important:** If `user-service` is also running a MySQL on 3306, adjust the `ports` for `mysql_product_db` to `3307:3306` and update `spring.datasource.url` in `application.properties` to `jdbc:mysql://localhost:3307/xopix_product_db`.

3.  **Configure `application.properties`:**
    Ensure your `src/main/resources/application.properties` matches your local MySQL setup.

4.  **Run Flyway Migrations:**
    Flyway migrations will run automatically when Spring Boot starts if `spring.flyway.enabled=true`. Ensure your `db/migration` folder (inside `src/main/resources`) contains the `V1__Create_products_table.sql` script.

5.  **Build and Run the Application:**
    Navigate to the `product-service` project root in your terminal and run:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
    The service should start on `http://localhost:8081`.

6.  **Test Endpoints (e.g., using curl or Postman):**

    * **Create Product (requires authentication - simulate ADMIN role if testing directly):**
        ```bash
        curl -X POST http://localhost:8081/api/products \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer <ADMIN_JWT_TOKEN>" \ # Replace with a valid JWT with 'ADMIN' role/scope for testing
        -d '{
            "name": "Xopix Smartwatch",
            "description": "A stylish smartwatch with health tracking.",
            "price": 199.99,
            "sku": "XW-SW-001",
            "category": "Wearables",
            "imageUrl": "[https://images.xopix.com/smartwatch.jpg](https://images.xopix.com/smartwatch.jpg)",
            "stockQuantity": 50
        }'
        ```
    * **Get All Products:**
        ```bash
        curl http://localhost:8081/api/products?page=0&size=10&sortBy=name&sortDir=asc
        ```
    * **Get Product by ID (replace `{productId}` with an actual ID):**
        ```bash
        curl http://localhost:8081/api/products/{productId}
        ```