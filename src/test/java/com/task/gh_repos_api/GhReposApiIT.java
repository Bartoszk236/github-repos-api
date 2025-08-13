package com.task.gh_repos_api;

import com.task.gh_repos_api.data.Branch;
import com.task.gh_repos_api.data.RepositoryData;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockRestServiceServer
class GhReposApiIT {

	@LocalServerPort
	int port;
	@Autowired
	RestClient.Builder builder;
	@Autowired
	MockServerRestClientCustomizer restClientCustomizer;
	MockRestServiceServer mockServer;
	RestClient http;

	@TestConfiguration
	static class TestHttpConfig {
		@Bean @Primary
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

	@Test
	void givenStubResponseForExternalApiWhenGetRepositoriesThenReturnListOfRepositoriesWithoutForks() {
		//given
		String username = "Bartoszk236";
		String firstRepositoryName = "codeForces_tasks";
		String secondRepositoryName = "Homework_Mentoring";

		String reposJson = GithubApiStubs.getRepositoriesResponseStub();
		String firstBranchJson = GithubApiStubs.getBranchesCodeForcesResponseStub();
		String secondBranchJson = GithubApiStubs.getBranchesHomeworkResponseStub();

		mockServer.expect(requestTo(containsString("/users/" + username + "/repos")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(reposJson, MediaType.APPLICATION_JSON));

		mockServer.expect(requestTo(containsString("/repos/" + username + "/" + firstRepositoryName + "/branches")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(firstBranchJson, MediaType.APPLICATION_JSON));

		mockServer.expect(requestTo(containsString("/repos/" + username + "/" + secondRepositoryName + "/branches")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(secondBranchJson, MediaType.APPLICATION_JSON));

		//when
		List<RepositoryData> results = http
				.get()
				.uri("/" + username)
				.retrieve()
				.body(new ParameterizedTypeReference<>() {});

		//then
		assertNotNull(results);

		assertThat(results)
				.extracting(RepositoryData::getRepositoryName)
				.containsExactly(firstRepositoryName, secondRepositoryName);

        RepositoryData firstResult = results.stream()
				.filter(r -> r.getRepositoryName().equals(firstRepositoryName))
				.findFirst()
				.orElseThrow(() -> new AssertionError("First repository not found"));

		assertThat(firstResult.getOwnerLogin()).isEqualTo(username);
		assertThat(firstResult.getBranches())
				.extracting(Branch::getBranchName, Branch::getLastCommitSHA)
				.containsExactlyInAnyOrder(
						tuple("develop", "1b625ae0e4e4ef7317b33dcbaaa41a121c98dc5d"),
						tuple("main", "ec0f76559e0821c14ff9d52bf9694093851498d9"));

		mockServer.verify();
	}
}
