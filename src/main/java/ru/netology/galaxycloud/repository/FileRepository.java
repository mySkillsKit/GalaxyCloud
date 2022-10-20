package ru.netology.galaxycloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.galaxycloud.entities.File;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findFileByHash(String hash);
    List<File> findFilesByUserId(Long userId);
    void deleteFileByHash(String hash);
}
