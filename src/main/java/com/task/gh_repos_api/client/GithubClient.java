package com.task.gh_repos_api.client;

import com.task.gh_repos_api.dto.GithubBranchResponse;
import com.task.gh_repos_api.dto.GithubRepositoryResponse;
import com.task.gh_repos_api.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GithubClient {
    private final RestClient restClient;

    public List<GithubRepositoryResponse> getRepositories(String username) {
        return restClient
                .get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            throw new UserNotFoundException("Not found GitHub repository for user " + username);
                        })
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public List<GithubBranchResponse> getBranches(String username, String repositoryName) {
        return restClient
                .get()
                .uri("/repos/{username}/{repositoryName}/branches", username, repositoryName)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
