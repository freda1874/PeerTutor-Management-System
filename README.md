###  Group project for Peertutor Registration managment system using Java EE, REST API, JSON, MySQL
 
### Project Overview
This project demonstrates the use of Java Persistence API (JPA) for model objects, session beans for business logic, RESTful services for backend resource representation, JEE security roles for access control, and JUnit for testing.
![image](https://github.com/user-attachments/assets/b5f533c4-6734-4284-8d7f-7d1b6a17e2c9)

---

### Technologies Used

- **Java EE**: For building robust and scalable enterprise applications.
- **REST API**: For creating RESTful web services to interact with the backend.
- **JSON**: For data interchange between the client and the server.
- **MySQL**: As the database management system.
- **EntityManager**: For managing JPA entities.
- **JUnit**: For testing the application's functionality and correctness.
- **Eclipse**: As the integrated development environment (IDE).
![image](https://github.com/user-attachments/assets/98a239d1-91e7-4e9c-9e14-efbdb052e137)

---

### Project Structure

1. **JPA**: Used for mapping Java objects to database tables.
2. **Session Beans**: Encapsulate business logic and provide a way to interact with JPA entities.
3. **REST**: Expose backend resources as RESTful services.
4. **JEE Security Roles**: Control access to various operations based on user roles.
5. **JUnit**: Implement a series of test cases to verify the system's operation and correctness.

---

### Requirements Summary

1. **Resource Creation for All Tables**:
   - Each resource should support CRUD operations (Create, Read, Update, Delete).
   - Resources are needed for the following entities:
     - ClubMembership
     - Course
     - PeerTutorRegistration
     - MembershipCard
     - PeerTutor
     - Student
     - StudentClub

2. **Postman Collection**:
   - Use the Postman collection to test REST endpoints. 

3. **Entity Annotations**:
   - Update entities with appropriate Jackson annotations for JSON serialization/deserialization.
   - Use `@JsonIgnore` to exclude fields from JSON processing.
   - Use `@JsonSerialize` for custom serialization.
   - Create named queries with joins for accessing lazy-fetched objects.

4. **JUnit Tests**:
   - Implement a minimum of 40 JUnit tests.
   - Use the Client API to test CRUD operations and role-based access controls.
   - Ensure the REST API is running before executing tests.
   - Optionally generate a Maven surefire report to summarize test results.
 
 

---

### Contact

For any questions or support, please contact Lei Luo at [email@example.com].
