# GitHub Repositories API

A Spring Boot 3.5 / Java 21 application that exposes a REST endpoint
returning a user’s public GitHub repositories (excluding forks)
along with branch names and the latest commit SHA.

---

## Table of Contents
- [Features](#features)
- [Stack / Technologies](#stack--technologies)
- [Requirements](#requirements)
- [Configuration](#configuration)
- [Quickstart](#quickstart)
- [API](#api)
- [Example Response](#example-response)
- [Tests (mocking GitHub)](#tests-mocking-github)
- [Error Handling](#error-handling)
- [Author](#author)

---

## Features
- **GET / {username}** – returns the user’s public repositories without forks
- For each repository, returns branches (name + latest commit SHA)
- No token required — requests use GitHub’s public API (subject to rate limits)

---

## Stack / Technologies
- Java 21
- Spring Boot 3.5.0 (Spring Framework 6.2)
- RestClient for HTTP calls
- JUnit 5, AssertJ, Hamcrest
- MockRestServiceServer + MockServerRestClientCustomizer (mock external API in tests)
- Lombok

---

## Requirements
- JDK 21
- Maven 3.9+

---

## Configuration
Application properties (override as needed):

```properties
github.api.base-url=https://api.github.com
github.api.endpoints.user-repos=/users/{username}/repos
github.api.endpoints.repo-branches=/repos/{owner}/{repo}/branches
```

---

## Quickstart

```bash
# build
mvn clean install

# run
mvn spring-boot:run
```

---

## API
``GET /{username}``

Returns a list of the user’s public repositories excluding forks, plus their branches.
Example:

```GET http://localhost:8080/Bartoszk236```

---

## Example Response
```json
[
    {
        "repositoryName": "codeForces_tasks",
        "ownerLogin": "Bartoszk236",
        "branches": [
            {
                "branchName": "develop",
                "lastCommitSHA": "1b625ae0e4e4ef7317b33dcbaaa41a121c98dc5d"
            },
            {
                "branchName": "main",
                "lastCommitSHA": "ec0f76559e0821c14ff9d52bf9694093851498d9"
            }
        ]
    },
    {
        "repositoryName": "github-repos-api",
        "ownerLogin": "Bartoszk236",
        "branches": [
            {
                "branchName": "master",
                "lastCommitSHA": "efd3d4a13df5cc3283e2ddbd24626e58adbe54d2"
            }
        ]
    },
    {
        "repositoryName": "Homework_Mentoring",
        "ownerLogin": "Bartoszk236",
        "branches": [
            {
                "branchName": "develop",
                "lastCommitSHA": "3e41112d7f8a7998663c5e0c8963a840886035ab"
            },
            {
                "branchName": "main",
                "lastCommitSHA": "2b3278e0ec0fec284797a2a82c90714a85f34d33"
            }
        ]
    }
]
```

---

## Tests (mocking GitHub)

- Uses `MockRestServiceServer` associated with a specific `RestClient.Builder`
  via `MockServerRestClientCustomizer` to mock the external GitHub API.

---

## Error Handling

GitHub errors are mapped to domain exceptions or appropriate HTTP status codes:
- GitHub Error (404 Not Found) -> `UserNotFoundException` -> HTTP Status 404

---

## Author

Bartosz Kocyło