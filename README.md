# Payment Integration System

A production-style Payment Integration System built using Spring Boot and MySQL.

The goal of this project is to simulate how real-world payment systems are designed, implemented, documented, and maintained in enterprise environments.

---

## Project Status

### Completed

#### Story 1: Project Setup

* Spring Boot project initialization
* Maven configuration
* Dependency management
* Package structure setup
* Application configuration
* Application startup verification

#### Story 2: Database Setup (In Progress)

* MySQL database creation
* Spring Boot database connectivity
* Payment transaction schema design
* Payment transaction table creation

---

## Technology Stack

* Java 21
* Spring Boot
* Maven
* MySQL
* Git
* GitHub
* Jira
* Docker (Planned)

---

## Database

### Database Name

payment_system_db

### Core Table

payment_transactions

Stores payment requests and tracks their lifecycle.

Current statuses:

* PENDING
* SUCCESS
* FAILED

---

## Project Structure

```text
payment-system
│
├── docs
│   └── database-design.md
│
├── src
│   └── main
│       ├── java
│       └── resources
│           └── db
│               └── schema.sql
│
├── pom.xml
└── README.md
```

---

## Documentation

| Document           | Purpose                              |
| ------------------ | ------------------------------------ |
| database-design.md | Database design and schema decisions |
| schema.sql         | Database creation scripts            |

---
## Current Progress

### Completed Stories

* Project Setup (Spring Boot Base Structure)
* Database Setup (MySQL Schema + Connection)

### Current Story

* Payment Core Design (API + Flow Design)

### Next Milestones

* DTO Implementation
* Controller Layer
* Service Layer
* Exception Handling
* Payment Processing Flow
* Gateway Integration



## Upcoming Work

### Database Layer

* Create JPA entities  
* Create repositories  
* Verify CRUD operations

### Payment Core

* Payment API design
* Request validation
* Service layer implementation

### Gateway Integration

* Payment gateway adapter
* Webhook processing
* Transaction reconciliation

### Production Readiness

* Dockerization
* Logging
* Monitoring
* Security enhancements

---

## Development Process

This project follows a real-world Agile workflow:

* Jira stories and subtasks
* Feature branches
* Git commits per task
* Pull Request based development
* Documentation-driven implementation

---

## Author

P-Pranjali

Building enterprise-grade backend systems using Java and Spring Boot.
