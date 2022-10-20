package ru.netology.galaxycloud.service;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.galaxycloud.dto.*;


import java.util.List;

public interface FileService {

    FileDto uploadFile (MultipartFile file, String fileName);

    FileDto downloadFile(String fileName);

    FileDto editFileName(String fileName);

    void deleteFile(String fileName);

    List<FileDto> getAllFiles(int limit);
}
