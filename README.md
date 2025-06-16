# Pocket Ledger API

A simple, thread-safe ledger system built with Java 21 and Spring Boot 3.5.0 for managing financial transactions using in-memory data structures.

## Features

- **Transaction Management**: Create deposits and withdrawals with automatic balance validation
- **Balance Tracking**: Real-time balance calculation with O(1) performance
- **Transaction History**: Paginated transaction listing with filtering by type and date range
- **Thread-Safe**: Concurrent operations using ConcurrentHashMap and atomic operations
- **REST API**: Clean RESTful endpoints with comprehensive error handling
- **Data Seeding**: Automatic test data generation for development

## Tech Stack

- **Java 21** with Spring Boot 3.5.0
- **Build Tool**: Gradle 8.14.2
- **Storage**: In-memory (ConcurrentHashMap)
- **Documentation**: OpenAPI 3 (Swagger)
- **Testing**: JUnit 5 with 99% test coverage

## Quick Start

```bash
# Clone and run
./gradlew bootRun

# Access API documentation
open http://localhost:8080/swagger-ui.html

# Run tests
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport
```

## API Endpoints

**Base URL**: `http://localhost:8080/api/v1`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/transactions` | Create transaction |
| GET | `/transactions/{id}` | Get transaction by ID |
| GET | `/transactions` | List transactions (paginated) |
| GET | `/balance` | Get current balance |

### Example Usage

```bash
# Create a deposit
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{"amount": 1000.00, "type": "DEPOSIT", "description": "Initial deposit"}'

# Check balance
curl http://localhost:8080/api/v1/balance

# Get transaction history
curl "http://localhost:8080/api/v1/transactions?page=0&size=10"
```

## Configuration

- **Default Port**: 8080
- **Default Profile**: dev (includes 100k test transactions)
- **Profiles**: dev, prod, test, staging

## Code Quality

- **Test Coverage**: 99% instruction coverage, 92% branch coverage
- **Code Style**: Google Java Format with Spotless
- **Static Analysis**: PMD, Checkstyle
- **Build Reports**: JaCoCo coverage reports

## Project Structure

```shell
src/main/java/pocket/ledger/
├── config/           # Configuration (OpenAPI/Swagger)
├── controller/v1/    # REST API endpoints (versioned)
├── dto/v1/           # Data Transfer Objects (versioned)
├── enums/            # Enumeration types
├── exception/        # Global error handling & custom exceptions
├── init/             # Data seeding for development
├── model/            # Domain entities (Transaction, BaseModel)
├── repository/       # Data access layer (in-memory)
├── service/          # Business logic & query strategies
└── util/             # Utility classes & constants
```

## Development

```bash
# Format code
./gradlew spotlessApply

# Run quality checks
./gradlew check

# Build project
./gradlew build
```

## Assignment Notes

This implementation goes beyond the "few hours" scope with comprehensive enhancements:

### Core Assignment (3-4 hours)

- ✅ Basic API endpoints for money movements, balance, and transaction history
- ✅ In-memory data structures with thread-safe operations
- ✅ Functional web application with no external dependencies

### Additional Quality Improvements

- 🚀 **99% Test Coverage**: Comprehensive unit and integration tests
- 🛡️ **Production-ready Error Handling**: Global exception handler with structured responses
- 🔒 **Thread Safety**: ConcurrentHashMap and atomic operations for concurrent access
- 📚 **API Documentation**: OpenAPI 3/Swagger integration
- 🎯 **Code Quality**: PMD, Checkstyle, Spotless integration
- 📊 **Performance**: O(1) balance calculation with caching

**Rationale**: While the assignment scope was minimal, these enhancements demonstrate:

- Professional development practices
- Production-ready code quality
- Scalability considerations
- Testing best practices

## Current Limitations

- **In-memory storage**: Data is lost on restart
- **Single instance**: No distributed system support  
- **No persistence**: No database integration
- **Simple validation**: Basic business rule validation only

## Future Enhancements

### Phase 1: Production Readiness

- **Database Integration**: PostgreSQL/MySQL with JPA
- **Authentication & Authorization**: JWT-based security
- **API Versioning**: Proper REST API versioning strategy
- **Input Validation**: Enhanced request validation with custom constraints

### Phase 2: Scalability & Monitoring

- **Event-Driven Architecture**: Async processing with domain events
  - Transaction events for balance updates
  - Audit trail with event sourcing
  - Notification system for large transactions
- **Monitoring & Observability**:
  - Prometheus metrics integration
  - Custom business metrics (transaction volume, error rates)
  - Grafana dashboards for real-time monitoring
- **Distributed Caching**: Redis for session and balance caching

### Phase 3: Enterprise Features

- **Multi-Locale Support**: i18n with Accept-Language header support
- **CQRS Pattern**: Separate command and query models for better performance
- **Microservices**: Split into account, transaction, and notification services
- **Advanced Security**: Rate limiting, fraud detection, audit logging

### Phase 4: Advanced Architecture

- **Event Sourcing**: Complete transaction history reconstruction
- **Saga Pattern**: Distributed transaction management
- **Circuit Breakers**: Fault tolerance for external service calls
- **Message Brokers**: Kafka/RabbitMQ for reliable event processing

## Architecture Evolution

### Current: Synchronous Monolith

```shell
Client → Controller → Service → Repository → Response
```

### Future: Event-Driven Microservices

```shell
Client → API Gateway → Transaction Service → Event Bus
                                              ↓
                    Balance Service ← ← ← ← ← ← 
                    Audit Service
                    Notification Service
```
