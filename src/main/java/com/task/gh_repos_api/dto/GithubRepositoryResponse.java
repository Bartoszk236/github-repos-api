package com.task.gh_repos_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepositoryResponse(
        @JsonProperty("name")
        String repositoryName,
        @JsonProperty("owner")
        NestedOwner owner,
        @JsonProperty("fork")
        boolean fork
) {
    public String ownerLogin() {
        return this.owner.ownerLogin;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NestedOwner(
            @JsonProperty("login")
            String ownerLogin
    ) {
    }
}
