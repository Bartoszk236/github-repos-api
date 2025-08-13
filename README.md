GitHub Repositories API

A Spring Boot 3.5 / Java 21 application that exposes an endpoint returning a user’s public GitHub repositories excluding forks, along with branch names and the latest commit SHA.

Table of Contents
1. Features
2. Stack / Technologies
3. Requirements
4. Configuration
5. API
6. Tests (mocking GitHub)
7. Error Handling
8. Author

Features
1. ```GET /{username}``` – returns the user’s public repositories without forks.
2. For each repository, returns branches (name + latest commit SHA).
3. No token required — requests use GitHub’s public API (subject to GitHub’s rate limits).

Stack / Technologies
1. Java 21
2. Spring Boot 3.5.0 (Spring Framework 6.2)
3. RestClient for HTTP calls
4. JUnit 5, AssertJ, Hamcrest
5. MockRestServiceServer + MockServerRestClientCustomizer (mock external API in tests)
6. Lombok

Requirements
1. JDK 21
2. Maven 3.9+

Configuration
1. The application does not require additional configuration.

API

```GET /{username}```

Returns a list of the user’s public repositories excluding forks, plus their branches.

Example: ```http://localhost:8080/Bartoszk236```

Tests (mocking GitHub)

Uses MockRestServiceServer associated with a specific RestClient.Builder via MockServerRestClientCustomizer to mock the external GitHub API.

Error Handling
1. In the client/API layer, GitHub errors are mapped to domain exceptions or appropriate HTTP status codes.
2. 404 from GitHub → ```UserNotFoundException``` → HTTP 404 returned by the application.

Author
Bartosz Kocyło
