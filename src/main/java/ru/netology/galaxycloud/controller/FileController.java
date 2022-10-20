package ru.netology.galaxycloud.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.galaxycloud.dto.FileDto;

import ru.netology.galaxycloud.service.FileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloud/file")
public class FileController {

    private final FileService fileService;

    @PostMapping("/")
    public ResponseEntity<FileDto> uploadFile(@RequestBody MultipartFile file, @RequestParam String fileName) {
        return ResponseEntity.ok()
                .body(fileService.uploadFile(file, fileName));
    }

    @GetMapping("/")
    public ResponseEntity<FileDto> downloadFile(@RequestParam String fileName) {
        return ResponseEntity.ok()
                .body(fileService.downloadFile(fileName));
    }

    @PutMapping("/")
    public ResponseEntity<FileDto> editFileName(@RequestParam String fileName) {
        return ResponseEntity.ok()
                .body(fileService.editFileName(fileName));
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deleteFile(@RequestParam String fileName) {
        fileService.deleteFile(fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<FileDto>> getAllFiles(@RequestParam int limit) {
        return new ResponseEntity<>(fileService.getAllFiles(limit), HttpStatus.OK);
    }

}
