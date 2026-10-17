# CVScanner — Automated Bulk CV Parsing and Extraction System

CVScanner is a powerful backend architecture designed for HR teams and recruitment platforms to automatically process and extract structured information from thousands of CVs (PDF and DOCX formats). Built on Spring Batch, the system efficiently handles large volumes of uploaded files, parsing out relevant data such as technical skills, years of experience, and job preferences without manual review. 

The extracted data is stored in a PostgreSQL database, enabling dynamic querying, filtering, and easy export to CSV formats for external HR workflows.

## 🚀 Key Features

### Core Functionality
* **Bulk File Processing:** Upload a single `.zip` file containing hundreds or thousands of resumes. The system automatically unzips and stages the files.
* **Document Parsing Pipeline:** Integrates **Apache Tika** to seamlessly extract raw text from diverse file formats (PDF, DOCX) without requiring separate libraries.
* **Intelligent Data Extraction:** Utilizes custom Regex and dictionary-matching heuristics to identify Candidate Names, Emails, Years of Experience, Technical Skills, and Work Preferences.
* **Fault-Tolerant Batch Jobs:** Implements robust **Retry and Skip** logic using Spring Batch. Corrupted files or temporary I/O errors are skipped or retried without crashing the entire batch job.
* **Asynchronous Execution:** Batch jobs are launched in background threads, providing immediate HTTP 202 Accepted responses and preventing API timeouts during massive batch runs.
* **Relational Storage:** Extracted candidate profiles are securely stored as structured entities in a **PostgreSQL** database.

### API & Integrations
* **Dynamic Querying:** REST APIs feature dynamic filtering using Spring Data JPA Specifications (e.g., "Find candidates with Java and 5+ years of experience").
* **CSV Data Export:** Generate and download structured `.csv` reports of filtered candidate pools directly via the API.
* **Real-time Job Tracking:** Monitor the success, failure, read count, and skip count of ongoing batch jobs via a dedicated status endpoint.
* **Automated Email Notifications (Bonus):** Uses `JavaMailSender` to dispatch summary reports to HR teams upon the completion or failure of a batch job.
* **Interactive Documentation:** Fully documented with **Swagger/OpenAPI 3.0** for easy frontend integration and manual testing.

## 🛠️ Technical Stack

* **Core:** Java 21, Spring Boot 3.x
* **Batch Processing:** Spring Batch 5.x+
* **Document Extraction:** Apache Tika 
* **Database & ORM:** PostgreSQL, Spring Data JPA, Hibernate
* **API Documentation:** SpringDoc OpenAPI (Swagger UI)
* **Data Export:** OpenCSV
* **Build Tool:** Gradle
* **Infrastructure:** Docker & Docker Compose

## ⚙️ Getting Started

### Prerequisites
* Java 17 or 21 installed
* Docker and Docker Compose running
* Gradle installed (or use the provided Gradle wrapper)
* A valid SMTP Email and App Password (for email notifications)

### 1. Database Setup
Spin up the required PostgreSQL database using the included Docker Compose file:
```bash
docker-compose up -d
```

2. Configure Environment Variables
To enable the automated email notification system, provide your SMTP App Password via an environment variable.
```
Bash
export MAIL_PASSWORD="your-16-character-app-password"
```
(If using an IDE like IntelliJ, add MAIL_PASSWORD=... to your Run/Debug Configuration).

3. Build and Run
Run the application using Gradle:
```
Bash
./gradlew bootRun
```
Note: The application is configured to run on port 9191.

🔌 API Reference & Usage
Access the interactive Swagger UI to test the endpoints:
👉 http://localhost:9191/swagger-ui/index.html

Core Endpoints
POST /api/v1/cv/upload

Consumes: multipart/form-data (ZIP file)

Description: Uploads a zip archive of resumes, extracts them to a temporary directory, and asynchronously launches the Spring Batch processing job. Returns a jobExecutionId.

GET /api/v1/cv/job-status/{id}

Description: Retrieve the execution status, read count, write count, and skip metrics for a specific batch job.

GET /api/v1/cv/candidates

Query Parameters: skill (String), minExperience (Integer), jobType (String)

Description: Retrieve a list of parsed candidates. Can be dynamically filtered to isolate specific talent requirements.

GET /api/v1/cv/export/csv

Query Parameters: skill (String), minExperience (Integer), jobType (String)

Produces: text/csv

Description: Downloads the dynamically filtered candidate pool as a formatted .csv file.

🏗️ Architecture Flow
Upload Phase: A .zip is uploaded via the REST API. The FileStorageService unpacks it into a uniquely generated folder to prevent batch cross-contamination.

Read Phase (CvFileItemReader): Scans the target directory and feeds valid .pdf and .docx files to the processor one by one.

Process Phase (CvItemProcessor): DocumentParsingService extracts raw text using Tika. CvInformationExtractor runs regex and dictionary matching to build a Candidate entity. If an exception occurs, Spring Batch evaluates the configured Retry/Skip limit policies.

Write Phase (RepositoryItemWriter): Chunks of 20 successfully processed candidates are committed to the PostgreSQL database in a single transaction.

Completion Phase: JobCompletionNotificationListener fires, evaluating the final BatchStatus and dispatching an email report to the configured HR inbox.
