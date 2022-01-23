# Erapulus

Backend application for Erapulus project, made as an Engineer Thesis for Poznan University of Technology.

## Requirements:
- Java 17
- Maven
- Docker and Docker-compose (optional)

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


