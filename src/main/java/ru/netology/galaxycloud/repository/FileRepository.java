package ru.netology.galaxycloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.netology.galaxycloud.entities.File;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findFileByUserIdAndHash(Long userId, String hash);

    Optional<File> findFileByUserIdAndFileName(Long userId, String fileName);

    @Query(value = "select * from files s where s.user_id = ?1 order by s.id desc limit ?2", nativeQuery = true)
    List<File> findFilesByUserIdWithLimit(Long userId, int limit);

    void deleteFileByHash(String hash);
}
