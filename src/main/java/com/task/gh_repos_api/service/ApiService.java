package com.task.gh_repos_api.service;

import com.task.gh_repos_api.client.GithubClient;
import com.task.gh_repos_api.dto.Branch;
import com.task.gh_repos_api.dto.RepositoryData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiService {
    private final GithubClient client;

    public List<RepositoryData> getRepositories(String username) {
        return client.getRepositories(username)
                .stream()
                .filter(r -> !r.fork())
                .map(r -> {
                    List<Branch> branches = client.getBranches(username, r.repositoryName())
                            .stream()
                            .map(b -> new Branch(b.branchName(), b.lastCommitSHA()))
                            .toList();

                    return new RepositoryData(
                            r.repositoryName(),
                            r.ownerLogin(),
                            branches
                    );
                })
                .toList();
    }
}
