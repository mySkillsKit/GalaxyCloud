package ru.netology.galaxycloud.service.Impl;

import com.github.dockerjava.api.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.galaxycloud.dto.FileDto;
import ru.netology.galaxycloud.entities.File;
import ru.netology.galaxycloud.entities.FileBody;
import ru.netology.galaxycloud.entities.User;
import ru.netology.galaxycloud.repository.FileRepository;
import ru.netology.galaxycloud.service.FileService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Transactional
    @Override
    public void uploadFile(MultipartFile file, String fileName) {
        if (file.isEmpty()) {
            throw new NotFoundException("File not attached");
        }
        Long userId = 100L;

        findFileNameInStorage(fileName, userId);

        String hash = null;
        byte[] fileBytes = null;

        try {
            hash = generateChecksum(file);
            fileBytes = file.getBytes();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        fileRepository.findFileByUserIdAndHash(userId, hash).ifPresent(
                s -> {
                    throw new RuntimeException("This file already uploaded");
                });

        File createdFile = getBuild(file, fileName, userId, hash, fileBytes);
        fileRepository.save(createdFile);

    }



    @Override
    public FileDto downloadFile(String fileName) {
        Long userId = 100L;
        File fileFound = getFileFromStorage(fileName, userId);

        String downFileName = (fileFound.getFileName() + "." + fileFound.getType()).trim();
        try (FileOutputStream outputStream = new FileOutputStream(downFileName)) {
            outputStream.write(fileFound.getFileByte());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileDto.builder()
                .hash(fileFound.getHash())
                .fileByte(fileFound.getFileByte())
                .build();
    }


    @Override
    public void editFileName(String fileName, FileBody body) {
        Long userId = 100L;
        File fileFoundForUpdate = getFileFromStorage(fileName, userId);
        findFileNameInStorage(body.getName(), userId);
        fileFoundForUpdate.setFileName(body.getName());
        fileFoundForUpdate.setUpdated(LocalDateTime.now());
        fileRepository.save(fileFoundForUpdate);

    }

    @Override
    public void deleteFile(String fileName) {
        Long userId = 100L;
        File fileFromStorage = getFileFromStorage(fileName, userId);
        fileRepository.deleteById(fileFromStorage.getId());
    }

    @Override
    public List<FileDto> getAllFiles(int limit) {
        Long userId = 100L;
        List<File> filesByUserIdWithLimit = fileRepository.findFilesByUserIdWithLimit(userId, limit);

        return filesByUserIdWithLimit.stream()
                .map(file -> FileDto.builder()
                        .fileName(file.getFileName())
                        .size(file.getSize())
                        .build()).collect(Collectors.toList());
    }


    private void findFileNameInStorage(String fileName, Long userId) {
        fileRepository.findFileByUserIdAndFileName(userId, fileName).ifPresent(s -> {
            throw new RuntimeException("The file with this name:{" + fileName + "} " +
                    "has already been loaded. " +
                    "Please change the file name and try again");
        });
    }

    private File getFileFromStorage(String fileName, Long userId) {
        return fileRepository.findFileByUserIdAndFileName(userId, fileName)
                .orElseThrow(() -> new NotFoundException("File not found"));
    }

    private File getBuild(MultipartFile file, String fileName, Long userId, String hash, byte[] fileBytes) {
        return File.builder()
                .hash(hash)
                .fileName(fileName)
                .type(file.getContentType())
                .size(String.valueOf(file.getSize()))
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
        return result.toString();
    }

}

