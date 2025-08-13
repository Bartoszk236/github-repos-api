package com.task.gh_repos_api.contoller;

import com.task.gh_repos_api.data.RepositoryData;
import com.task.gh_repos_api.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApiController {
    private final ApiService apiService;

    @GetMapping("/{username}")
    public ResponseEntity<List<RepositoryData>> getRepositories(@PathVariable String username) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiService.getRepositories(username));
    }
}
