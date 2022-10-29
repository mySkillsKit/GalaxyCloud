package ru.netology.galaxycloud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.galaxycloud.dto.FileDto;
import ru.netology.galaxycloud.exception.FileNotFoundException;
import ru.netology.galaxycloud.model.FileBody;
import ru.netology.galaxycloud.service.FileService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Cloud", description = "File management")
@SecurityRequirement(name = "Bearer-token-auth")
@RequestMapping("/")
public class FileController {

    private final FileService fileService;

    public static final String REGEXP_NAME = "\\w";
    //The same as [0-9A-Za-z_]

    @Operation(summary = "Upload file to server")
    @ApiResponse(responseCode = "200", description = "Success upload")
    @ApiResponse(responseCode = "400", description = "Error input data",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "403", description = "Unauthorized error",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "500", description = "Error upload file",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping(value = "/file",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadFile(@NotNull @RequestPart("file") MultipartFile file,
                                           @Pattern(regexp = REGEXP_NAME)
                                           @RequestParam("filename") String fileName) {
        log.info("Check if file is attached: {}", !file.isEmpty());
        if (file.isEmpty()) {
            throw new FileNotFoundException("File not attached", 0);
        }
        log.info("Upload file to server: {}", fileName);
        fileService.uploadFile(file, fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Download file from cloud")
    @ApiResponse(responseCode = "200", description = "Success download file",
            content = {@Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)})
    @ApiResponse(responseCode = "400", description = "Error input data",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "403", description = "Unauthorized error",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "404", description = "File not found",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "500", description = "Error download file",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping(value = "/file",
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<byte[]> downloadFile(@Pattern(regexp = REGEXP_NAME)
                                               @RequestParam("filename") String fileName) {
        log.info("Download file from cloud: {}", fileName);
        FileDto fileDto = fileService.downloadFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileDto.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileDto.getFileName() + "\"")
                .body(fileDto.getFileByte());
    }

    @Operation(summary = "Edit file name")
    @ApiResponse(responseCode = "200", description = "Success edited file name")
    @ApiResponse(responseCode = "400", description = "Error input data",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "403", description = "Unauthorized error",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "404", description = "File not found",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "500", description = "Error edit file name",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/file")
    public ResponseEntity<Void> editFileName(@Pattern(regexp = REGEXP_NAME)
                                             @RequestParam("filename") String fileName,
                                             @Valid @RequestBody FileBody body) {
        log.info("Edit file name: {} --> new file name: {}", fileName, body.getName());
        fileService.editFileName(fileName, body);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete file from cloud")
    @ApiResponse(responseCode = "200", description = "Success deleted file")
    @ApiResponse(responseCode = "400", description = "Error input data",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "403", description = "Unauthorized error",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "404", description = "File not found",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "500", description = "Error delete file",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@Pattern(regexp = REGEXP_NAME)
                                           @RequestParam("filename") String fileName) {
        log.info("Delete file from cloud: {}", fileName);
        fileService.deleteFile(fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get list all files")
    @ApiResponse(responseCode = "200", description = "Success get list",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FileDto.class))})
    @ApiResponse(responseCode = "400", description = "Error input data",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "403", description = "Unauthorized error",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "404", description = "File not found",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "500", description = "Error getting file list",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/list")
    public ResponseEntity<List<FileDto>> getAllFiles(@Min(1) @RequestParam int limit) {
        log.info("Get list all files. Limit: {}", limit);
        return new ResponseEntity<>(fileService.getAllFiles(limit), HttpStatus.OK);
    }
}
