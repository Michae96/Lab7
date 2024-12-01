package com.example.taskapp.config;

import com.example.taskapp.models.Task;
import com.example.taskapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByTitleContainingAndStatus(@Param("title") String title,
                                              @Param("status") String status,
                                              Pageable pageable);

    Page<Task> findByTitleContaining(@Param("title") String title, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.title LIKE %:title% AND (:status IS NULL OR t.status = :status)")
    Page<Task> findByTitleContainingAndStatusAndUser(
            @Param("title") String title,
            @Param("status") String status,
            @Param("user") User user,
            Pageable pageable
    );

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.title LIKE %:title%")
    Page<Task> findByTitleContainingAndUser(
            @Param("title") String title,
            @Param("user") User user,
            Pageable pageable
    );

}