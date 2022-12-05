# Docker Containerization Demo Project

Spring Boot / MySQL application to accompany my [blog post](oneexists.github.io/containerize_api) about containerizing 
an application using Docker.

## Environment Variables

The application requires three environment variables to be passed on runtime for database configuration:
  - `DB_URL`
  - `DB_USERNAME`
  - `DB_PASSWORD`

## API Endpoints

- `/authenticate`: provides a user login that returns a JWT token
- `/refresh`: allows an authenticated user to receive a new JWT token
- `/api/appUsers`: RESTful endpoint for app users, allowing for:
  - POST request: create a new user
  - GET request: view all users (JWT required)
  - PUT request: update a user (admin JWT required)
  - DELETE request: delete a user (admin JWT required)
  Sample POST and GET requests are included in the file `http/requests.http`.

## Testing

Some sample tests are included in `src/test/java/com.docker.containers`. A MySQL database is required for some tests. A 
schema can be found in the file `sql/schema.sql` with the procedure and data required for the tests to pass.