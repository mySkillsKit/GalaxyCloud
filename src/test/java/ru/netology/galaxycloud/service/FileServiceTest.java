package ru.netology.galaxycloud.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.galaxycloud.dto.FileDto;
import ru.netology.galaxycloud.entities.File;
import ru.netology.galaxycloud.entities.User;
import ru.netology.galaxycloud.exception.FileNotFoundException;
import ru.netology.galaxycloud.model.FileBody;
import ru.netology.galaxycloud.repository.FileRepository;
import ru.netology.galaxycloud.security.JwtProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FileServiceTest {

    private static final String MY_FILE_NAME = "fileName.txt";

    @Autowired
    private FileService fileService;
    @MockBean
    private FileRepository fileRepository;
    @MockBean
    private JwtProvider jwtProvider;

    private User user;
    private File fileFound;

    @BeforeEach
    public void init() {
        user = User.builder().id(1L).build();
        fileFound = File.builder()
                .id(10L)
                .fileName(MY_FILE_NAME)
                .hash("0b28122bac2239a5968bdb1112cae820")
                .type(MediaType.TEXT_PLAIN_VALUE)
                .size(19L)
                .fileByte("Java 11 GalaxyCloud".getBytes())
                .created(LocalDateTime.now())
                .user(user)
                .build();
    }

    @Test
    void testUploadFile_thenSuccess() throws IOException {
        String hash = "0b28122bac2239a5968bdb1112cae820";
        MultipartFile multipartFile =
                new MockMultipartFile("file", MY_FILE_NAME,
                        MediaType.TEXT_PLAIN_VALUE, "Java 11 GalaxyCloud".getBytes());
        Mockito.when(jwtProvider.getAuthorizedUser()).thenReturn(user);
        Mockito.when(fileRepository.findFileByUserIdAndFileName(1L, MY_FILE_NAME))
                .thenReturn(Optional.empty());
        Mockito.when(fileRepository.findFileByUserIdAndHash(1L, hash))
                .thenReturn(Optional.empty());
        File createdFile = File.builder()
                .id(10L)
                .hash(hash)
                .fileName(MY_FILE_NAME)
                .type(multipartFile.getContentType())
                .size(multipartFile.getSize())
                .fileByte(multipartFile.getBytes())
                .user(User.builder().id(1L).build())
                .build();

        fileService.uploadFile(multipartFile, MY_FILE_NAME);

        Assertions.assertEquals(19L, createdFile.getSize());
    }

    @Test
    void testDownloadFile_thenSuccess() {

        Mockito.when(jwtProvider.getAuthorizedUser()).thenReturn(user);
        Mockito.when(fileRepository.findFileByUserIdAndFileName(1L, MY_FILE_NAME))
                .thenReturn(Optional.ofNullable(fileFound));

        FileDto downloadFile = fileService.downloadFile(MY_FILE_NAME);

        Assertions.assertEquals(MY_FILE_NAME, downloadFile.getFileName());
    }

    @Test
    void testEditFileName_thenSuccess() {
        FileBody newName = new FileBody("anyName.txt");
        Mockito.when(jwtProvider.getAuthorizedUser()).thenReturn(user);
        Mockito.when(fileRepository.findFileByUserIdAndFileName(1L, MY_FILE_NAME))
                .thenReturn(Optional.ofNullable(fileFound));

        fileService.editFileName(MY_FILE_NAME, newName);

        Mockito.verify(fileRepository,
                Mockito.times(1)).save(fileFound);
    }

    @Test
    void testDeleteFile_thenSuccess() {
        Mockito.when(jwtProvider.getAuthorizedUser()).thenReturn(user);
        Mockito.when(fileRepository.findFileByUserIdAndFileName(1L, MY_FILE_NAME))
                .thenReturn(Optional.ofNullable(fileFound));

        fileService.deleteFile(MY_FILE_NAME);

        Mockito.verify(fileRepository,
                Mockito.times(1)).deleteById(fileFound.getId());
    }

    @Test
    void testGetAllFiles_thenSuccess() {
        int limit = 3;
        List<File> listFile = List.of(
                File.builder().size(1233L).fileName("file1.txt").build(),
                File.builder().size(32272L).fileName("file2.txt").build(),
                File.builder().size(2353L).fileName("file3.txt").build());
        Mockito.when(jwtProvider.getAuthorizedUser()).thenReturn(user);
        Mockito.when(fileRepository.findFilesByUserIdWithLimit(user.getId(), limit))
                .thenReturn(listFile);

        List<FileDto> files = fileService.getAllFiles(limit);

        Assertions.assertEquals("file1.txt", files.get(0).getFileName());
    }

    @Test
    void testDownloadFileIsEmpty_thenFileNotFoundException() {
        Mockito.when(jwtProvider.getAuthorizedUser()).thenReturn(user);
        Mockito.when(fileRepository.findFileByUserIdAndFileName(1L, MY_FILE_NAME))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(FileNotFoundException.class, () -> {
            fileService.downloadFile(MY_FILE_NAME);
        });

        String expectedMessage = "File not found by file name";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}