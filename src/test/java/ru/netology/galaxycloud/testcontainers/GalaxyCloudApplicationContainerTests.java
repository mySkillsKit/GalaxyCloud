package ru.netology.galaxycloud.testcontainers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.galaxycloud.GalaxyCloudApplication;
import ru.netology.galaxycloud.dto.UserDto;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Testcontainers
@SpringBootTest(classes = GalaxyCloudApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GalaxyCloudApplicationContainerTests {

    private static final int PORT = 8080;
    private static final String LOGIN = "testContainers@test.com";
    private static final String PASSWORD = "Qwerty1234";

    @Autowired
    public TestRestTemplate restTemplate;
    private UserDto userDto;

    @Container
    public static PostgreSQLContainer postgreSQLContainer
            = PostgresTestContainer.getInstance();

    @Container
    public static GenericContainer<?> appcloud =
            new GenericContainer<>("galaxycloudapp:latest")
                    .withExposedPorts(PORT)
                    .dependsOn(postgreSQLContainer);
    @BeforeEach
    public void init()
    {
        userDto = UserDto.builder()
                .login(LOGIN).password(PASSWORD).build();
    }

    @Test
    void createUserTest_ThenSuccess() throws URISyntaxException {
        System.out.println(userDto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        URI url = new URI("http://" + appcloud.getHost() + ":" + PORT + "/users/create");
        HttpEntity<UserDto> requestEntity = new HttpEntity<>(userDto, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<UserDto> responseEntity = restTemplate.postForEntity(url, requestEntity, UserDto.class);
        System.out.println("Status Code: " + responseEntity.getStatusCode());

       Assertions.assertEquals(LOGIN,
               Objects.requireNonNull(responseEntity.getBody()).getLogin());
    }

    @Test
    void swaggerTest_ThenSuccess() {
        String getSwaggerURI = "http://" + appcloud.getHost() + ":" + PORT + "/swagger-ui/index.html";
        ResponseEntity<String> responseEntity = restTemplate.exchange(getSwaggerURI,
                HttpMethod.GET,
                new HttpEntity<>("body"),
                String.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void loginAppTest_ThenSuccess() {
        String getLoginURI = "http://" + appcloud.getHost() + ":" + PORT + "/login";
        String authToken = restTemplate.postForObject(getLoginURI, userDto, String.class);
        System.out.println(authToken);
        Assertions.assertNotNull(authToken);
    }
}