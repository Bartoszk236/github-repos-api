package com.task.gh_repos_api.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RepositoryData {
    private String repositoryName;
    private String ownerLogin;
    private List<Branch> branches;
}
