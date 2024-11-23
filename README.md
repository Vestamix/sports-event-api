# Sport Events CRUD REST API

![Java](https://img.shields.io/badge/Java-11-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.5.4-brightgreen)
![H2 Database](https://img.shields.io/badge/H2%20Database-in--memory-orange)
![JUnit](https://img.shields.io/badge/JUnit-5.7.2-yellow)
![Lombok]

A CRUD REST API for managing sport events, built with Java and Spring Boot. This project demonstrates fundamental concepts of building RESTful APIs, including entity management, validation and exception handling.

## Table of Contents

- [Features](#features)
- [Used technologies](#used-technologies)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Project Structure](#project-structure)

## Features

- Create, read, update, and delete sport events
- Filter events by status and sport type
- Robust exception handling
- In-memory H2 database for ease of setup
- Unit tests and Integration tests

## Used technologies

- Java 21
- Spring boot 3
- H2 database
- JUnit 5
- Gradle
- Git

## Installation

1. **Clone the repository:**

    ```sh
    git clone https://github.com/Vestamix/sports-event-api.git
    cd sport-events-api
    ```

2. **Build the project using Gradle:**

    ```sh
    ./gradlew build
    ```

## Running the Application

You can run the application using your IDE or via the command line:

```sh
./gradlew bootRun
```
## API Endpoints

### Create a Sport Event

- **URL:** `/api/events`
- **Method:** `POST`
- **Request Body:**
    ```json
    {
        "name": "Champions League Final",
        "sport": "FOOTBALL",
        "status": "INACTIVE",
        "startTime": "2024-06-01T20:00:00"
    }
    ```

### Get List of Sport Events

- **URL:** `/api/events`
- **Method:** `GET`
- **Query Parameters (optional):**
    - `status` - Filter by event status (INACTIVE, ACTIVE, FINISHED)
    - `sport` - Filter by sport type (FOOTBALL, HOCKEY, BASKETBALL, BASEBALL, BOXING, OTHER)

### Get a Sport Event by ID

- **URL:** `/api/events/{id}`
- **Method:** `GET`

### Update Event Status

- **URL:** `/api/events/{id}/status`
- **Method:** `PUT`
- **Query Parameter:**
    - `newStatus` - New status for the event (INACTIVE to ACTIVE, ACTIVE to FINISHED)


## Project structure
```
src
├── main
│   ├── java
│   │   └── com
│   │       └── entain
│   │           └── sporteventsapi
│   │               ├── controller    # REST controller
│   │               ├── dto           # Data Transfer Objects
│   │               ├── entity        # JPA entities
│   │               ├── repository    # JPA repositories
│   │               ├── service       # Service layer
│   │               └── SportEventsApiApplication.java
│   └── resources
│       ├── application.properties    # Application configuration
└── test
    └── java
        └── com
            └── entain
                └── sporteventsapi    # Unit and Integration tests
```
