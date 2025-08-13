package com.task.gh_repos_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github.api")
public record GithubApiProps(
        String baseUrl,
        Endpoints endpoints
) {
    public record Endpoints(
            String userRepos,
            String repoBranches
    ) {
    }
}
