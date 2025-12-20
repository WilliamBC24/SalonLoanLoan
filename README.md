# Salon LoanLoan  
*A Web-based Salon Service Management System*

## Project Overview  
**Salon LoanLoan** is a web-based management system built to help salons efficiently manage their daily operations.
It centralizes appointment scheduling, staff coordination, service management, payments, and customer records — providing a unified dashboard for both salon owners and employees.
Developed as part of a **capstone project**, the system emphasizes scalability, automation, and modern web technologies.

## Key Features  
Salon LoanLoan includes, but is not limited to, the following features, covering a wide range of salon operations:
- Menu operations and service browsing
- Authentication and user profile management
- Appointment registration and order management
- Cart and payment processing (cash, QR, bank transfer)
- Loyalty point and promotion systems
- Staff, supplier, and inventory management
- Reporting, payroll, and analytics

## Tech Stack  
- **Backend (OLTP):** Spring Boot (Java)
- **Frontend:** HTML, CSS, JavaScript
- **Database:** PostgreSQL
- **Analytics & Data Pipelines (OLAP):**
  - Python
  - Apache Airflow
- **Containerization & Orchestration:** Docker, Kubernetes
- **Cloud Platform:** Amazon Web Services (AWS)

## How to Run

### Database Setup with Docker

1. Navigate to the database directory:
   ```bash
   cd database
   ```

2. Build the Docker image:
   ```bash
   docker build -t sll .
   ```

3. Run the Docker container with the environment file:
   ```bash
   docker run -d --network host --env-file .env sll
   ```

   The database will be accessible on `localhost:5432` with the following credentials:
   - Database: `sll`
   - User: `sonbui`
   - Password: `sonbui`

### Backend Application

1. Navigate to the backend directory:
   ```bash
   cd backend/SLLBackend
   ```

2. Build the application:
   ```bash
   ./gradlew build
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

   The application will start on `http://localhost:8080`

### Test Credentials

After the application starts, it will automatically load test data with the following credentials:

- **User Account:**
  - Username: `alice`
  - Password: `alice`

- **Staff Account:**
  - Username: `admin`
  - Role: `admin`

### Internationalization (i18n)

The application supports multiple languages through Thymeleaf's i18n features:
- English: `messages_en.properties`
- Vietnamese: `messages_vi.properties`

All frontend templates use i18n keys for text content, making it easy to add new languages or modify existing translations.


