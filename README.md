About the Project
This is a Spring Boot RESTful API that includes CRUD operations with role-based access control and JWT-based authentication. The project allows Management, Teachers, and Students to perform various operations based on their roles.

Management can add and manage teachers and students.
Teachers can update and view student details.
Students can view their own details.
The application uses JWT for secure authentication, H2 Database for in-memory storage, and role-based access to secure endpoints.

Features
CRUD Operations: Manage Teachers and Students.
Role-Based Access Control: Access restrictions based on roles (Management, Teacher, Student).
JWT Authentication: Token-based authentication for secure API access.
Spring Security: For authentication and authorization.
Caching: Optional caching layer for improved performance.
In-Memory Database: Uses H2 for quick testing and development.
Tech Stack
Spring Boot 3.3.4
Spring Security
JWT (JSON Web Token)
H2 Database
Spring Data JPA
SLF4J 2.0.13 (Logging)
Maven (Build Tool)
Prerequisites
Ensure you have the following installed:

Java 17 or later
Maven 3.8+
Any REST Client (e.g., Postman, cURL)
An IDE (IntelliJ, Eclipse, VS Code, etc.)
Build the Project with Maven
Run the following Maven command to compile and package the project:

bash
Copy code
mvn clean install
Run the Application
Use the following command to start the Spring Boot application:

bash
Copy code
mvn spring-boot:run
