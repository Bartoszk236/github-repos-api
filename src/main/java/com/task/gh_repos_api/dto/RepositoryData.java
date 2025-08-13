package com.task.gh_repos_api.dto;

import java.util.List;

public record RepositoryData(
        String repositoryName,
        String ownerLogin,
        List<Branch> branches
) {
}
