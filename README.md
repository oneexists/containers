# Docker Containerization Demo Project

Spring Boot / MySQL application to accompany my [blog post](oneexists.github.io/containerize_api) about containerizing 
an application using Docker.

## Environment Variables

The application requires three environment variables to be passed on runtime for database configuration:
  - `CONTAINERS_DB_URL`
  - `CONTAINERS_DB_USERNAME`
  - `CONTAINERS_DB_PASSWORD`

## API Endpoints

- `/`: provides a welcome page with the current date
- `/authenticate`: provides a user login that returns a JWT token
- `/refresh`: allows an authenticated user to receive a new JWT token
- `/api/appUsers`: RESTful endpoint for app users, allowing for:
  - POST request: create a new user
  - GET request: view all users (JWT required)
  - PUT request: update a user (admin JWT required)
  - DELETE request: delete a user (admin JWT required)

Sample requests for creating a user, authentication, and viewing all users can be found [here](/http/requests.http).

## Testing

Sample tests can be found in the [testing package](/src/test/java/com/docker/containers/). The database schema 
for testing can be found [here](/sql/schema.sql), which also includes the procedure and sample data.

## Docker Script

The Bash [run](run.sh) script can be used to initialize the application, update
the API image, run the application, and stop the application.

Run `./run.sh -h` for usage instructions.

### Resources

- [Create a database within MySQL Docker container](https://stackoverflow.com/questions/28389458/how-to-execute-mysql-command-from-the-host-to-container-running-mysql-server)
- [Connect to MySQL inside a Docker container](https://community.xibo.org.uk/t/how-can-i-run-a-sql-command-when-using-a-docker-install/9946)