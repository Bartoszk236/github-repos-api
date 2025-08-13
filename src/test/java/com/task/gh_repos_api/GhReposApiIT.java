package com.task.gh_repos_api;

import com.task.gh_repos_api.dto.Branch;
import com.task.gh_repos_api.dto.RepositoryData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockRestServiceServer
class GhReposApiIT {
    private static final String USERNAME = "Bartoszk236";
    private static final String FIRST_REPO_NAME = "codeForces_tasks";
    private static final String SECOND_REPO_NAME = "Homework_Mentoring";

    @LocalServerPort
    private int port;
    @Autowired
    private RestClient.Builder builder;
    @Autowired
    private MockServerRestClientCustomizer restClientCustomizer;
    private MockRestServiceServer mockServer;
    private RestClient http;

    @TestConfiguration
    static class TestHttpConfig {
        @Bean
        @Primary
        RestClient.Builder testRestClientBuilder(MockServerRestClientCustomizer customizer) {
            RestClient.Builder b = RestClient.builder();
            customizer.customize(b);
            return b;
        }
    }

    @BeforeEach
    void setUp() {
        mockServer = restClientCustomizer.getServer(builder);
        mockServer.reset();
        http = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    void givenStubResponseForExternalApiWhenGetRepositoriesThenReturnListOfRepositoriesWithoutForks() {
        //given
        expectRepos(GithubApiStubs.getRepositoriesResponseStub());
        expectBranches(FIRST_REPO_NAME, GithubApiStubs.getBranchesCodeForcesResponseStub());
        expectBranches(SECOND_REPO_NAME, GithubApiStubs.getBranchesHomeworkResponseStub());

        //when
        List<RepositoryData> results = http
                .get()
                .uri("/" + USERNAME)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        //then
        assertThat(results)
                .isNotNull()
                .hasSize(2)
                .extracting(RepositoryData::repositoryName)
                .containsExactlyInAnyOrder(FIRST_REPO_NAME, SECOND_REPO_NAME);

        assertThat(results)
                .anySatisfy(repo -> {
                    assertThat(repo.repositoryName()).isEqualTo(FIRST_REPO_NAME);
                    assertThat(repo.ownerLogin()).isEqualTo(USERNAME);
                    assertThat(repo.branches())
                            .extracting(Branch::branchName, Branch::lastCommitSHA)
                            .containsExactlyInAnyOrder(
                                    tuple("develop", "1b625ae0e4e4ef7317b33dcbaaa41a121c98dc5d"),
                                    tuple("main", "ec0f76559e0821c14ff9d52bf9694093851498d9")
                            );
                });

        assertThat(results)
                .anySatisfy(repo -> {
                    assertThat(repo.repositoryName()).isEqualTo(SECOND_REPO_NAME);
                    assertThat(repo.ownerLogin()).isEqualTo(USERNAME);
                    assertThat(repo.branches())
                            .extracting(Branch::branchName, Branch::lastCommitSHA)
                            .containsExactlyInAnyOrder(
                                    tuple("develop", "3e41112d7f8a7998663c5e0c8963a840886035ab"),
                                    tuple("main", "2b3278e0ec0fec284797a2a82c90714a85f34d33")
                            );
                });
    }

    private void expectRepos(String reposJson) {
        mockServer.expect(requestTo(containsString("/users/" + USERNAME + "/repos")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(reposJson, MediaType.APPLICATION_JSON));
    }

    private void expectBranches(String repoName, String branchesJson) {
        mockServer.expect(requestTo(containsString("/repos/" + USERNAME + "/" + repoName + "/branches")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(branchesJson, MediaType.APPLICATION_JSON));
    }
}
