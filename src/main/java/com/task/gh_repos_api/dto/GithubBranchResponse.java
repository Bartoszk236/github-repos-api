package com.task.gh_repos_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubBranchResponse(
        @JsonProperty("name")
        String branchName,
        @JsonProperty("commit")
        NestedCommit commit
) {
    public String lastCommitSHA() {
        return commit.lastCommitSHA();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NestedCommit(
            @JsonProperty("sha")
            String lastCommitSHA
    ) {
    }
}
