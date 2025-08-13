package com.task.gh_repos_api.dto;

public record Branch(
        String branchName,
        String lastCommitSHA
) {
}
