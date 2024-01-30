# ShareIt

ShareIt is a Spring Boot application that gives users, firstly, the ability to
tell what things they are ready to share, and secondly, find the right thing and
rent it for a while.

The service not only allows you to book a thing for certain dates, but also
closes access to it for the time of booking from other people. In case the
necessary thing is not on the service, users should be able to leave requests.
Suddenly, an ancient gramophone, which is strange even to offer for rent, will
suddenly be needed for a theatrical production. Upon request, it will be
possible to add new things for sharing.

## Technologies Used

- Spring Boot
- Hibernate
- Docker
- PostgreSQL

## Getting Started

### Prerequisites

Ensure you have the following installed on your system:

- Java Development Kit (JDK) 11 or later
- Docker
- PostgreSQL

### Building the Project

1. Clone the repository:

   `git clone https://github.com/23mser/ShareIt.git`

2. Change the current directory to the project root:

   `cd java-shareit`

3. Build the project using Maven:

   `./mvnw clean install`

4. Run the Docker container for PostgreSQL:

   `docker-compose up -d`

### Running the Application

Run the application using the following command:

`./mvnw spring-boot:run`

The application will start on port 8080. Access the public API at
`http://localhost:8080`.
