package ru.practicum.mainservice.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.comment.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
