# Lab Sample Tracking API

A Spring Boot RESTful API for managing laboratory sample workflows — registration, search, status transitions, and result-value capture — built with a layered **Controller → Service → Repository** architecture and deployed as a containerized service on AWS.

## Overview

This project models a simplified LIMS (Laboratory Information Management System) workflow: a lab sample is registered with a type and a test parameter, moves through a status lifecycle as it's processed, and has a measured value recorded against it once testing is complete. The API exposes CRUD-style endpoints for creating, retrieving, searching, updating, and deleting samples.

It's built as a personal cloud-native backend project, informed by real-world LIMS domain experience (LabVantage LIMS configuration and customization).

## Features

- **Sample registration** with request validation (`@Valid`, `@NotBlank`) via a dedicated DTO layer
- **Retrieval** of a single sample by ID or a full listing of all samples
- **Dynamic column search** — query samples by status, type, creator, parameter list, or description prefix through a single flexible endpoint
- **Status lifecycle management** (`RECEIVED` → `IN_PROGRESS` → `COMPLETED` → `ARCHIVED`) with a business rule preventing a sample from being marked `COMPLETED` until a result value has been entered
- **Result value capture**, which automatically transitions a sample to `IN_PROGRESS` on first entry
- **Sample deletion**
- Custom runtime exception handling for not-found and illegal-state operations
- Unit tests for the service layer using JUnit 5 and Mockito

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 (Docker build targets Java 21) |
| Framework | Spring Boot 3.3.2 |
| Data access | Spring Data JPA / Hibernate |
| Database | MySQL (via `mysql-connector-j`) |
| Validation | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Build tool | Maven |
| Testing | JUnit 5, Mockito |
| Containerization | Docker (multi-stage build) |
| Cloud hosting | AWS ECS Fargate, Amazon RDS, ECR, Application Load Balancer, CloudWatch |
| CI/CD | GitHub Actions |

## Architecture

The application follows a standard layered design:

```
Controller  →  Service  →  Repository  →  Database
(SampleController)  (SampleService)  (SampleRepository / Spring Data JPA)
```

- **`SampleController`** — exposes the REST endpoints under `/samples` and delegates all logic to the service layer.
- **`SampleService`** — contains the business logic: sample creation, lookups, dynamic search dispatch, status-transition rules, and value updates.
- **`SampleRepository`** — a Spring Data JPA interface with derived query methods (no manual SQL/HQL required).
- **`Sample`** — the JPA entity mapped to the `samples` table, auto-generating its ID and timestamps.
- **`SampleRequest`** — a DTO used to validate and shape incoming creation requests.
- **`SampleRuntimeException`** — centralizes not-found and illegal-operation exceptions.

## API Endpoints

Base path: `/samples`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/samples/create` | Register a new sample |
| `GET` | `/samples/getsample/{id}` | Retrieve a sample by its ID |
| `GET` | `/samples/listsamples` | List all samples |
| `GET` | `/samples/search?columnToSearch={column}&searchValue={value}` | Search samples by a specific column (`samplestatus`, `sampletype`, `createdby`, `parameterlist`, `sampledesc`) |
| `PATCH` | `/samples/updatestatus/{id}?updatedStatus={status}` | Update a sample's status |
| `PATCH` | `/samples/updatevalue/{id}?parameterList={param}&value={value}` | Enter or update a result value for a sample parameter |
| `DELETE` | `/samples/deletesample/{id}` | Delete a sample |

### Example requests

**Create a sample**
```http
POST http://localhost:8080/samples/create
Content-Type: application/json

{
  "sampleType": "Blood",
  "parameterList": "Cholesterol",
  "sampleDesc": "Routine cholesterol panel"
}
```

**Update status**
```http
PATCH http://localhost:8080/samples/updatestatus/1?updatedStatus=archived
```

**Enter a result value**
```http
PATCH http://localhost:8080/samples/updatevalue/1?parameterList=Cholesterol&value=23.44
```

A full set of ready-to-run requests is available in [`src/main/resources/api_testing.http`](src/main/resources/api_testing.http) (compatible with the IntelliJ HTTP Client / VS Code REST Client).

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8+ running locally (or accessible via connection string)
- Docker (optional, for containerized runs)

### Run locally

```bash
git clone <repository-url>
cd SampleLabTrackingLIMS

# Create the database
mysql -u root -p -e "CREATE DATABASE sampletracking;"

# Build and run
mvn spring-boot:run
```

The API starts on **`http://localhost:8080`**.

### Configuration

Database connection settings live in `src/main/resources/application.yml` and can be overridden with environment variables — this is the recommended approach for anything beyond local development:

| Variable | Purpose | Default |
|---|---|---|
| `DB_URL` | JDBC connection string | `jdbc:mysql://localhost:3306/sampletracking` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | — |

> **Note:** The checked-in `application.yml` contains local-development defaults only. Do not commit real credentials — override `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` via environment variables or a separate `application-prod.yml` for any non-local environment, and rotate any credentials that may already be exposed in version control.

`spring.jpa.hibernate.ddl-auto` is set to `update` for convenience in local development. Switch to `validate` and introduce a migration tool (Flyway or Liquibase) before pointing this at a shared or production database.

### Run with Docker

The project includes a multi-stage `Dockerfile` (Maven/Temurin build stage → Temurin JRE Alpine runtime), so no local Maven or JDK install is required to containerize it:

```bash
docker build -t sample-tracking-api .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://<host>:3306/sampletracking \
  -e DB_USERNAME=<username> \
  -e DB_PASSWORD=<password> \
  sample-tracking-api
```

### Run tests

```bash
mvn test
```

Service-layer behavior (creation, status-transition rules, value updates, and exception handling) is covered with JUnit 5 and Mockito in `SampleServiceTest`.

## Deployment

This API is designed to run as a containerized service on AWS:

- **Amazon ECR** stores the built Docker image.
- **AWS ECS (Fargate)** runs the containerized application without managing underlying servers.
- **Amazon RDS** hosts the MySQL database.
- An **Application Load Balancer** routes traffic to the ECS service.
- **Amazon CloudWatch** provides logging and monitoring.
- A **GitHub Actions** pipeline builds the Docker image, pushes it to ECR, and performs rolling deployments to ECS on each change.

```
GitHub Actions (build & push image)
        │
        ▼
   Amazon ECR
        │
        ▼
AWS ECS Fargate  ──►  Application Load Balancer  ──►  Client
        │
        ▼
   Amazon RDS (MySQL)
        │
        ▼
   Amazon CloudWatch (logs/metrics)
```

## Data Model

**Sample**

| Field | Type | Notes |
|---|---|---|
| `sampleId` | `Long` | Auto-generated primary key |
| `sampleDesc` | `String` | Free-text description |
| `sampleType` | `String` | e.g. Blood, Urine — required, immutable after creation |
| `sampleStatus` | `String` | `RECEIVED`, `IN_PROGRESS`, `COMPLETED`, `ARCHIVED` |
| `createDate` | `LocalDateTime` | Set on creation, immutable |
| `createdBy` | `String` | Set on creation, immutable |
| `updatedBy` | `String` | Last user to update the sample |
| `parameterList` | `String` | Test parameter associated with the sample |
| `value` | `double` | Measured result value |
| `lastUpdated` | `LocalDateTime` | Refreshed automatically on update |

## Roadmap / Known Limitations

- Barcode-based duplicate checking is present in the codebase but currently commented out.
- No authentication/authorization layer is implemented yet — the `createdBy`/`updatedBy` fields are placeholder values (`"admin"`).
- No database migration tooling (Flyway/Liquibase) yet; schema is managed via `ddl-auto: update`.
- No CI pipeline definition is committed to this repository yet, though the project is deployed via a GitHub Actions build/push/deploy flow.

## Author

**Debojyoti Mallick**
Backend Software Engineer
[LinkedIn](#) · [GitHub](#) · [HackerRank](#)
