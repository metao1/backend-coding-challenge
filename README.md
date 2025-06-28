# Backend Senior Coding Challenge 🍿

Welcome to our Movie Rating System Coding Challenge! We appreciate you taking
the time to participate and submit a coding challenge! 🥳

In this challenge, you'll be tasked with designing and implementing a robust
backend system that handles user interactions and provides movie ratings. We
don't want to check coding conventions only; **we want to see your approach
to systems design!**

**⚠️ As a tech-agnostic engineering team, we ask you to pick the technologies
you are most comfortable with and those that will showcase your strongest
performance. 💪**

## ✅ Requirements

- [x] The backend should expose RESTful endpoints to handle user input and
  return movie ratings.
- [x] The system should store data in a database. You can use any existing
  dataset or API to populate the initial database.
- [x] Implement user endpoints to create and view user information.
- [x] Implement movie endpoints to create and view movie information.
- [x] Implement a rating system to rate the entertainment value of a movie.
- [x] Implement a basic profile where users can view their rated movies.
- [x] Include unit tests to ensure the reliability of your code.
- [x] Ensure proper error handling and validation of user inputs.

## ✨ Bonus Points

- [ ] Implement authentication and authorization mechanisms for users.
- [x] Provide documentation for your API endpoints using tools like Swagger.
- [x] Implement logging to record errors and debug information.
- [x] Implement caching mechanisms to improve the rating system's performance.
- [x] Implement CI/CD quality gates.

## 📋 Evaluation Criteria

- **Systems Design:** We want to see your ability to design a flexible and
  extendable system. Apply design patterns and software engineering concepts.
- **Code quality:** Readability, maintainability, and adherence to best
  practices.
- **Functionality:** Does the system meet the requirements? Does it provide
  movie
  ratings?
- **Testing:** Adequate test coverage and thoroughness of testing.
- **Documentation:** Clear documentation for setup, usage, and API endpoints.

## 📐 Submission Guidelines

- Fork this GitHub repository.
- Commit your code regularly with meaningful commit messages.
- Include/Update the README.md file explaining how to set up and run your
  backend, including any dependencies.
- Submit the link to your repository.

## 🗒️ Notes

- You are encouraged to use third-party libraries or frameworks to expedite
  development but be prepared to justify your choices.
- Feel free to reach out if you have any questions or need clarification on the
  requirements.
- Remember to approach the challenge as you would a real-world project, focusing
  on scalability, performance, and reliability.

## 🤔 What if I don't finish?

Part of the exercise is to see what you prioritize first when you have a limited
amount of time. For any unfinished tasks, please do add `TODO` comments to
your code with a short explanation. You will be given an opportunity later to go
into more detail and explain how you would go about finishing those tasks.

---

# 🚀 Implementation Details

## Technology Stack

- **Language**: Kotlin 2.2.0
- **Framework**: Spring Boot 3.5.3
- **Database**: H2 (development) / PostgreSQL (testing/production)
- **ORM**: Spring Data JPA with Hibernate
- **Testing**: JUnit 5, MockK, RestAssured, TestContainers
- **Documentation**: OpenAPI 3 (Swagger)[http://localhost:8080/swagger-ui.html]
- **Build Tool**: Gradle with Kotlin DSL
- **CI/CD pipeline**: Github Action code quality check and build

## Architecture

This implementation follows **Pragmatic DDD** and **Clean Architecture** principles:

### Domain Layer (`src/main/kotlin/com/movie/rate/domain/`)
- **Entities**: `User`, `Movie`, `Rating` with business logic
- **Value Objects**: `UserId`, `MovieId`, `Email`, `RatingValue` for type safety
- **Repository Interfaces**: Define contracts for data access

### Application Layer (`src/main/kotlin/com/movie/rate/application/`)
- **Use Cases**: `CreateUserUseCase`, `CreateMovieUseCase`, `CreateRatingUseCase`, etc.
- **DTOs**: Request/Response objects for application boundaries

### Infrastructure Layer (`src/main/kotlin/com/movie/rate/infrastructure/`)
- **JPA Entities**: Database mapping with proper relationships
- **Repository Implementations**: Adapter pattern for domain repositories
- **Configuration**: Spring configuration and caching

### Presentation Layer (`src/main/kotlin/com/movie/rate/presentation/`)
- **Controllers**: REST endpoints with comprehensive validation
- **DTOs**: API-specific request/response objects
- **Exception Handling**: Global error handling with proper HTTP status codes

## Use cases
Using use cases instead of a single service class is a concept coming from [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Docker (optional, for TestContainers)

### Running the Application

```bash
# Build the project
make clean build

# Run the application
make run
```

The application will start on `http://localhost:8080`

### API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **H2 Console**: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:moviedb`, User: `sa`, Password: `password`)

## 📋 API Endpoints

### Users
- `POST /api/users` - Create a new user
- `GET /api/users/{id}` - Get user by ID

### Movies
- `POST /api/movies` - Create a new movie
- `GET /api/movies/{id}` - Get movie by ID
- `GET /api/movies` - Get all movies

### Ratings
- `POST /api/ratings` - Create or update a rating
- `GET /api/ratings/user/{userId}` - Get user's ratings

## 📊 Sample API Usage

### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "username": "johndoe",
    "full_name": "John Doe"
  }'
```

### Create Movie
```bash
curl -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Matrix",
    "description": "A computer hacker learns about the true nature of reality",
    "release_date": "1999-03-31",
    "genre": "Science Fiction",
    "director": "The Wachowskis"
  }'
```

### Create Rating
```bash
curl -X POST http://localhost:8080/api/ratings \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "USER_ID_HERE",
    "movie_id": "MOVIE_ID_HERE",
    "value": 5,
    "comment": "Amazing movie!"
  }'
```

## 🧪 Testing

### Run All Tests
```bash
make test
```

## 🎯 Key Features

### Domain-Driven Design
- **Rich Domain Models** with business logic encapsulated in entities
- **Value Objects** for type safety (`UserId`, `Email`, `RatingValue`)
- **Repository Pattern** with clean separation between domain and infrastructure
- **Use Cases** that orchestrate business operations

### Kotlin Best Practices
- **Data Classes** for immutable DTOs with automatic equals/hashCode
- **Value Classes** for type-safe IDs and values
- **Null Safety** throughout the codebase
- **Extension Functions** and idiomatic Kotlin patterns

### Testing Strategy
- **Unit Tests** for domain logic with MockK
- **Integration Tests** using RestAssured with real JSON data files
- **TestContainers** for database integration testing
- **Performance Tests** for concurrent request handling

### Data & Validation
- **Comprehensive Input Validation** with meaningful error messages
- **Global Exception Handling** with proper HTTP status codes
- **Database Constraints** ensuring data integrity
- **Caching Strategy** with Spring Cache and JPA persistence context

## 🗄️ Test Data

The application uses clean test data creation:
- **Integration Tests**: Create isolated test data for each test
- **Constants**: All test values use constants for maintainability
- **No Shared State**: Each test creates its own data to avoid dependencies

Test data creation is handled programmatically in integration tests for better reliability.

## 🔧 Production Considerations

### Database Migration
For production, update `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/moviedb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate  # Use Flyway/Liquibase for migrations
```

### Environment Variables
```bash
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export SPRING_PROFILES_ACTIVE=production
```

### Build Production JAR
```bash
make run
```
