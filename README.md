# Sample Tracking API (LIMS)

A REST API for tracking lab samples — individually or grouped into batches — through their full lifecycle, from receipt through testing to completion. Inspired by real-world LIMS (Laboratory Information Management System) workflows. Built as a hands-on project to move from LIMS configuration into cloud-native backend engineering.

Deployed on **AWS ECS Fargate**, backed by **RDS MySQL**, fronted by an **Application Load Balancer**, with a fully automated **CI/CD pipeline via GitHub Actions**.

## Features

- Register individual samples, or create a **Batch** that generates multiple linked samples in one call
- Track sample status through a defined lifecycle: `RECEIVED` → `IN_PROGRESS` → `COMPLETED` → `ARCHIVED`
- Automatic status transition to `IN_PROGRESS` the moment a test value is first entered
- Guard against marking a sample `COMPLETED` before any value has been recorded
- Flexible search across multiple sample fields (type, status, creator, parameter list, description)
- Batch deletion with a **soft-delete guard** (refuses to delete a batch with samples still attached) or an explicit **force-delete** to remove the batch and all its samples together
- Centralized error handling via `@RestControllerAdvice` — every error returns a consistent, structured JSON body instead of a bare stack trace
- Response DTOs throughout — controllers never return JPA entities directly, avoiding circular-reference/serialization issues and keeping the API contract decoupled from the database schema
- Full CRUD operations backed by MySQL via Spring Data JPA
- Containerized and deployed to AWS with zero-downtime rolling deployments on every push to `main`

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.2, Spring Data JPA / Hibernate |
| Database | MySQL (Amazon RDS in production) |
| Build | Maven |
| Testing | JUnit 5, Mockito |
| Containerization | Docker (multi-stage build) |
| Cloud | AWS ECS (Fargate), ECR, RDS, Application Load Balancer, VPC |
| CI/CD | GitHub Actions |

## Architecture

```
GitHub push (main branch)
        │
        ▼
GitHub Actions: build → test → Docker build → push to ECR
        │
        ▼
Amazon ECR (image registry)
        │
        ▼
Amazon ECS (Fargate) — pulls new image, rolls out new task
        │
        ├──► Application Load Balancer ──► Internet (stable public endpoint)
        │
        └──► Amazon RDS (MySQL) — persistent data layer
```

Fargate is used instead of the EC2 launch type — AWS manages the underlying compute entirely; only the task definition (image, CPU, memory) is defined, and ECS handles scheduling and infrastructure.

### Domain model

```
Batch (1) ───< (many) Sample
```
A `Batch` groups multiple `Sample`s created together (e.g., one lab run). Samples can also exist independently of a batch. Deleting a batch with samples still attached is blocked unless `forceDelete=true` is explicitly passed.

## Project Structure

```
src/main/java/com/labtrack/sampletracking/
├── controller/     # REST endpoints (SampleController, BatchController)
├── service/        # Business logic (SampleService, BatchService)
├── repository/     # Spring Data JPA repositories
├── model/          # JPA entities (Sample, Batch) and status/column constants
├── dto/            # Request/response DTOs — never expose entities to clients
├── util/           # ConvertUtil — entity → DTO conversion, kept out of the service layer
└── Exceptions/     # Custom exceptions + GlobalExceptionHandler (@RestControllerAdvice)
src/test/java/       # JUnit 5 + Mockito unit tests (SampleServiceTest, BatchServiceTest)
.github/workflows/   # CI/CD pipeline definition
Dockerfile            # Multi-stage build (Maven build stage + slim JRE runtime stage)
```

## Running Locally

### Prerequisites
- JDK 21
- Maven
- MySQL running locally (or accessible remotely)
- Docker (optional, for container testing)

### Setup

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd sample-tracking-api
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE sampletracking;
   ```

3. **Configure the connection** — edit `src/main/resources/application.yml`, or set environment variables:
   ```
   DB_URL=jdbc:mysql://localhost:3306/sampletracking
   DB_USERNAME=root
   DB_PASSWORD=your_password
   ```

4. **Run**
   ```bash
   mvn spring-boot:run
   ```
   API available at `http://localhost:8080`.

### Running with Docker

```bash
docker build -t sample-tracking-api .
docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://<host>:3306/sampletracking" \
  -e DB_USERNAME="admin" \
  -e DB_PASSWORD="<password>" \
  sample-tracking-api
```

## API Endpoints

### Samples

| Method | Endpoint | Description |
|--------|----------|--------------|
| `POST` | `/samples/create` | Register a new sample |
| `GET` | `/samples/getsample/{id}` | Get a sample by ID |
| `GET` | `/samples/listsamples` | List all samples |
| `GET` | `/samples/search?columnToSearch={column}&searchValue={value}` | Search samples by a specific column |
| `PATCH` | `/samples/updatestatus/{id}?updatedStatus={status}` | Update a sample's status |
| `PATCH` | `/samples/updatevalue/{id}?parameterList={param}&value={value}` | Enter or update a test value (auto-transitions status to `IN_PROGRESS` on first entry) |
| `DELETE` | `/samples/deletesample/{id}` | Delete a sample |

### Batches

| Method | Endpoint | Description |
|--------|----------|--------------|
| `POST` | `/batch/createBatch` | Create a batch and generate its linked samples in one call |
| `GET` | `/batch/listBatches` | List all batches, each with its samples |
| `GET` | `/batch/getBatch/{id}` | Get a single batch by ID |
| `DELETE` | `/batch/deleteBatch/{id}?forceDelete={true\|false}` | Delete a batch. Refused if samples are still attached, unless `forceDelete=true` |

### Example: Create a sample

**Request**
```http
POST /samples/create
Content-Type: application/json

{
    "sampleType": "Blood",
    "parameterList": "Glucose,Cholesterol",
    "sampleDesc": "Routine blood panel"
}
```

**Response**
```json
{
    "sampleId": 1,
    "sampleType": "Blood",
    "sampleDesc": "Routine blood panel",
    "sampleStatus": "RECEIVED",
    "parameterList": "Glucose,Cholesterol",
    "value": 0.0,
    "createDate": "2026-08-01T10:15:30"
}
```

### Example: Create a batch

**Request**
```http
POST /batch/createBatch
Content-Type: application/json

{
    "noOfSample": 3,
    "batchDesc": "Morning blood panel run",
    "sampleType": "Blood",
    "sampleParameterList": "Glucose,Cholesterol",
    "sampleDesc": "Routine panel"
}
```

**Response**
```json
{
    "batchId": 10,
    "noOfSample": 3,
    "batchDesc": "Morning blood panel run",
    "createDate": "2026-08-22T10:15:30",
    "sample": [
        { "sampleId": 1, "sampleType": "Blood", "sampleStatus": "RECEIVED", "value": 0.0 },
        { "sampleId": 2, "sampleType": "Blood", "sampleStatus": "RECEIVED", "value": 0.0 },
        { "sampleId": 3, "sampleType": "Blood", "sampleStatus": "RECEIVED", "value": 0.0 }
    ]
}
```

### Searchable columns

`sampleStatus`, `sampleType`, `createdBy`, `parameterList`, `sampleDesc`

### Error responses

All errors return a consistent structure via a global `@RestControllerAdvice` handler, rather than a bare stack trace:

```json
{
    "status": "Not Found",
    "message": "Sample with 99 Sample ID is not found",
    "time": "2026-08-22T10:15:30",
    "errorCode": 404
}
```

| Exception | HTTP Status |
|---|---|
| `SampleNotFoundException` / `BatchNotFoundException` | 404 |
| `IllegalUpdateException` (invalid search column, illegal status transition, blocked batch deletion) | 403 |
| Bean Validation failures (`@Valid` on request DTOs) | 400 |
| Any other unhandled exception | 500 (generic message, no internal details leaked) |

## Running Tests

```bash
mvn test
```
`SampleServiceTest` and `BatchServiceTest` cover the service layer — status-transition rules, search logic, batch creation/linking, soft- vs. force-delete behavior, and error handling — using JUnit 5 and Mockito with mocked repository dependencies.

## CI/CD Pipeline

Every push to `main` triggers `.github/workflows/deploy.yml`, which:
1. Builds the Docker image
2. Pushes it to Amazon ECR, tagged with the commit SHA and `latest`
3. Registers a new ECS task definition revision pointing at the new image
4. Updates the ECS service and waits for the rolling deployment to stabilize

No manual AWS console steps are required for a deploy — pushing code is the entire release process.

## AWS Infrastructure Notes

- **Networking**: custom VPC with an attached Internet Gateway and route table entry (`0.0.0.0/0`) for public reachability
- **Security groups**: RDS only accepts inbound MySQL traffic from the ECS service's security group, not the open internet
- **Load balancing**: an Application Load Balancer sits in front of ECS, giving the API a stable public endpoint that survives task restarts and redeployments (ECS Fargate tasks otherwise receive a new IP on every deployment)

## Known Issues / Next Steps

- [ ] Add Flyway/Liquibase migrations instead of relying on `ddl-auto: update`
- [ ] Some columns accepted by `searchSamples`'s validation aren't yet wired into the search logic and silently fall back to returning all samples instead of erroring — tracked for a follow-up fix
- [ ] `BatchService`/`SampleService` cast repository `List` results directly to `ArrayList` in a couple of places; works against the current Spring Data JPA implementation but isn't guaranteed by the interface contract
- [ ] Add HTTPS via AWS Certificate Manager on the load balancer
- [ ] Add a custom domain via Route 53

## Author

**Debojyoti Mallick**
Associate Software Engineer @ LabVantage Solutions. 
AWS Certified Solutions Architect – Associate (SAA-C03)
