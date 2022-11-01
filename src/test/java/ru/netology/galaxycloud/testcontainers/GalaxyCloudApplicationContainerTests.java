package ru.netology.galaxycloud.testcontainers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.galaxycloud.GalaxyCloudApplication;
import ru.netology.galaxycloud.dto.UserDto;

@Testcontainers
@SpringBootTest(classes = GalaxyCloudApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GalaxyCloudApplicationContainerTests {

    private static final int PORT = 8080;
    private static final String LOGIN = "test@test.com";
    private static final String PASSWORD = "test1234";

    @Autowired
    public TestRestTemplate restTemplate;

    @Container
    public static PostgreSQLContainer postgreSQLContainer
            = PostgresTestContainer.getInstance();

    @Container
    public static GenericContainer<?> appcloud =
            new GenericContainer<>("galaxycloudapp:latest")
                    .withExposedPorts(PORT)
                    .dependsOn(postgreSQLContainer);

    @Test
    void createUserTest_ThenSuccess() {
        UserDto userDto = UserDto.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .build();

        String getCreateUserURI = "http://" + appcloud.getHost() + ":" + PORT + "/users/create";
        var forEntity = restTemplate
                .postForObject(getCreateUserURI, userDto, UserDto.class);
        System.out.println(forEntity.getLogin());
        System.out.println(getCreateUserURI);

        Assertions.assertEquals(LOGIN, forEntity.getLogin());
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
        UserDto userDto = UserDto.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .build();
        String getLoginURI = "http://" + appcloud.getHost() + ":" + PORT + "/login";
        String authToken = restTemplate.postForObject(getLoginURI, userDto, String.class);
        System.out.println(authToken);
        Assertions.assertNotNull(authToken);
    }
}