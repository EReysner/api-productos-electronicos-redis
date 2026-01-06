# API REST - Gestión de Productos Electrónicos con Redis Cache

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7.4-red.svg)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

## Descripción

API RESTful para gestión de productos electrónicos (CRUD) con caché Redis para optimizar rendimiento. Incluye validaciones, manejo de excepciones, DTOs, paginación, tests y documentación Swagger. Usa PostgreSQL como base de datos relacional.

**Stack Tecnológico:** Java 21 | Spring Boot 3.4 | PostgreSQL 16 | Redis 7.4 | JPA | Swagger | Docker | JUnit

---

## Características Principales

- **Redis Cache**: Optimización de consultas con TTL de 10 minutos
- **Validaciones**: Bean Validation con mensajes personalizados
- **DTOs**: Separación modelo/presentación con ModelMapper
- **Exception Handling**: Manejo global de errores (@RestControllerAdvice)
- **Swagger/OpenAPI**: Documentación interactiva
- **Paginación**: Soporte para grandes volúmenes de datos
- **Tests**: Cobertura con JUnit 5 + Mockito
- **Docker**: Despliegue completo con docker-compose

**Arquitectura:** Controller → Service (Caché Redis) → Repository → PostgreSQL Database

---

## Ejecutar el Proyecto

```bash
# Levantar toda la infraestructura
docker-compose up -d

# Ver logs de la aplicación
docker-compose logs -f app

# Detener servicios
docker-compose down
```

### URLs Disponibles

- **API REST**: http://localhost:8080/api/products
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Adminer (DB Manager)**: http://localhost:8081
  - Sistema: PostgreSQL
  - Servidor: `postgres`
  - Usuario: `postgres`
  - Contraseña: `postgres`
  - Base de datos: `productdb`
- **PostgreSQL (externo)**: localhost:5432

---

## Endpoints API

| Método | Endpoint | Descripción | Caché |
|--------|----------|-------------|-------|
| `GET` | `/api/products` | Listar todos los productos | Sí (TTL: 10min) |
| `GET` | `/api/products/paginated?page=0&size=10` | Listar con paginación | No |
| `GET` | `/api/products/{id}` | Obtener producto por ID | Sí (TTL: 10min) |
| `POST` | `/api/products` | Crear nuevo producto | Invalida caché |
| `PUT` | `/api/products/{id}` | Actualizar producto existente | Actualiza caché |
| `DELETE` | `/api/products/{id}` | Eliminar producto | Invalida caché |

### Ejemplo: Crear Producto

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mouse Logitech MX Master 3S",
    "description": "Mouse inalámbrico ergonómico de alta precisión",
    "price": 99.99,
    "stock": 25
  }'
```

### Validaciones Implementadas

- **name**: 3-100 caracteres (obligatorio)
- **price**: Mayor a 0 (obligatorio)
- **stock**: Mayor o igual a 0 (obligatorio)
- **description**: Máximo 1000 caracteres (opcional)

---

## Sistema de Caché Redis

### Endpoints con Caché

**Operaciones cacheadas:**
- `GET /api/products` - Lista completa (clave: `allProducts`)
- `GET /api/products/{id}` - Consulta individual (clave: `{id}`)

**Operaciones sin caché:**
- `GET /api/products/paginated` - No se cachea por múltiples combinaciones de parámetros

**Invalidación automática:**
- `POST` - Invalida lista completa
- `PUT` - Actualiza entrada individual + invalida lista
- `DELETE` - Invalida todas las entradas

### Estrategias de Caché

- **@Cacheable**: Consulta caché antes de acceder a la base de datos
- **@CachePut**: Actualiza el caché tras una modificación
- **@CacheEvict**: Invalida el caché al crear o eliminar

### Configuración

- **TTL**: 10 minutos
- **Puerto**: 6379
- **Serialización**: JSON


## Tests

```bash
# Ejecutar tests
mvn test

# Ejecutar con cobertura
mvn clean test
```


