package ru.netology.galaxycloud.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.galaxycloud.dto.FileDto;
import ru.netology.galaxycloud.repository.FileRepository;
import ru.netology.galaxycloud.service.FileService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public FileDto uploadFile(MultipartFile file, String fileName) {
        // String hash;
        // byte[] fileByte;
        return null;
    }

    @Override
    public FileDto downloadFile(String fileName) {
        // String hash;
        // byte[] fileByte;
        return null;
    }

    @Override
    public FileDto editFileName(String fileName) {
        //namefile
        return null;
    }

    @Override
    public void deleteFile(String fileName) {

    }

    @Override
    public List<FileDto> getAllFiles(int limit) {
        // String fileName;
        // String size;
        return null;
    }
}
