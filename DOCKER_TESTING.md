# Docker Testing Guide

This guide explains how to use Docker to run the database and backend for testing.

## Prerequisites

- Docker installed on your system
- Docker Compose installed on your system

## Quick Start

### Option 1: Using Docker Compose (Recommended)

1. Make sure you have the `.env` file in the root directory with the following configuration:
   ```
   POSTGRES_DB=sll
   POSTGRES_USER=alice
   POSTGRES_PASSWORD=alice
   DB_URL=jdbc:postgresql://localhost:5432/sll
   DB_USER=alice
   DB_PASS=alice
   SPRING_PROFILES_ACTIVE=dev
   ```

2. Start both database and backend services:
   ```bash
   docker-compose up --build
   ```

3. To run in detached mode:
   ```bash
   docker-compose up -d --build
   ```

4. To stop services:
   ```bash
   docker-compose down
   ```

5. To stop and remove volumes:
   ```bash
   docker-compose down -v
   ```

### Option 2: Manual Docker Commands

#### Running the Database

1. Build the database image:
   ```bash
   cd database
   docker build -t sll-db .
   ```

2. Run the database container with environment variables from .env:
   ```bash
   docker run -d --network host --env-file ../.env --name sll-database sll-db
   ```

3. Check database logs:
   ```bash
   docker logs sll-database
   ```

#### Running the Backend

1. Build the backend image:
   ```bash
   cd backend/SLLBackend
   docker build -t sll-backend .
   ```

2. Run the backend container:
   ```bash
   docker run -d --network host --env-file ../../.env --name sll-backend sll-backend
   ```

3. Check backend logs:
   ```bash
   docker logs sll-backend
   ```

## Running Tests

### Run All Tests
```bash
cd backend/SLLBackend
./gradlew test
```

### Run Specific Test Classes
```bash
./gradlew test --tests "service.sllbackend.utils.*Test"
./gradlew test --tests "service.sllbackend.service.*Test"
```

### Run Tests with Database
Make sure the database is running first, then:
```bash
./gradlew test
```

## Test Credentials

The DataLoader class automatically loads test data with the following credentials:

- **User Account:**
  - Username: `alice`
  - Password: `alice`
  - Email: `alice@wonderland.com`
  - Phone: `0999999999`

- **Staff Account:**
  - Name: `admin`
  - Role: `admin`

## Accessing the Application

- Backend API: http://localhost:8080
- Database: localhost:5432

## Troubleshooting

### Port Already in Use
If you get a "port already in use" error:
```bash
# Find and stop the process using the port
lsof -ti:5432 | xargs kill -9  # For database
lsof -ti:8080 | xargs kill -9  # For backend
```

### Database Connection Issues
1. Check if the database container is running:
   ```bash
   docker ps | grep sll-database
   ```

2. Check database logs:
   ```bash
   docker logs sll-database
   ```

3. Verify database connectivity:
   ```bash
   docker exec -it sll-database psql -U alice -d sll
   ```

### Clean Start
To start fresh:
```bash
# Stop and remove all containers
docker-compose down -v

# Remove built images
docker rmi sll-backend sll-db

# Rebuild and start
docker-compose up --build
```

## Notes

- The database uses the OLTP.sql schema file located in the `database` directory
- The backend uses Spring Boot with JPA and automatically creates/drops tables on startup (configured in application.yml)
- Test data is loaded by the DataLoader class on application startup
- All unit tests use mocked dependencies and don't require a running database
