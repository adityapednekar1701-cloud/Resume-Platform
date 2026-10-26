# Resume Platform

A full-stack job portal with AI-powered resume screening. Recruiters can post jobs and review applicants, candidates can browse jobs and apply with a resume, and each resume is scored automatically by a locally hosted LLM.

The backend is built with **Spring Boot**, **Spring AI (Ollama)**, and **Kafka**. The frontend is a **React + Vite** app located in `Resume-Platform-Ui`.

**Live Demo:** <your-deployed-link>
**Repository:** https://github.com/adityapednekar1701-cloud/Resume-Platform

---

---

## Features

- **Authentication:** Register and login with Spring Security and JWT.
- **Job Management:** Create and manage job postings (My Jobs, Job Form).
- **Job Browsing:** Browse all jobs and view job details.
- **Resume Upload:** Apply to jobs by uploading a PDF resume (max 5 MB).
- **PDF Text Extraction:** Resume text is extracted using Apache PDFBox.
- **AI Resume Scoring:** Extracted text is analysed by an Ollama model (`llama3.2:1b`) through Spring AI to generate a score.
- **Asynchronous Processing:** Scoring runs in the background through Kafka, so uploads do not block while the model works.
- **Applicant Review:** Recruiters can view applicants for their jobs, and candidates can track their own applications.
- **Input Validation:** Request validation using Spring Boot Validation.

---

## Tech Stack

| Category | Technology |
|----------|-----------|
| Backend Language | Java 21 |
| Backend Framework | Spring Boot 4.1.1 |
| Security | Spring Security, JWT (jjwt 0.13.0) |
| Database | MySQL, Spring Data JPA |
| AI Integration | Spring AI 2.0.0, Ollama (`llama3.2:1b`) |
| Messaging | Apache Kafka |
| PDF Parsing | Apache PDFBox 3.0.3 |
| Build Tool | Maven (wrapper included) |
| Frontend | React, Vite |
| Frontend State | React Context API (`AuthContext`) |
| Styling | Custom CSS |

---

## Project Structure

```
Resume-Platform/
├── src/                          # Spring Boot backend
├── pom.xml
├── mvnw / mvnw.cmd
└── Resume-Platform-Ui/           # React + Vite frontend
    ├── index.html
    ├── vite.config.js
    ├── package.json
    └── src/
        ├── pages/
        │   ├── Login.jsx
        │   ├── Register.jsx
        │   ├── Jobs.jsx
        │   ├── JobDetail.jsx
        │   ├── JobForm.jsx
        │   ├── MyJobs.jsx
        │   ├── Applicants.jsx
        │   └── MyApplications.jsx
        ├── api.js                # API client
        ├── App.jsx
        ├── AuthContext.jsx       # Global auth state
        ├── Navbar.jsx
        ├── main.jsx
        └── styles.css
```

---

## Getting Started

### 1. Prerequisites

- [Java 21](https://adoptium.net)
- [Node.js](https://nodejs.org) (v18 or higher)
- [MySQL](https://www.mysql.com) 8+
- [Apache Kafka](https://kafka.apache.org) running on `localhost:9092`
- [Ollama](https://ollama.com) installed and running

### 2. Clone the Repository

```bash
git clone https://github.com/adityapednekar1701-cloud/Resume-Platform.git
cd Resume-Platform
```

### 3. Pull the AI Model

The backend is configured with `pull-model-strategy=never`, so the model must be downloaded manually:

```bash
ollama pull llama3.2:1b
```

### 4. Configure the Backend

Edit `src/main/resources/application.properties`. Secrets are read from environment variables so they are never committed:

```properties
spring.application.name=PlatForm

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/Users?createDatabaseIfNotExist=true
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Ollama
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3.2:1b
spring.ai.ollama.init.pull-model-strategy=never
spring.ai.ollama.chat.options.num-predict=500
spring.ai.ollama.chat.options.num-ctx=8192

# File upload limits
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=6MB

# JWT
app.security.jwt.secret-key=${JWT_SECRET}

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=resume-scoring-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.producer.properties.max.block.ms=5000
```

Set the environment variables before running the app.

**Windows (PowerShell):**
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_db_password"
$env:JWT_SECRET="your_long_random_secret"
```

**macOS / Linux:**
```bash
export DB_USERNAME=root
export DB_PASSWORD=your_db_password
export JWT_SECRET=your_long_random_secret
```

> **Warning:** Never commit real passwords or secret keys. Generate a long random JWT secret (at least 32 characters).

### 5. Run the Backend

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The API starts on `http://localhost:8080` by default.

### 6. Run the Frontend

```bash
cd Resume-Platform-Ui
npm install
npm run dev
```

Open `http://localhost:5173` in your browser.

---

## How It Works

1. A user registers or logs in and receives a JWT.
2. A recruiter creates a job posting.
3. A candidate browses jobs and applies with a PDF resume.
4. The backend extracts the resume text with PDFBox and publishes a scoring task to Kafka.
5. A Kafka consumer sends the text to the Ollama model through Spring AI.
6. The score is saved to MySQL and shown to the recruiter in the applicants list.

---

## Build for Production

Backend:
```bash
./mvnw clean package
java -jar target/PlatForm-0.0.1-SNAPSHOT.jar
```

Frontend:
```bash
cd Resume-Platform-Ui
npm run build
npm run preview
```

---

## Future Improvements

- [ ] Add Docker Compose for MySQL, Kafka, and Ollama
- [ ] Support DOCX resumes
- [ ] Show a detailed score breakdown (skills match, experience, keywords)
- [ ] Add search and filters for jobs
- [ ] Add unit and integration tests

---

## Author

**Aditya Pednekar**
[GitHub](https://github.com/adityapednekar1701-cloud)
