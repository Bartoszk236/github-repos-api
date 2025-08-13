package com.task.gh_repos_api.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Branch {
    private String branchName;
    private String lastCommitSHA;
}
