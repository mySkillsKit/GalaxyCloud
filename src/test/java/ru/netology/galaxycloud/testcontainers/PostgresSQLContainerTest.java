package ru.netology.galaxycloud.testcontainers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.galaxycloud.GalaxyCloudApplication;
import ru.netology.galaxycloud.controller.UserController;
import ru.netology.galaxycloud.dto.UserDto;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GalaxyCloudApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
public class PostgresSQLContainerTest {

    @Container
    public static PostgreSQLContainer postgreSQLContainer
            = PostgresTestContainer.getInstance();

    @Autowired
    private UserController userController;

    private UserDto userDto;

    @BeforeEach
    public void init() {
         userDto = UserDto.builder()
                .login("test@test.com")
                .password("test1234")
                .build();
    }

    @Test
    @Rollback
    public void WhenCreateUserExpectHttpStatus() {
        System.out.println(postgreSQLContainer.getDatabaseName());
        ResponseEntity<UserDto> responseEntity = userController.createUser(userDto);
        Assertions.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        UserDto entityBody = responseEntity.getBody();

        assert entityBody != null;
        Assertions.assertEquals("test@test.com", entityBody.getLogin());
    }
}