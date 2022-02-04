# Erapulus
Erapulus is an application created by [@Piotr Gleska](https://github.com/pgleska/), [@Michał Szczepaniak](https://github.com/Szczepaniak-M) 
and [@Filip Szóstak](https://github.com/Cheriit/) as the Bachelor Thesis at Poznan University of Technology.

The application is focused on the students participating in Erasmus+ project. 
Its main purpose is to simplify their process of acclimatisation and make available all essential information.
The students can use the application via their smartphones with Android system. Whereas university employees can manage content via web application.

The application consists of the three part:
- Server application created by [@Michał Szczepaniak](https://github.com/Szczepaniak-M): [Erapulus-server](https://github.com/Szczepaniak-M/Erapulus-server)
- Mobile application for students created by [@Piotr Gleska](https://github.com/pgleska/): [Erapulus-client-mobile](https://github.com/pgleska/erapulus-client-mobile)
- Web application for university employees created by [@Filip Szóstak](https://github.com/Cheriit/): [Erapulus-client-web](https://github.com/Cheriit/erapulus-client-web)

# Erapulus server
The server application for Erapulus is based on `Spring WebFlux` and `Java 17`.
Its main purpose is to expose REST API for mobile and web applications.
Thanks to that mobile and web clients can perform CRUD operation on database, upload files to Azure Blob Storage and receive PushNotifications

## Technologies:
- `Java 17` - the newest LTS Java version
- `Spring WebFlux` - the library based on Project Reactor to create reactive application runned on Netty server
- `Spring Data R2DBC` and R2DBC drivers- the library and database drivers used to communicate with a database in a non-blocking way
- `Liquibase` - the library used to manage changes in a database schema
- `MS SQL Server` - the main database used by server
- `H2` - the in-memory database used during unit tests of repositories
- `Firebase SDK Admin` - the library used to communicate with Firebase to send Push Notification
- `Azure Spring Boot Starter Storage` - the library used to upload files to Azure Blob Storage
- `SpringDoc OpenAPI` - the library used to automatically generate the OpenAPI 3 specification documentation for REST API
- `Lombok` - the library used to automatically generate getters, setters, constructors and builders

# Running application

## Requirements:
- `Java 17`
- `Maven`
- `Docker` and `Docker-compose` (optional)

## Configuration
To run a project, there is a need to set a few environmental variables. 
They can be set by changing them in `src/main/resources/aoolication.properties` or set during running Docker image.
All variables are described in the table.

| Environmental variable      | Example value                            | Description                              |
|-----------------------------|------------------------------------------|------------------------------------------|
| `SERVER_PORT`               | `8080`                                   | Port used by application                 |
| `DB_USER`                   | `sa`                                     | Database user                            |
| `DB_PASSWORD`               | `Q1W2e3r4`                               | Database user's password                 |
| `DB_HOST`                   | `localhost`                              | Database host                            |
| `DB_PORT`                   | `1433`                                   | Database port                            |
| `DB_NAME`                   | `erapulus-db`                            | Database schema name                     |
| `ERAPULUS_JWT_ISSUER`       | `erapulus`                               | JWT issuer                               |
| `ERAPULUS_JWT_SECRET`       | `my-incredibly-strong-and-secure-secret` | JWT secret                               |
| `ERAPULUS_GOOGLE_CLIENT_ID` | `dummy-client-id`                        | Google client Id used by login endpoint  |
| `AZURE_ACCOUNT_NAME`        | `accountname`                            | Account name in Microsoft Azure service  |
| `AZURE_ACCOUNT_KEY`         | `secret-key`                             | Account key from Microsoft Azure service |
| `AZURE_CONTAINER_NAME`      | `example`                                | Blob container where files will be saved |
| `ADMIN_FIRST_NAME`          | `John`                                   | Initial admin first name                 |
| `ADMIN_LAST_NAME`           | `Smith`                                  | Initial admin last name                  |
| `ADMIN_EMAIL`               | `example@gmail.com`                      | Initial admin email                      |
| `ADMIN_PASSWORD`            | `pass`                                   | Initial admin password                   |

Additionally, you need to replace content in `src/main/resources/firebase.json.template` 
with your own credentials to Firebase Cloud Messaging service and change the file name to `firebase.json`.


## Run using Docker Compose:
1. Set all environmental variables
2. Run command `make` to run `mvn clean install` and copy JAR file to correct catalog
3. Run command `docker-compose up` to run MS SQL database and application


