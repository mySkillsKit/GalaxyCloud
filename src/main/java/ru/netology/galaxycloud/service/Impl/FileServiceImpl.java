package ru.netology.galaxycloud.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.galaxycloud.dto.FileDto;
import ru.netology.galaxycloud.entities.File;
import ru.netology.galaxycloud.entities.User;
import ru.netology.galaxycloud.exception.FileNotFoundException;
import ru.netology.galaxycloud.exception.InvalidInputData;
import ru.netology.galaxycloud.model.FileBody;
import ru.netology.galaxycloud.repository.FileRepository;
import ru.netology.galaxycloud.security.JwtProvider;
import ru.netology.galaxycloud.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    @Override
    public void uploadFile(MultipartFile file, String fileName) {
        Long userId = jwtProvider.getAuthorizedUser().getId();
        log.info("Find file in Storage by file name {} and ID {}", fileName, userId);
        findFileNameInStorage(fileName, userId);
        String hash = null;
        byte[] fileBytes = null;
        try {
            hash = generateChecksum(file);
            fileBytes = file.getBytes();
            log.info("Get fileByte! and Generated check sum file hash:{}", hash);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        log.info("Find file in Storage by userId:{} and hash :{}", userId, hash);
        fileRepository.findFileByUserIdAndHash(userId, hash).ifPresent(
                s -> {
                    throw new InvalidInputData("This file already uploaded. " +
                            "Please upload other file", userId);
                });

        File createdFile = getBuild(file, fileName, userId, hash, fileBytes);
        log.info("Creating file and save to Storage: {}", createdFile);

        fileRepository.save(createdFile);
    }

    @Transactional
    @Override
    public FileDto downloadFile(String fileName) {
        Long userId = jwtProvider.getAuthorizedUser().getId();
        log.info("Find file in Storage by file name {} and ID {}", fileName, userId);
        File fileFound = getFileFromStorage(fileName, userId);
        log.info("Downloaded file: {} from Storage. UserId: {}", fileName, userId);
        return FileDto.builder()
                .fileName(fileFound.getFileName())
                .type(fileFound.getType())
                .fileByte(fileFound.getFileByte())
                .build();
    }

    @Override
    public void editFileName(String fileName, FileBody body) {
        Long userId = jwtProvider.getAuthorizedUser().getId();
        log.info("Find file in Storage for edit " +
                "by file name {} and userID {}", fileName, userId);
        File fileFoundForUpdate = getFileFromStorage(fileName, userId);
        log.info("Check new file name in Storage for edit " +
                "by file name {} and userID {}", body.getName(), userId);
        findFileNameInStorage(body.getName(), userId);
        log.info("Editing file name in Storage " +
                "by file name {} and userID {}", fileFoundForUpdate, userId);
        fileFoundForUpdate.setFileName(body.getName());
        fileFoundForUpdate.setUpdated(LocalDateTime.now());
        log.info("Edited file name in Storage " +
                "by file name {} and userID {}", fileFoundForUpdate, userId);
        fileRepository.save(fileFoundForUpdate);
    }

    @Override
    public void deleteFile(String fileName) {
        Long userId = jwtProvider.getAuthorizedUser().getId();
        log.info("Find file in Storage for delete" +
                " by file name {} and userID {}", fileName, userId);
        File fileFromStorage = getFileFromStorage(fileName, userId);
        log.info("Delete file from Storage " +
                "by file name {} and userID {}", fileFromStorage, userId);
        fileRepository.deleteById(fileFromStorage.getId());
    }

    @Override
    public List<FileDto> getAllFiles(int limit) {
        Long userId = jwtProvider.getAuthorizedUser().getId();
        log.info("Find all file in Storage " +
                " by userID {} and limit: {}", userId, limit);
        List<File> filesByUserIdWithLimit =
                fileRepository.findFilesByUserIdWithLimit(userId, limit);
        log.info("Found all file in Storage " +
                " by userID {} and limit: {} | List<File>: {}", userId, limit, filesByUserIdWithLimit);
        return filesByUserIdWithLimit.stream()
                .map(file -> FileDto.builder()
                        .fileName(file.getFileName())
                        .size(file.getSize())
                        .build()).collect(Collectors.toList());
    }

    private void findFileNameInStorage(String fileName, Long userId) {
        fileRepository.findFileByUserIdAndFileName(userId, fileName).ifPresent(s -> {
            throw new InvalidInputData("The file with this name:{" + fileName + "} " +
                    "was found. UserId:{}. Please change the file name and try again", userId);
        });
        log.info("File not found in Storage by file name {} and ID {}",
                fileName, userId);
    }

    private File getFileFromStorage(String fileName, Long userId) {
        return fileRepository.findFileByUserIdAndFileName(userId, fileName)
                .orElseThrow(() -> new FileNotFoundException(
                        "File not found by file name: " + fileName + " and userID", userId));
    }

    private File getBuild(MultipartFile file, String fileName, Long userId, String hash, byte[] fileBytes) {
        log.info("Building file: {} hash: {} and UserID: {}", fileName, hash, userId);
        return File.builder()
                .hash(hash)
                .fileName(fileName)
                .type(file.getContentType())
                .size(file.getSize())
                .fileByte(fileBytes)
                .created(LocalDateTime.now())
                .user(User.builder().id(userId).build())
                .build();
    }

    private String generateChecksum(MultipartFile file) throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance("MD5");

        try (InputStream fis = file.getInputStream()) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
        }
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        log.info("Successful Generate Checksum for fileName: {}", file.getName());
        return result.toString();
    }
}

